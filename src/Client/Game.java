package Client;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Game extends JFrame implements ActionListener,Runnable {
    private JLabel welcome = new JLabel("欢迎进入六级单词强化记忆游戏",JLabel.CENTER);
    private JButton start = new JButton("开始游戏");
    private JLabel bottom = new JLabel("Welcome to CET-6 enhanced memory game.",JLabel.CENTER);
    private JLabel Word_Trans= new JLabel("",JLabel.CENTER);
    private JLabel welcome_name = new JLabel();
    private JLabel welcome_time = new JLabel();
    private JLabel Tips = new JLabel("",JLabel.CENTER);
    private JLabel Point_current = new JLabel();
    private JLabel Prepare_tip1 = new JLabel("",JLabel.CENTER);
    private JLabel Prepare_tip2 = new JLabel("",JLabel.CENTER);
    private JTextField field = new JTextField(10);
    private JButton send = new JButton("提交");
    private String NickName = null;
    private int Word_X,Word_Y;
    private boolean Moni_flag = true;     //保证Monitor线程一个客户端只启动一个
    private static boolean RUN = true;
    private String ReadNew = null;
    private String[] str_sp = null;
    private PrintStream ps = null;
    private BufferedReader br = null;
    private TimeThread time = null;
    public Game(String path,PrintStream ps, BufferedReader br) throws Exception{
        this.ps = ps;
        this.br = br;
        Font font = new Font("微软雅黑", Font.PLAIN, 25);
        this.setLayout(null);
        //初始界面
        welcome.setFont(font);
        welcome.setSize(400,60);
        welcome.setLocation(0,200);
        start.setFont(font);
        start.setSize(200,60);
        start.setLocation(100,260);
        start.addActionListener(this);
        field.setFont(font);
        field.setSize(200,60);
        field.setLocation(0,510);
        field.setVisible(false);
        field.addActionListener(this);
        send.setFont(font);
        send.setSize(200,60);
        send.setLocation(200,510);
        send.addActionListener(this);
        send.setVisible(false);
        Prepare_tip1.setFont(new Font("微软雅黑", Font.BOLD, 25));
        Prepare_tip1.setSize(400,60);
        Prepare_tip1.setLocation(0,60);
        Prepare_tip1.setVisible(false);
        Prepare_tip2.setFont(new Font("楷体", Font.BOLD, 20));
        Prepare_tip2.setSize(400,60);
        Prepare_tip2.setLocation(0,100);
        Prepare_tip2.setVisible(false);
        bottom.setFont(new Font("微软雅黑", Font.BOLD, 14));
        bottom.setSize(400,60);
        bottom.setLocation(0,500);
        bottom.setVisible(true);
        welcome_name.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        welcome_name.setSize(400,20);
        welcome_name.setLocation(20,10);
        welcome_name.setVisible(true);
        welcome_time.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        welcome_time.setSize(400,20);
        welcome_time.setLocation(20,30);
        welcome_time.setVisible(true);
        //游戏界面
        Word_X = 0;
        Word_Y = 0;
        Word_Trans.setLocation(Word_X,Word_Y);
        Word_Trans.setSize(400,60);
        Word_Trans.setFont(font);
        Word_Trans.setVisible(false);
        Point_current.setLocation(280,20);
        Point_current.setSize(120,60);
        Point_current.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        Point_current.setVisible(false);
        Tips.setLocation(0,450);
        Tips.setSize(400,60);
        Tips.setFont(new Font("Consolas", Font.PLAIN, 18));
        Tips.setVisible(false);
        this.NickName = path;       //传入构造函数参数
        Calendar c = Calendar.getInstance();
        //左上角欢迎词
        if((c.get(Calendar.HOUR_OF_DAY)>= 0 && c.get(Calendar.HOUR_OF_DAY)<= 5)
                ||(c.get(Calendar.HOUR_OF_DAY)>= 18 && c.get(Calendar.HOUR_OF_DAY)<= 24))
            welcome_name.setText("晚上好,"+NickName+"!");
        else if(c.get(Calendar.HOUR_OF_DAY)>= 6 && c.get(Calendar.HOUR_OF_DAY)<= 10)
            welcome_name.setText("早上好,"+NickName+"!");
        else if(c.get(Calendar.HOUR_OF_DAY)>= 14 && c.get(Calendar.HOUR_OF_DAY)<= 17)
            welcome_name.setText("下午好,"+NickName+"!");
        else
            welcome_name.setText("中午好,"+NickName+"!");
        welcome_time.setText("现在是"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        time = new TimeThread();     //时间线程，使得时间不断变化
        time.start();
        //窗体基本设置
        this.setTitle(NickName+"的游戏界面");
        this.add(welcome);
        this.add(start);
        this.add(welcome_name);
        this.add(welcome_time);
        this.add(field);
        this.add(send);
        this.add(Word_Trans);
        this.add(Tips);
        this.add(Point_current);
        this.add(bottom);
        this.add(Prepare_tip1);
        this.add(Prepare_tip2);
        this.setLocation(200,100);
        this.setSize(400,600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    public void run(){
        while(true)     //不断接收服务器的信息
        {
            try {
                ReadNew = br.readLine();            //接收单词信息，第一次可能收到WAIT
            } catch (Exception em) {}
            str_sp = ReadNew.split(":");
            if (str_sp[0].equals("WAIT"))              //收到WAIT
            {
                Word_Trans.setText("等待对方开始,请稍后!");
                Prepare_tip1.setText("不如先看个单词练练手?");
                Prepare_tip2.setText(str_sp[1]+"   "+str_sp[2]);    //设置一随机单词
                Prepare_tip1.setVisible(true);
                Prepare_tip2.setVisible(true);
                try {
                    ReadNew = br.readLine();        //等待收到信息
                } catch (Exception em) {}
                Prepare_tip1.setVisible(false);
                Prepare_tip2.setVisible(false);
            }
            str_sp = ReadNew.split(":");   //第一位中文，第二位是tips，第三位是当前分数
            Word_Trans.setText(str_sp[0]);
            Tips.setText("Tips:" + str_sp[1]);
            Point_current.setText("当前得分:" + str_sp[2] + "分");
            if(Moni_flag){
                MonitorThread mnt = new MonitorThread(this.getX(),this.getY());
                mnt.start();        //启动监控
                Moni_flag = false;
            }
            MonitorThread.Monitor_start = true;  //接收服务器信息完则开始监视
            Word_Y = 0;         //Word_Y初始赋值为0
            while (RUN) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {}
                Word_Y += 3;
                Word_Trans.setLocation(Word_X, Word_Y);
                if (Word_Y > 540) {
                    RUN = false;        //双方都没有回答
                    field.setText("");  //没回答的时候也可能有人填单词了，只不过没提交
                    ps.println("NO");
                }
            }
            while(!RUN){    //接收服务器的单词正误信息时保持等待
                try{
                    Thread.sleep(50);
                }catch(Exception ex){};
            }
        }
    }
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == start)
        {
            welcome.setVisible(false);
            start.setVisible(false);
            bottom.setVisible(false);
            welcome_name.setVisible(false);
            welcome_time.setVisible(false);
            Word_Trans.setVisible(true);
            Word_Trans.setLocation(Word_X,Word_Y);
            Tips.setVisible(true);
            field.setVisible(true);
            send.setVisible(true);
            Point_current.setVisible(true);
            time.Time_RUN = false;
            try{
                ps.println("NEW");           //一用户点击开始，向服务器发出提示信息
            }catch(Exception ex){}
            new Thread(this).start();
        }
        else if(e.getSource() == send || e.getSource() == field){       //提交按钮
            Game.RUN = false;               //单词下落过程停止
            ps.println(field.getText());      //把提交结果给服务器
            field.setText("");
        }
    }
    class TimeThread extends Thread{        //变化时间
        public boolean Time_RUN = true;
        public void run(){
            while(Time_RUN){
                try{
                    Thread.sleep(1000);
                    welcome_time.setText("现在是"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                }catch(Exception exc){}
            }
        }
    }
    class MonitorThread extends Thread{       //监视线程类，当服务器发来"STOP"时停止下落过程
        private static boolean Monitor_start = true;
        private int X;
        private int Y;
        public MonitorThread(int X,int Y){
            this.X = X;
            this.Y = Y;
        }
        public void run(){
            while(true){
                try{
                    ReadNew = br.readLine();
                }catch(Exception exc){}
                if(ReadNew.equals("STOP")){
                    Game.RUN = false;               //将RUN改为false，使单词停止下落
                    try{
                        ReadNew = br.readLine();    //读到的结果
                    }catch(Exception exc){}
                }
                String[] Read_str = ReadNew.split(":");
                //第一位0代表回答错误，1代表回答正确，2代表未回答
                new Select(Integer.parseInt(Read_str[0]),Read_str[1],NickName,this.X,this.Y);
                Point_current.setText("当前得分:"+Read_str[2]+"分");
                if(Integer.parseInt(Read_str[2]) <= 0){
                    new Select(3,null,NickName,this.X,this.Y);    //游戏输
                    Monitor_start = false;
                    break;
                }else{
                    try{
                        ReadNew = br.readLine();      //接收可能的赢输信息，若收到nop就没有操作
                    }catch(Exception e){}
                    if(ReadNew.equals("WIN")){
                        new Select(4,null,NickName,this.X,this.Y);    //游戏赢
                        break;
                    }
                    //等9.9秒钟（前0.1秒在等上一次提示信息）
                    try{
                        Thread.sleep(9900);
                    }catch (Exception e){};
                    Game.RUN = true;      //run函数可以正常下落
                    Monitor_start = false;  //先读入单词信息再监视
                    while(!Monitor_start){
                        try{
                            Thread.sleep(50);   //读入单词的时候线程保持睡眠状态
                        }catch(Exception ex){};
                    }
                }
            }
        }
    }
}