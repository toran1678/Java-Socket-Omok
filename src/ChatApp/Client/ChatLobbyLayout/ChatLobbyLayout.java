package ChatApp.Client.ChatLobbyLayout;

import Function.Music.BackgroundMusicPlayer;
import ChatApp.Client.OmokRankingLayout.OmokRankingFrame;
import ChatApp.Client.UserProfilePopup.UserProfilePopup;
import Database.UserInfo.UserInfo;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import Database.Database;
import ChatApp.Client.ClientApplication;
import ChatApp.Client.UserProfileEditLayout.UserProfileEditLayout;
import Function.ImageLoad;
import Function.SwingCompFunc.BubblePanel;
import Function.SwingCompFunc.SwingCompFunc;
import ChatApp.Server.ServerInfo.ServerInfo;
import Function.Image.ResizeImage;
import Function.WeatherSearch.WeatherSearchPanel;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;

public class ChatLobbyLayout extends JFrame {
    Database db = new Database();

    /* ------------ GUI ------------ */
    /* Panels */
    JPanel mainPanel = new JPanel();
    JPanel showChatPanel = new JPanel();
    JPanel userPanel = new JPanel();
    JPanel roomPanel = new JPanel();
    JPanel topPanel = new JPanel();
    JPanel chatPanel = new JPanel();
    JPanel weatherPanel = new JPanel();
    WeatherSearchPanel weatherSearchPanel = new WeatherSearchPanel();
    JPanel buttonPanel = new JPanel();
    
    /* Components */
    // JTextArea chatArea = new JTextArea();
    JTextPane chatPane = new JTextPane();
    JPanel chatPanel2 = new JPanel();
    JScrollPane chatScrollPane = new JScrollPane(chatPanel2);
    JTextField chatTextField = new JTextField();
    public JList<String> userList = new JList<String>();
    public JList<String> roomList = new JList<String>();
    public DefaultListModel<String> omokModel;
    public JList<String> omokRoomList = new JList<>();

    /* Labels */
    JLabel iconLabel = new JLabel();
    JLabel welcomeLabel = new JLabel("~님 어서오세요");
    JLabel userListLabel = new JLabel("접속중인 유저");
    JLabel roomListLabel = new JLabel("생성된 방");

    /* Buttons */
    JButton sendBtn = new JButton(">");
    JButton exitBtn = new JButton("종료");
    JButton createRoomBtn = new JButton("방 생성");
    JButton emoticonBtn = new JButton("㋡");
    JButton userRankingBtn = new JButton("유저 랭킹");
    JButton darkModeBtn = new JButton("다크 모드");
    JButton musicPlayBtn = new JButton("노래 재생");
    boolean isDarkMode = false;
    /* ------------ GUI ------------ */

    /* ------------ 기능 ------------ */
    ClientApplication clientApplication = new ClientApplication();
    String nickName;
    public DefaultListModel<String> roomModel;
    public DefaultListModel<String> userModel;
    public String getNickName() { return this.nickName; }

    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    /* Popup Menu */
    JPopupMenu pm = new JPopupMenu();
    JMenuItem pmItem1 = new JMenuItem("방 입장");
    JMenuItem pmItem2 = new JMenuItem("방 제거");
    int currentY = 10;

    JPopupMenu userPopupMenu = new JPopupMenu();
    JMenuItem infoMenuItem = new JMenuItem("정보 확인");
    JMenuItem whisperMenuItem = new JMenuItem("귓속말 보내기");
    JMenuItem oneToOneChatMenuItem = new JMenuItem("1대1 채팅");
    JMenuItem userProfileItem = new JMenuItem("프로필 보기");

    JPopupMenu chatPopupMenu = new JPopupMenu();
    JMenuItem searchTextItem = new JMenuItem("메시지 검색");

    /* Border */
    EtchedBorder etchedBorder = new EtchedBorder(EtchedBorder.LOWERED, null, null);

    private BackgroundMusicPlayer musicPlayer = new BackgroundMusicPlayer();
    private boolean isMusicPlaying = false; // 음악 상태

