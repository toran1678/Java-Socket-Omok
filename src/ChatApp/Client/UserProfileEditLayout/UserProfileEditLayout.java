package ChatApp.Client.UserProfileEditLayout;

import ChatApp.Client.ClientApplication;
import Database.Database;
import Database.UserInfo.UserInfo;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import Function.ImageLoad;
import Function.SwingCompFunc.SwingCompFunc;
import Function.ProfileCharacterSelector.ProfileCharacterSelector;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserProfileEditLayout {
    Database db = new Database();
    int profileCharacter;
    UserInfo userinfo;
    String nickname = null;
    boolean isNicknameChecked = false;
    ClientApplication clientApplication;

    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    JFrame frame;
    JPanel topPanel = new JPanel();
    JPanel middlePanel = new JPanel();
    JPanel idPanel = new JPanel();
    JPanel namePanel = new JPanel();
    JPanel nicknamePanel = new JPanel();
    JPanel passwordPanel = new JPanel();
    JPanel passwdSafetyPanel = new JPanel();
    JPanel editPasswordPanel = new JPanel();
    JPanel confirmPasswdPanel = new JPanel();
    JPanel checkPanel = new JPanel();
    JPanel profileCharacterPanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    JLabel userEditLabel = new JLabel("회원 정보 수정", SwingConstants.CENTER);
    JLabel idLabel = new JLabel("아이디");
    JLabel idTextLabel = new JLabel("");
    JLabel nameLabel = new JLabel("이름");
    JLabel nameTextLabel = new JLabel("");
    JLabel nicknameLabel = new JLabel("닉네임");
    JProgressBar passwdSafetyProgressBar = new JProgressBar(0, 100);
    JLabel passwdSafetyLabel = new JLabel();
    JLabel passwordLabel = new JLabel("현재 비밀번호");
    JLabel editPasswordLabel = new JLabel("비밀번호 수정");
    JLabel confirmPasswdLabel = new JLabel("비밀번호 확인");
    JLabel checkLabel = new JLabel("비밀번호 검사");
    JLabel profileCharacterLabel = new JLabel();

    JTextField nicknameTextField = new JTextField();
    JPasswordField passwdTextField = new JPasswordField();
    JPasswordField editPasswordTextField = new JPasswordField();
    JPasswordField confirmPasswdTextField = new JPasswordField();

    JButton selectCharacterButton = new JButton("캐릭터 선택");
    JButton nicknameCheckButton = new JButton("중복확인");
    JButton editButton = new JButton("수정하기");
    JLabel emptyLabel = new JLabel();
    JButton cancelButton = new JButton("취소하기");

    public static void main(String[] args) {
        new UserProfileEditLayout("user1", null).showLayout();
    }

    public UserProfileEditLayout(String _nickname, ClientApplication clientApplication) {
        this.clientApplication = clientApplication;

        frame = new JFrame("User Profile Edit");
        frame.setLayout(new BorderLayout());
        frame.setSize(370, 520);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        SwingCompFunc.setFrameStyle(frame);

        this.nickname = _nickname;
        userinfo = db.getUserInfo(_nickname);
        this.profileCharacter = userinfo.getProfileCharacter();

        frame.add(topPanel, BorderLayout.NORTH);
        SwingCompFunc.setTopPanelStyle(topPanel);
        middlePanel.setBackground(SwingCompFunc.MiddlePanelColor);
        SwingCompFunc.setTopPanelStyle(bottomPanel);

        topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        bottomPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        idPanel.setBackground(new Color(255, 255, 255, 0));
        namePanel.setBackground(new Color(255, 255, 255, 0));
        nicknamePanel.setBackground(new Color(255, 255, 255, 0));
        passwordPanel.setBackground(new Color(255, 255, 255, 0));
        passwdSafetyPanel.setBackground(new Color(255, 255, 255, 0));
        editPasswordPanel.setBackground(new Color(255, 255, 255, 0));
        confirmPasswdPanel.setBackground(new Color(255, 255, 255, 0));
        checkPanel.setBackground(new Color(255, 255, 255, 0));
        profileCharacterPanel.setBackground(new Color(255, 255, 255, 0));

        userEditLabel.setPreferredSize(new Dimension(frame.getWidth(), 40));
        userEditLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        userEditLabel.setForeground(Color.WHITE);
        topPanel.add(userEditLabel);

        frame.add(middlePanel, BorderLayout.CENTER);
        middlePanel.setLayout(null);

        idPanel.setBounds(30, 10, 250, 33);
        middlePanel.add(idPanel);
        idPanel.setLayout(null);

        idLabel.setBounds(10, 9, 40, 15);
        idPanel.add(idLabel);

        idTextLabel.setText(userinfo.getId());
        idTextLabel.setBounds(90, 9, 96, 15);
        idPanel.add(idTextLabel);

        namePanel.setLayout(null);
        namePanel.setBounds(30, 40, 250, 33);
        middlePanel.add(namePanel);

        nameLabel.setBounds(10, 9, 40, 15);
        namePanel.add(nameLabel);

        nameTextLabel.setText(userinfo.getName());
        nameTextLabel.setBounds(90, 9, 96, 15);
        namePanel.add(nameTextLabel);

        nicknamePanel.setLayout(null);
        nicknamePanel.setBounds(30, 70, 300, 33);
        middlePanel.add(nicknamePanel);

        nicknameLabel.setBounds(10, 9, 40, 15);
        nicknamePanel.add(nicknameLabel);

        nicknameTextField.setBounds(90, 6, 96, 21);
        nicknamePanel.add(nicknameTextField);
        nicknameTextField.setColumns(10);

        SwingCompFunc.setButtonStyle(nicknameCheckButton);
        SwingCompFunc.setButtonStyle(editButton);

        nicknameCheckButton.setBounds(200, 5, 87, 23);
        nicknamePanel.add(nicknameCheckButton);

        passwordPanel.setLayout(null);
        passwordPanel.setBounds(30, 100, 300, 33);
        middlePanel.add(passwordPanel);

        passwordLabel.setBounds(10, 9, 80, 15);
        passwordPanel.add(passwordLabel);

        passwdTextField.setBounds(90, 6, 130, 21);
        passwordPanel.add(passwdTextField);

        passwdSafetyLabel.setOpaque(true);
        passwdSafetyLabel.setBackground(SwingCompFunc.MiddlePanelColor);
        checkLabel.setOpaque(true);
        checkLabel.setBackground(SwingCompFunc.MiddlePanelColor);


        passwdSafetyPanel.setLayout(null);
        passwdSafetyPanel.setBounds(30, 130, 300, 33);
        middlePanel.add(passwdSafetyPanel);

        passwdSafetyLabel.setBounds(200, 9, 100, 15);
        passwdSafetyPanel.add(passwdSafetyLabel);

        passwdSafetyProgressBar.setBounds(90, 9, 100, 15);
        passwdSafetyProgressBar.setValue(0);
        passwdSafetyProgressBar.setStringPainted(true);
        passwdSafetyPanel.add(passwdSafetyProgressBar);

        editPasswordPanel.setLayout(null);
        editPasswordPanel.setBounds(30, 160, 300, 33);
        middlePanel.add(editPasswordPanel);

        editPasswordLabel.setBounds(10, 9, 80, 15);
        editPasswordPanel.add(editPasswordLabel);

        editPasswordTextField.setBounds(90, 6, 130, 21);
        editPasswordPanel.add(editPasswordTextField);

        confirmPasswdPanel.setLayout(null);
        confirmPasswdPanel.setBounds(30, 190, 300, 33);
        middlePanel.add(confirmPasswdPanel);

        confirmPasswdLabel.setBounds(10, 9, 80, 15);
        confirmPasswdPanel.add(confirmPasswdLabel);

        confirmPasswdTextField.setBounds(90, 6, 130, 21);
        confirmPasswdPanel.add(confirmPasswdTextField);

        checkPanel.setLayout(null);
        checkPanel.setBounds(30, 220, 300, 33);
        middlePanel.add(checkPanel);

        checkLabel.setBounds(90, 9, 180, 15);
        checkPanel.add(checkLabel);

        profileCharacterPanel.setLayout(null);
        profileCharacterPanel.setBounds(30, 250, 300, 110);
        middlePanel.add(profileCharacterPanel);

        profileCharacterLabel.setBorder(new LineBorder(Color.BLACK, 1));
        profileCharacterLabel.setBounds(50, 10, 80, 90);
        profileCharacterPanel.add(profileCharacterLabel);

        setCharacterLabel();

        selectCharacterButton.setBounds(160, 40, 100, 30);
        profileCharacterPanel.add(selectCharacterButton);
        SwingCompFunc.setButtonStyle(selectCharacterButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        editButton.setFont(new Font("굴림", Font.BOLD, 13));
        bottomPanel.add(editButton);

        emptyLabel.setPreferredSize(new Dimension(10, 40));
        bottomPanel.add(emptyLabel);

        cancelButton.setFont(new Font("굴림", Font.BOLD, 13));
        bottomPanel.add(cancelButton);
        SwingCompFunc.setButtonStyle(cancelButton);

        /* Password Add DocumentListener */
        editPasswordTextField.getDocument().addDocumentListener(new PasswordFieldListener());
        confirmPasswdTextField.getDocument().addDocumentListener(new PasswordFieldListener());

        /* Placeholder */
        String placeholder = "8~16자, 특수문자 포함";
        SwingCompFunc.setPlaceholderPasswordField(editPasswordTextField, placeholder);
        SwingCompFunc.setPlaceholderPasswordField(confirmPasswdTextField, placeholder);

        /* LimitedTextField Event */
        SwingCompFunc.setDocumentFilter(passwdTextField, 16);
        SwingCompFunc.setDocumentFilter(editPasswordTextField, 16);
        SwingCompFunc.setDocumentFilter(confirmPasswdTextField, 16);
        SwingCompFunc.setDocumentFilter(nicknameTextField, 12);

        nicknameCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameTextField.getText().trim();
                if (nickname.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "현재 닉네임을 유지합니다.",
                            "닉네임 유지", JOptionPane.INFORMATION_MESSAGE);
                    isNicknameChecked = true;
                } else if (nickname.equals(_nickname)) {
                    JOptionPane.showMessageDialog(null, "현재 닉네임을 유지합니다.",
                            "닉네임 유지", JOptionPane.INFORMATION_MESSAGE);
                    isNicknameChecked = true;
                } else {
                    isNicknameChecked = db.nicknameCheck(nickname, isNicknameChecked);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initField();
                frame.dispose();
            }
        });

        selectCharacterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProfileCharacterSelector CharacterSelector = new ProfileCharacterSelector(frame, nickname, userinfo);
                int selectedId = CharacterSelector.getSelectedCharacterId();
                if (selectedId != -1) {
                    setCharacterLabel();
                }
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nickname = nicknameTextField.getText().trim();
                String password = new String(passwdTextField.getPassword()).trim();
                String editPassword = new String(editPasswordTextField.getPassword()).trim();
                String confirmPassword = new String(confirmPasswdTextField.getPassword()).trim();
                String placeholder = "8~16자, 특수문자 포함";

                if (!isNicknameChecked) {
                    JOptionPane.showMessageDialog(frame, "닉네임 중복 확인을 해주세요.",
                            "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "비밀번호를 입력하세요.",
                            "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!db.loginCheck(userinfo.getId(), password)) {
                    JOptionPane.showMessageDialog(frame, "비밀번호를 틀렸습니다.",
                            "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                /* 수정 비밀번호가 비어있을 경우에는 비밀번호 변경 X */
                if (!editPassword.equals(placeholder)) {
                    if(!editPassword.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(frame, "비밀번호가 일치하지 않습니다.",
                                "오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    /* 비밀번호 형식 맞는지 */
                    if(SwingCompFunc.isValidPassword(editPassword)) {
                        if (nickname.isEmpty()) {
                            /* 프로필 캐릭터, 비밀번호 */
                            db.updateProfileCharacter(userinfo.getId(), userinfo.getProfileCharacter());
                            db.updatePassword(userinfo.getId(), editPassword);
                            JOptionPane.showMessageDialog(frame, "회원 정보가 변경되었습니다.",
                                    "정보", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            /* 비밀번호, 닉네임, 프로필 캐릭터 */
                            db.updatePassword(userinfo.getId(), editPassword);
                            db.updateNickname(userinfo.getId(), nickname);
                            db.updateProfileCharacter(userinfo.getId(), userinfo.getProfileCharacter());
                            JOptionPane.showMessageDialog(frame, "회원 정보가 변경되었습니다.",
                                    "정보", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        return;
                    }
                }

                if (nickname.isEmpty()) {
                    /* 프로필 캐릭터만 */
                    db.updateProfileCharacter(userinfo.getId(), userinfo.getProfileCharacter());
                    JOptionPane.showMessageDialog(frame, "회원 정보가 변경되었습니다.",
                            "정보", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    /* 프로필 캐릭터, 닉네임 */
                    db.updateProfileCharacter(userinfo.getId(), userinfo.getProfileCharacter());
                    db.updateNickname(userinfo.getId(), nickname);
                    JOptionPane.showMessageDialog(frame, "회원 정보가 변경되었습니다.",
                            "정보", JOptionPane.INFORMATION_MESSAGE);
                }

                clientSendEditMessage(nickname);

                frame.dispose();
            }
        });

        nicknameTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { resetCheck(); }
            @Override
            public void removeUpdate(DocumentEvent e) { resetCheck(); }
            @Override
            public void changedUpdate(DocumentEvent e) { resetCheck(); }
            private void resetCheck() { isNicknameChecked = false; }
        });

    }

    private void setCharacterLabel() {
        String profileCharacterPath = "src/Function/ProfileCharacterSelector/Img/" + userinfo.getProfileCharacter() + ".jpg";
        profileCharacterLabel.setIcon(ImageLoad.getImageIcon(profileCharacterLabel, profileCharacterPath));
    }

    public class PasswordFieldListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            checkPasswords();
            PasswordSafetyStrength();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            checkPasswords();
            PasswordSafetyStrength();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            checkPasswords();
            PasswordSafetyStrength();
        }
    }

    public void clientSendEditMessage(String editNickname) {
        clientApplication.sendMessage(new MessageDTO(MessageType.EditUser, editNickname + ":" + nickname));
    }

    /* 프레임 보이게 */
    public void showLayout() {
        frame.setVisible(true);
    }

    public void checkPasswords() {
        String password = new String(editPasswordTextField.getPassword());
        String confirmPassword = new String(confirmPasswdTextField.getPassword());
        String placeholder = "8~16자, 특수문자 포함";

        // 플레이스홀더 상태 확인
        boolean isPasswordPlaceholder = password.equals(placeholder);
        boolean isConfirmPlaceholder = confirmPassword.equals(placeholder);

        if (isPasswordPlaceholder || isConfirmPlaceholder) {
            checkLabel.setText("비밀번호를 입력하세요.");
            checkLabel.setForeground(Color.GRAY);
        } else if (password.equals(confirmPassword)) {
            checkLabel.setText("비밀번호가 일치합니다.");
            checkLabel.setForeground(new Color(47, 157, 39)); // 초록색
        } else {
            checkLabel.setText("비밀번호가 일치하지 않습니다.");
            checkLabel.setForeground(Color.RED);
        }
    }

    public void PasswordSafetyStrength() {
        String password = new String(editPasswordTextField.getPassword());
        int strength = SwingCompFunc.calculateStrength(password);
        String placeholder = "8~16자, 특수문자 포함";

        if (password.equals(placeholder) || password.isEmpty()) {
            passwdSafetyLabel.setText("안전성 검사");
            passwdSafetyLabel.setForeground(Color.GRAY);
        }else if (strength <= 20) {
            passwdSafetyLabel.setText("위험");
            passwdSafetyLabel.setForeground(Color.RED);
        } else if (strength <= 40) {
            passwdSafetyLabel.setText("약함");
            passwdSafetyLabel.setForeground(Color.RED);
        } else if (strength <=60) {
            passwdSafetyLabel.setText("보통");
            passwdSafetyLabel.setForeground(Color.ORANGE);
        } else {
            passwdSafetyLabel.setText("정상");
            passwdSafetyLabel.setForeground(new Color(47, 157, 39));
        }

        passwdSafetyProgressBar.setValue(strength);
    }

    public void initField() {
        nicknameTextField.setText("");

        passwdTextField.setText("");

        String placeholder = "8~16자, 특수문자 포함";
        editPasswordTextField.setText(placeholder);
        editPasswordTextField.setEchoChar((char) 0);
        editPasswordTextField.setForeground(Color.GRAY);

        confirmPasswdTextField.setText(placeholder);
        confirmPasswdTextField.setEchoChar((char) 0);
        confirmPasswdTextField.setForeground(Color.GRAY);
    }
}