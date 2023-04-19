/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lemony.chatgpt.service;

import cn.hutool.json.JSONUtil;
import com.lemony.chatgpt.pojo.ChatRequest;
import com.lemony.chatgpt.util.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@ServerEndpoint("/streamChat/{sid}")
public class WebSocketServer {

    private final static Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    //使用AtomicInteger保证线程安全
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    //ConcurrentHashMap，用来存放每个客户端对应的MyWebSocket对象，提高并发能力
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    //与某个客户端的连接会话，通过它给客户端发送数据
    private Session session;

    //接收sid
    private String sid = "";
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        this.sid = sid;
        // 将 WebSocketServer 实例加入到 ConcurrentHashMap 中
        webSocketMap.put(sid, this);
        // 在线数加 1
        addCount();
        log.info("有新窗口开始监听:" + sid + ",当前在线人数为:" + getOnlineCount());
    }

    @OnClose
    public void onClose() {
        // 从 ConcurrentHashMap 中删除 WebSocketServer 实例
        webSocketMap.remove(sid);
        // 在线数减 1
        subCount();
        log.info("释放的 sid 为：" + sid);
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message) {
        ChatRequest chatRequest = JSONUtil.toBean(message, ChatRequest.class);
        try {
            SpringContextHolder.getBean(GptApiService.class).generateMessage(chatRequest,this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnError
    public void onError(Throwable error) {
        log.error("连接错误");
        error.printStackTrace();
    }

    public void sendMessage(String message) throws IOException {
        if (message != null) {
            this.session.getBasicRemote().sendText(message);
        }
    }
    public void groupMessage(String message, @PathParam("sid") String sid) {
        for (WebSocketServer item : webSocketMap.values()) {
            try {
                // 这里可以设定只推送给这个 sid 的，为 null 则全部推送
                if (sid == null) {
                    item.sendMessage(message);
                } else if (item.sid.equals(sid)) {
                    item.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getOnlineCount() {
        return WebSocketServer.onlineCount.get();
    }
    public static void addCount() {
        WebSocketServer.onlineCount.getAndIncrement();
    }

    public static  void subCount() {
        WebSocketServer.onlineCount.decrementAndGet();
    }

}