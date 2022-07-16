package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.PrintStream;
import java.sql.Connection;

class Register extends JFrame implements ActionListener {
    private JTextField Id = new JTextField(20);
    private JPasswordField Passwd =new JPasswordField(20);
    private JPasswordField querenPasswd =new JPasswordField(20);
    private static Connection con;              //建立连接
    private JLabel welcome = new JLabel("极致游戏体验，就差一步。",JLabel.CENTER);
    private JLabel zhanghao = new JLabel("账号:");
    private JLabel mima = new JLabel("密码:");
    private JLabel querenmima = new JLabel("确认密码:");
    private JButton Zhuce_Button = new JButton("注册");
    private JLabel Picture = new JLabel();
    private FileDialog dialog = null;
    private PrintStream ps = null;
    private BufferedReader br = null;
    public Register(PrintStream ps, BufferedReader br) throws Exception{
        this.ps = ps;
        this.br = br;
        this.setLayout(null);
        Font font = new Font("微软雅黑", Font.PLAIN, 25);
        welcome.setLocation(20,50);
        welcome.setSize(600,80);
        welcome.setFont(new Font("微软雅黑", Font.BOLD, 45));
        Id.setLocation(290,200);
        Id.setSize(300,50);
        Id.setFont(font);
        Passwd.setLocation(290,260);
        Passwd.setSize(300,50);
        Passwd.setFont(font);
        querenPasswd.setLocation(290,320);
        querenPasswd.setSize(300,50);
        querenPasswd.setFont(font);
        zhanghao.setLocation(180,190);
        zhanghao.setSize(200,60);
        zhanghao.setFont(new Font("微软雅黑", Font.BOLD, 25));
        mima.setLocation(180,250);
        mima.setSize(200,60);
        mima.setFont(new Font("微软雅黑", Font.BOLD, 25));
        querenmima.setLocation(180,310);
        querenmima.setSize(200,60);
        querenmima.setFont(new Font("微软雅黑", Font.BOLD, 25));
        ImageIcon image = new ImageIcon("src\\people.png");  //将图片路径作为参数传入
        image.setImage(image.getImage().getScaledInstance(100,100,Image.SCALE_DEFAULT));  //创建缩放版本图像
        Picture.setIcon(image);
        Picture.setLocation(60, 230);
        Picture.setSize(100,100);
        Picture.setFont(font);
        Zhuce_Button.setLocation(200, 400);
        Zhuce_Button.setSize(200,60);
        Zhuce_Button.setFont(font);
        Zhuce_Button.addActionListener(this);
        //窗体基本设置
        this.add(welcome);
        this.add(Id);
        this.add(Passwd);
        this.add(querenPasswd);
        this.add(zhanghao);
        this.add(mima);
        this.add(querenmima);
        this.add(Zhuce_Button);
        this.add(Picture);
        this.setTitle("欢迎注册");
        this.setLocation(200,100);
        this.setSize(620,550);
        this.setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        try{
            String passwd1 = String.valueOf(Passwd.getPassword());  //getPassword得到的是字符数组，要转为字符串类型
            String passwd2 = String.valueOf(querenPasswd.getPassword());
            if(Id.getText().isEmpty()||passwd1.isEmpty()||passwd2.isEmpty()){
                JOptionPane.showMessageDialog(null,"请检查您的输入后重新输入!");
            }else {
                ps.println("REG1:" + passwd1 + ":" + passwd2);
                String message = br.readLine();
                if (message.equals("YES")) {     //两次输入的密码一致
                    ps.println("REG2:" + Id.getText() + ":" + passwd1);
                    try {
                        message = br.readLine();
                        if (message.equals("EXISTS")) {       //该账户已被注册
                            JOptionPane.showMessageDialog(null, "该账号已被注册，请更改后重试!");
                            Id.setText("");
                            Passwd.setText("");
                            querenPasswd.setText("");
                        } else {                              //未被注册，注册完成
                            JOptionPane.showMessageDialog(null, "注册成功!");
                            this.setVisible(false);     //关闭窗口
                        }
                    } catch (Exception exce) {
                    }
                } else {      //两次输入的密码不一致
                    JOptionPane.showMessageDialog(null, "两次输入的密码不相符，请更改后重试!");
                    Id.setText("");
                    Passwd.setText("");
                    querenPasswd.setText("");
                }
            }
        }catch(Exception exce){}
    }
}