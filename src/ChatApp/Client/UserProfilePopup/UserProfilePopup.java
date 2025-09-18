package ChatApp.Client.UserProfilePopup;

import ChatApp.Client.ClientApplication;
import Database.UserInfo.UserInfo;
import Function.ImageLoad;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class UserProfilePopup extends JFrame {
    JLabel characterImageLabel = new JLabel();
    ClientApplication client;

    public UserProfilePopup(ClientApplication client, String username, byte[] profileImageBytes, int characterNumber, UserInfo userInfo) {
        super("유저 프로필");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.client = client;

        SwingCompFunc.setFrameStyle(this);
        this.setBackground(SwingCompFunc.LobbyPanelColor);

        // 메인 패널
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(SwingCompFunc.LobbyPanelColor);

        // 상단 타이틀 패널
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(username + "님의 프로필", SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        SwingCompFunc.setTopPanelStyle(titlePanel);
        SwingCompFunc.setTopLabelStyle(titleLabel);

        // 중앙 프로필 이미지 영역
        JPanel profilePanel = new JPanel(null); // 절대 레이아웃 사용
        profilePanel.setBorder(BorderFactory.createTitledBorder("프로필 정보"));
        profilePanel.setPreferredSize(new Dimension(400, 400));
        profilePanel.setBackground(SwingCompFunc.LobbyPanelColor);

        // 프로필 이미지
        JLabel profileImageLabel = new JLabel();
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        SwingCompFunc.setLabelBorder(profileImageLabel);
        if (profileImageBytes != null) {
            ImageIcon profileImage = byteArrayToImageIcon(profileImageBytes, 400, 400);
            profileImageLabel.setIcon(profileImage);
        } else {
            profileImageLabel.setText("프로필 이미지 없음");
            profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        profileImageLabel.setBounds(40, 30, 380, 370); // 프로필 이미지 위치
        profilePanel.add(profileImageLabel);

        // 프로필 캐릭터 이미지
        characterImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        characterImageLabel.setOpaque(true);
        SwingCompFunc.setLabelBorder(characterImageLabel);

        // 캐릭터 이미지를 프로필 이미지의 중앙 아래쪽에 위치
        int profileImageCenterX = profileImageLabel.getBounds().x + (profileImageLabel.getWidth() / 2);
        int characterImageX = profileImageCenterX - 50; // 캐릭터 이미지 절반 크기
        characterImageLabel.setBounds(characterImageX, 300, 100, 100); // 위치 조정

        if (characterNumber != -1) {
            setCharacterLabel(characterNumber);
        } else {
            characterImageLabel.setText("캐릭터 없음");
        }

        // 캐릭터 이미지를 프로필 이미지 위로 설정
        profilePanel.add(characterImageLabel);
        profilePanel.setComponentZOrder(characterImageLabel, 0); // 캐릭터를 맨 앞으로 설정
        profilePanel.setComponentZOrder(profileImageLabel, 1);  // 프로필 이미지를 뒤로 설정

        mainPanel.add(profilePanel, BorderLayout.CENTER);

        // 하단 영역: 버튼 패널 + 정보 패널
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // 정보 패널
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 0, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("추가 정보"));
        infoPanel.setBackground(SwingCompFunc.LobbyPanelColor);

        infoPanel.setBackground(SwingCompFunc.ButtonColor);
        infoPanel.setForeground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JLabel genderTextLabel = new JLabel("성별: ", SwingConstants.RIGHT);
        JLabel genderLabel = new JLabel(userInfo.getGender(), SwingConstants.LEFT);

        JLabel winTextLabel = new JLabel("승리: ", SwingConstants.RIGHT);
        JLabel winLabel = new JLabel(String.valueOf(userInfo.getWin()), SwingConstants.LEFT);

        JLabel lossTextLabel = new JLabel("패배: ", SwingConstants.RIGHT);
        JLabel lossLabel = new JLabel(String.valueOf(userInfo.getLose()), SwingConstants.LEFT);

        JLabel winRateTextLabel = new JLabel("승률: ", SwingConstants.RIGHT);
        JLabel winRateLabel;

        if (userInfo.getWin() == 0) {
            winRateLabel = new JLabel("승리 기록이 없습니다", SwingConstants.LEFT);
        } else {
            double winRate = (double) userInfo.getWin() / (userInfo.getWin() + userInfo.getLose()) * 100;
            winRateLabel = new JLabel(String.format("%.2f%%", winRate), SwingConstants.LEFT);
        }

        SwingCompFunc.setLabelStyle(genderTextLabel);
        SwingCompFunc.setLabelStyle(winTextLabel);
        SwingCompFunc.setLabelStyle(lossTextLabel);
        SwingCompFunc.setLabelStyle(winRateTextLabel);

        SwingCompFunc.setLabelStyle(genderLabel);
        SwingCompFunc.setLabelStyle(winLabel);
        SwingCompFunc.setLabelStyle(lossLabel);
        SwingCompFunc.setLabelStyle(winRateLabel);

        infoPanel.add(genderTextLabel);
        infoPanel.add(genderLabel);
        infoPanel.add(winTextLabel);
        infoPanel.add(winLabel);
        infoPanel.add(lossTextLabel);
        infoPanel.add(lossLabel);
        infoPanel.add(winRateTextLabel);
        infoPanel.add(winRateLabel);
        bottomPanel.add(infoPanel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton chatButton = new JButton("채팅 시작");
        JButton closeButton = new JButton("닫기");

        SwingCompFunc.setTopPanelStyle(buttonPanel);

        chatButton.setPreferredSize(new Dimension(120, 50));
        closeButton.setPreferredSize(new Dimension(120, 50));

        SwingCompFunc.setButtonStyle(chatButton);
        SwingCompFunc.setButtonStyle(closeButton);

        // 채팅 시작 버튼 이벤트
        chatButton.addActionListener(e -> {
            if (!client.getNickName().equals(userInfo.getNickname())) {
                client.enterAlonePrivateRoom(userInfo.getNickname());
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "본인의 프로필입니다.");
            }
        });

        // 닫기 버튼 이벤트
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(chatButton);
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 프레임 설정
        setContentPane(mainPanel);
        setSize(500, 700);
        setLocationRelativeTo(null);
    }

    private void setCharacterLabel(int characterNumber) {
        String profileCharacterPath = "src/Function/ProfileCharacterSelector/Img/" + characterNumber + ".jpg";
        characterImageLabel.setIcon(ImageLoad.getImageIcon(characterImageLabel, profileCharacterPath));
    }

    // byte[] 데이터를 ImageIcon으로 변환하는 메서드
    private ImageIcon byteArrayToImageIcon(byte[] imageBytes, int width, int height) {
        ImageIcon icon = new ImageIcon(imageBytes);
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    public static void main(String[] args) {
        // 테스트용 데이터
        byte[] profileImage = null; // 실제 데이터로 대체
        new UserProfilePopup(null, "홍길동", profileImage, 5, null).setVisible(true);
    }
}
