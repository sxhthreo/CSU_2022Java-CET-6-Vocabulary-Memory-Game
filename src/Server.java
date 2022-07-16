import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
public class Server extends JFrame implements Runnable{
    private static String url = "jdbc:mysql://localhost:3306/login?useSSL=false&allowPublicKeyRetrieval=true";    //连接数据库的url,login是数据库名
    private static String mysql_user = "root";        //mysql登录名
    private static String mysql_pass = "qzc789654";   //mysql登录密码
    private static Connection con;              //建立连接
    private ServerSocket ss = null;
    private JTextArea jta = new JTextArea();
    public static ArrayList<UserThread> users = new ArrayList<UserThread>();  //存放用户
    public static ArrayList<String> words = new ArrayList<String>();
    public static ArrayList<String> meanings = new ArrayList<String>();
    private JScrollPane listPane = new JScrollPane(jta);       //设置滚动视图
    public Server() throws Exception{
        Class.forName("com.mysql.cj.jdbc.Driver");     //Class.forName查找并加载指定的类,加载数据库连接驱动并连接
        con = DriverManager.getConnection(url,mysql_user,mysql_pass);
        Font font = new Font("微软雅黑", Font.PLAIN, 20);
        jta.setFont(font);
        //窗体基本设置
        this.setTitle("服务器");
        this.add(listPane);
        this.setSize(500,500);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        Load();
        ss = new ServerSocket(9999);
        new Thread(this).start();
    }
    //导入六级单词
    public void Load() throws Exception{
        FileInputStream fis = new FileInputStream("CET6.txt");
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        while(true){
            String str = br.readLine();
            if(str == null){
                break;
            }
            int index = str.indexOf(" ", 3);    //index分割中英文
            String str1 = str.substring(3,index).trim();      //trim删除前导和尾随空格
            String str2 = str.substring(index+1).trim();
            words.add(str1);
            meanings.add(str2);
        }
    }
    public void run(){
        while(true){
            try{
                Socket s = ss.accept();
                UserThread user = new UserThread(s);
                user.start();
            }catch(Exception e){}
        }
    }
    //用户和服务器数据传输
    class UserThread extends Thread {
        private BufferedReader ReadThing = null;
        private PrintStream SendWord = null;
        private static String Word_Random = null;
        private static String Word_Random_Trans = null;
        private static int connect = 0;
        private static int Word_List;
        private static StringBuffer Word_Tips = new StringBuffer();
        private int point = 10;
        private String aa = null;
        private static int point1 = 10;
        private static int point2 = 10;
        private String NickName;
        private static boolean flag = true;
        private static boolean NResponseFlag = true;
        public UserThread(Socket s) throws Exception {
            ReadThing = new BufferedReader(new InputStreamReader(s.getInputStream()));
            SendWord = new PrintStream(s.getOutputStream());
        }

        public void run() {
            while (true) {
                try {
                    aa = ReadThing.readLine();
                    String[] msgs = aa.split(":");
                    //注册和登录产生的通信行为
                    if(msgs[0].equals("REG1")){          //客户端发送注册消息
                        if(msgs[1].equals(msgs[2])){     //判断两次输入的密码是否一致
                            SendWord.println("YES");
                        }else{
                            SendWord.println("NO");
                        }
                    }else if(msgs[0].equals("REG2")) {  //客户端发来账号和密码
                        try{
                            //检测该账号是否已被使用
                            String sql = "select username from client where username=?";  //?为占位符
                            PreparedStatement ptmt = con.prepareStatement(sql);     //加入预编译语句
                            ptmt.setString(1,msgs[1]);  //1是ID
                            ResultSet rs = ptmt.executeQuery();
                            if(rs.next()){
                                SendWord.println("EXISTS");
                            }else{
                                SendWord.println("INSERT");     //将账号密码信息加入数据库中
                                sql = "insert into client (username,password) values(?,?)";  //?为占位符
                                ptmt = con.prepareStatement(sql);     //加入预编译语句
                                ptmt.setString(1,msgs[1]);
                                ptmt.setString(2,msgs[2]);
                                ptmt.execute();     //执行sql语句
                                ptmt.close();
                            }
                        }catch(Exception exce){}
                    }else if(msgs[0].equals("LOGIN")){
                        String sql= "select username,password from client where username=? and password=?";
                        PreparedStatement ptmt = con.prepareStatement(sql);
                        ptmt.setString(1,msgs[1]);
                        ptmt.setString(2,msgs[2]);
                        ResultSet rs = ptmt.executeQuery();
                        if(rs.next()) {      //存在此人且账号密码正确
                            SendWord.println("OK");     //同意登录
                            NickName = msgs[1];
                            users.add(this);            //将自己加入users数组中
                            File file1 = new File(NickName);
                            file1.mkdirs();     //创建该名字命名的文件夹，用于后续复习单词
                            jta.append("等待连接……\n");
                            jta.append(NickName + "连接成功!\n");
                            if(users.size() == 1)     //size返回变长数组的元素数
                            {
                                jta.append("等待匹配玩家,请稍后……\n");
                            }
                            else if(users.size() == 2){
                                jta.append("玩家匹配完成,准备开始游戏!\n");
                            }
                        }else {      //拒绝登录
                            SendWord.println("NO");
                        }
                    }
                    //游戏产生的客户端与服务器通信
                    else if (aa.equals("NEW")) {     //开始游戏采用该方法
                        connect++;
                        //开始游戏
                        if (connect == 1) {     //初始connect可能为1，即只有一个用户连接
                            Word_List = (int) (Math.random() * words.size()); //分发一个随机单词供复习
                            Word_Random = words.get(Word_List);
                            Word_Random_Trans = meanings.get(Word_List);
                            SendWord.println("WAIT:"+Word_Random+":"+Word_Random_Trans);
                        } else {
                            jta.append("游戏开始!\n");
                            Distribute();       //负责第一次的选单词发单词
                        }
                    } else {
                        Change_Info();          //判断回答正确性
                    }
                } catch (Exception e) {}
            }
        }

