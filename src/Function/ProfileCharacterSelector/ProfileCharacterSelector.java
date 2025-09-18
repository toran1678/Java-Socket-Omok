package Function.ProfileCharacterSelector;

import ChatApp.Client.ChatLobbyLayout.ChatLobbyLayout;
import ChatApp.Server.ServerInfo.ServerInfo;
import Database.Database;
import Database.UserInfo.UserInfo;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileCharacterSelector extends JDialog {
    private JFrame frame;
    private Map<Integer, ImageIcon> characterImages;  // 캐릭터 ID와 이미지 매핑
    private Database db;
    private String userId;
    /* 선택한 캐릭터 ID (-1은 선택되지 않음) */
    private int selectedCharacterId = -1;
    private JLabel selectedCharacterLabel = new JLabel();

    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    public ProfileCharacterSelector(JFrame parent, String userId, UserInfo userInfo) {
        super(parent, "Character Selection", true);
        this.userId = userId;
        this.db = new Database();
        loadCharacterImages();  // 캐릭터 이미지 로드
        createAndShowGUI(userInfo);
    }

    /* 캐릭터 이미지를 로드하는 메서드 (이미지 아이디와 이미지를 매핑) */
    private void loadCharacterImages() {
        characterImages = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            String imagePath = "src/Function/ProfileCharacterSelector/Img/";
            characterImages.put(i, new ImageIcon(imagePath + i + ".jpg"));
        }
    }

    // GUI 생성 메서드
    private void createAndShowGUI(UserInfo userInfo) {
        //frame = new JFrame("Character Selection");
        setLayout(new BorderLayout());
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // 크기 조절 비활성화
        setResizable(false);
        // 프레임을 화면 중앙에 위치
        setLocationRelativeTo(null);

        setIconImage(gameIcon.getImage());

        // 상단에 "캐릭터 선택창" 텍스트 라벨 추가
        JLabel titleLabel = new JLabel("캐릭터 선택창", SwingConstants.CENTER);
        titleLabel.setPreferredSize(new Dimension(getWidth(), 60));
        titleLabel.setFont(new Font("Gulim", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        //SwingCompFunc.setTopLabelStyle(titleLabel);

        // 캐릭터 이미지 버튼을 담을 패널
        JPanel characterPanel = new JPanel();
        characterPanel.setLayout(new GridLayout(2, 5, 10, 10));  // 5개씩 두 줄로 배치

        for (int i = 1; i <= 10; i++) {
            JButton characterButton = new JButton(characterImages.get(i));
            int characterId = i;  // 캐릭터 ID 저장

            // "ComponentListener"를 사용하여 버튼의 크기가 설정된 후 이미지를 스케일링
            characterButton.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    // 버튼의 현재 크기에 맞게 이미지 스케일링
                    ImageIcon originalIcon = characterImages.get(characterId);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(
                            characterButton.getWidth(), characterButton.getHeight(), Image.SCALE_SMOOTH);
                    characterButton.setIcon(new ImageIcon(scaledImage));
                }
            });

            /* 버튼 클릭 시 해당 캐릭터 선택 */
            characterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedCharacterId = characterId;
                    ImageIcon originalIcon = characterImages.get(characterId);
                    Image scaledImage = originalIcon.getImage().getScaledInstance(
                            selectedCharacterLabel.getWidth(), selectedCharacterLabel.getHeight(), Image.SCALE_SMOOTH);
                    selectedCharacterLabel.setIcon(new ImageIcon(scaledImage));
                }
            });

            characterPanel.add(characterButton);  // 버튼 추가
        }

        add(characterPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JLabel selectedLabel = new JLabel("선택한 이미지: ");

        selectedCharacterLabel.setBorder(new LineBorder(Color.BLACK, 1));
        //selectedCharacterLabel.setHorizontalAlignment(SwingConstants.CENTER);
        selectedCharacterLabel.setPreferredSize(new Dimension(100, 120));

        JLabel emptyLabel = new JLabel();
        emptyLabel.setPreferredSize(new Dimension(30, 50));

        JButton selectButton = new JButton("캐릭터 선택");
        selectButton.setPreferredSize(new Dimension(120, 50));

        SwingCompFunc.setButtonStyle(selectButton);

        /* 유저 정보가 있을 경우, 없을 경우 리스너 */
        if (!userInfo.getName().equals("이름 없음")) {
            selectButton.addActionListener(e -> {
                if (selectedCharacterId == -1) {
                    JOptionPane.showMessageDialog(this, "캐릭터를 먼저 선택해주세요.");
                } else {
                    userInfo.setProfileCharacter(selectedCharacterId);
                    JOptionPane.showMessageDialog(this, "캐릭터 " + selectedCharacterId + "이(가) 선택되었습니다!");
                    dispose();
                }
            });
        } else {
            selectButton.addActionListener(e -> {
                if (selectedCharacterId == -1) {
                    JOptionPane.showMessageDialog(this, "캐릭터를 먼저 선택해주세요.");
                } else {
                    if (db.updateProfileCharacter(userId, selectedCharacterId)) {
                        JOptionPane.showMessageDialog(this, "캐릭터 " + selectedCharacterId + "이(가) 선택되었습니다!");
                        new ChatLobbyLayout(db.getNickname(userId), ServerInfo.IPNUMBER, ServerInfo.PORTNUMBER);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "캐릭터를 선택하지 못했습니다.");
                    }
                }
            });
        }

        bottomPanel.add(selectedLabel);
        bottomPanel.add(selectedCharacterLabel);
        bottomPanel.add(emptyLabel);
        bottomPanel.add(selectButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /* 선택된 캐릭터 ID를 반환하는 메서드 */
    public int getSelectedCharacterId() {
        setVisible(true); // 다이얼로그 표시 (모달이므로 여기에 멈춤)
        return selectedCharacterId;
    }

    public static void main(String[] args) {
        String userId = "user"; // 예시로 사용자 ID 1을 사용
        new ProfileCharacterSelector(null ,userId, null);
    }
}
