package Database.Design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButtonExample extends JFrame {
    public CustomButtonExample() {
        setTitle("Hover Effect Button Example");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1, 10, 10));  // 레이아웃 설정

        // 기본 버튼 크기를 유지하면서 외곽선과 배경색을 설정
        JButton customButton = new JButton("Custom Button");
        customButton.setPreferredSize(new Dimension(150, 50)); // 버튼 크기 설정

        // 버튼 기본 상태 설정 (배경색, 글자색, 외곽선)
        customButton.setBackground(new Color(255, 161, 83));
        customButton.setForeground(new Color(113, 95, 68));
        customButton.setBorder(BorderFactory.createLineBorder(new Color(151, 89, 50), 3)); // 외곽선 설정

        // 버튼에 호버 효과 추가
        customButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // 마우스가 버튼 위로 올라왔을 때 배경과 외곽선 색 변경
                customButton.setBackground(new Color(255, 187, 131));
                customButton.setBorder(BorderFactory.createLineBorder(new Color(170, 123, 93), 3)); // 외곽선 변경
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 마우스가 버튼에서 나갔을 때 원래 상태로 돌아감
                customButton.setBackground(new Color(255, 177, 113));
                customButton.setBorder(BorderFactory.createLineBorder(new Color(151, 89, 50), 3)); // 외곽선 원상태로
            }
        });

        customButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customButton.setBackground(new Color(255, 215, 182));
            }
        });

        add(customButton);

        // 다른 기본 버튼 (크기 비교를 위해 추가)
        JButton defaultButton = new JButton("Default Button");
        add(defaultButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomButtonExample frame = new CustomButtonExample();
            frame.setVisible(true);
        });
    }
}
