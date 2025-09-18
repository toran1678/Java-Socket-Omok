package OmokGame.Panel;

import ChatApp.Client.ClientApplication;
import Data.Data;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import Function.ImageLoad;
import Function.SwingCompFunc.SwingCompFunc;
import OmokGame.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serial;

public class RightPanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;

    JLabel myProfileLabel = new JLabel();
    JLabel opponentProfileLabel = new JLabel();
    JLabel myReadyLabel = new JLabel("상태: 대기 중");
    JLabel opponentReadyLabel = new JLabel("상태: 대기 중");
    JList<String> playerList = new JList<>();
    JTextField chatInputField;
    ClientApplication clientApplication;
    JTextArea chatArea = new JTextArea();
    JScrollPane chatScroll = new JScrollPane(chatArea);
    GameFrame gameFrame;
    JButton sendButton = new JButton("전송");

    public RightPanel(ClientApplication clientApplication, GameFrame gameFrame) {
        this.clientApplication = clientApplication;
        this.gameFrame = gameFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // 세로로 정렬

        // 상단에 플레이어 프로필
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridLayout(1, 2)); // 2개의 프로필
        profilePanel.setBorder(BorderFactory.createTitledBorder("플레이어 프로필"));

        JPanel myProfilePanel = new JPanel();
        myProfilePanel.setLayout(new BoxLayout(myProfilePanel, BoxLayout.Y_AXIS));
        myProfileLabel.setPreferredSize(new Dimension(100, 100));
        //myProfileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        myProfileLabel.setBorder(BorderFactory.createTitledBorder("[ Empty ]"));
        myReadyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //myReadyLabel.setVerticalAlignment(SwingConstants.CENTER);
        ImageLoad.setImageIcon(myProfileLabel,
                "src/Function/ProfileCharacterSelector/Img/11.jpg");

        myProfilePanel.add(myProfileLabel);
        myProfilePanel.add(myReadyLabel);

        JPanel opponentProfilePanel = new JPanel();
        opponentProfilePanel.setLayout(new BoxLayout(opponentProfilePanel, BoxLayout.Y_AXIS));
        opponentProfileLabel.setPreferredSize(new Dimension(100, 100));
        //opponentProfileLabel.setHorizontalAlignment(SwingConstants.CENTER);
        opponentProfileLabel.setBorder(BorderFactory.createTitledBorder("[ Empty ]"));
        opponentReadyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        //opponentReadyLabel.setVerticalAlignment(SwingConstants.CENTER);
        ImageLoad.setImageIcon(opponentProfileLabel,
                "src/Function/ProfileCharacterSelector/Img/11.jpg");

        opponentProfilePanel.add(opponentProfileLabel);
        opponentProfilePanel.add(opponentReadyLabel);

        profilePanel.add(myProfilePanel);
        profilePanel.add(opponentProfilePanel);

        // 플레이어 목록과 도전하기 버튼 패널
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setBorder(BorderFactory.createTitledBorder("플레이어 목록"));

        // 플레이어 목록
        JScrollPane playerListScroll = new JScrollPane(playerList);
        /* 크기 줄이기 */
        playerListScroll.setPreferredSize(new Dimension(200, 100));
        playerPanel.add(playerListScroll, BorderLayout.CENTER);

        // 도전하기 버튼
        JButton readyButton = new JButton("준비하기");
        SwingCompFunc.setButtonStyle(readyButton);
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonContainer.add(readyButton);
        playerPanel.add(buttonContainer, BorderLayout.SOUTH);

        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel reviewLabel = new JLabel("  0 / 0  ");

        JButton undoBtn = new JButton("<");
        SwingCompFunc.setButtonStyle(undoBtn);

        JButton redoBtn = new JButton(">");
        SwingCompFunc.setButtonStyle(redoBtn);

        reviewPanel.add(undoBtn);
        reviewPanel.add(reviewLabel);
        reviewPanel.add(redoBtn);

        // 채팅 영역
        chatArea.setEditable(false);
        chatArea.setFont(new Font("나눔고딕", Font.PLAIN, 14));
        // 줄바꿈
        chatArea.setLineWrap(true);
        // 단어 단위로 바꿈
        chatArea.setWrapStyleWord(true);

        chatScroll.setBorder(BorderFactory.createTitledBorder("채팅"));
        chatScroll.setPreferredSize(new Dimension(200, 130));

        JPanel chatInputPanel = new JPanel(new BorderLayout());
        chatInputField = new JTextField();

        SwingCompFunc.setButtonStyle(sendButton);

        chatInputPanel.add(chatInputField, BorderLayout.CENTER);
        chatInputPanel.add(sendButton, BorderLayout.EAST);

        // 나가기, 다시하기, 준비 버튼
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton surrenderButton = new JButton("항복");
        JButton retryButton = new JButton("화면 초기화");
        JButton exitButton = new JButton("나가기");

        SwingCompFunc.setButtonStyle(surrenderButton);
        SwingCompFunc.setButtonStyle(retryButton);
        SwingCompFunc.setButtonStyle(exitButton);

        buttonPanel.add(surrenderButton);
        buttonPanel.add(retryButton);
        buttonPanel.add(exitButton);

        /* 유저 리스트 팝업 */
        JPopupMenu userPopupMenu = new JPopupMenu();
        JMenuItem infoMenuItem = new JMenuItem("정보 확인");
        userPopupMenu.add(infoMenuItem);
        playerList.add(userPopupMenu);

        // 오른쪽 패널에 컴포넌트 추가
        add(profilePanel);
        add(playerPanel);
        add(chatScroll);
        add(chatInputPanel);
        add(reviewPanel);
        add(buttonPanel);

        playerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int index = playerList.locationToIndex(e.getPoint());
                    playerList.setSelectedIndex(index);  // 선택한 유저를 설정
                    userPopupMenu.show(playerList, e.getX(), e.getY());  // 팝업 메뉴 표시
                }
            }
        });

        /* 유저 정보 팝업 */
        infoMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedUser = playerList.getSelectedValue();
                if (selectedUser != null) {
                    getUserinfo(selectedUser);
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });

        chatInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChatMessage();
            }
        });

        surrenderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* 관전자일 경우 */
                if (Data.observer) {
                    JOptionPane.showMessageDialog(null, "당신은 관전자입니다.");
                }

                /* 게임이 시작되었을 때 */
                if (Data.started) {
                    /* 항복 여부 확인 */
                    int value = JOptionPane.showConfirmDialog(gameFrame, "게임이 아직 끝나지 않았습니다. 항복하시겠습니까?", "항복",
                            JOptionPane.YES_NO_OPTION);
                    // YES 선택
                    if (value == JOptionPane.YES_OPTION) {
                        // 항복처리 해야댐 메시지랑 서버
                        JOptionPane.showMessageDialog(null, "항복하셨습니다.");

                        clientApplication.sendMessage(new MessageDTO(MessageType.OmokSurrender,
                                clientApplication.getNickName() + ":" + gameFrame.getRoomName()));

                        Data.initData();

                        clientApplication.resetCanvas();
                    }
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* 관전자일 경우 */
                if (Data.observer) {
                    String userState = "OBSERVER";
                    clientApplication.sendMessage(new MessageDTO(MessageType.OmokLeaveRoom,
                            clientApplication.getNickName() + ":" + gameFrame.getRoomName() + ":" + userState));

                    Data.initData();

                    clientApplication.resetCanvas();
                    gameFrame.dispose();
                }

                /* 게임이 시작되었을 때 */
                if (Data.started) {
                    /* 항복 여부 확인 */
                    int value = JOptionPane.showConfirmDialog(gameFrame, "게임이 아직 끝나지 않았습니다. 항복하시겠습니까?", "항복",
                            JOptionPane.YES_NO_OPTION);
                    // YES 선택
                    if (value == JOptionPane.YES_OPTION) {
                        // 항복처리 해야댐 메시지랑 서버
                        JOptionPane.showMessageDialog(null, "항복하셨습니다.");
                        clientApplication.sendMessage(new MessageDTO(MessageType.OmokLeaveRoom,
                                clientApplication.getNickName() + ":" + gameFrame.getRoomName() + ":SURRENDER"));

                        Data.initData();

                        clientApplication.resetCanvas();
                        gameFrame.dispose();
                    }
                } else {
                    String userState = "PLAYER";

                    clientApplication.sendMessage(new MessageDTO(MessageType.OmokLeaveRoom,
                            clientApplication.getNickName() + ":" + gameFrame.getRoomName() + ":" + userState));

                    Data.initData();

                    clientApplication.resetCanvas();
                    gameFrame.dispose();
                }
            }
        });

        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Data.started) {
                    BoardCanvas mapCanvas = gameFrame.getGamePanel().getBoardCanvas();
                    mapCanvas.paintBoardImage();
                    mapCanvas.repaint();
                }
            }
        });

        gameFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                String userState = "PLAYER";
                if (Data.observer) {
                    userState = "OBSERVER";
                } else if (Data.started) {
                    userState = "SURRENDER";
                    JOptionPane.showMessageDialog(null, "항복하셨습니다.");
                }
                clientApplication.sendMessage(new MessageDTO(MessageType.OmokLeaveRoom,
                        clientApplication.getNickName() + ":" + gameFrame.getRoomName() + ":" + userState));
                gameFrame.dispose();
            }
        });

        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Data.started) {
                    clientApplication.sendMessage(new MessageDTO(
                            MessageType.OmokReady, gameFrame.roomName + ":" + clientApplication.getNickName()
                    ));

                    BoardCanvas mapCanvas = gameFrame.getGamePanel().getBoardCanvas();
                    mapCanvas.paintBoardImage();
                    mapCanvas.repaint();
                }
            }
        });
    }

    public void getUserinfo(String selectedValue) {
        clientApplication.sendMessage(new MessageDTO(MessageType.ShowUserInfo, selectedValue));
    }

    private void sendChatMessage() {
        String message = chatInputField.getText().trim();
        if (!message.isEmpty()) {
            clientApplication.sendMessage(new MessageDTO(MessageType.OmokChat,
                    gameFrame.roomName + ":" + clientApplication.getNickName() + ":" + message));
            chatInputField.setText("");
        }
    }

    public void displayMessage(String message) {
        chatArea.append(message + "\n");

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScroll.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void setReadyLabel(boolean myReady, boolean oppoReady) {
        if (myReady && oppoReady) {
            myReadyLabel.setText("상태: 게임 중");
            opponentReadyLabel.setText("상태: 게임 중");
            return;
        }

        if (myReady) {
            myReadyLabel.setText("상태: 준비 완료");
        } else {
            myReadyLabel.setText("상태: 대기 중");
        }

        if (oppoReady) {
            opponentReadyLabel.setText("상태: 준비 완료");
        } else {
            opponentReadyLabel.setText("상태: 대기 중");
        }
    }

    public JList<String> getPlayerList() {
        return playerList;
    }

    public JLabel getMyProfileLabel() {
        return myProfileLabel;
    }

    public JLabel getOpponentProfileLabel() {
        return opponentProfileLabel;
    }
}
