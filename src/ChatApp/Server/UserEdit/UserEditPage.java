package ChatApp.Server.UserEdit;

import Database.Database;
import Database.UserInfo.UserInfo;
import Function.ImageLoad;
import Function.ProfileCharacterSelector.ProfileCharacterSelector;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class UserEditPage extends JFrame {
    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");
    UserInfo userInfo;
    Database db = new Database();

    JPanel topPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    JPanel middlePanel = new JPanel();

    JPanel idPanel = new JPanel();
    JPanel namePanel = new JPanel();
    JPanel nicknamePanel = new JPanel();
    JPanel passwordPanel = new JPanel();
    JPanel phonePanel = new JPanel();
    JPanel birthPanel = new JPanel();
    JPanel genderPanel = new JPanel();
    JPanel profileCharacterPanel = new JPanel();
    JPanel emailPanel = new JPanel();

    JLabel userEditLabel = new JLabel("회원 정보 수정", SwingConstants.CENTER);
    JLabel idLabel = new JLabel("아이디");
    JLabel nameLabel = new JLabel("이름");
    JLabel nicknameLabel = new JLabel("닉네임");
    JLabel passwordL = new JLabel("비밀번호");
    JLabel phoneNumberL = new JLabel("핸드폰 번호");
    JLabel phoneDashLabel1 = new JLabel("-");
    JLabel phoneDashLabel2 = new JLabel("-");
    JLabel birthL = new JLabel("생일");
    JLabel birthYearL = new JLabel("년");
    JLabel birthMonthL = new JLabel("월");
    JLabel birthDayL = new JLabel("일");
    JLabel genderL = new JLabel("성별");
    JLabel profileCharacterLabel = new JLabel();
    JButton selectCharacterButton = new JButton("캐릭터 선택");
    JLabel emailL = new JLabel("이메일");
    JLabel emailL2 = new JLabel("@");

    JTextField nicknameTextField = new JTextField();
    JTextField birthYearT = new JTextField();
    JTextField nameT = new JTextField();
    JTextField idT = new JTextField();
    JTextField passwordT = new JTextField();
    JTextField phoneMiddleT = new JTextField();
    JTextField phoneLastT = new JTextField();
    JTextField emailT = new JTextField();
    JTextField emailDomainT = new JTextField();

    JButton editButton = new JButton("수정하기");
    JLabel emptyLabel = new JLabel();
    JButton cancelButton = new JButton("취소하기");
    JLabel emptyLabel2 = new JLabel();
    JButton deleteButton = new JButton("삭제하기");

    /* ComboBox Set */
    static String[] phoneFirstStr = {"010", "011"};
    static String[] birthMonthStr = new String[12];
    static String[] birthDayStr = new String[31];
    static String[] emailDomainStr = {"직접 입력", "naver.com", "gmail.com", "nate.com",
            "yahoo.com", "tistory.com", "daum.net", "hanmail.net"};

    // 정적 초기화 블록으로 배열 초기화
    static {
        birthMonthStr = new String[12];
        for (int i = 0; i < 12; i++) {
            birthMonthStr[i] = String.valueOf(i + 1);
        }

        birthDayStr = new String[31];
        for (int i = 0; i < 31; i++) {
            birthDayStr[i] = String.valueOf(i + 1);
        }
    }

    JComboBox<String> phoneFirstComboBox;
    JComboBox<String> birthMonthComboBox;
    JComboBox<String> birthDayComboBox;
    JComboBox<String> emailDomainComboBox;

    JRadioButton maleRadioB = new JRadioButton("남자");
    JRadioButton femaleRadioB = new JRadioButton("여자");

    public static void main(String[] args) {
        new UserEditPage("torangod").setVisible(true);
    }

    public UserEditPage(String nickname) {
        setTitle("유저 관리");
        setSize(450, 510);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        setResizable(false);
        setLocationRelativeTo(null);

        setIconImage(gameIcon.getImage());
        userInfo = db.getUserInfo(nickname);

        idT.setEditable(false);

        topPanel.setBackground(new Color(153, 204, 255));
        middlePanel.setBackground(new Color(251, 251, 251));
        bottomPanel.setBackground(new Color(153, 204, 255));

        idPanel.setBackground(new Color(255, 255, 255, 0));
        namePanel.setBackground(new Color(255, 255, 255, 0));
        nicknamePanel.setBackground(new Color(255, 255, 255, 0));
        passwordPanel.setBackground(new Color(255, 255, 255, 0));
        phonePanel.setBackground(new Color(255, 255, 255, 0));
        birthPanel.setBackground(new Color(255, 255, 255, 0));
        genderPanel.setBackground(new Color(255, 255, 255, 0));
        emailPanel.setBackground(new Color(255, 255, 255, 0));
        profileCharacterPanel.setBackground(new Color(255, 255, 255, 0));
        maleRadioB.setBackground(new Color(251, 251, 251));
        femaleRadioB.setBackground(new Color(251, 251, 251));

        userEditLabel.setPreferredSize(new Dimension(this.getWidth(), 40));

        topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        bottomPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        profileCharacterLabel.setBorder(new LineBorder(Color.BLACK, 1));

        add(topPanel, BorderLayout.NORTH);
        topPanel.add(userEditLabel);

        add(middlePanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(editButton);
        emptyLabel.setPreferredSize(new Dimension(10, 40));
        bottomPanel.add(emptyLabel);
        bottomPanel.add(cancelButton);
        emptyLabel2.setPreferredSize(new Dimension(10, 40));
        bottomPanel.add(emptyLabel2);
        bottomPanel.add(deleteButton);

        userEditLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        userEditLabel.setForeground(Color.WHITE);
        editButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        editButton.setBackground(new Color(122, 178, 211));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);

        cancelButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        cancelButton.setBackground(new Color(122, 178, 211));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        deleteButton.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        deleteButton.setBackground(new Color(122, 178, 211));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        selectCharacterButton.setBackground(new Color(122, 178, 211));
        selectCharacterButton.setForeground(Color.WHITE);
        selectCharacterButton.setFocusPainted(false);

        middlePanel.setLayout(null);
        idPanel.setLayout(null);
        namePanel.setLayout(null);
        nicknamePanel.setLayout(null);
        phonePanel.setLayout(null);
        passwordPanel.setLayout(null);
        birthPanel.setLayout(null);
        genderPanel.setLayout(null);
        profileCharacterPanel.setLayout(null);
        emailPanel.setLayout(null);

        phoneFirstComboBox = new JComboBox<>(phoneFirstStr);
        birthMonthComboBox = new JComboBox<>(birthMonthStr);
        birthDayComboBox = new JComboBox<>(birthDayStr);
        emailDomainComboBox = new JComboBox<>(emailDomainStr);

        idPanel.setBounds(30, 10, 300, 33);
        idLabel.setBounds(10, 9, 40, 15);
        idT.setBounds(90, 6, 120, 21);
        idPanel.add(idLabel); idPanel.add(idT);
        middlePanel.add(idPanel);

        namePanel.setBounds(30, 40, 300, 33);
        nameLabel.setBounds(10, 9, 40, 15);
        nameT.setBounds(90, 6, 120, 21);
        namePanel.add(nameLabel);
        namePanel.add(nameT);
        middlePanel.add(namePanel);

        nicknamePanel.setBounds(30, 70, 300, 33);
        nicknameLabel.setBounds(10, 9, 40, 15);
        nicknameTextField.setBounds(90, 6, 120, 21);
        nicknamePanel.add(nicknameLabel);
        nicknamePanel.add(nicknameTextField);
        middlePanel.add(nicknamePanel);

        phonePanel.setBounds(30, 130, 300, 33);
        phoneNumberL.setBounds(10, 9, 80, 15);
        phoneFirstComboBox.setBounds(90, 5, 55, 21);
        phoneDashLabel1.setBounds(155, 6, 16, 15);
        phoneMiddleT.setBounds(169, 5, 45, 21);
        phoneDashLabel2.setBounds(221, 6, 16, 15);
        phoneLastT.setBounds(234, 5, 45, 21);
        phonePanel.add(phoneNumberL);
        phonePanel.add(phoneFirstComboBox);
        phonePanel.add(phoneDashLabel1);
        phonePanel.add(phoneMiddleT);
        phonePanel.add(phoneDashLabel2);
        phonePanel.add(phoneLastT);
        middlePanel.add(phonePanel);

        passwordPanel.setBounds(30, 100, 300, 33);
        passwordL.setBounds(10, 9, 80, 15);
        passwordT.setBounds(90, 6, 120, 21);
        passwordPanel.add(passwordL);
        passwordPanel.add(passwordT);
        middlePanel.add(passwordPanel);

        birthPanel.setBounds(30, 160, 300, 33);
        birthYearT.setBounds(90, 5, 50, 21);
        birthL.setBounds(10, 9, 80, 15);
        birthYearL.setBounds(144, 9, 16, 15);
        birthMonthComboBox.setBounds(162, 5, 48, 21);
        birthMonthL.setBounds(215, 9, 16, 15);
        birthDayComboBox.setBounds(233, 5, 48, 21);
        birthDayL.setBounds(284, 9, 16, 15);
        birthPanel.add(birthYearT);
        birthPanel.add(birthL);
        birthPanel.add(birthYearL);
        birthPanel.add(birthMonthComboBox);
        birthPanel.add(birthMonthL);
        birthPanel.add(birthDayComboBox);
        birthPanel.add(birthDayL);
        middlePanel.add(birthPanel);

        genderPanel.setBounds(30, 190, 300, 33);
        genderL.setBounds(10, 9, 80, 15);
        maleRadioB.setBounds(90, 5, 60, 23);
        femaleRadioB.setBounds(150, 5, 60, 23);
        genderPanel.add(genderL);
        genderPanel.add(maleRadioB);
        genderPanel.add(femaleRadioB);
        middlePanel.add(genderPanel);

        /* 버튼 그룹화 */
        ButtonGroup sexRBGroup = new ButtonGroup();
        sexRBGroup.add(femaleRadioB);
        sexRBGroup.add(maleRadioB);

        profileCharacterPanel.setBounds(30, 250, 300, 110);
        profileCharacterLabel.setBounds(50, 10, 80, 90);
        selectCharacterButton.setBounds(150, 35, 125, 40);
        profileCharacterPanel.add(profileCharacterLabel);
        profileCharacterPanel.add(selectCharacterButton);
        middlePanel.add(profileCharacterPanel);

        emailPanel.setBounds(30, 220, 400, 33);
        emailL.setBounds(10, 9, 80, 15);
        emailT.setBounds(90, 5, 80, 21);
        emailDomainComboBox.setBounds(280, 5, 100, 23);
        emailL2.setBounds(175, 9, 16, 15);
        emailDomainT.setBounds(192, 5, 80, 21);
        emailPanel.add(emailL);
        emailPanel.add(emailT);
        emailPanel.add(emailDomainComboBox);
        emailPanel.add(emailL2);
        emailPanel.add(emailDomainT);
        middlePanel.add(emailPanel);

        setUserInfo();

        SwingCompFunc.setNumericInputFilter(phoneMiddleT, 4);
        SwingCompFunc.setNumericInputFilter(phoneLastT, 4);
        SwingCompFunc.setNumericInputFilter(birthYearT, 4);

        SwingCompFunc.setDocumentFilter(idT, 12);
        SwingCompFunc.setDocumentFilter(nameT, 12);
        SwingCompFunc.setDocumentFilter(nicknameTextField, 12);
        SwingCompFunc.setDocumentFilter(passwordT, 16);
        SwingCompFunc.setDocumentFilter(emailT, 12);
        SwingCompFunc.setDocumentFilter(emailDomainT, 12);

        /* EmailDomainComboBox Event */
        emailDomainComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) emailDomainComboBox.getSelectedItem();
                if(Objects.requireNonNull(selectedItem).equals("직접 입력")) {
                    emailDomainT.setText("");
                } else {
                    emailDomainT.setText(selectedItem);
                }
            }
        });

        setCharacterLabel();
        selectCharacterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfileCharacterSelector CharacterSelector = new ProfileCharacterSelector(null, nickname, userInfo);
                int selectedId = CharacterSelector.getSelectedCharacterId();
                if (selectedId != -1) {
                    setCharacterLabel();
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String realId = userInfo.getId();
                String id = idT.getText();
                String name = nameT.getText();
                String nickname = nicknameTextField.getText();
                String password = passwordT.getText();
                String phone = phoneFirstComboBox.getSelectedItem() + "-" + phoneMiddleT.getText() + "-" + phoneLastT.getText();
                String birth = birthYearT.getText() + "/" + birthMonthComboBox.getSelectedItem() + "/" + birthDayComboBox.getSelectedItem();
                String gender = maleRadioB.isSelected() ? "남자" : "여자";
                String email = emailT.getText() + "@" + emailDomainT.getText();
                int profileCharacterInt = userInfo.getProfileCharacter();

                if (db.updateUserInfo(realId, id, name, nickname, password, phone, birth, gender, email, profileCharacterInt)) {
                    JOptionPane.showMessageDialog(null, "회원 정보가 성공적으로 수정되었습니다.");
                } else {
                    JOptionPane.showMessageDialog(null, "회원 정보 수정에 실패했습니다.");
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, "정말로 해당 유저를 삭제하시겠습니까?",
                        "삭제 확인", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    // 삭제 메서드 호출
                    if (db.moveUserToTrash(userInfo.getId())) {
                        JOptionPane.showMessageDialog(null, "회원 정보가 성공적으로 삭제되었습니다.");
                        dispose(); // 삭제 후 창 닫기
                    } else {
                        JOptionPane.showMessageDialog(null, "회원 정보 삭제에 실패했습니다.");
                    }
                }
            }
        });
    }

    private void setCharacterLabel() {
        String profileCharacterPath = "src/Function/ProfileCharacterSelector/Img/" + userInfo.getProfileCharacter() + ".jpg";
        profileCharacterLabel.setIcon(ImageLoad.getImageIcon(profileCharacterLabel, profileCharacterPath));
    }

    public void setUserInfo() {
        idT.setText(userInfo.getId());
        nameT.setText(userInfo.getName());
        nicknameTextField.setText(userInfo.getNickname());
        passwordT.setText(userInfo.getPassword());

        // 핸드폰 번호 세팅
        String[] phoneParts = userInfo.getPhoneNum().split("-");
        if (phoneParts.length == 3) {
            phoneFirstComboBox.setSelectedItem(phoneParts[0]);
            phoneMiddleT.setText(phoneParts[1]);
            phoneLastT.setText(phoneParts[2]);
        }

        // 생년월일 세팅
        String[] birthParts = userInfo.getBirth().split("/");
        if (birthParts.length == 3) {
            birthYearT.setText(birthParts[0]);
            birthMonthComboBox.setSelectedItem(birthParts[1]);
            birthDayComboBox.setSelectedItem(birthParts[2]);
        }

        // 성별 세팅
        if ("남자".equals(userInfo.getGender())) {
            maleRadioB.setSelected(true);
        } else {
            femaleRadioB.setSelected(true);
        }

        // 이메일 세팅
        String[] emailParts = userInfo.getEmail().split("@");
        if (emailParts.length == 2) {
            emailT.setText(emailParts[0]);
            emailDomainT.setText(emailParts[1]);
        }
    }
}