        public static void Distribute() {
            Word_List = (int) (Math.random() * words.size()); //随机选单词
            Word_Random = words.get(Word_List);
            Word_Random_Trans = meanings.get(Word_List);
            int number = (int) (Math.random() * 2);    //number为0提示一个,为1提示两个
            int index1 = (int) (Math.random() * Word_Random.length());      //随机选择一个位置
            char tip1 = Word_Random.charAt(index1);    //获取整个位置对应的字符
            Word_Tips.delete(0,Word_Tips.length());    //清空上一次单词的提示信息
            if (number == 0) {
                for (int i = 0; i < Word_Random.length(); i++) {
                    if (i == index1) {
                        Word_Tips.append(tip1 + " ");     //将char转为string类型
                    } else {
                        Word_Tips.append("_ ");
                    }
                }
            } else {
                int index2 = (int) (Math.random() * Word_Random.length());
                char tip2 = Word_Random.charAt(index2);
                for (int i = 0; i < Word_Random.length(); i++) {
                    if (i == index1) {
                        Word_Tips.append(tip1 + " ");
                    } else if (i == index2) {
                        Word_Tips.append(tip2 + " ");
                    } else {
                        Word_Tips.append("_ ");
                    }
                }
            }
            String Word_T = Word_Tips.toString();       //将StringBuffer类型转为String类型
            for (UserThread user_a : users) {           //分发单词翻译、提示和目前用户分数
                user_a.SendWord.println(Word_Random_Trans+":"+Word_T+":"+user_a.point);
            }
        }

