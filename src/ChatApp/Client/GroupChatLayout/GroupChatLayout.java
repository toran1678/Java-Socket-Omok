package ChatApp.Client.GroupChatLayout;

import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import ChatApp.Client.ClientApplication;
import Function.Image.ResizeImage;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupChatLayout extends JFrame {
    /* GUI Components */
    JPanel mainPanel = new JPanel();
    JPanel chatPanel = new JPanel();
    JPanel chatTextPanel = new JPanel();
    JPanel subPanel = new JPanel();
    JTextField chatTextField = new JTextField();;
    // JTextArea chatArea = new JTextArea();
    JTextPane chatPane = new JTextPane();
    JScrollPane chatScrollPane = new JScrollPane(chatPane);
    public JList<String> userList = new JList<String>();
    public DefaultListModel<String> userModel;
    JLabel userLabel = new JLabel("접속중인 유저");
    JButton sendBtn = new JButton(">");
    JButton emoticonBtn = new JButton("㋡");
    JButton exitBtn = new JButton("종료");

    /* Functional Variables */
    String nickName, ipAddress;
    int portNum;
    GroupChatBack groupChatBack = new GroupChatBack();
    ClientApplication client;
    public GroupChatBack getGroupChatBack() { return groupChatBack; }

    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    JPopupMenu userPopupMenu = new JPopupMenu();
    JMenuItem infoMenuItem = new JMenuItem("정보 확인");
    JMenuItem whisperMenuItem = new JMenuItem("귓속말 보내기");

    /* 메시지 검색 */
    JPopupMenu chatPopupMenu = new JPopupMenu();
    JMenuItem searchTextItem = new JMenuItem("메시지 검색");

    /* Border */
    EtchedBorder etchedBorder = new EtchedBorder(EtchedBorder.LOWERED, null, null);
    public GroupChatLayout(String nickName, String roomName, String ipAddress, int portNum, ClientApplication client) {
        this.nickName = nickName;
        this.portNum = portNum;
        this.client = client;

        setContentPane(mainPanel);
        setSize(515, 660);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);

        mainPanel.setBackground(new Color(153, 204, 255));
        mainPanel.setBounds(0, 0, 500, 620);
        mainPanel.setLayout(null);

        setIconImage(gameIcon.getImage());

        /* 읽기만 */
        // chatArea.setEditable(false);
        chatPane.setEditable(false);
        /* 자동 줄바꿈 */
        // chatArea.setLineWrap(true);


        /* setBorder */
        chatPanel.setBorder(etchedBorder);
        userList.setBorder(etchedBorder);

        /* setFont */
        // chatArea.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        chatPane.setFont(new Font("나눔고딕", Font.PLAIN, 16));

        /* setBackground */
        chatTextPanel.setBackground(new Color(153, 204, 255));
        subPanel.setBackground(new Color(153, 204, 255));

        /* setBound  */
        chatPanel.setBounds(10, 10, 300, 560);
        //chatArea.setBounds(0, 0, 300, 560);
        chatScrollPane.setBounds(0, 0, 300, 560);
        chatTextPanel.setBounds(10, 580, 500, 30);
        chatTextField.setBounds(0, 0, 240, 30);
        sendBtn.setBounds(250, 0, 50, 30);
        emoticonBtn.setBounds(310, 0, 50, 30);
        subPanel.setBounds(320, 10, 170, 600);
        userList.setBounds(0, 20, 170, 500);
        userLabel.setBounds(0, 0, 120, 15);
        exitBtn.setBounds(395, 0, 85, 30);

        SwingCompFunc.setButtonStyle(sendBtn);
        SwingCompFunc.setButtonStyle(emoticonBtn);
        SwingCompFunc.setButtonStyle(exitBtn);

        /* setLayout */
        chatPanel.setLayout(null);
        chatTextPanel.setLayout(null);
        subPanel.setLayout(null);

        /* add Panel */
        mainPanel.add(chatPanel);
        //chatPanel.add(chatArea);
        chatPanel.add(chatScrollPane);
        mainPanel.add(chatTextPanel);
        chatTextPanel.add(chatTextField);
        chatTextPanel.add(sendBtn);
        chatTextPanel.add(emoticonBtn);
        chatTextPanel.add(exitBtn);
        mainPanel.add(subPanel);
        subPanel.add(userList);
        subPanel.add(userLabel);

        setVisible(true);

        /* 기능 */
        userModel = new DefaultListModel<>();
        userList.setModel(userModel);

        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Message = chatTextField.getText().trim();
                if (!Message.isEmpty()) {
                    groupChatBack.sendMessage(new MessageDTO(MessageType.CHAT, "[" + nickName + "]: " + Message));
                    chatTextField.setText(null);
                }
            }
        });

        emoticonBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEmojiSelector();
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApplication();
            }
        });

        chatTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Message = chatTextField.getText().trim();
                if (!Message.isEmpty()) {
                    groupChatBack.sendMessage(new MessageDTO(MessageType.CHAT, "[" + nickName + "]: " + Message));
                    chatTextField.setText(null);
                }
            }
        });

        /* 유저 리스트 팝업 */
        userPopupMenu.add(infoMenuItem);
        userPopupMenu.add(whisperMenuItem);
        userList.add(userPopupMenu);

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int index = userList.locationToIndex(e.getPoint());
                    userList.setSelectedIndex(index);  // 선택한 유저를 설정
                    userPopupMenu.show(userList, e.getX(), e.getY());  // 팝업 메뉴 표시
                }
            }
        });
        /* 유저 정보 팝업 */
        infoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    getUserinfo(selectedUser);
                }
            }
        });
        /* 귓속말 팝업 */
        whisperMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser != null) {
                    // 귓속말 메시지 입력을 위한 다이얼로그
                    String message = JOptionPane.showInputDialog("귓속말을 입력하세요:");
                    if (message != null && !message.trim().isEmpty()) {
                        client.sendMessage(new MessageDTO(MessageType.Whisper,
                                nickName + ":" + selectedUser + ":" + message));
                    }
                }
            }
        });

        /* 채팅 영역 팝업 */
        chatPopupMenu.add(searchTextItem);
        //chatArea.add(chatPopupMenu);
        chatPane.add(chatPopupMenu);
        //chatArea.setComponentPopupMenu(chatPopupMenu);
        chatPane.setComponentPopupMenu(chatPopupMenu);

