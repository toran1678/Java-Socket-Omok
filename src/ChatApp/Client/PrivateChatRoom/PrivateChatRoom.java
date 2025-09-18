package ChatApp.Client.PrivateChatRoom;

import ChatApp.Client.ClientApplication;
import Database.Database;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import Function.Image.ResizeImage;
import Function.SwingCompFunc.BubblePanel;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class PrivateChatRoom extends JFrame {
    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");
    private String nickname;
    private String roomName;
    private final ClientApplication client;
    private String oppo;
    JTextPane chatPane = new JTextPane();
    JPanel chatPanel = new JPanel();
    JScrollPane chatScrollPane = new JScrollPane(chatPanel);
    JTextField inputField;
    JButton sendButton = new JButton("전송");
    JButton emojiButton = new JButton("㋡");
    JButton paletteButton = new JButton("🎨");
    JButton fileSendButton = new JButton("📎");
    JButton inviteButton = new JButton("초대");
    JLabel titleLabel = new JLabel();
    JPanel topPanel = new JPanel();
    ArrayList<String> loadMessages;

    int currentY = 10;

    public PrivateChatRoom(String user, String roomName, ClientApplication client, ArrayList<String> messages, String oppo) {
        this.nickname = user;
        this.roomName = roomName;
        this.client = client;
        this.loadMessages = messages;
        this.oppo = oppo;
        initUI();
    }

    public static void main(String[] args) {
        new PrivateChatRoom("toran", "test", new ClientApplication(), null, "oppo").setVisible(true);
    }

    public String getRoomName() { return this.roomName; }

    private void initUI() {
        setTitle("1대1 채팅");
        setSize(600, 600);
        setLayout(new BorderLayout());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        setIconImage(gameIcon.getImage());

        /* 채팅 패널 */
        chatPanel.setLayout(null);
        chatPanel.setPreferredSize(new Dimension(460, 460));
        chatPanel.setBackground(Color.WHITE);

        titleLabel.setText(roomName);
        // topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        // topPanel.setBackground(new Color(153, 204, 255));

        SwingCompFunc.setTopPanelStyle(topPanel);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(0, 40));

        topPanel.add(titleLabel);

        chatPane.setEditable(false);
        chatPane.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 입력 및 전송 버튼 설정
        inputField = new JTextField();
        inputField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        SwingCompFunc.setButtonStyle(sendButton);
        SwingCompFunc.setButtonStyle(emojiButton);
        SwingCompFunc.setButtonStyle(paletteButton);
        SwingCompFunc.setButtonStyle(fileSendButton);
        SwingCompFunc.setButtonStyle(inviteButton);

        // 전송 버튼 및 입력 필드 이벤트 설정
        ActionListener sendMessageAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText().trim();
                if (!message.isEmpty()) {
                    client.sendMessage(new MessageDTO(MessageType.PrivateChat, roomName + ":" + nickname + ":" + message));
                    inputField.setText("");
                }
            }
        };
        inputField.addActionListener(sendMessageAction);
        sendButton.addActionListener(sendMessageAction);

        JPanel buttonPanel = new JPanel(); // 버튼을 나란히 배치할 패널 생성
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT)); // 버튼을 오른쪽 정렬

        buttonPanel.add(sendButton);
        buttonPanel.add(emojiButton);
        buttonPanel.add(paletteButton);
        buttonPanel.add(fileSendButton);
        buttonPanel.add(inviteButton);

        // 하단 패널 설정
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        // 프레임에 컴포넌트 추가
        add(topPanel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });

        emojiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEmojiSelector();
            }
        });

        paletteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPaletteFrame();
            }
        });

        fileSendButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                sendFile(selectedFile);
            }
        });

        inviteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.sendMessage(new MessageDTO(
                        MessageType.InvitePrivateRoom, client.getNickName() + ":" + oppo
                ));
            }
        });

        loadMessage();
    }

    private void loadMessage() {
        if (loadMessages.isEmpty()) {
            System.out.println("검색 결과를 찾을 수 없음");
        } else {
            // content[0]: nickName, content[1]: message, content[2]: time
            for (String message : loadMessages) {
                String[] content = message.split(":", 3);
                if (!content[0].contains("서버")) {
                    SwingUtilities.invokeLater(() -> {
                        if (content[1].startsWith("[EmojiCode]-")) {
                            String[] parts = content[1].split("-");
                            int emojiNumber = Integer.parseInt(parts[1]);
                            appendEmoji(emojiNumber, content[0], content[2]);
                        } else {
                            displayMessage(content[0] + ": " + content[1], content[2]);
                        }
                    });
                }
            }
        }
    }

    private void sendFile(File file) {
        try {
            String roomName = getRoomName();
            String fileName = file.getName();
            long fileSize = file.length();

            client.sendMessage(new MessageDTO(
                    MessageType.FileTransferRequest,
                    roomName + ":" + nickname + ":" + file.getName() + ":" + file.length()
            ));

            // 파일 데이터 전송
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                client.sendFileData(buffer, bytesRead);
            }
            fis.close();

            JOptionPane.showMessageDialog(this, "파일 전송 완료: " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "파일 전송 실패: " + e.getMessage());
        }
    }

    private void handleWindowClosing() {
        // client.sendMessage(new MessageDTO(MessageType.PrivateChat, roomName + ":" + "서버" + ":" + nickname + " 님이 종료하셨습니다."));
        chatPane.setText("");
        System.out.println("1대1 채팅 종료 - " + roomName);
        dispose();
    }

    public void displayMessage(String message) {
        boolean isMine = message.split(":")[0].startsWith("[" + nickname + "]");

        String time = getCurrentTime();

        // 말풍선 패널 생성
        BubblePanel bubble = new BubblePanel(message, time, isMine);

        // 말풍선 위치 계산 및 추가
        int panelWidth = chatPanel.getWidth();
        int bubbleWidth = bubble.getPreferredSize().width;
        int x = isMine ? panelWidth - bubbleWidth - 20 : 20; // 오른쪽 또는 왼쪽 정렬

        bubble.setBounds(x, currentY, bubbleWidth, bubble.getPreferredSize().height);
        chatPanel.add(bubble);

        // Y 좌표 갱신
        currentY += bubble.getPreferredSize().height + 10;

        // 채팅 패널 크기 갱신
        chatPanel.setPreferredSize(new Dimension(chatPanel.getWidth(), currentY));
        chatPanel.revalidate();
        chatPanel.repaint();

        // 스크롤을 맨 아래로 이동
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void displayMessage(String message, String time) {
        boolean isMine = message.split(":")[0].startsWith("[" + nickname + "]");

        // 말풍선 패널 생성
        BubblePanel bubble = new BubblePanel(message, time, isMine);

        // 말풍선 위치 계산 및 추가
        int panelWidth = chatPanel.getWidth();
        int bubbleWidth = bubble.getPreferredSize().width;
        int x = isMine ? panelWidth - bubbleWidth - 20 : 20; // 오른쪽 또는 왼쪽 정렬

        bubble.setBounds(x, currentY, bubbleWidth, bubble.getPreferredSize().height);
        chatPanel.add(bubble);

        // Y 좌표 갱신
        currentY += bubble.getPreferredSize().height + 10;

        // 채팅 패널 크기 갱신
        chatPanel.setPreferredSize(new Dimension(chatPanel.getWidth(), currentY));
        chatPanel.revalidate();
        chatPanel.repaint();

        // 스크롤을 맨 아래로 이동
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void appendEmoji(int emojiIndex, String sender, String time) {
        boolean isMine = sender.equals(nickname);

        // 이모티콘 경로
        String emojiPath = "src/Function/EMOJI/" + emojiIndex + ".jpg";
        ImageIcon emojiIcon = ResizeImage.resizeImage(emojiPath, 48, 48); // 이모티콘 크기 조정

        // 말풍선 패널 생성
        BubblePanel bubble = new BubblePanel(emojiIcon, time, isMine);

        // 말풍선 위치 계산 및 추가
        int panelWidth = chatPanel.getWidth();
        int bubbleWidth = bubble.getPreferredSize().width;
        int x = isMine ? panelWidth - bubbleWidth - 20 : 20; // 오른쪽 또는 왼쪽 정렬

        bubble.setBounds(x, currentY, bubbleWidth, bubble.getPreferredSize().height);
        chatPanel.add(bubble);

        // Y 좌표 갱신
        currentY += bubble.getPreferredSize().height + 10;

        // Panel 및 Scroll 업데이트
        chatPanel.setPreferredSize(new Dimension(chatPanel.getWidth(), currentY));
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void addFileMessage(String sender, String fileName, long fileSize) {
        boolean isMine = sender.equals(nickname);
        String time = getCurrentTime();

        // 파일 말풍선 생성
        BubblePanel bubble = new BubblePanel(
                sender + "님이 파일을 전송했습니다: " + fileName + " (" + fileSize / 1024 + " KB)",
                time,
                isMine,
                true,
                client
        );

        // 클릭 시 파일 다운로드 로직 실행
        bubble.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                downloadFile(fileName);
            }
        });

        bubble.setBackground(SwingCompFunc.LoginButtonColor);

        // 말풍선 위치 계산 및 추가
        int panelWidth = chatPanel.getWidth();
        int bubbleWidth = bubble.getPreferredSize().width;
        int x = isMine ? panelWidth - bubbleWidth - 20 : 20;

        bubble.setBounds(x, currentY, bubbleWidth, bubble.getPreferredSize().height);
        chatPanel.add(bubble);

        // Y 좌표 갱신
        currentY += bubble.getPreferredSize().height + 10;

        // 패널 및 스크롤 갱신
        chatPanel.setPreferredSize(new Dimension(chatPanel.getWidth(), currentY));
        chatPanel.revalidate();
        chatPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = chatScrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    private void downloadFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "파일 이름이 잘못되었습니다.");
            return;
        }

        if (client == null || client.socket == null || client.socket.isClosed()) {
            JOptionPane.showMessageDialog(this, "서버와의 연결이 끊어졌습니다.");
            return;
        }

        client.sendMessage(new MessageDTO(MessageType.FileDownloadRequest, fileName));
    }

    /* 현재 시간 가져오기 */
    public String getCurrentTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

    public void displayMessage2(String message) {
        // chatArea.append(message + "\n");
        boolean isMine = message.startsWith("["+ nickname +"]");
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

    private void sendEmoji(int emojiNumber) {
        // client.sendMessage(new MessageDTO(MessageType.EMOJI, nickname + ":" + emojiNumber));
        client.sendMessage(new MessageDTO(MessageType.PrivateChat, roomName + ":" + nickname + "::" + emojiNumber));
    }

    public void appendEmoji(int emojiIndex, String sender) {
        boolean isMine = sender.equals(nickname);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        // 이모티콘 경로
        String emojiPath = "src/Function/EMOJI/" + emojiIndex + ".jpg";
        ImageIcon emojiIcon = ResizeImage.resizeImage(emojiPath, 48, 48); // 이모티콘 크기 조정

        // 말풍선 패널 생성
        BubblePanel bubble = new BubblePanel(emojiIcon, time, isMine);

        // 말풍선 위치 계산 및 추가
        int panelWidth = chatPanel.getWidth();
        int bubbleWidth = bubble.getPreferredSize().width;
        int x = isMine ? panelWidth - bubbleWidth - 20 : 20; // 오른쪽 또는 왼쪽 정렬

        bubble.setBounds(x, currentY, bubbleWidth, bubble.getPreferredSize().height);
        chatPanel.add(bubble);

        // Y 좌표 갱신
        currentY += bubble.getPreferredSize().height + 10;

        // Panel 및 Scroll 업데이트
        chatPanel.setPreferredSize(new Dimension(chatPanel.getWidth(), currentY));
        chatPanel.revalidate();
        chatPanel.repaint();

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

    private void openPaletteFrame() {
        JFrame paletteFrame = new JFrame("색상 설정");
        paletteFrame.setSize(400, 350); // 상단 패널 공간을 고려해 높이 증가
        paletteFrame.setLayout(new BorderLayout(10, 10));
        paletteFrame.setLocationRelativeTo(this);

        SwingCompFunc.setFrameStyle(paletteFrame);

        // 상단 패널 생성
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("색상 선택", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        SwingCompFunc.setTopPanelStyle(titlePanel);
        titlePanel.setPreferredSize(new Dimension(paletteFrame.getWidth(), 50));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 색상 선택 버튼 생성
        JButton backgroundColorButton = new JButton("배경색 변경");
        JButton myBubbleColorButton = new JButton("내 색상 변경");
        JButton otherBubbleColorButton = new JButton("상대 색상 변경");

        backgroundColorButton.setFont(new Font("나눔 고딕", Font.PLAIN, 12));
        myBubbleColorButton.setFont(new Font("나눔 고딕", Font.PLAIN, 12));
        otherBubbleColorButton.setFont(new Font("나눔 고딕", Font.PLAIN, 12));

        SwingCompFunc.setButtonStyle(backgroundColorButton);
        SwingCompFunc.setButtonStyle(myBubbleColorButton);
        SwingCompFunc.setButtonStyle(otherBubbleColorButton);

        // 프리뷰 패널 생성
        JPanel backgroundPreview = new JPanel();
        backgroundPreview.setBackground(chatPanel.getBackground());
        backgroundPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel myBubblePreview = new JPanel();
        myBubblePreview.setBackground(BubblePanel.getMyBubbleColor());
        myBubblePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel otherBubblePreview = new JPanel();
        otherBubblePreview.setBackground(BubblePanel.getOtherBubbleColor());
        otherBubblePreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // 색상 선택 버튼 동작
        backgroundColorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(paletteFrame, "배경색 선택", chatPanel.getBackground());
            if (selectedColor != null) {
                chatPanel.setBackground(selectedColor);
                backgroundPreview.setBackground(selectedColor);
            }
        });

        myBubbleColorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(paletteFrame, "내 채팅색 선택", BubblePanel.getMyBubbleColor());
            if (selectedColor != null) {
                BubblePanel.setMyBubbleColor(selectedColor);
                myBubblePreview.setBackground(selectedColor);
            }
        });

        otherBubbleColorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(paletteFrame, "상대 채팅색 선택", BubblePanel.getOtherBubbleColor());
            if (selectedColor != null) {
                BubblePanel.setOtherBubbleColor(selectedColor);
                otherBubblePreview.setBackground(selectedColor);
            }
        });

        // 메인 패널 생성
        JPanel mainPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(new JLabel("배경색", SwingConstants.CENTER));
        mainPanel.add(backgroundPreview);
        mainPanel.add(backgroundColorButton);

        mainPanel.add(new JLabel("내 채팅색", SwingConstants.CENTER));
        mainPanel.add(myBubblePreview);
        mainPanel.add(myBubbleColorButton);

        mainPanel.add(new JLabel("상대 채팅색", SwingConstants.CENTER));
        mainPanel.add(otherBubblePreview);
        mainPanel.add(otherBubbleColorButton);

        // 프레임에 컴포넌트 추가
        paletteFrame.add(titlePanel, BorderLayout.NORTH); // 상단 패널
        paletteFrame.add(mainPanel, BorderLayout.CENTER); // 메인 패널

        paletteFrame.setVisible(true);
    }
}

