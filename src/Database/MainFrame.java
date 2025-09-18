package Database;

import ChatApp.Client.ChatLobbyLayout.ChatLobbyLayout;
import Database.FindFrame.FindIDFrame;
import Database.FindFrame.FindPasswordFrame;
import Database.UserInfo.UserInfo;
import Function.ProfileCharacterSelector.ProfileCharacterSelector;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import ChatApp.Server.ServerInfo.ServerInfo;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class MainFrame extends JFrame {
    /* Panel */
    JPanel mainPanel = new JPanel();
    JPanel loginPanel = new JPanel();
    JPanel findPanel = new JPanel();

    /* Label */
    JLabel idL = new JLabel("아이디");
    JLabel pwL = new JLabel("비밀번호");

    /* TextField */
    JTextField id = new JTextField();
    JPasswordField pw = new JPasswordField();

    /* Button */
    JButton loginBtn = new JButton("로그인");
    JButton joinBtn = new JButton("회원가입");
    JButton exitBtn = new JButton("프로그램 종료");
    JButton findIdBtn = new JButton("아이디 찾기");
    JButton findPwBtn = new JButton("비밀번호 찾기");

    /* Image */
    JLabel imageL = new JLabel();
    ImageIcon defaultImageIcon = new ImageIcon("src/Database/Image/오목.jpg");
    Image defaultImage = defaultImageIcon.getImage();

    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    Database db = new Database();

    MainFrame(){
        setTitle("로그인");

        SwingCompFunc.setFrameStyle(this);

        /* Panel 크기 작업 */
        mainPanel.setBounds(0, 0, 490, 380);
        mainPanel.setLayout(null);
        loginPanel.setBounds(85, 235, 320, 85);
        loginPanel.setLayout(null);
        loginPanel.setOpaque(false);
        findPanel.setBounds(12, 310, 470, 50);
        findPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        findPanel.setOpaque(false);

        findIdBtn.setPreferredSize(new Dimension(142, 40));
        findPwBtn.setPreferredSize(new Dimension(142, 40));
        joinBtn.setPreferredSize(new Dimension(142, 40));

        findIdBtn.setFont(new Font("나눔고딕", Font.BOLD, 15));
        findPwBtn.setFont(new Font("나눔고딕", Font.BOLD, 15));
        joinBtn.setFont(new Font("나눔고딕", Font.BOLD, 15));

        // joinBtn.setBounds(340, 320, 130, 40);
        // joinBtn.setFocusPainted(false);

        SwingCompFunc.setLoginButtonStyle(findIdBtn);
        SwingCompFunc.setLoginButtonStyle(findPwBtn);
        SwingCompFunc.setLoginButtonStyle(joinBtn);

        findPanel.add(findIdBtn);
        findPanel.add(findPwBtn);
        findPanel.add(joinBtn);

        mainPanel.add(findPanel);

        /* Label 크기 작업 */
        idL.setFont(new Font("나눔고딕", Font.BOLD, 15));
        idL.setBounds(17, 16, 50, 15);
        idL.setForeground(Color.WHITE);

        pwL.setFont(new Font("나눔고딕", Font.BOLD, 15));
        pwL.setBounds(10, 51, 65, 17);
        pwL.setForeground(Color.WHITE);

        /* TextField 크기 작업 */
        id.setBounds(80, 7, 140, 30);
        pw.setBounds(80, 45, 140, 30);

        /* Button 크기 작업 */
        loginBtn.setFont(new Font("나눔고딕", Font.BOLD, 15));
        loginBtn.setBounds(230, 7, 80,68);
        loginBtn.setFocusPainted(false);

        SwingCompFunc.setLoginButtonStyle(loginBtn);

        exitBtn.setFont(new Font("나눔고딕", Font.BOLD, 13));
        exitBtn.setBounds(350, 10, 120, 30);
        exitBtn.setFocusPainted(false);

        SwingCompFunc.setLoginButtonStyle(exitBtn);

        /* Image */
        imageL.setBounds(0, 0, 490, 380);
        Image defaultScaleImage = defaultImage.getScaledInstance(imageL.getWidth(), imageL.getHeight(), defaultImage.SCALE_SMOOTH);
        ImageIcon defaultScaledIcon = new ImageIcon(defaultScaleImage);
        imageL.setIcon(defaultScaledIcon);
        // imageL.setOpaque(true);

        setIconImage(gameIcon.getImage());

        /* Panel 추가 작업 */
        setContentPane(mainPanel);
        mainPanel.add(imageL);
        mainPanel.add(loginPanel);
        loginPanel.add(idL);
        loginPanel.add(pwL);
        loginPanel.add(id);
        loginPanel.add(pw);
        loginPanel.add(loginBtn);
        // mainPanel.add(joinBtn);
        mainPanel.add(exitBtn);

        /* ImageLabel 뒤로 보내기 */
        mainPanel.setComponentZOrder(imageL, mainPanel.getComponentCount() - 1);
        //+30 + 20
        setSize(500, 415);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        id.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!id.getText().isEmpty()) {
                    loginMethod();
                }
            }
        });

        /* 채팅 텍스트 필드 이벤트 */
        pw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String password = new String(pw.getPassword()).trim();
                if (!password.isEmpty()) {
                    loginMethod();
                }
            }
        });

        /* LoginButton Event */
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginMethod();
            }
        });

        /* JoinButton Event */
        joinBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new JoinFrame().setVisible(true);
            }
        });

        /* ExitButton Event */
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("프로그램 종료");
                System.exit(0);
            }
        });

        findIdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FindIDFrame();
            }
        });

        findPwBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new FindPasswordFrame();
            }
        });

        /* LimitedTextField Event */
        ((AbstractDocument) id.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= 12) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() + text.length() - length) <= 12) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        ((AbstractDocument) pw.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= 12) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() + text.length() - length) <= 12) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    private void loginMethod() {
        /* "TextField"에 입력된 아이디와 비밀번호를 변수에 초기화 */
        String uid = id.getText();
        String upass = "";
        for(int i=0; i<pw.getPassword().length; i++) {
            upass += pw.getPassword()[i];
        }

        if(uid.isEmpty() || upass.isEmpty()) {
            JOptionPane.showMessageDialog(null, "아이디와 비밀번호 모두 입력해주세요",
                    "로그인 실패", JOptionPane.ERROR_MESSAGE);
        } else {
            if(uid.equals("admin") && upass.equals("1234")) {
                try {
                    ObjectOutputStream out;
                    Socket socket = new Socket(ServerInfo.IPNUMBER, ServerInfo.PORTNUMBER);
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.flush();
                    out.writeObject(new MessageDTO(MessageType.AdminTool, ""));
                    out.flush();
                    out.close();
                    socket.close();
                    dispose();
                } catch (IOException ex) {
                    System.err.println("메시지 전송 중 오류 발생: " + ex.getMessage());
                }
                return;
            }
            if(db.loginCheck(uid, upass)) {
                if (db.profileCharacterCheck(uid)) {
                    ProfileCharacterSelector CharacterSelector = new ProfileCharacterSelector(null, uid, new UserInfo());
                    CharacterSelector.getSelectedCharacterId();
                    //new ProfileCharacterSelector(null, uid, null);
                } else {
                    new ChatLobbyLayout(db.getNickname(uid), ServerInfo.IPNUMBER, ServerInfo.PORTNUMBER);
                }
                dispose();
            } else {
                System.out.println("로그인 실패 > 로그인 정보 불일치");
                JOptionPane.showMessageDialog(null, "로그인에 실패하였습니다");
            }
        }
    }
}