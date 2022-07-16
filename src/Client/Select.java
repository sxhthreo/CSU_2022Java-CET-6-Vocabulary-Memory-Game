package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Select extends JFrame implements Runnable,ActionListener {
    private JLabel Info= new JLabel("",JLabel.CENTER);
    private JButton Cuoti = new JButton("打开错题集");
    private String NickName = null;
    public Select(int sel,String word,String Nick,int X,int Y){
        Font font = new Font("微软雅黑", Font.PLAIN, 20);
        Info.setFont(font);
        Info.setSize(400,150);
        Cuoti.setFont(font);
        Cuoti.setSize(200,60);
        Cuoti.addActionListener(this);
        Cuoti.setVisible(false);
        this.NickName = Nick;
        this.add(Info,BorderLayout.CENTER);
        this.add(Cuoti,BorderLayout.SOUTH);
        this.setTitle(NickName+"的答题结果");
        switch(sel){
            case 0:     //回答错误
                Info.setText("回答错误，答案是"+word);
                break;
            case 1:     //回答正确
                Info.setText("恭喜回答正确!");
                break;
            case 2:     //未回答
                Info.setText("您没有回答，正确答案是"+word);
                break;
            case 3:     //游戏输
                Info.setText("您输掉了本次比赛!打开您的错题看看吧~");
                Cuoti.setVisible(true);
                break;
            case 4:     //游戏赢
                Info.setText("恭喜您获胜!骄傲之余看看错题?");
                Cuoti.setVisible(true);
                break;
        }
        //窗体基本设置
        this.setLocation(X,Y+180);             //设置答题结果位置
        this.setSize(400,200);
        this.setVisible(true);
        if(sel == 0 || sel ==1 || sel ==2)      //输赢界面无需持续10秒，其他界面持续10秒
            new Thread(this).start();
    }
    public void actionPerformed(ActionEvent e)
    {
        File file = new File(NickName+"\\未掌握单词.txt");
        Desktop desktop = Desktop.getDesktop();
        try{
            if(file.exists())
                desktop.open(file);
        }catch(Exception exc){}
    }
    public void run(){
        try{
            Thread.sleep(10000);
        }catch (Exception e){};
        this.setVisible(false);
    }
}
