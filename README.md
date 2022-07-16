这是CSU 2022 Java大作业，是关于六级单词记忆对战游戏的项目。

# 项目文件

WordMemory文件夹共含有以下文件：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image001.png)

下面简要介绍src源文件：

Src源文件共分为Client软件包（内有Game.java,Login.java,Register.java,select.java四个java文件）,people.png,Server.java,CET6.txt,WordMemory.iml等文件。

（1）Login.java用于客户端登录账户，包含类Login，其中有以下函数：

Login()，为构造函数；

actionPerformed(ActionEvent e)，为按钮事件触发函数；

main()，为主函数；

（2）Register.java用于客户端注册账户，包含类Register，其中有以下函数：

Register(PrintStream ps, BufferedReader br) ，为构造函数；

actionPerformed(ActionEvent e)，为按钮事件触发函数；

（3）Game.java用于客户端游戏的进行，包含类Game，该类含有类中类TimeThread、MonitorThread；

**类Game**中有以下函数：

Game(String path,PrintStream ps, BufferedReader br) ，为构造函数；

run()为线程运行函数，不断接收服务器的信息；

actionPerformed(ActionEvent e) ，为按钮事件触发函数；

类TimeThread中有以下函数：

run()为线程运行函数，一秒变化一次时间；

**类MonitorThread**中有以下函数：

MonitorThread(int X,int Y) ，为构造函数；

run()为线程运行函数，接收服务器发来的信息；

（4）select.java用于客户端弹出正确、错误等消息，包含类select，其中有以下函数：

Select(int sel,String word,String Nick,int X,int Y) ，为构造函数；

actionPerformed(ActionEvent e) ，为按钮事件触发函数；

run()为线程运行函数，用于等10秒；

（5）Server.java用于服务器操作的实现，包含类Server，该类含有类中类UserThread、WaitingThread；

**类Server**中有以下函数：

Server()，为构造函数；

​    Load()，为导入六级单词函数；

run()为线程运行函数；

**类UserThread**中有以下函数：

UserThread(Socket s) ，为构造函数；

run()为线程运行函数；

Distribute()，为单词分发函数；

Change_Info()，为更改信息函数；

**类WaitingThread**中有以下函数：

run()为线程运行函数，用于等10秒；

main()，为主函数。

# 项目演示

客户端登录页面：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image002.jpg)

客户端注册页面：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image004.jpg)

客户端欢迎界面：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image006.jpg)

客户端有人回答正确时界面：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image008.jpg)

客户端有人回答错误时界面：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image010.jpg)

客户端无人回答时界面：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image012.jpg)

一方扣至0分时界面：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image014.jpg)

一方未掌握单词的截图：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image016.jpg)

一方已掌握单词的截图：

![img](https://qianzeshu.oss-cn-hangzhou.aliyuncs.com/img/clip_image018.jpg)