        public void Change_Info() throws Exception{
            if (aa.equals("NO")) {       //双方都没有回答
                point--;        //双方都扣一分
                SendWord.println("2:" + Word_Random + ":" + point);
                NResponseFlag = false;    //flag标记是没回答导致的扣分
                File f = new File(this.NickName+"\\未掌握单词.txt");
                FileOutputStream fos = new FileOutputStream(f, true);   //append参数代表字节写入文件末尾
                PrintStream Print_word = new PrintStream(fos);
                Print_word.println(Word_Random+"  "+Word_Random_Trans+"（没有答题）");    //在txt中记录
                fos.close();
                if(users.get(0) == this){
                    point1 = point;         //将分数赋给各自，用于后面判断是否剩余分数是正数
                }else{
                    point2 = point;
                }
            } else if (aa.equals(Word_Random)) {    //回答正确
                point++;
                jta.append(NickName+"回答正确，加一分!\n当前比分为:"+users.get(0).NickName+
                        users.get(0).point+"分,"+users.get(1).NickName+
                        users.get(1).point+"分。\n");     //服务器界面显示这些文字
                for (UserThread user_a : users) {
                    if (user_a == this) {
                        user_a.SendWord.println("1:" + Word_Random + ":" + user_a.point);
                        //写入txt
                        File f = new File(user_a.NickName+"\\已掌握单词.txt");
                        FileOutputStream fos = new FileOutputStream(f, true);
                        PrintStream Print_word = new PrintStream(fos);
                        Print_word.println(Word_Random+"  "+Word_Random_Trans);
                        fos.close();
                    } else {       //未回答的一方
                        user_a.SendWord.println("STOP");        //另一方发送停止信息，使单词停止下落
                        user_a.SendWord.println("2:" + Word_Random + ":" + user_a.point);
                        //写入txt
                        File f = new File(user_a.NickName+"\\未掌握单词.txt");
                        FileOutputStream fos = new FileOutputStream(f, true);
                        PrintStream Print_word = new PrintStream(fos);
                        Print_word.println(Word_Random+"  "+Word_Random_Trans+"（没有答题）");
                        fos.close();
                    }
                }
            } else {       //回答错误
                point = point - 2;
                if(point < 0){
                    point = 0;      //得分最小值为0
                }
                jta.append(NickName+"回答错误，减2分!\n当前比分为:"+users.get(0).NickName+
                        users.get(0).point+"分,"+users.get(1).NickName+
                        users.get(1).point+"分。\n");
                for (UserThread user_a : users) {
                    if (user_a == this) {
                        user_a.SendWord.println("0:" + Word_Random + ":" + user_a.point);
                        File f = new File(user_a.NickName+"\\未掌握单词.txt");
                        FileOutputStream fos = new FileOutputStream(f, true);
                        PrintStream Print_word = new PrintStream(fos);
                        Print_word.println(Word_Random+"  "+Word_Random_Trans+"（回答错误）");
                        fos.close();
                        point1 = user_a.point;          //分数赋给point1，判断是否还大于0
                    } else {       //未回答的一方
                        user_a.SendWord.println("STOP");
                        user_a.SendWord.println("2:" + Word_Random + ":" + user_a.point);
                        File f = new File(user_a.NickName+"\\未掌握单词.txt");
                        FileOutputStream fos = new FileOutputStream(f, true);
                        PrintStream Print_word = new PrintStream(fos);
                        Print_word.println(Word_Random+"  "+Word_Random_Trans+"（没有答题）");
                        fos.close();
                        point2 = user_a.point;
                    }
                }
            }
            if (point1 <= 0 || point2 <= 0) {
                flag = false;       //flag=false，代表有人分数小于0
            }
            try{
                Thread.sleep(100);      //0.1s，防止两方数据不同步
            }catch(Exception ee){}
            if (flag) {
                if (NResponseFlag) {        //不是双方不回答引起的，每个客户都要发一个NOP
                    for (UserThread user_a : users) {
                        user_a.SendWord.println("NOP");
                    }
                    WaitingThread waiting = new WaitingThread();
                    waiting.start();        //等十秒发单词
                } else {                    //双方不回答引起的，对连接的用户发一个NOP
                    SendWord.println("NOP");
                    if (this == users.get(0)) {     //指定第一个用户socket发单词（指定哪个都没关系）
                        jta.append("双方都没有回答问题，各扣一分!\n当前比分为:"+users.get(0).NickName+
                                users.get(0).point+"分,"+users.get(1).NickName+
                                users.get(1).point+"分。\n");
                        WaitingThread waiting = new WaitingThread();
                        waiting.start();        //等十秒发单词
                    }
                }
            }else if ((point1 <= 0 && point2 > 0)||(point2 <= 0 && point1 > 0)) {
                for (UserThread user_a : users) {
                    user_a.SendWord.println("WIN");        //这里双方都发，输的人已经break了
                }
                if(users.get(0).point == 0){
                    //如果是有人回答错误抵达的(这样NResponseFlag是true)，服务器端直接显示，否则只需一人显示信息
                    if(NResponseFlag || this == users.get(1)){
                        jta.append(users.get(1).NickName+"赢得了本次比赛的胜利!\n");
                        jta.append("最终比分为:" +users.get(0).NickName+ users.get(0).point+"分,"
                                +users.get(1).NickName+ users.get(1).point+"分。\n");
                    }
                }else{
                    if(NResponseFlag || this == users.get(0)) {
                        jta.append(users.get(0).NickName + "赢得了本次比赛的胜利!\n");
                        jta.append("最终比分为:" +users.get(0).NickName+ users.get(0).point+"分,"
                                +users.get(1).NickName+ users.get(1).point+"分。\n");
                    }
                }
            }
        }
    }
    class WaitingThread extends Thread{
        public void run(){
            //等十秒钟
            try{
                Thread.sleep(9900);
            }catch (Exception e){};
            UserThread.NResponseFlag = true;   //保证之后正常发，为防止线程冲突，要等十秒后再设置
            UserThread.Distribute();
        }
    }
    public static void main(String[] args) throws Exception{
        new Server();
    }
}
