package Function.ImageResize;

import Database.JoinFrame;
import Function.SwingCompFunc.SwingCompFunc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;

public class ImageResize extends JFrame {
    private JLabel imageLabel;
    private JButton uploadButton;
    private BufferedImage originalImage;
    private Rectangle selectionRect;
    private Point startPoint, endPoint;
    private double scaleX, scaleY;

    public ImageResize(JoinFrame jf, File _selectedImage) throws IOException {
        setTitle("사진 첨부 및 크롭");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 창을 닫을 때 리소스 정리
        setLocationRelativeTo(null);  // 창을 화면 중앙에 표시
        SwingCompFunc.setFrameStyle(this);

        // 커스텀 JLabel: 선택한 영역이 보이도록 하기 위해 paintComponent를 오버라이드
        imageLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (originalImage != null) {
                    g.drawImage(originalImage, 0, 0, getWidth(), getHeight(), this);
                }

                // 드래그로 선택한 영역을 그려줍니다.
                if (selectionRect != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(Color.GRAY);  // 선택 영역 색상
                    g2d.setStroke(new BasicStroke(2));  // 선택 영역 두께
                    g2d.draw(selectionRect);  // 선택한 영역 그리기
                }
            }
        };
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (originalImage != null) {
                    startPoint = e.getPoint();
                    selectionRect = new Rectangle();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (originalImage != null && selectionRect != null) {
                    try {
                        // 선택한 영역의 실제 이미지 좌표를 계산
                        int x = (int) (selectionRect.x * scaleX);
                        int y = (int) (selectionRect.y * scaleY);
                        int width = (int) (selectionRect.width * scaleX);
                        int height = (int) (selectionRect.height * scaleY);

                        // 선택한 영역을 잘라냅니다.
                        BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

                        // 메인 앱으로 잘라낸 이미지 전달
                        jf.setCroppedImage(croppedImage);

                        // 창 닫기
                        dispose();  // 창을 닫습니다.
                    } catch (RasterFormatException ex) {
                        System.out.println("선택 영역이 잘못되었습니다.");
                    }
                }
            }
        });

        imageLabel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (originalImage != null) {
                    endPoint = e.getPoint();
                    selectionRect = createSelectionRectangle(startPoint, endPoint);
                    imageLabel.repaint(); // 드래그할 때마다 영역을 다시 그립니다.
                }
            }
        });

        imageLabel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (_selectedImage != null) {
                    setImage(_selectedImage);
                }
            }
        });

        originalImage = ImageIO.read(_selectedImage);

        uploadButton = new JButton("사진 첨부");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "jpeg", "gif"));

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        originalImage = ImageIO.read(selectedFile);
                        scaleX = (double) originalImage.getWidth() / imageLabel.getWidth();
                        scaleY = (double) originalImage.getHeight() / imageLabel.getHeight();
                        imageLabel.repaint();  // 이미지를 라벨에 그립니다.
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setLayout(new BorderLayout());
        add(imageLabel, BorderLayout.CENTER);
        add(uploadButton, BorderLayout.SOUTH);
    }

    // 드래그한 영역을 생성하는 메서드
    private Rectangle createSelectionRectangle(Point start, Point end) {
        int x = Math.min(start.x, end.x);
        int y = Math.min(start.y, end.y);
        int width = Math.abs(end.x - start.x);
        int height = Math.abs(end.y - start.y);
        return new Rectangle(x, y, width, height);
    }

    public void setImage(File file) {
        try {
            originalImage = ImageIO.read(file);
            scaleX = (double) originalImage.getWidth() / imageLabel.getWidth();
            scaleY = (double) originalImage.getHeight() / imageLabel.getHeight();
            imageLabel.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