    /* ------------ 기능 ------------ */

    public ChatLobbyLayout(String nickName, String ipAddress, int portNum) {
        this.nickName = nickName;
        welcomeLabel.setText(nickName + "님 어서오세요!");

        setContentPane(mainPanel);
        mainPanel.setBackground(new Color(153, 204, 255));
        mainPanel.setLayout(null);
        setSize(805, 620);
        setTitle("Chat");

        userModel = new DefaultListModel<String>();
        roomModel = new DefaultListModel<String>();

        userList.setModel(userModel);
        roomList.setModel(roomModel);

        omokModel = new DefaultListModel<String>();
        omokRoomList.setModel(omokModel);

        setIconImage(gameIcon.getImage());

        /* 채팅 패널 */
        chatPanel2.setLayout(null);
        chatPanel2.setPreferredSize(new Dimension(460, 460));
        chatPanel2.setBackground(SwingCompFunc.LobbyPanelColor);

        chatScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        /* 읽기만 */
        // chatArea.setEditable(false);
        chatPane.setEditable(false);
        // chatPane.setContentType("text/html");

        /* setBorder */
        showChatPanel.setBorder(etchedBorder);
        // chatArea.setBorder(etchedBorder);
        chatPane.setBorder(etchedBorder);
        userPanel.setBorder(etchedBorder);
        userList.setBorder(etchedBorder);
        roomPanel.setBorder(etchedBorder);
        roomList.setBorder(etchedBorder);
        omokRoomList.setBorder(etchedBorder);
        topPanel.setBorder(etchedBorder);
        weatherPanel.setBorder(etchedBorder);
        buttonPanel.setBorder(etchedBorder);
        
        /* setFont */
        welcomeLabel.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 20));
        exitBtn.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 14));
        createRoomBtn.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 14));
        // chatArea.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        chatPane.setFont(new Font("나눔고딕", Font.PLAIN, 16));
        
        /* setBackground */
        showChatPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        userPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        roomPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        topPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        chatPanel.setBackground(SwingCompFunc.LobbyMainColor);
        exitBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        createRoomBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        emoticonBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        userRankingBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        darkModeBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        musicPlayBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        sendBtn.setBackground(SwingCompFunc.LobbyButtonColor);

        weatherPanel.setBackground(new Color(255, 255, 255, 0));
        buttonPanel.setBackground(new Color(255, 255, 255, 0));

        JScrollPane userListScrollPane = new JScrollPane(userList);
        JScrollPane roomListScrollPane = new JScrollPane(roomList);
        JScrollPane omokRoomListScrollPane = new JScrollPane(omokRoomList);

        /* setBound  */
        topPanel.setBounds(10, 10, 480, 40);
        iconLabel.setBounds(5, 5, 30, 30);
        welcomeLabel.setBounds(50, 5, 420, 30);
        showChatPanel.setBounds(10, 60, 480, 460);
        chatScrollPane.setBounds(0, 0, 480, 460);
        userPanel.setBounds(510, 40, 270, 130);
        userListScrollPane.setBounds(0, 0, 270, 130);
        roomPanel.setBounds(510, 205, 270, 130);
        //roomListScrollPane.setBounds(0, 0, 270, 130);
        omokRoomListScrollPane.setBounds(0, 0, 270, 130);
        weatherPanel.setBounds(510, 345, 270, 130);
        buttonPanel.setBounds(510, 480, 270, 40);
        userListLabel.setBounds(510, 20, 120, 15);
        roomListLabel.setBounds(510, 185, 120, 15);
        chatPanel.setBounds(10, 520, 770, 50);
        chatTextField.setBounds(0, 10, 420, 40);
        sendBtn.setBounds(430, 10, 50, 40);
        exitBtn.setBounds(685, 10, 85, 40);
        createRoomBtn.setBounds(550, 10, 125, 40);
        emoticonBtn.setBounds(490, 10, 50, 40);

        userRankingBtn.setPreferredSize(new Dimension(85, 35));
        darkModeBtn.setPreferredSize(new Dimension(85, 35));
        musicPlayBtn.setPreferredSize(new Dimension(85, 35));

        userPanel.add(userListScrollPane);
        // roomPanel.add(roomListScrollPane);
        roomPanel.add(omokRoomListScrollPane);

        String iconPath = "src/ChatApp/Client/ChatLobbyLayout/icon.png";
        iconLabel.setIcon(ImageLoad.getImageIcon(iconLabel, iconPath));
        
        /* setLayout */
        showChatPanel.setLayout(null);
        userPanel.setLayout(null);
        roomPanel.setLayout(null);
        topPanel.setLayout(null);
        chatPanel.setLayout(null);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
        // buttonPanel.setLayout(new GridLayout(2, 1, 0, 0));

        /* add Panel */
        /* Top Panel */
        getContentPane().add(topPanel);
        topPanel.add(iconLabel);
        topPanel.add(welcomeLabel);
        /* 채팅 화면 */
        getContentPane().add(showChatPanel);
        showChatPanel.add(chatScrollPane);
        /* 유저 리스트 */
        // userPanel.add(userList);
        getContentPane().add(userPanel);
        getContentPane().add(userListLabel);
        /* 방 리스트 */
        // roomPanel.add(roomList);
        getContentPane().add(roomPanel);
        getContentPane().add(roomListLabel);
        /* Chat Panel */
        getContentPane().add(chatPanel);
        chatPanel.add(chatTextField);
        chatPanel.add(sendBtn);
        chatPanel.add(exitBtn);
        chatPanel.add(createRoomBtn);
        chatPanel.add(emoticonBtn);
        /* Weather Panel */
        getContentPane().add(weatherPanel);
        weatherPanel.add(weatherSearchPanel);
        /* Button Panel */
        getContentPane().add(buttonPanel);
        buttonPanel.add(userRankingBtn);
        buttonPanel.add(darkModeBtn);
        buttonPanel.add(musicPlayBtn);

        setVisible(true);

        /* 기능 */
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //UserProfileEdit.showLayout();
                new UserProfileEditLayout(nickName, clientApplication).showLayout();
            }
        });

        /* 채팅 텍스트 필드 이벤트 */
        chatTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Message = chatTextField.getText().trim();
                if (!Message.isEmpty()) {
                    clientApplication.sendMessage(new MessageDTO(MessageType.CHAT, "[" + nickName + "]: " + Message));
                    chatTextField.setText(null);
                }
            }
        });

        sendBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Message = chatTextField.getText().trim();
                if (e.getSource() == sendBtn && !Message.isEmpty()) {
                    clientApplication.sendMessage(new MessageDTO(MessageType.CHAT, "[" + nickName + "]: " + Message));
                    chatTextField.setText(null);
                }
            }
        });

        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApplication();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeApplication();
            }
        });

        createRoomBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String room = JOptionPane.showInputDialog("이름을 입력하세요.");
                if (room != null) {
                    clientApplication.sendMessage(new MessageDTO(MessageType.OmokCreateRoom, nickName + ":" + room));

                    // 원래는 그룹 채팅
                    // clientApplication.sendMessage(new MessageDTO(MessageType.CreateRoom, ));
                }
            }
        });

        /* 채팅 영역 팝업 */
        chatPopupMenu.add(searchTextItem);

        chatPane.add(chatPopupMenu);
        chatPane.setComponentPopupMenu(chatPopupMenu);

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
                    clientApplication.sendMessage(searchRequest);
                }
            }
        });

        /* 유저 리스트 팝업 */
        userPopupMenu.add(infoMenuItem);
        userPopupMenu.add(whisperMenuItem);
        userPopupMenu.add(oneToOneChatMenuItem);
        userPopupMenu.add(userProfileItem);
        userList.add(userPopupMenu);

        userModel = new DefaultListModel<>();
        userList.setModel(userModel);

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

                if (selectedUser == null) {
                    return;
                }

                if (selectedUser.equals(nickName)) {
                    JOptionPane.showMessageDialog(null, "올바른 유저를 선택하세요.");
                    return;
                }

                // 귓속말 메시지 입력을 위한 다이얼로그
                String message = JOptionPane.showInputDialog("귓속말을 입력하세요:");
                if (message != null && !message.trim().isEmpty()) {
                    clientApplication.sendMessage(new MessageDTO(MessageType.Whisper,
                            nickName + ":" + selectedUser + ":" + message));
                }
            }
        });
        userProfileItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = userList.getSelectedValue();
                if (selectedUser == null) {
                    return;
                }

                UserInfo userInfo = db.getUserInfo(selectedUser);
                new UserProfilePopup(clientApplication, userInfo.getName(),
                        userInfo.getImage(), userInfo.getProfileCharacter(), userInfo).setVisible(true);
            }
        });

        /* 1대1 채팅 옵션 */
        oneToOneChatMenuItem.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null && !selectedUser.equals(nickName)) {
                clientApplication.requestOneToOneChat(selectedUser);
            } else {
                JOptionPane.showMessageDialog(this, "올바른 유저를 선택하세요.");
            }
        });

        roomModel = new DefaultListModel<>();
        roomList.setModel(roomModel);
        roomList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    clientApplication.sendMessage(new MessageDTO(MessageType.EnterRoom, nickName + ":" + roomList.getSelectedValue()));
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    pm.show(roomList, e.getX(), e.getY());
                }
            }
        });

        pm.add(pmItem1);
        pmItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (roomList.getSelectedValue() != null) {
                    clientApplication.sendMessage(new MessageDTO(MessageType.EnterRoom, nickName + ":" + roomList.getSelectedValue()));
                } else {
                    JOptionPane.showMessageDialog(null, "선택 후 우클릭 해주세요.");
                }
            }
        });

        pm.add(pmItem2);
        pmItem2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (roomList.getSelectedValue() != null) {
                    clientApplication.sendMessage(new MessageDTO(MessageType.RemoveRoom, roomList.getSelectedValue()));
                } else {
                    JOptionPane.showMessageDialog(null, "선택 후 우클릭 해주세요.");
                }
            }
        });
        this.add(pm);

        emoticonBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEmojiSelector();
            }
        });

        /* 랭킹 확인 버튼 */
        userRankingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OmokRankingFrame().setVisible(true);
            }
        });

        /* 다크 모드 버튼 */
        darkModeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDarkMode) {
                    SwingCompFunc.setDefaultMode();
                    applyDarkMode();
                    darkModeBtn.setText("다크 모드");
                } else {
                    SwingCompFunc.setDarkMode();
                    applyDarkMode();
                    darkModeBtn.setText("기본 모드");
                }
                isDarkMode = !isDarkMode;
                repaint();
            }
        });

        /* 배경 음악 재생 버튼 */
        musicPlayBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isMusicPlaying) {
                    musicPlayer.stopMusic();
                    musicPlayBtn.setText("노래 재생");
                } else {
                    musicPlayer.playMusic("src/Function/Music/wewishuamerrychrismas.wav"); // 파일 경로
                    musicPlayBtn.setText("노래 정지");
                }
                isMusicPlaying = !isMusicPlaying;
            }
        });

        clientApplication.setGui(this);
        clientApplication.setUserInfo(nickName, ipAddress, portNum);
        clientApplication.start();

        /* 오목 방 리스트 이벤트 */
        omokRoomList.addMouseListener(new OmokRoomListMouseListener(omokRoomList, nickName, clientApplication));
    }

    public JFrame getClientFrame() {
        return this;
    }

    public void appendMessage(String message) {
        boolean isMine = message.split(":")[0].startsWith("[" + nickName + "]"); // 발신자 확인
        String time = getCurrentTime(); // 현재 시간 가져오기

        // 말풍선 패널 생성
        BubblePanel bubble = new BubblePanel(message, time, isMine);

        // 말풍선 위치 계산 및 추가
        int panelWidth = chatPanel2.getWidth();
        int bubbleWidth = bubble.getPreferredSize().width;
        int x = isMine ? panelWidth - bubbleWidth - 20 : 20; // 오른쪽 또는 왼쪽 정렬

        /// 말풍선 추가
        bubble.setBounds(x, currentY, bubbleWidth, bubble.getPreferredSize().height);
        chatPanel2.add(bubble);

        // 다음 말풍선을 위한 Y 좌표 갱신
        currentY += bubble.getPreferredSize().height + 10;

        // 채팅 패널 크기 갱신
        chatPanel2.setPreferredSize(new Dimension(chatPanel2.getWidth(), currentY));
        chatPanel2.revalidate();
        chatPanel2.repaint();

        // 스크롤을 맨 아래로 이동
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void applyDarkMode() {
        // 패널 색상 변경
        mainPanel.setBackground(SwingCompFunc.LobbyMainColor);
        showChatPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        userPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        roomPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        topPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        chatPanel.setBackground(SwingCompFunc.LobbyMainColor);
        // buttonPanel.setBackground(SwingCompFunc.LobbyPanelColor);
        chatPanel2.setBackground(SwingCompFunc.LobbyPanelColor);
        chatPane.setBackground(SwingCompFunc.LobbyPanelColor);
        JPanel internalPanel = weatherSearchPanel.getInternalPanel();
        if (internalPanel != null) {
            internalPanel.setBackground(SwingCompFunc.LobbyWeatherColor);
            for (Component comp : internalPanel.getComponents()) {
                if (comp instanceof JLabel) {
                    comp.setForeground(Color.WHITE);
                } else if (comp instanceof JButton) {
                    ((JButton) comp).setBackground(SwingCompFunc.LobbyWeatherBtnColor);
                }
            }
        }

        // 버튼 색상 변경
        exitBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        createRoomBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        emoticonBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        userRankingBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        darkModeBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        musicPlayBtn.setBackground(SwingCompFunc.LobbyButtonColor);
        sendBtn.setBackground(SwingCompFunc.LobbyButtonColor);

        // 스크롤 관련 색상 변경
        chatScrollPane.getViewport().setBackground(SwingCompFunc.LobbyMainColor);
        chatTextField.setBackground(SwingCompFunc.LobbyPanelColor);
        userList.setBackground(SwingCompFunc.LobbyPanelColor);
        roomList.setBackground(SwingCompFunc.LobbyPanelColor);
        omokRoomList.setBackground(SwingCompFunc.LobbyPanelColor);
    }

    /* 현재 시간 가져오기 */
    public String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    /* 스타일 설정 */
    public void setStyleIsMine(Style style, boolean isMine) {
        if (isMine) {
            // 내 메시지 스타일 (오른쪽 정렬, 초록색 배경)
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
            StyleConstants.setForeground(style, new Color(76, 175, 80)); // 텍스트 초록색
            StyleConstants.setBackground(style, new Color(232, 245, 233)); // 배경 연한 초록색
            StyleConstants.setFontSize(style, 14);
        } else {
            // 상대 메시지 스타일 (왼쪽 정렬, 파란색 배경)
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
            StyleConstants.setForeground(style, new Color(33, 150, 243)); // 텍스트 파란색
            StyleConstants.setBackground(style, new Color(227, 242, 253)); // 배경 연한 파란색
            StyleConstants.setFontSize(style, 14);
        }
    }

    public void resetRoomList(ArrayList<String> roomNameList) {
        /* 리스트 업데이트 시 "ScrollPane"에 문제가 생기는 것 같아서 추가 */
        ArrayList<String> safeRoomList = new ArrayList<>(roomNameList);

        SwingUtilities.invokeLater(() -> {
            roomModel.removeAllElements();
            roomList.removeAll();
            roomModel.clear();

            if (safeRoomList.size() == 1 && safeRoomList.get(0).equals("EMPTY")) {
                // 방 목록이 비어있는 경우
                return;
            }

            for (String roomName : safeRoomList) {
                //roomModel.addElement(roomName);
                addRoom(roomName);
            }

            roomList.revalidate();
            roomList.repaint();
        });
    }

    public void omokResetRoomList(ArrayList<String> roomNameList) {
        ArrayList<String> safeRoomList = new ArrayList<>(roomNameList);

        SwingUtilities.invokeLater(() -> {
            omokModel.removeAllElements();
            omokRoomList.removeAll();
            omokModel.clear();

            if (safeRoomList.isEmpty()) {
                return;
            }

            for (String roomName : safeRoomList) {
                if (!omokModel.contains(roomName)) {
                    omokModel.addElement(roomName);
                }
            }
            omokRoomList.setModel(omokModel);

            omokRoomList.revalidate();
            omokRoomList.repaint();
        });
    }

    public JList<String> getOmokRoomList() {
        return omokRoomList;
    }

    public void resetUserList(ArrayList<String> nickNameList) {
        /* 리스트 업데이트 시 "ScrollPane"에 문제가 생기는 것 같아서 추가 */
        ArrayList<String> safeNickNameList = new ArrayList<>(nickNameList);

        SwingUtilities.invokeLater(() -> {
            userModel.removeAllElements();
            userList.removeAll();

            userModel.clear();
            for (String userName : safeNickNameList) {
                //userModel.addElement(userName);
                addUser(userName);
            }
            userList.setModel(userModel);
            userList.repaint();
        });
    }

    public void addUser(String userName) {
        if(!userModel.contains(userName)) {
            userModel.addElement(userName);
        }
    }

    public void addRoom(String roomName) {
        if (!roomModel.contains(roomName)) {
            roomModel.addElement(roomName);
        }
    }

    public void getUserinfo(String selectedValue) {
        clientApplication.sendMessage(new MessageDTO(MessageType.ShowUserInfo, selectedValue));
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

    private void closeApplication() {
        try {
            if (clientApplication.socket != null && !clientApplication.socket.isClosed()) {
                clientApplication.socket.close();
            }
        } catch (IOException e) {
            System.err.println("소켓 닫기 중 오류 발생: " + e.getMessage());
        }
        dispose();
    }

    /* 이미지 전송 */
    public void openEmojiSelector() {
        JFrame emojiFrame = new JFrame("이모티콘 선택");
        emojiFrame.setSize(400, 300);
        emojiFrame.setLayout(new GridLayout(2, 3));
        emojiFrame.setLocationRelativeTo(this);

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

    // 이모티콘 전송 메서드
    private void sendEmoji(int emojiNumber) {
        clientApplication.sendMessage(new MessageDTO(MessageType.EMOJI, nickName + ":" + emojiNumber));
    }

    public void appendEmoji(int emojiIndex, String sender) {
        boolean isMine = sender.equals(nickName);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // 이모티콘 경로
        String emojiPath = "src/Function/EMOJI/" + emojiIndex + ".jpg";
        ImageIcon emojiIcon = ResizeImage.resizeImage(emojiPath, 48, 48); // 이모티콘 크기 조정

        // 말풍선 패널 생성
        BubblePanel bubble = new BubblePanel(emojiIcon, time, isMine);

        // 말풍선 위치 계산
        int panelWidth = chatPanel2.getWidth();
        int bubbleWidth = bubble.getPreferredSize().width;
        int x = isMine ? panelWidth - bubbleWidth - 20 : 20; // 오른쪽 또는 왼쪽 정렬

        // 말풍선 추가
        bubble.setBounds(x, currentY, bubbleWidth, bubble.getPreferredSize().height);
        chatPanel2.add(bubble);

        // 다음 말풍선을 위한 Y 좌표 갱신
        currentY += bubble.getPreferredSize().height + 10;

        // 패널 크기와 스크롤 갱신
        chatPanel2.setPreferredSize(new Dimension(chatPanel2.getWidth(), currentY));
        chatPanel2.revalidate();
        chatPanel2.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public static void main(String[] args) {
        new ChatLobbyLayout("user1", ServerInfo.IPNUMBER,  ServerInfo.PORTNUMBER);
    }
}