package Database.UserInfo;

import Database.Database;
import Function.ImageLoad;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.Color;
import java.awt.Font;

public class showUserInfo extends JFrame {
    private JFrame frame;
    Database db = new Database();
    UserInfo showUserInfo;

    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    /* Panel */
    private JPanel mainPanel = new JPanel();
    private JPanel namePanel = new JPanel();
    private JPanel winPanel = new JPanel();
    private JPanel losePanel = new JPanel();
    private JPanel winRatePanel = new JPanel();

    /* Label */
    private JLabel imageLabel = new JLabel();
    private JLabel nameLabel = new JLabel("이름 :");
    private JLabel winLabel = new JLabel("승리 :");
    private JLabel loseLabel = new JLabel("패배 :");
    JLabel winRateLabel = new JLabel("승률 :");

    public showUserInfo(UserInfo _userInfo) {
        this.showUserInfo = _userInfo;
        ImageLoad img = new ImageLoad();

        setTitle("유저: " + _userInfo.getNickname());
        setSize(315, 433);
        getContentPane().setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setIconImage(gameIcon.getImage());

        /* Set Background */
        mainPanel.setBackground(new Color(153, 204, 255));

        namePanel.setBackground(new Color(251, 251, 251));
        winPanel.setBackground(new Color(251, 251, 251));
        losePanel.setBackground(new Color(251, 251, 251));
        winRatePanel.setBackground(new Color(251, 251, 251));

        /* Set Border */
        imageLabel.setBorder(new LineBorder(new Color(0, 0, 0)));
        namePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        winPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        losePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
        winRatePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));

        /* Set Bounds */
        mainPanel.setBounds(0, 0, 300, 400);
        imageLabel.setBounds(100, 35, 100, 120);
        namePanel.setBounds(35, 200, 230, 30);
        winPanel.setBounds(35, 240, 230, 30);
        losePanel.setBounds(35, 280, 230, 30);
        winRatePanel.setBounds(35, 320, 230, 30);

        nameLabel.setBounds(12, 8, 200, 15);
        winLabel.setBounds(12, 8, 200, 15);
        loseLabel.setBounds(12, 8, 200, 15);
        winRateLabel.setBounds(12, 8, 200, 15);

        /* Set Font */
        nameLabel.setFont(new Font("빙그레체Ⅱ", Font.BOLD, 12));
        winLabel.setFont(new Font("빙그레체Ⅱ", Font.BOLD, 12));
        loseLabel.setFont(new Font("빙그레체Ⅱ", Font.BOLD, 12));
        winRateLabel.setFont(new Font("빙그레체Ⅱ", Font.BOLD, 12));

        /* Set Layout */
        mainPanel.setLayout(null);
        namePanel.setLayout(null);
        winPanel.setLayout(null);
        losePanel.setLayout(null);
        winRatePanel.setLayout(null);

        /* add */
        getContentPane().add(mainPanel);
        mainPanel.add(imageLabel);
        mainPanel.add(namePanel);
        namePanel.add(nameLabel);
        mainPanel.add(winPanel);
        winPanel.add(winLabel);
        mainPanel.add(losePanel);
        losePanel.add(loseLabel);
        mainPanel.add(winRatePanel);
        winRatePanel.add(winRateLabel);

        nameLabel.setText("이름 : " + showUserInfo.getName());
        winLabel.setText("승리 : " + showUserInfo.getWin());
        loseLabel.setText("패배 : " + showUserInfo.getLose());

        int wins = showUserInfo.getWin();
        int losses = showUserInfo.getLose();
        if (wins != 0) {
            winRateLabel.setText("승률 : " + (wins * 100 / (wins + losses)) + "%");
        } else {
            winRateLabel.setText("승률 : 승리 횟수가 없습니다."); // 또는 적절한 메시지 표시
        }

        String profileCharacterPath = "src/Function/ProfileCharacterSelector/Img/" + showUserInfo.getProfileCharacter() + ".jpg";
        imageLabel.setIcon(ImageLoad.getImageIcon(imageLabel, profileCharacterPath));

        // imageLabel.setIcon(img.getImageIcon(imageLabel, showUserInfo.getImage()));

        setVisible(true);
    }
}
