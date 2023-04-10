# chatgpt-master

#### 介绍
Java+gpt-3.5-turbo聊天应用,后端代码
使用前后端分离，前端用vue实现,代码见另一个仓库

#### 软件架构
springboot


#### 安装教程

1.  后端部署时需要在配置文件application.yaml中将openai的apiKey替换会你自己的apiKey

![输入图片说明](https://foruda.gitee.com/images/1679397592238625282/16cb367f_9596093.png "截屏2023-03-21 19.18.53.png")

2. 微软azure语音服务的使用

    a.Azure 订阅 - 免费创建订阅

    ![输入图片说明](%E5%BE%AE%E8%BD%AFAzure%E6%9C%8D%E5%8A%A1.png)

    b.在 Azure 门户中创建语音资源。

    ![输入图片说明](%E5%88%9B%E5%BB%BA%E8%AF%AD%E9%9F%B3%E8%B5%84%E6%BA%90.png)
    
    c.获取语音资源密钥和区域。 部署语音资源后，选择“转到资源”以查看和管理密钥。

    ![输入图片说明](%E8%8E%B7%E5%8F%96%E8%AF%AD%E9%9F%B3%E8%B5%84%E6%BA%90.png)

    d.设置环境变量。必须对应用程序进行身份验证才能访问认知服务资源。
     例如，获取语音资源的密钥后，请将其写入运行应用程序的本地计算机上的新环境变量。
     
    若要为语音资源密钥设置环境变量，请打开控制台窗口，并按照操作系统和开发环境的说明进行操作。
    若要设置 SPEECH_KEY 环境变量，请将 your-key 替换为资源的其中一个密钥。
    若要设置 SPEECH_REGION 环境变量，请将 your-region 替换为你的资源的其中一个区域。
    
    Windows:
    
    ```
    setx SPEECH_KEY your-key
    setx SPEECH_REGION your-region
    ```

    Linux:

    ```
    export SPEECH_KEY=your-key
    export SPEECH_REGION=your-region
    ```
    添加环境变量后，请从控制台窗口运行 source ~/.bashrc，使更改生效。

    Mac:

    ```
        export SPEECH_KEY=your-key
        export SPEECH_REGION=your-region
    ```
    添加环境变量后，请从控制台窗口运行 source ~/.bashrc，使更改生效。
    
    e.在后端配置文件application.yaml文件中，将密钥和区域设置成自己的密钥和区域

    ![输入图片说明](%E6%9B%BF%E6%8D%A2%E5%AF%86%E9%92%A5.png)
    

3. 音频格式化
    a.微软语音转文字服务要求的音频格式为:采样率16000HZ、深度为16的wav格式
    b.如果前端录入的语音满足上述格式，则不需要进行格式转换
    c.转换格式可以用到FFMPEG框架,需要电脑本地安装FFMPEG，需注意安装容易出错，各种踩坑

4. 语音保存路径自行修改

   在AudioUtil中

    ![输入图片说明](%E8%AF%AD%E9%9F%B3%E8%B7%AF%E5%BE%841.png)

   在TextToSpeechImpl中

    ![输入图片说明](%E8%AF%AD%E9%9F%B3%E8%B7%AF%E5%BE%842.png)
    







#### 使用说明
1.  调用的model是gpt-3.5-turbo
2.  可实现联系上下文对话
3.  支持选择是否开启“连续对话”，由于token会消耗费用，关闭连续对话可以节省token
4.  支持语音对话
![输入图片说明](%E7%95%8C%E9%9D%A2%E4%BB%8B%E7%BB%8D.png)

5.支持余额查询，自定义ApiKey和token大小
![输入图片说明](%E8%AE%BE%E7%BD%AE%E4%BB%8B%E7%BB%8D.png)


#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
