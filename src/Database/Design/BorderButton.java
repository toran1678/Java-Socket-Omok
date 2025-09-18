package Database.Design;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BorderButton extends JButton {
    private final Color unClickBackground;
    private final Color clickBackground;
    private final Color foreground;

    int paddingWidth = 15, paddingHeight = 5;
    int strokeWidth = 5;

    boolean isMouseEnter = false;

    public BorderButton(Color unClickBackground, Color clickBackground, Color foreground) {
        this.unClickBackground = unClickBackground;
        this.clickBackground = clickBackground;
        this.foreground = foreground;
        setText("Border");

        Dimension dimension = getPreferredSize();
        int w = (int) dimension.getWidth() + paddingWidth * 2;
        int h = (int) dimension.getHeight() + paddingHeight * 2;

        setPreferredSize(new Dimension(w + strokeWidth * 2, h + strokeWidth * 2));
        setOpaque(false);
        setBorder(null);
        setBackground(unClickBackground);
        setForeground(foreground);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(clickBackground);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(unClickBackground);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isMouseEnter = true;
                revalidate();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isMouseEnter = false;
                revalidate();
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Dimension dimension = getPreferredSize();
        int w = (int) dimension.getWidth() - strokeWidth;
        int h = (int) dimension.getHeight() - strokeWidth;

        g2.setColor(getBackground());
        g2.fillRoundRect(3, 4, (int) (w - strokeWidth / 3.5), (int) (h - strokeWidth / 2.8), 35, 35);

        if(isMouseEnter) {
            g2.setColor(clickBackground);
            g2.setStroke(new BasicStroke(strokeWidth));
            g2.drawRoundRect(2, 2, w, h, 35, 35);
        }

        g2.setColor(getForeground());
        g2.setFont(new Font("맑은 고딕", 1, 18));

        FontMetrics fontMetrics = g2.getFontMetrics();
        Rectangle rectangle = fontMetrics.getStringBounds(getText(), g2).getBounds();

        g2.drawString(getText(), (w - rectangle.width + strokeWidth) / 2, (h - rectangle.height) / 2 + fontMetrics.getAscent());
    }
}