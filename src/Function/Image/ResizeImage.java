package Function.Image;

import javax.swing.*;
import java.awt.*;

public class ResizeImage {
    public static ImageIcon resizeImage(String imagePath, int width, int height) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage();
        Image resizeImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizeImage);
    }
}
