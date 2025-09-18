package Function.SwingCompFunc;

import ChatApp.Client.ClientApplication;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BubblePanel extends JPanel {
    private final String message;
    private final String time;
    private final boolean isMine;
    private final boolean isFile;
    private ImageIcon emoji = null; // 이모티콘 이미지 추가
    private final int maxBubbleWidth = 220; // 말풍선 최대 너비
    private static Color myBubbleColor = new Color(76, 175, 80); // 기본 내 말풍선 색
    private static Color otherBubbleColor = new Color(33, 150, 243); // 기본 상대방 말풍선 색
    private ClientApplication client = null;

    public static Color getMyBubbleColor() {
        return myBubbleColor;
    }

    public static void setMyBubbleColor(Color color) {
        myBubbleColor = color;
    }

    public static Color getOtherBubbleColor() {
        return otherBubbleColor;
    }

    public static void setOtherBubbleColor(Color color) {
        otherBubbleColor = color;
    }


    public BubblePanel(String message, String time, boolean isMine, boolean isFile, ClientApplication client) {
        this.message = message;
        this.time = time;
        this.isMine = isMine;
        this.isFile = isFile;
        this.client = client;

        setOpaque(false);
        setPreferredSize(calculateSize());
    }

    public BubblePanel(String message, String time, boolean isMine) {
        this.message = message;
        this.time = time;
        this.isMine = isMine;
        this.isFile = false;

        setOpaque(false);
        setPreferredSize(calculateSize());
    }

    public BubblePanel(ImageIcon emoji, String time, boolean isMine) {
        this.message = null; // 텍스트는 사용하지 않음
        this.time = time;
        this.isMine = isMine;
        this.emoji = emoji; // 이모티콘 설정
        this.isFile = false;

        setOpaque(false);
        setPreferredSize(calculateSize());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 말풍선 색상
        Color bubbleColor;

        if (message != null) {
            if (message.startsWith("[서버]")) {
                bubbleColor = new Color(19, 62, 135);
            } else if (isMine) {
                bubbleColor = myBubbleColor;
            } else {
                bubbleColor = otherBubbleColor;
            }
        } else {
            if (isMine) {
                bubbleColor = myBubbleColor;
            } else {
                bubbleColor = otherBubbleColor;

            }
        }
        if (isFile) {
            bubbleColor = SwingCompFunc.LoginButtonColor;
        }

        g2.setColor(bubbleColor);

        int bubbleWidth, bubbleHeight;

        if (emoji == null) { // 텍스트 말풍선 처리
            FontMetrics fm = g.getFontMetrics(new Font("나눔고딕", Font.PLAIN, 14));
            java.util.List<String> wrappedText = wrapText(fm, message, maxBubbleWidth);

            bubbleWidth = maxBubbleWidth + 20;
            bubbleHeight = fm.getHeight() * wrappedText.size() + 30;

            // 말풍선 본체
            g2.fillRoundRect(0, 0, bubbleWidth, bubbleHeight, 15, 15);

            // 텍스트 출력
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("나눔고딕", Font.PLAIN, 14));
            int textY = 20;
            for (String line : wrappedText) {
                g2.drawString(line, 10, textY);
                textY += fm.getHeight();
            }
        } else { // 이모티콘 말풍선 처리
            bubbleWidth = emoji.getIconWidth() + 20;
            bubbleHeight = emoji.getIconHeight() + 30;

            // 말풍선 본체
            g2.fillRoundRect(0, 0, bubbleWidth, bubbleHeight, 15, 15);

            // 이모티콘 출력
            g2.drawImage(emoji.getImage(), 10, 10, this);
        }

        // 시간 출력
        g2.setFont(new Font("나눔고딕", Font.ITALIC, 10));
        g2.setColor(new Color(232, 236, 215));
        g2.drawString(time, 10, bubbleHeight - 5);
    }

    private java.util.List<String> wrapText(FontMetrics fm, String text, int maxWidth) {
        java.util.List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines; // 빈 문자열 처리
        }

        StringBuilder line = new StringBuilder();
        for (char c : text.toCharArray()) {
            // 문자 추가 후 폭 검사
            if (fm.stringWidth(line.toString() + c) > maxWidth) {
                lines.add(line.toString().trim());
                line = new StringBuilder(); // 새로운 줄로 전환
            }
            line.append(c);
        }
        if (!line.isEmpty()) {
            lines.add(line.toString().trim()); // 마지막 줄 추가
        }
        return lines;
    }

    private Dimension calculateSize() {
        if (emoji != null) { // 이모티콘 크기 계산
            int bubbleWidth = emoji.getIconWidth() + 40; // 패딩 포함
            int bubbleHeight = emoji.getIconHeight() + 40; // 패딩 포함
            return new Dimension(bubbleWidth, bubbleHeight);
        }

        FontMetrics fm = getFontMetrics(new Font("나눔고딕", Font.PLAIN, 14));
        java.util.List<String> wrappedText = wrapText(fm, message, maxBubbleWidth);
        int lineHeight = fm.getHeight();
        int bubbleWidth = maxBubbleWidth + 20;
        int bubbleHeight = lineHeight * wrappedText.size() + 40; // 패딩 포함
        return new Dimension(bubbleWidth, bubbleHeight);
    }
}