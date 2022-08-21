package cn.margele.hmclInserter;

import javax.swing.*;
import java.awt.*;

public class LoggingPanel extends JFrame {
    public JPanel panel = new JPanel();
    public JLabel label = new JLabel("登录中...");

    public LoggingPanel() {
        this.setUndecorated(true);
        this.setSize(400, 60);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 30));
        panel.add(label);

        this.setContentPane(panel);
        this.setVisible(true);
    }

    public void log(String msg) {
        label.setText(msg);
    }
}
