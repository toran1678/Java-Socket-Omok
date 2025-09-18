package Function.EmotionDisplay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class EmotionDisplay extends JFrame {
    private JLabel emotionLabel;

    public EmotionDisplay(String emotion) {
        setTitle("감정 표시");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setUndecorated(true);  // 창 테두리 없애기
        getContentPane().setBackground(Color.WHITE);  // 배경을 하얗게 설정\

        System.out.println(emotion);

        /* 감정을 표시할 Label */
        emotionLabel = new JLabel("", SwingConstants.CENTER);
        emotionLabel.setHorizontalAlignment(JLabel.CENTER);
        // emotionLabel.setPreferredSize(new Dimension(300, 300));  // 원하는 크기로 설정

        add(emotionLabel, BorderLayout.CENTER);

        /* 이미지 라벨의 크기가 정해진 후 메소드 실행 */
        emotionLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                displayEmotion(emotion);
            }
        });

        /* 창을 표시하고 일정 시간이 지나면 자동으로 창을 닫습니다 */
        setVisible(true);
        autoCloseAfterDelay(3000);  // 3초 후에 창 닫기
    }

    /* 감정에 맞는 이미지를 표시하는 메서드 */
    private void displayEmotion(String emotion) {
        String imagePath = "src/Function/EmotionDisplay/img/";

        switch (emotion) {
            case "넘신나":
                imagePath += "넘신나.gif";  // 행복한 감정을 나타내는 이미지 경로
                System.out.println(imagePath);
                break;
            case "삐짐":
                imagePath += "삐짐.jpg";  // 슬픈 감정을 나타내는 이미지 경로
                break;
            case "화남":
                imagePath += "화남.jpg";  // 화난 감정을 나타내는 애니메이션 GIF 경로
                break;
            case "빙글":
                imagePath += "빙글.gif";  // 놀란 감정을 나타내는 이미지 경로
                break;
        }
        showImage(imagePath);
    }

    /* ImageLabel 설정 */
    private void showImage(String imagePath) {
        /* 이미지 주소가 ".jpg"일 경우 */
        if (!imagePath.isEmpty() && imagePath.toLowerCase().endsWith(".jpg")) {
            /* GIF 이미지를 "BufferedImage"로 읽기 */
            try {
                BufferedImage originalImage = ImageIO.read(new File(imagePath));
                // 이미지 크기 조정
                Image scaledImage = originalImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH); // 원하는 크기로 조정
                /* 크기 조정된 이미지를 "ImageIcon"으로 변환 */
                ImageIcon emotionImage = new ImageIcon(scaledImage);
                /* 크기 조정된 이미지를 "JLabel"에 설정 */
                emotionLabel.setIcon(emotionImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (imagePath.toLowerCase().endsWith(".gif")) {
            ImageIcon emotionImage = new ImageIcon(imagePath);
            emotionLabel.setIcon(emotionImage);
        }
    }

    /* 일정 시간이 지나면 창을 자동으로 닫는 메서드 */
    private void autoCloseAfterDelay(int delayMillis) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dispose();  // 창 닫기
            }
        }, delayMillis);
    }
}