//        chatArea.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (SwingUtilities.isRightMouseButton(e)) {  // 우클릭 체크
//                    chatPopupMenu.show(chatArea, e.getX(), e.getY());  // 클릭한 위치에 팝업 메뉴 표시
//                }
//            }
//        });

        chatPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {  // 우클릭 체크
                    chatPopupMenu.show(chatPane, e.getX(), e.getY());  // 클릭한 위치에 팝업 메뉴 표시
                }
            }
        });

        searchTextItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String keyword = JOptionPane.showInputDialog("검색할 키워드를 입력하세요:");
                if (keyword != null && !keyword.trim().isEmpty()) {
                    // List<String> results = db.getMessagesByKeyword(keyword, "Lobby");  // 데이터베이스에서 키워드로 메시지 검색
                    // displaySearchResults(results);  // 검색 결과를 별도의 창에 표시

                    MessageDTO searchRequest = new MessageDTO(MessageType.SEARCH_REQUEST, keyword);
                    groupChatBack.sendMessage(searchRequest);
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // super.windowClosing(e);
                closeApplication();
            }
        });

        groupChatBack.setGui(this);
        groupChatBack.setUserInfo(nickName, roomName, ipAddress, portNum);
        groupChatBack.start();
    }

    private void closeApplication() {
        client.sendMessage(new MessageDTO(MessageType.LeaveRoom, nickName));

        try {
            if (groupChatBack.socket != null && !groupChatBack.socket.isClosed()) {
                groupChatBack.socket.close();
            }
        } catch (IOException e) {
            System.err.println("소켓 닫기 중 오류 발생: " + e.getMessage());
        }

        removeUser(nickName);
        dispose();
    }

    public void removeUser(String userName) {
        userList.removeAll();
        userModel.removeElement(userName);
        userList.setModel(userModel);
        userList.repaint();
    }

    public void appendMessage(String message) {
        // chatArea.append(message + "\n");
        boolean isMine = message.startsWith("["+nickName+"]");
        Style style = chatPane.addStyle("ChatStyle", null);
        StyledDocument doc = chatPane.getStyledDocument();
        setStyleIsMine(style, isMine);

        if (isMine) {
            message = message.split(":")[1] + "< [나]";
        }
        try {
            doc.insertString(doc.getLength(), message + "\n", style);
            doc.setParagraphAttributes(doc.getLength() - 1, 1, style, false);
        } catch (BadLocationException e) {
            System.err.println(e.getMessage());
        }


        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void setStyleIsMine(Style style, boolean isMine) {
        if (isMine) {
            // 내 메시지: 오른쪽 정렬
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setForeground(style, new Color(76, 175, 80)); // 초록색
        } else {
            // 상대 메시지: 왼쪽 정렬
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(style, new Color(33, 150, 243)); // 파란색
        }
    }

    public void resetUserList(ArrayList<String> nickNameList) {
        userModel.removeAllElements();
        userList.removeAll();

        userModel.clear();
        for (String userName : nickNameList) {
            //userModel.addElement(userName);
            addUser(userName);
        }
        userList.setModel(userModel);
        userList.repaint();
    }

    public void addUser(String userName) {
        if(!userModel.contains(userName)) {
            userModel.addElement(userName);
        }
    }

    public void getUserinfo(String selectedValue) {
        client.sendMessage(new MessageDTO(MessageType.ShowUserInfo, selectedValue));
    }

    public void displaySearchResults(List<String> results) {
        // 검색 결과 텍스트 영역
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setFont(new Font("나눔고딕", Font.PLAIN, 14));

        // 결과 텍스트 추가
        for (String message : results) {
            resultArea.append(message + "\n");
        }

        // 스크롤 패널 설정
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        // 상단에 검색 결과 제목 레이블 추가
        JLabel titleLabel = new JLabel("검색 결과");
        titleLabel.setFont(new Font("나눔고딕", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // 패널 레이아웃 구성
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // JDialog 생성 및 설정
        JDialog dialog = new JDialog(this, "검색 결과", true);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 이모티콘 전송 메서드
    private void sendEmoji(int emojiNumber) {
        groupChatBack.sendMessage(new MessageDTO(MessageType.EMOJI, nickName + ":" + emojiNumber));
    }

    public void appendEmoji(int emojiIndex, String sender) {
        StyledDocument doc = chatPane.getStyledDocument();

        if (sender.equals(nickName)) sender = "나";

        Style style = chatPane.addStyle("EmojiStyle", null);
        boolean isMine = sender.equals("나");

        setStyleIsMine(style, isMine);

        try {
            if (!isMine) {
                doc.insertString(doc.getLength(), "[" + sender + "]: ", style);
            }

            // 이모지 추가 (이미지 삽입)
            String emojiPath = "src/Function/EMOJI/" + emojiIndex + ".jpg"; // 이모지 이미지 경로
            ImageIcon emojiIcon = ResizeImage.resizeImage(emojiPath, 128, 128);

            if (isMine) {
                doc.insertString(doc.getLength(), "" , style);
            }

            chatPane.setCaretPosition(doc.getLength());
            chatPane.insertIcon(emojiIcon);

            if (isMine) {
                doc.insertString(doc.getLength(), "<- [나]" , style);
            }

            // 줄바꿈
            doc.insertString(doc.getLength(), "\n", style);
            doc.setParagraphAttributes(doc.getLength() - 1, 1, style, false);
        } catch (BadLocationException e) {
            System.err.println(e.getMessage());
        }
    }

    public void openEmojiSelector() {
        JFrame emojiFrame = new JFrame("이모티콘 선택");
        emojiFrame.setSize(400, 300);
        emojiFrame.setLayout(new GridLayout(2, 3));

        SwingCompFunc.setFrameStyle(emojiFrame);

        // 이모티콘 번호와 이미지를 매핑
        Map<Integer, String> emojiMap = Map.of(
                1, "src/Function/EMOJI/1.jpg",
                2, "src/Function/EMOJI/2.jpg",
                3, "src/Function/EMOJI/3.jpg",
                4, "src/Function/EMOJI/4.jpg",
                5, "src/Function/EMOJI/5.jpg",
                6, "src/Function/EMOJI/6.jpg"
        );

        // 이미지 버튼 생성
        for (Map.Entry<Integer, String> entry : emojiMap.entrySet()) {
            int emojiNumber = entry.getKey();
            String imagePath = entry.getValue();

            ImageIcon imageIcon = ResizeImage.resizeImage(imagePath, 128, 128);

            JButton emojiButton = new JButton(imageIcon);
            emojiButton.addActionListener(e -> {
                // 선택된 이미지 번호 전송
                sendEmoji(emojiNumber);

                // 프레임 닫기
                emojiFrame.dispose();
            });
            emojiFrame.add(emojiButton);
        }

        emojiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        emojiFrame.setVisible(true);
    }
}
