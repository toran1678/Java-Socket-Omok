package Function;

import Database.UserInfo.UserInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class ImageLoad {
    /* 라벨의 크기에 맞추기 위해 라벨과, 이미지 데이터를 가져옴 */
    public ImageIcon getImageIcon(JLabel imageLabel, byte[] imageData) {
        try {
            /* byte 배열을 "InputStream"으로 변환 */
            InputStream in = new ByteArrayInputStream(imageData);

            /* "InputStream"을 "BufferedImage"로 변환 */
            BufferedImage originalImage = ImageIO.read(in);

            /* 이미지 크기 조정 (JLabel 크기에 맞게) */
            Image scaledImage = originalImage.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* 라벨의 크기에 맞추기 위해 라벨과, 이미지 경로를 가져옴 */
    public static ImageIcon getImageIcon(JLabel imageLabel, String imagePath) {
        try {
            ImageIcon icon = new ImageIcon(imagePath);

            /* 이미지 크기 조정 (JLabel 크기에 맞게) */
            Image scaledImage = icon.getImage().getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
            imageLabel.setText("");
            imageLabel.setHorizontalAlignment(JLabel.CENTER);
            imageLabel.setVerticalAlignment(JLabel.CENTER);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public static void setImageIcon(JLabel imageLabel, String imagePath) {
        ImageIcon icon = new ImageIcon(imagePath);

        /* 이미지 크기 조정 (JLabel 크기에 맞게) */
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaledImage));
        // imageLabel.setText("");
        //imageLabel.setHorizontalAlignment(JLabel.CENTER);
        //imageLabel.setVerticalAlignment(JLabel.CENTER);
    }
}