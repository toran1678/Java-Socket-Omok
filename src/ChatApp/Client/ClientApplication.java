package ChatApp.Client;

import ChatApp.Client.ChatLobbyLayout.ChatLobbyLayout;
import ChatApp.Client.PrivateChatRoom.PrivateChatRoom;
import ChatApp.Server.ServerInfo.OmokRoom;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import ChatApp.Client.GroupChatLayout.GroupChatLayout;
import Database.UserInfo.showUserInfo;
import Database.UserInfo.UserInfo;
import Function.ImageLoad;
import OmokGame.GameFrame;
import Data.Data;
import OmokGame.Panel.BoardCanvas;

import javax.swing.*;
import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class ClientApplication extends Thread {
    private String nickName, ipAddress;
    private int portNum;
    private ChatLobbyLayout chatLayout;
    public Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    ArrayList<String> nickNameList = new ArrayList<>(); // 유저목록을 저장합니다.
    ArrayList<String> roomNameList = new ArrayList<>();
    /* 오목 처리 */
    public GameFrame gameFrame;
    // OmokRoom omokRoom;

    public String getNickName() {
        return nickName;
    }

    // public ClientApplication getClientApplication() { return this; }
    /*
    public void resetNickName(String nickName) {
        this.nickName = nickName;
    }*/

    public void setGui(ChatLobbyLayout chatLayout) {
        // 실행했던 ClientGUI 그 자체의 정보를 들고옵니다.
        this.chatLayout = chatLayout;
    }

    public void setUserInfo(String nickName, String ipAddress, int portNum) {
        // "ClientGUI"로부터 닉네임, 아이피, 포트 값을 받아옵니다.
        this.nickName = nickName;
        this.ipAddress = ipAddress;
        this.portNum = portNum;
    }

    @Override
    public void run() {
        try {
            connectToServer();
            processMessages();
        } catch (Exception e) {
            System.out.println("Error in client application: " + e.getMessage());
        }
    }

    private void connectToServer() throws IOException {
        socket = new Socket(ipAddress, portNum);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        out.writeObject(new MessageDTO(MessageType.CHAT, nickName));
    }

    private void leaveServer() throws IOException {
        if (gameFrame != null) {
            sendMessage(new MessageDTO(MessageType.OmokLeaveRoom, nickName + ":" + gameFrame.getRoomName()));
        }
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null && !socket.isClosed()) socket.close();
    }

    private void processMessages() throws IOException, ClassNotFoundException {
        while (in != null) {
            MessageDTO messageDTO = (MessageDTO) in.readObject();
            handleMessage(messageDTO);
        }
        leaveServer();
    }

    private void handleMessage(MessageDTO messageDTO) {
        switch (messageDTO.getType()) {
//            case ResetUserList, EnterUser:
//                updateUserList(messageDTO.getContent());
//                break;
            case ResetRoomList:
                updateRoomList(messageDTO.getContent());
                break;
            case UpdateUserList, EnterUser:
                handleUpdateUserList(messageDTO);
                break;
            case RemoveRoom:
                roomNameList.clear();
                break;
            case EnterRoom:
                handleEnterRoom(messageDTO.getContent());
                break;
            case UserLeft:
                break;
            case SEARCH_RESPONSE:
                List<String> results = Arrays.asList(messageDTO.getContent().split("\n"));
                chatLayout.displaySearchResults(results);
                break;
            case EnterPrivateRoom: // 비밀 채팅 입장
                handlePrivateRoomEntry(messageDTO);
                break;
            case EnterAlonePrivateRoom:
                handleEnterPrivateRoom(messageDTO);
                break;
            case PrivateChat: // 비밀 채팅
                handlePrivateChat(messageDTO.getContent());
                break;
            case FileData:
                handleFileDownload(messageDTO);
                break;
            case FileTransfer:
                System.out.println("전송 받음");
                handlePrivateFile(messageDTO);
                break;
            case LeaveUser:
                nickNameList.remove(messageDTO.getContent());
                chatLayout.resetUserList(nickNameList);
                break;
            case EMOJI:
                handleEmojiChat(messageDTO.getContent());
                break;
            case ShowUserInfo:
                showUserInfo(messageDTO);
                break;
            case OmokEnterRoom: // 오목 방 입장
                handleOmokEnterRoom(messageDTO);
                break;
            case OmokUpdateRoomUserList: // 오목 유저 업데이트
                handleOmokUpdateRoomUserList(messageDTO);
                break;
            case OmokCreateRoom: // 오목 방 생성
                handleOmokCreateRoom(messageDTO);
                break;
            case OmokUpdateRoom:
                handleOmokUpdateRoomList(messageDTO);
                break;
            case OmokUpdatePlayer:
                handleOmokUpdatePlayer(messageDTO);
                break;
            case OmokLeaveRoom:
                // 오목 방 나갈 때 처리
                break;
            case OmokRemoveRoom: // 방 삭제되었을 때
                handleOmokRemoveRoom(messageDTO);
                break;
            case OmokChat:
                handleOmokChat(messageDTO);
                break;
            case OmokReady:
                handleOmokReady(messageDTO);
                break;
            case OmokStart:
                handleOmokStart(messageDTO);
                break;
            case OmokPlaceStone:
                handleOmokState(messageDTO);
                break;
            case CHAT:
                if (messageDTO.getContent().contains("생성되었습니다.")) {
                    roomNameList.clear();
                }
                chatLayout.appendMessage(messageDTO.getContent());
                break;
        }
    }

    public void handleFileDownload(MessageDTO messageDTO) {
        try {
            String fileName = messageDTO.getContent();

            // 파일 저장 경로 설정
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(fileName));
            int returnValue = fileChooser.showSaveDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();

                byte[] fileData = (byte[]) messageDTO.getObject();

                try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                    fos.write(fileData);
                    JOptionPane.showMessageDialog(null, "파일 다운로드 완료: " + saveFile.getName());
                }
            } else {
                JOptionPane.showMessageDialog(null, "파일 저장 경로를 선택하지 않았습니다.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "파일 다운로드 중 오류 발생: " + e.getMessage());
        }
    }

    public void sendFileData(byte[] data, int length) {
        try {
            out.write(data, 0, length);
            out.flush();
        } catch (IOException e) {
            System.err.println("파일 데이터 전송 중 오류 발생: " + e.getMessage());
        }
    }

    /* 유저 리스트 처리 */
    private void handleUpdateUserList(MessageDTO messageDTO) {
        chatLayout.userModel.removeAllElements();
        chatLayout.userList.removeAll();

        ArrayList<String> userList = (ArrayList<String>) messageDTO.getObject();
        nickNameList.clear();
        if (userList != null) {
            nickNameList.addAll(userList);
        }
        chatLayout.resetUserList(nickNameList);
    }

    /* 오목 게임 시작 */
    private void handleOmokStart(MessageDTO messageDTO) {
        OmokRoom room = (OmokRoom) messageDTO.getObject();

        Data.chessBoard = new int[15][15];

        gameFrame.getRightPanel().displayMessage("[서버]: 게임이 시작되었습니다.");

        /* 내가 방장일 때 */
        if (room.getCreator().equals(nickName)) {
            Data.started = room.getStarted();
            Data.myChess = room.getCreatorChess();
            Data.oppoChess = room.getOppoChess();
            Data.turn = room.getTurn();
            gameFrame.getRightPanel().displayMessage("[서버]: 당신의 차례입니다.");
        }

        /* 내가 상대방일 때 */
        if (room.getOppoName().equals(nickName)) {
            Data.started = room.getStarted();
            Data.myChess = room.getOppoChess();
            Data.oppoChess = room.getCreatorChess();
            Data.turn = room.getTurn();
            gameFrame.getRightPanel().displayMessage("[서버]: 상대방의 차례입니다.");
        }

        // JOptionPane.showMessageDialog(null, "게임이 시작되었습니다.");
    }

    /* OmokRoom 객체로 오목 게임 상태 업데이트 */
    private void handleOmokState(MessageDTO messageDTO) {
        OmokRoom room = (OmokRoom) messageDTO.getObject();

        if (!messageDTO.getContent().isEmpty()) {
            Data.ready = false;
            String winner = messageDTO.getContent();
            if (!Data.observer) {
                if (winner.equals(nickName)) {
                    gameFrame.getRightPanel().setReadyLabel(room.getCreatorReady(), room.getOppoReady());
                    gameFrame.getRightPanel().displayMessage("[서버]: " + winner + "님이 승리하였습니다.");
                } else {
                    gameFrame.getRightPanel().setReadyLabel(room.getCreatorReady(), room.getOppoReady());
                    gameFrame.getRightPanel().displayMessage("[서버]: 당신은 패배하였습니다.");
                    gameFrame.getRightPanel().displayMessage("[서버]: " + winner + "님이 승리하였습니다.");
                }
            } else {
//                JOptionPane.showMessageDialog(null, winner + "님이 승리하셨습니다.");
                gameFrame.getRightPanel().displayMessage(winner + "님이 승리하셨습니다.");
            }

            /* 마지막 수는 그리기 */
            Data.chessBoard = room.getChessBoard();
            Data.last = room.last;

            BoardCanvas mapCanvas = gameFrame.getGamePanel().getBoardCanvas();
            mapCanvas.paintBoardImage();
            mapCanvas.repaint();

            /* 데이터 초기화 */
            Data.initData();
            System.out.println(Data.started);
            Data.started = false;
            gameFrame.getRightPanel().setReadyLabel(false, false);
        }

        /* 내가 관전자일 때 */
        if (Data.observer) {
            Data.chessBoard = room.getChessBoard();

            // 오목 판을 다시 그리고 화면을 갱신
            BoardCanvas mapCanvas = gameFrame.getGamePanel().getBoardCanvas();
            mapCanvas.paintBoardImage();
            mapCanvas.repaint();
            return;
        }

        if (room.turn == Data.myChess) {
            gameFrame.getRightPanel().displayMessage("[서버]: 당신의 차례입니다.");
        }

        // Data.started = room.started;

        if (Data.started) {
            Data.turn = room.getTurn();
            Data.chessBoard = room.getChessBoard();
            Data.last = room.last;

            // 오목 판을 다시 그리고 화면을 갱신
            BoardCanvas mapCanvas = gameFrame.getGamePanel().getBoardCanvas();
            mapCanvas.paintBoardImage();
            mapCanvas.repaint();
        }
    }

    /* 오목 준비 처리 */
    private void handleOmokReady(MessageDTO messageDTO) {
        OmokRoom room = (OmokRoom) messageDTO.getObject();
        gameFrame.getRightPanel().setReadyLabel(room.getCreatorReady(), room.getOppoReady());
    }

    /* 오목 채팅방 처리 */
    private void handleOmokChat(MessageDTO messageDTO) {
        String[] content = messageDTO.getContent().split(":", 3);
        String roomName = content[0];
        String sender = content[1];
        String message = content[2];

        if (gameFrame.getRoomName().equals(roomName)) {
            // 채팅 삽입 처리
            gameFrame.getRightPanel().displayMessage("[" + sender + "]: " + message);
        }
    }

    /* 방 입장 처리 */
    private void handleOmokCreateRoom(MessageDTO messageDTO) {
        String[] content = messageDTO.getContent().split(":");
        // String nickName = content[0];
        String roomName = content[1];
        OmokRoom omokRoom = (OmokRoom) messageDTO.getObject();

        if (!omokRoom.getCreator().equals(this.nickName)) return;

        if (gameFrame == null) {
            this.gameFrame = new GameFrame(roomName, this);
        } else {
            this.gameFrame = new GameFrame(roomName, this);
            gameFrame.setVisible(true);
        }

        // 프로필 정보 업데이트
        setCreatorProfile(omokRoom);
        setOppoProfile(omokRoom);
    }

    private void handleOmokRemoveRoom(MessageDTO messageDTO) {
        String roomName = messageDTO.getContent();
        if (gameFrame != null) {
            SwingUtilities.invokeLater(() -> {
                if (roomName.equals(gameFrame.getRoomName())) {
                    Data.initData();
                    gameFrame.dispose();
                    // gameFrame = null;
                }
            });
            /* 만약 내 방일 때 제거 */
//            if (roomName.equals(gameFrame.getRoomName())) {
//                Data.initData();
//                gameFrame.dispose();
//                // gameFrame = null;
//            }
        } // 아마 룸 리스트 업데이트는 계속 됨
    }

    private void handleOmokUpdatePlayer(MessageDTO messageDTO) {
        String state = messageDTO.getContent();
        Data.observer = state.equals("OBSERVER");
    }

    /* 오목 게임 처리 */
    private void handleOmokEnterRoom(MessageDTO messageDTO) {
        String[] content = messageDTO.getContent().split(":");
        String nickName = content[0];
        String roomName = content[1];
        String state = content[2];
        OmokRoom omokRoom = (OmokRoom) messageDTO.getObject();

        System.out.println(omokRoom.getCreator() + ", " + omokRoom.getOppoName());

        System.out.println(omokRoom);
        /* 방 생성자가 받았을 경우 */
        if (omokRoom.getCreator().equals(this.nickName)) {
            System.out.println("방장이 받은 거임");
            setCreatorProfile(omokRoom);
            setOppoProfile(omokRoom);
            return;
        }

        /* 전송 받은 닉네임이 내 닉네임일 경우 */
        if (nickName.equals(this.nickName)) {
            this.gameFrame = new GameFrame(roomName, this);
            /* 방 생성자 프로필 가져오기 */
            setCreatorProfile(omokRoom);
            setOppoProfile(omokRoom);
            Data.observer = state.equals("OBSERVER");
            gameFrame.setVisible(true);
        }
    }

    /* "omokRoom" 클래스로 오목 방 설정, 생성자 */
    private void setCreatorProfile(OmokRoom omokRoom) {
        if (gameFrame != null && omokRoom != null) {
            SwingUtilities.invokeLater(() -> {
                int CreatorProfileNumber = omokRoom.getCreatorProfileNumber();

                ImageLoad.setImageIcon(this.gameFrame.getRightPanel().getMyProfileLabel(),
                        "src/Function/ProfileCharacterSelector/Img/" + CreatorProfileNumber + ".jpg");
                this.gameFrame.getRightPanel().getMyProfileLabel().setBorder(
                        BorderFactory.createTitledBorder(omokRoom.getCreator()));

                this.gameFrame.getRightPanel().getMyProfileLabel().revalidate();
                this.gameFrame.getRightPanel().getMyProfileLabel().repaint();
            });
        }
    }

    /* "omokRoom" 클래스로 오목 방 설정, 상대방 */
    private void setOppoProfile(OmokRoom omokRoom) {
        if (gameFrame != null) {
            int oppoProfileNumber = omokRoom.getOppoProfileNumber();
            /* 이미지 설정 */
            ImageLoad.setImageIcon(this.gameFrame.getRightPanel().getOpponentProfileLabel(),
                    "src/Function/ProfileCharacterSelector/Img/" + oppoProfileNumber + ".jpg");

            this.gameFrame.getRightPanel().getOpponentProfileLabel().setBorder(
                    BorderFactory.createTitledBorder(omokRoom.getOppoName()));

            this.gameFrame.getRightPanel().getOpponentProfileLabel().revalidate();
            this.gameFrame.getRightPanel().getOpponentProfileLabel().repaint();

            gameFrame.revalidate();
            gameFrame.repaint();
        }
    }

    private void handleOmokUpdateRoomList(MessageDTO messageDTO) {
        // MessageDTO에서 방 목록 데이터 가져오기
        if (!(messageDTO.getObject() instanceof List<?>)) {
            System.err.println("MessageDTO의 객체가 List가 아닙니다.");
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> roomListData = (List<String>) messageDTO.getObject();

        // 방 목록 데이터 출력 (디버깅용)
        System.out.println("받은 방 목록: " + roomListData);

        // DefaultListModel 생성
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String roomName : roomListData) {
            model.addElement(roomName);
        }

        // UI 업데이트
        chatLayout.getOmokRoomList().setModel(model); // chatLayout에서 오목 방 목록 JList 설정
        chatLayout.revalidate(); // 레이아웃 갱신
        chatLayout.repaint();    // UI 갱신
        System.out.println("오목 방 목록 업데이트 완료.");
    }

    private static final int MAX_RETRY = 3; // 최대 재시도 횟수
    private int retryCount = 0; // 현재 재시도 횟수

    /* 여기 문제? */
    private void handleOmokUpdateRoomUserList(MessageDTO messageDTO) {
        OmokRoom room = (OmokRoom) messageDTO.getObject();

        if (gameFrame == null) {
            if (retryCount < MAX_RETRY) {
                retryCount++;
                System.err.println("gameFrame이 아직 초기화되지 않았습니다. 작업을 큐에 추가합니다. (재시도: " + retryCount + ")");
                SwingUtilities.invokeLater(() -> handleOmokUpdateRoomUserList(messageDTO));
            } else {
                System.err.println("gameFrame 초기화 재시도를 초과했습니다. 작업을 취소합니다.");
                retryCount = 0;
            }
            return;
        }

        System.out.println(messageDTO.getContent());
        if (!gameFrame.getRoomName().equals(messageDTO.getContent().trim())) {
            System.err.println("잘못된 방 이름입니다: " + messageDTO.getContent());
            return;
        }

        // @SuppressWarnings("unchecked")
        gameFrame.getRightPanel().setReadyLabel(room.getCreatorReady(), room.getOppoReady());
        setOppoProfile(room);
        setCreatorProfile(room);
        List<String> playerListData = room.getUsers();
        updatePlayerListInUI(playerListData);
    }

    /* 여기도 문제? */
    private void updatePlayerListInUI(List<String> playerListData) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String player : playerListData) {
            model.addElement(player);
        }

        gameFrame.getRightPanel().getPlayerList().setModel(model);
        System.out.println("플레이어 리스트 업데이트 완료: " + playerListData);

        /* revalidate() - 레이아웃 다시 계산, repaint() - 다시 그리기 */
        gameFrame.getRightPanel().getPlayerList().revalidate();
        gameFrame.getRightPanel().getPlayerList().repaint();
    }

    private void showUserInfo(MessageDTO messageDTO) {
        UserInfo userinfo = (UserInfo) messageDTO.getObject();
        new showUserInfo(userinfo);
    }

    private void updateUserList(String newUser) {
        chatLayout.userModel.removeAllElements();
        chatLayout.userList.removeAll();

        nickNameList.add(newUser);
        chatLayout.resetUserList(nickNameList);
    }

    private void updateRoomList(String newRoom) {
        chatLayout.roomModel.removeAllElements();
        chatLayout.roomList.removeAll();

        roomNameList.add(newRoom);
        if (!newRoom.equals("EMPTY")) {
            roomNameList.remove("EMPTY");
        }
        chatLayout.resetRoomList(roomNameList);
    }

    private void handleEnterRoom(String content) {
        String[] parts = content.split(":");
        String enterNickName = parts[0];
        String enterRoomName = parts[1];
        int enterPortNum = Integer.parseInt(parts[2]);
        if (chatLayout.getNickName().equals(enterNickName)) {
            new GroupChatLayout(enterNickName, enterRoomName, ipAddress, enterPortNum, this);
        }
    }

    Map<String, PrivateChatRoom> privateChatRooms = new HashMap<>();

    public void addPrivateChatRoom(String roomName, PrivateChatRoom chatRoom) {
        privateChatRooms.put(roomName, chatRoom);
    }

    public PrivateChatRoom getPrivateChatRoom(String roomName) {
        return privateChatRooms.get(roomName);
    }

    public void handlePrivateRoomEntry(MessageDTO messageDTO) {
        String[] contents = messageDTO.getContent().split(":", 3);
        String user = contents[0];
        String roomName = contents[1];
        String sender = contents[2];
        boolean isCurrentUser = nickName.equals(sender);
        ArrayList<String> messages = (ArrayList<String>) messageDTO.getObject();

        if (isCurrentUser) {
            setPrivateRoom(nickName, roomName, messages, sender);
            return;
        }

        int result = JOptionPane.showConfirmDialog(null,
                sender + "님의 채팅 요청을 수락하시겠습니까?", "1대1 채팅 요청", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // 1대1 채팅방 UI를 별도로 띄우고, 해당 방에서만 상대 유저와 대화 가능하도록 설정
            sendMessage(new MessageDTO(MessageType.PrivateChat, roomName + ":" + "서버" + ":" + nickName + "님이 방에 입장하셨습니다."));
            setPrivateRoom(nickName, roomName, messages, sender);
        } else {
            sendMessage(new MessageDTO(MessageType.PrivateChat, roomName + ":" + "서버" + ":" + nickName + "님이 요청을 거절하셨습니다."));
        }
    }

    private void handleEnterPrivateRoom(MessageDTO messageDTO) {
        String[] contents = messageDTO.getContent().split(":", 3);
        String sender = contents[0];
        String roomName = contents[1];
        String oppo = contents[2];
        boolean isCurrentUser = nickName.equals(sender);
        ArrayList<String> messages = (ArrayList<String>) messageDTO.getObject();

        setPrivateRoom(nickName, roomName, messages, oppo);
    }

    public void setPrivateRoom(String nickName, String roomName, ArrayList<String> messages, String oppo) {
        if (privateChatRooms.containsKey(roomName)) {
            privateChatRooms.get(roomName).setVisible(true);
        } else {
            PrivateChatRoom privateChatRoom = new PrivateChatRoom(nickName, roomName, this, messages, oppo);

            addPrivateChatRoom(roomName, privateChatRoom);
            privateChatRoom.setVisible(true);
        }
    }

    public void handlePrivateFile(MessageDTO messageDTO) {
        String[] content = messageDTO.getContent().split(":", 4);
        String roomName = content[0];
        String sender = content[1];
        String fileName = content[2];
        long fileSize = Long.parseLong(content[3]);

        PrivateChatRoom privateChatRoom = getPrivateChatRoom(roomName);
        privateChatRoom.addFileMessage(sender, fileName, fileSize);
    }

    public void handlePrivateChat(String _content) {
        String[] content = _content.split(":", 4);
        String roomName = content[0];
        String sender = content[1];
        String message = content[2];
        String emojiNumber = content.length > 3 ? content[3] : null; // 이모지 정보가 있을 때만 가져옴

        PrivateChatRoom privateChatRoom = getPrivateChatRoom(roomName);
        if (emojiNumber != null && privateChatRoom != null) {
            // 이모지 처리
            int emoji = Integer.parseInt(emojiNumber);
            privateChatRoom.appendEmoji(emoji, sender);
        } else if (privateChatRoom != null) {
            // 일반 메시지 처리
            privateChatRoom.displayMessage("[" + sender + "]: " + message);
        }
    }

    public void handleEmojiChat(String _content) {
        String[] content = _content.split(":", 2);
        String sender = content[0];
        int emoji = Integer.parseInt(content[1]);

        chatLayout.appendEmoji(emoji, sender);
    }

    public void sendMessage(MessageDTO messageDTO) {
        try {
            if (out==null) {
                System.err.println("서버가 연결되지 않았습니다. 'out' is null.");
                return;
            }
            out.writeObject(messageDTO);
            out.flush();
        } catch (IOException e) {
            System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
        }
    }

    public void resetCanvas() {
        BoardCanvas boardCanvas = gameFrame.getGamePanel().getBoardCanvas();
        boardCanvas.paintBoardImage();
        boardCanvas.repaint();
    }

    public ClientApplication getClient() {
        return this;
    }

    /* 1대1 채팅 요청 */
    public void requestOneToOneChat(String selectedUser) {
        MessageDTO message = new MessageDTO(MessageType.OneToOneChatRequest, nickName + ":" + selectedUser);
        sendMessage(message);
    }

    public void enterAlonePrivateRoom(String selectedUser) {
        MessageDTO message = new MessageDTO(MessageType.EnterAlonePrivateRoom, nickName + ":" + selectedUser);
        sendMessage(message);
    }
}