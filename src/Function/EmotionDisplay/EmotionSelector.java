package Function.EmotionDisplay;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmotionSelector extends JFrame {
    private JComboBox<String> emotionComboBox;
    private JButton showEmotionButton;

    public EmotionSelector() {
        setTitle("감정 선택");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 감정 선택 ComboBox
        String[] emotions = {"넘신나", "삐짐", "화남", "빙글"};
        emotionComboBox = new JComboBox<>(emotions);

        // 감정 보여주기 버튼
        showEmotionButton = new JButton("감정 표시");
        showEmotionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedEmotion = (String) emotionComboBox.getSelectedItem();
                new EmotionDisplay(selectedEmotion);  // 감정 표시
            }
        });

        // Layout 설정
        JPanel panel = new JPanel();
        panel.add(emotionComboBox);
        panel.add(showEmotionButton);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EmotionSelector frame = new EmotionSelector();
            frame.setVisible(true);
        });
    }
}

