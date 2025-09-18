package ChatApp.Server;

// import ChatApp.Client.PrivateChatRoom.PrivateChatRoom;
import ChatApp.Server.AdminToolUI.AdminToolUI;
import ChatApp.Server.ServerInfo.OmokRoom;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import Database.Database;
import Database.UserInfo.UserInfo;

// import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerApplication extends Thread {
    int portNum;
    private int subPortNum = 1;
    private String currentRoom = "Lobby";
    Database db = new Database();
    ServerSocket serverSocket;
    Vector<ReceiveThread> clientThreadList = new Vector<>();
    ArrayList<String> userNameList = new ArrayList<>();
    ArrayList<String> roomNameList = new ArrayList<>();
    HashMap<String, ServerApplication> roomMap = new HashMap<>();
    HashMap<String, String> userRoomMap = new HashMap<>(); // 사용자와 현재 방을 매핑
    // HashMap<String, PrivateChatRoom> privateChatRooms = new HashMap<>();
    HashMap<String, OmokRoom> omokUserRoomMap = new HashMap<String, OmokRoom>();
    // ArrayList<String> omokRoomList = new ArrayList<>();
    /* userRoles = Key - nickName, Value - PLAYER or OBSERVER */
    HashMap<String, String> userRoles = new HashMap<>();
    Socket socket;
    private final static String PLAYER = "PLAYER";
    private final static String OBSERVER = "OBSERVER";

    private AdminToolUI adminToolUI;
    boolean isMainServer;

    ServerApplication(int portNum, boolean isMainServer) {
        this.portNum = portNum;
        this.isMainServer = isMainServer;
        if (isMainServer) {
            adminToolUI = new AdminToolUI(this);
        }
        runServer();
        start();
    }

    public void runAdminTool() {
        if (isMainServer) {
            adminToolUI = new AdminToolUI(this);
        }
    }

    public void runServer() {
        try {
            Collections.synchronizedList(clientThreadList); // 교통정리를 해준다.( clientList를 네트워크 처리해주는것 )
            serverSocket = new ServerSocket(portNum);
            System.out.println("서버가 시작되었습니다. IP: [" + InetAddress.getLocalHost() + "], Port: [" + portNum + "]");
        } catch (Exception e) {
            System.out.println("서버 시작 중 오류 발생: " + e.getMessage());
        }
    }

    public void run() {
        try {
            while (serverSocket != null && !serverSocket.isClosed()) {
                socket = serverSocket.accept();
                System.out.println("새로운 클라이언트 접속: " + socket.getInetAddress());
                ServerApplication.ReceiveThread receiveThread = new ServerApplication.ReceiveThread(socket);
                clientThreadList.add(receiveThread);
                receiveThread.start();
            }
        } catch (IOException e) {
            System.err.println("클라이언트 연결 중 오류 발생: " + e.getMessage());
        } finally {
        	try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
        	} catch (IOException e) {
        		System.err.println("클라이언트 연결 중 오류 발생: " + e.getMessage());
        	}
        	
        }
    }

    public void sendAll(MessageDTO messageDTO) {
        /* 메시지 타입이 "CHAT"인 경우에만 데이터베이스에 저장 */
        if (messageDTO.getType() == MessageType.CHAT) {
            String nickname = messageDTO.getContent().split("]:")[0].substring(1);
            db.saveChatLog(nickname, currentRoom, messageDTO.getContent());
        }

        if (messageDTO.getType() == MessageType.PrivateChat) {
            String[] content = messageDTO.getContent().split(":", 4);
            String roomName = content.length > 0 ? content[0] : "";
            String nickName = content.length > 1 ? content[1] : "";
            String message = content.length > 2 ? content[2] : "";
            String emoji = content.length > 3 ? content[3] : "";

            if (emoji.equals("")) {
                db.saveChatLog(nickName, roomName, message);
            } else {
                db.saveChatLog(nickName, roomName, "[EmojiCode]-" + emoji);
            }
        }

        if (messageDTO.getType() == MessageType.EMOJI) {
            String[] content = messageDTO.getContent().split(":", 2);
            String nickname = content[0];
            System.out.println(content[1]);
            String message = "[EmojiCode]-" + content[1];
            System.out.println(message);
            db.saveChatLog(nickname, currentRoom, message);
        }

        if (messageDTO.getType() == MessageType.OmokChat) {
            String[] content = messageDTO.getContent().split(":", 3);
            String roomName = content[0];
            String nickname = content[1];
            String message = content[2];
            db.saveChatLog(nickname, roomName, message);
        }

        synchronized (clientThreadList) {
            for (ReceiveThread clientThread : clientThreadList) {
                try {
                    clientThread.sendMessage(messageDTO);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            if (adminToolUI != null) {
                if (messageDTO.getType() == MessageType.CHAT) {
                    adminToolUI.displayMessage("[Broadcast] " + messageDTO.getContent());
                } else {
                    adminToolUI.displayMessage("[Command] @" + messageDTO.getType() +
                            "_" + messageDTO.getContent());
                }
            }
        }
    }

    public void sendAdminMessage(String adminMessage) {
        MessageDTO messageDTO = new MessageDTO(MessageType.CHAT, "[관리자]: " + adminMessage);
        sendAll(messageDTO);
    }

    public ArrayList<String> getUserList() {
        return new ArrayList<>(userNameList);
    }

    /* 관리자 도구의 유저 리스트를 업데이트하는 메서드 */
    public void updateAdminUserList() {
        if (isMainServer && adminToolUI != null) {
            adminToolUI.updateUserList(getUserList());
        }
    }

    /* 유저 강퇴 */
    public void kickUser(String nickName) {
        ReceiveThread targetThread = null;
        for (ReceiveThread clientThread : clientThreadList) {
            if (clientThread.nickName.equals(nickName)) {
                targetThread = clientThread;
                break;
            }
        }

        if (targetThread != null) {
            removeClient(targetThread, nickName); // 서버 내 유저 목록에서 제거
            sendAll(new MessageDTO(MessageType.CHAT, "[서버]: " + nickName + "님이 강퇴되었습니다.")); // 공지 메시지 전송
            updateUserList(); // 어드민 UI 업데이트
        }
    }

    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }

    public void removeClient(ReceiveThread client, String nickName) {
        // 퇴장한 유저 발생시 목록에서 삭제하는 역할을 합니다.
        clientThreadList.removeElement(client);
        userNameList.remove(nickName);

        leaveRoom(nickName);
    }

    public void leaveRoom(String nickName) {
        if (userRoomMap.containsKey(nickName)) {
            String roomName = userRoomMap.remove(nickName);

            userNameList.remove(nickName);

            ServerApplication roomServer = roomMap.get(roomName);
            if (roomServer != null) {
                roomServer.sendAll(new MessageDTO(MessageType.UserLeft, nickName));
            }

            updateUserList();
            updateRoomList();
        }
    }

    private void updateUserList() {
//        for (String nickName : userNameList) {
//            sendAll(new MessageDTO(MessageType.ResetUserList, nickName));
//        }
        MessageDTO updateMessage = new MessageDTO(MessageType.UpdateUserList,
                "Update user list", new ArrayList<>(userNameList));
        sendAll(updateMessage);
    }

    private void updateRoomList() {
        for (String roomName : roomNameList) {
            sendAll(new MessageDTO(MessageType.ResetRoomList, roomName));
        }
    }

    public void createPrivateChatRoom(String content) {
        String user1 = content.split(":")[0];
        String user2 = content.split(":")[1];

        String sender = user1;
        String oppo = user2;

        // 유니코드 값을 기준으로 정렬
        if (user1.compareTo(user2) > 0) {
            // user1이 user2보다 뒤에 있다면 스왑
            String temp = user1;
            user1 = user2;
            user2 = temp;
        }

        /* 요청한 유저를 순서롤 방을 생성하는 게 아닌, 이름순으로 생성하여 나중에 관리하기 편하도록 */
        String roomName = "1대1 채팅( " + user1 + " / " + user2 + " )";
        ArrayList<String> messages = db.searchMessages(roomName);

        // 방 생성 후 두 유저만 입장시키기
        sendUserToPrivateRoom(sender, roomName, sender, messages);
        sendUserToPrivateRoom(oppo, roomName, sender, messages);
    }

    public void invitePrivateRoom(MessageDTO messageDTO) {
        String[] contents = messageDTO.getContent().split(":", 2);
        String user1 = contents[0];
        String user2 = contents[1];

        String sender = user1;
        String oppo = user2;

        // 유니코드 값을 기준으로 정렬
        if (user1.compareTo(user2) > 0) {
            // user1이 user2보다 뒤에 있다면 스왑
            String temp = user1;
            user1 = user2;
            user2 = temp;
        }

        /* 요청한 유저를 순서롤 방을 생성하는 게 아닌, 이름순으로 생성하여 나중에 관리하기 편하도록 */
        String roomName = "1대1 채팅( " + user1 + " / " + user2 + " )";

        // 상대방이 현재 서버에 접속 중인지 확인
        boolean isUserOnline = false;
        for (String user : userNameList) {
            if (user.equals(oppo)) {
                isUserOnline = true;
                break;
            }
        }

//        synchronized (clientThreadList) {
//            for (ReceiveThread clientThread : clientThreadList) {
//                if (clientThread.nickName.equals(oppo)) {
//                    isUserOnline = true;
//                    break;
//                }
//            }
//        }

        if (!isUserOnline) {
            // 상대방이 접속하지 않았을 경우 메시지 전송
            for (ReceiveThread clientThread : clientThreadList) {
                if (clientThread.nickName.equals(sender)) {
                    clientThread.sendMessage(new MessageDTO(
                            MessageType.PrivateChat,
                            roomName + ":" + "서버" + ":" + oppo + "님은 현재 서버에 접속하지 않았습니다."
                    ));
                    return;
                }
            }
        }

        System.out.println("보내짐ㄴ거");

        ArrayList<String> messages = db.searchMessages(roomName);
        sendUserToPrivateRoom(oppo, roomName, sender, messages);
    }

    public void handleEnterAlonePrivateRoom(MessageDTO messageDTO) {
        String[] contents = messageDTO.getContent().split(":", 2);
        String user1 = contents[0];
        String user2 = contents[1];

        String sender = user1;
        String oppo = user2;

        // 유니코드 값을 기준으로 정렬
        if (user1.compareTo(user2) > 0) {
            // user1이 user2보다 뒤에 있다면 스왑
            String temp = user1;
            user1 = user2;
            user2 = temp;
        }

        /* 요청한 유저를 순서롤 방을 생성하는 게 아닌, 이름순으로 생성하여 나중에 관리하기 편하도록 */
        String roomName = "1대1 채팅( " + user1 + " / " + user2 + " )";
        ArrayList<String> messages = db.searchMessages(roomName);

        MessageDTO enterRoomMessage = new MessageDTO(MessageType.EnterAlonePrivateRoom, sender + ":" + roomName + ":" + oppo, messages);
        for (ReceiveThread clientThread : clientThreadList) {
            if (clientThread.nickName.equals(sender)) {
                clientThread.sendMessage(enterRoomMessage);
                break;
            }
        }
    }

    // 두 유저에게만 1대1 채팅 방에 입장하라는 메시지 전송
    private void sendUserToPrivateRoom(String userName, String roomName, String sender, ArrayList<String> messages) {
        MessageDTO enterRoomMessage = new MessageDTO(MessageType.EnterPrivateRoom, userName + ":" + roomName + ":" + sender, messages);
        for (ReceiveThread clientThread : clientThreadList) {
            if (clientThread.nickName.equals(userName)) {
                clientThread.sendMessage(enterRoomMessage);
                break;
            }
        }
    }

    private void handlePrivateChat(String content) {
        String[] parts = content.split(":", 3);

        String roomName = parts[0];
        String sender = parts[1];
        String message = parts[2];
        String emojiNumber = parts.length > 3 ? parts[3] : null; // 이모지 정보가 있을 때만 가져옴

        MessageDTO privateMessage;
        if (emojiNumber != null) {
            // 이모지 처리
            privateMessage = new MessageDTO(
                    MessageType.PrivateChat,
                    roomName + ":" + sender + "::" + emojiNumber
            );
        } else {
            // 일반 메시지 처리
            privateMessage = new MessageDTO(
                    MessageType.PrivateChat,
                    roomName + ":" + sender + ":" + message
            );
        }

        sendAll(privateMessage);
    }

    public ArrayList<String> getNickNameList() {
        return userNameList;
    }

    public int getPortNum() {
        return portNum;
    }

    class ReceiveThread extends Thread {
        /* 각 네트워크(클라이언트)로부터 소켓을 받아 다시 내보내는 역할 */
        private ObjectInputStream in;
        private ObjectOutputStream out;
//        private FileInputStream fileIn;
//        private FileOutputStream fileOut;

        private final Socket socket;
        String nickName;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                MessageDTO initialMessage = (MessageDTO) in.readObject();
                /* AdminTool 실행 명령어 */
                if (initialMessage.getType()==MessageType.AdminTool) {
                    runAdminTool();
                    nickName = "관리자";
                    return;
                }
                nickName = initialMessage.getContent();

                if (!userNameList.contains(nickName)) {
                    userNameList.add(nickName);
                    updateAdminUserList();
                }
                System.out.println("새로운 사용자 접속: " + userNameList);
            } catch (IOException|ClassNotFoundException e) {
                System.out.println("스트림 생성 중 오류 발생: " + e.getMessage());
                closeConnection();
            }
        }

        @Override
        public void run() {
            try {
                /* 새로운 유저 발생시 유저 목록 초기화, 새롭게 유저 목록 입력 */
                /* 새로운 유저가 입장하였음을 모든 클라이언트에 전송 */
                sendAll(new MessageDTO(MessageType.CHAT, "[서버]: " + nickName + "님이 입장하셨습니다."));
                sendAll(new MessageDTO(MessageType.EnterUser, nickName));
                updateUserList();
                //updateRoomList();

                synchronized (omokUserRoomMap) {
                    sendMessage(new MessageDTO(MessageType.OmokUpdateRoom, "", new ArrayList<>(omokUserRoomMap.keySet())));
                }

                while (in != null) {
                    MessageDTO messageDTO = (MessageDTO) in.readObject();
                    handleMessage(messageDTO);
                }
            } catch (IOException|ClassNotFoundException e) {
                System.err.println("연결 종료 중 오류 발생: " + e.getMessage());
            } finally {
                removeClient(this, nickName);
                updateUserList();
                updateAdminUserList();
                if (nickName.equals("관리자")) {
                    sendAll(new MessageDTO(MessageType.LeaveUser, nickName));
                    closeConnection();
                } else {
                    sendAll(new MessageDTO(MessageType.CHAT, "[서버]: " + nickName + "님이 퇴장하셨습니다."));
                    sendAll(new MessageDTO(MessageType.LeaveUser, nickName));
                    closeConnection();
                }
            }
        }

        private void handleMessage(MessageDTO messageDTO) {
            // userManagementUI.displayMessage("[Client]: " + messageDTO.getContent());
            switch (messageDTO.getType()) {
                case CreateRoom:
                    createRoom(messageDTO.getContent());
                    break;
                case RemoveRoom:
                    removeRoom(messageDTO.getContent());
                    break;
                case EnterRoom:
                    enterRoom(messageDTO.getContent());
                    break;
                case LeaveRoom:
                    leaveRoom(messageDTO.getContent());
                    break;
                case Whisper:
                    handleWhisper(messageDTO.getContent());
                    break;
                case SEARCH_REQUEST:
                    handleSearchRequest(messageDTO.getContent());
                    break;
                case OneToOneChatRequest:
                    createPrivateChatRoom(messageDTO.getContent());
                    break;
                case InvitePrivateRoom:
                    invitePrivateRoom(messageDTO);
                    break;
                case PrivateChat:
                    handlePrivateChat(messageDTO.getContent());
                    break;
                case EnterAlonePrivateRoom:
                    handleEnterAlonePrivateRoom(messageDTO);
                    break;
                case FileTransferRequest:
                    handleFileTransfer(messageDTO);
                    break;
                case FileDownloadRequest:
                    handleFileDownload(messageDTO);
                    break;
                case EMOJI:
                    sendAll(messageDTO);
                    break;
                case ShowUserInfo: // 유저가 특정 유저 정보를 요구할 때
                    sendUserInfo(messageDTO);
                    break;
                case EditUser: // 유저가 개인 정보를 수정할 때
                    handleEditUserMethod(messageDTO);
                    break;
                case PrivateChatLoadMessage:

                    break;
                case OmokCreateRoom:
                    handleOmokCreateRoom(messageDTO);
                    break;
                case OmokEnterRoom:
                    handleOmokRoomJoin(messageDTO);
                    break;
                case OmokLeaveRoom: // 방에서 나갔을 때 처리
                    handleOmokLeaveRoom(messageDTO);
                    break;
                case OmokSurrender: // 항복을 했을 때 처리
                    handleOmokSurrender(messageDTO);
                    break;
                case OmokChat:
                    handleOmokChat(messageDTO);
                    break;
                case OmokReady:
                    handleOmokReady(messageDTO);
                    break;
                case OmokPlaceStone:
                    handleOmokPlaceStone(messageDTO);
                    break;
                case CHAT:
                    sendAll(messageDTO);
                    break;
            }
        }

        private void handleFileDownload(MessageDTO messageDTO) {
            String fileName = messageDTO.getContent();
            File file = new File("server_files/" + fileName);

            if (!file.exists()) {
                sendMessage(new MessageDTO(MessageType.CHAT, "[서버] 파일이 존재하지 않습니다: " + fileName));
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] fileData = fis.readAllBytes(); // 파일 전체 데이터를 바이트 배열로 읽음

                // 파일 데이터를 MessageDTO로 클라이언트에 전송
                sendMessage(new MessageDTO(MessageType.FileData, fileName, fileData));
                System.out.println("파일 전송 완료: " + fileName);
            } catch (IOException e) {
                System.err.println("파일 전송 중 오류 발생: " + e.getMessage());
            }
        }

        private void handleFileTransfer(MessageDTO messageDTO) {
            try {
                // 파일 메타데이터 파싱
                String[] content = messageDTO.getContent().split(":", 4); // 필드가 4개로 나뉘어야 함
                String roomName = content[0];
                String sender = content[1];
                String fileName = content[2];
                long fileSize = Long.parseLong(content[3]); // 파일 크기를 Long으로 변환

                System.out.println("파일 전송 요청: " + fileName + " (" + fileSize + " bytes)");

                File directory = new File("server_files");
                if (!directory.exists()) {
                    directory.mkdirs(); // 경로가 없으면 생성
                }

                // 서버에 파일 저장 경로
                File file = new File("server_files/" + fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    long totalRead = 0;

                    // 파일 데이터를 수신하여 저장
                    while (totalRead < fileSize && (bytesRead = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                    }

                    System.out.println("파일 저장 완료: " + file.getAbsolutePath());

                    // 다른 사용자에게 파일 전송 메시지 전송
                    sendAll(new MessageDTO(MessageType.FileTransfer, roomName + ":" + sender + ":" + fileName + ":" + fileSize));
                }
            } catch (NumberFormatException e) {
                System.err.println("파일 크기 변환 중 오류 발생: " + e.getMessage());
            } catch (IOException e) {
                System.err.println("파일 저장 중 오류 발생: " + e.getMessage());
            }
        }

        /* 오목 착수했을 때 발생 */
        private void handleOmokPlaceStone(MessageDTO messageDTO) {
            String[] contents = messageDTO.getContent().split(":", 5);
            String roomName = contents[0];
            String sender = contents[1];
            int last = Integer.parseInt(contents[2]);
            int x = Integer.parseInt(contents[3]);
            int y = Integer.parseInt(contents[4]);
            boolean isWin = false;

            int[][] chessBoard = (int[][]) messageDTO.getObject();

            OmokRoom room = omokUserRoomMap.get(roomName);
            room.setChessBoard(chessBoard);
            if (room.getTurn() == room.creatorChess) {
                room.turn = room.getOppoChess();
                db.saveOmokMove(roomName, sender, x, y, room.getOppoChess());
            } else {
                room.turn = room.getCreatorChess();
                db.saveOmokMove(roomName, sender, x, y, room.getCreatorChess());
            }

            room.setLast(last);

            OmokRoom cloneRoom = deepClone(room);
            MessageDTO placeStoneMessage = new MessageDTO(MessageType.OmokPlaceStone, "", cloneRoom);

            /* 게임 승리 조건 */
            if (OmokCheckWinner(room.chessBoard, x, y, room.getCreatorChess())) {
                // 방장 승리
                placeStoneMessage.setContent(room.creator);
                db.updateWinLose(room.creator, room.oppoName);
                isWin = true;
            }

            if (OmokCheckWinner(room.chessBoard, x, y, room.getOppoChess())) {
                // 상대 승리
                placeStoneMessage.setContent(room.oppoName);
                db.updateWinLose(room.oppoName, room.creator);
                isWin = true;
            }

            for (ReceiveThread clientThread : clientThreadList) {
                if (room.getUsers().contains(clientThread.nickName)) {
                    clientThread.sendMessage(placeStoneMessage);
                }
            }

            if (isWin) {
                room.initGameData();
            }
        }

        /* 오목 승리 알고리즘 */
        public int OmokcountStones(int[][] board, int x, int y, int dx, int dy, int player) {
            int count = 0;
            int size = board.length; // 바둑판 크기

            while (x >= 0 && y >= 0 && x < size && y < size && board[x][y] == player) {
                count++;
                x += dx; // 방향에 따라 x 이동
                y += dy; // 방향에 따라 y 이동
            }

            return count;
        }

        /* 오목 승리 알고리즘 */
        private boolean OmokCheckWinner(int[][] board, int x, int y, int player) {
            // 4가지 방향: 수평, 수직, 대각선 ↘↖, 대각선 ↙↗
            int[][] directions = {
                    {1, 0},  // 수평 →
                    {0, 1},  // 수직 ↓
                    {1, 1},  // 대각선 ↘
                    {1, -1}  // 대각선 ↙
            };

            for (int[] dir : directions) {
                int dx = dir[0];
                int dy = dir[1];

                // 현재 방향과 반대 방향의 돌 개수 합산
                int count = OmokcountStones(board, x, y, dx, dy, player)
                        + OmokcountStones(board, x, y, -dx, -dy, player) - 1;

                if (count >= 5) {
                    return true; // 5개 이상 연속되면 승리
                }
            }

            return false; // 모든 방향 탐색 후 5개 미만
        }

        private void handleOmokReady(MessageDTO messageDTO) {
            String[] contents = messageDTO.getContent().split(":", 2);
            String roomName = contents[0];
            String sender = contents[1];

            OmokRoom room = omokUserRoomMap.get(roomName);
            if (room.creator.equals(sender)) {
                room.setCreatorReady(true);
            }
            if (room.oppoName.equals(sender)) {
                room.setOppoReady(true);
            }

            if (room.getCreatorReady() && room.getOppoReady() && !room.getStarted()) {
                // 게임 시작
                room.setStarted(true);
                room.setCreatorChess(room.BLACK);
                room.setOppoChess(room.WHITE);
                room.setTurn(room.BLACK);

                OmokRoom cloneRoom = deepClone(room);
                MessageDTO startMessage = new MessageDTO(MessageType.OmokStart, "", cloneRoom);

                for (ReceiveThread clientThread : clientThreadList) {
                    if (room.getUsers().contains(clientThread.nickName)) {
                        clientThread.sendMessage(startMessage);
                    }
                }
            }

            OmokRoom cloneRoom = deepClone(room);
            MessageDTO readyMessage = new MessageDTO(MessageType.OmokReady, "", cloneRoom);

            for (ReceiveThread clientThread : clientThreadList) {
                if (room.getUsers().contains(clientThread.nickName)) {
                    clientThread.sendMessage(readyMessage);
                }
            }
        }

        private void handleOmokChat(MessageDTO messageDTO) {
            String[] parts = messageDTO.getContent().split(":", 3);

            String roomName = parts[0];
            String sender = parts[1];
            String message = parts[2];

            sendAll(new MessageDTO(MessageType.OmokChat,
                    roomName + ":" + sender + ":" + message));
        }
        
        private void handleOmokRoomJoin(MessageDTO messageDTO) {
            String[] content = messageDTO.getContent().split(":");
            String nickName = content[0];
            String roomName = content[1];

            synchronized (omokUserRoomMap) {
                OmokRoom room = omokUserRoomMap.get(roomName);
                if (!omokUserRoomMap.containsKey(roomName)) {
                    sendMessage(new MessageDTO(MessageType.CHAT, "[서버]: " + roomName + " 방이 존재하지 않습니다."));
                    return;
                }

                //System.out.println(userRoles);
//                if (getAllUniqueUsers().contains(nickName)) {
//                    sendMessage(new MessageDTO(MessageType.CHAT,
//                            "[서버]: 이미 다른 방에 접속하고 있습니다."));
//                    return;
//                }

                if (userRoles.containsKey(nickName)) {
                    sendMessage(new MessageDTO(MessageType.CHAT,
                            "[서버]: 이미 다른 방에 접속하고 있습니다."));
                    return;
                }

                if (room.getUsers().size() < 2) {
                    room.setOppoName(nickName);
                    int oppoProfile = db.getProfileCharacterNumber(nickName);
                    room.setOppoProfile(oppoProfile);

                    OmokRoom cloneRoom = deepClone(room);
                    sendAll(new MessageDTO(MessageType.OmokEnterRoom, nickName + ":" + roomName + ":" + PLAYER, cloneRoom));
                    userRoles.putIfAbsent(nickName, PLAYER);
                } else {
                    OmokRoom cloneRoom = deepClone(room);
                    sendAll(new MessageDTO(MessageType.OmokEnterRoom, nickName + ":" + roomName + ":" + OBSERVER, cloneRoom));
                    userRoles.putIfAbsent(nickName, OBSERVER);
                }

                // 룸 유저에 추가
                if (!room.getUsers().contains(nickName)) {
                    room.addUser(nickName);
                }

                // 업데이트 룸 유저 리스트 메서드
                omokUpdateUserList(roomName);
            }
        }

        /* PLAYER 가 두 명인지 확인 */
        private boolean checkPlayerCount(String roomName) {
            if (!omokUserRoomMap.containsKey(roomName)) {
                System.err.println("[서버]: 방 " + roomName + "이(가) 존재하지 않습니다.");
                return false;
            }

            OmokRoom room = omokUserRoomMap.get(roomName);
            List<String> userInRoom = room.getUsers();

            int playerCount = 0;
            for (String user : userInRoom) {
                if (userRoles.containsKey(user) && userRoles.get(user).equals(PLAYER)) {
                    playerCount++;
                }
            }

            return playerCount >= 2;
        }

        /* 직렬화된 객체를 최신화하기 위한 깊은 복사 메서드 */
        private OmokRoom deepClone(OmokRoom original) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(original);
                oos.flush();
                ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bis);
                return (OmokRoom) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(e.getMessage());
                return null;
            }
        }

        /* 오목 방 유저 리스트 업데이트 */
        private void omokUpdateUserList(String roomName) {
            if (!omokUserRoomMap.containsKey(roomName)) {
                System.err.println("[서버]: " + roomName + " 방이 존재하지 않습니다.");
                return;
            }

            OmokRoom room = omokUserRoomMap.get(roomName);
            OmokRoom clonedRoom = deepClone(room);
            MessageDTO updateMessage = new MessageDTO(MessageType.OmokUpdateRoomUserList,
                    roomName, clonedRoom);

            for (ReceiveThread clientThread : clientThreadList) {
                if (room.getUsers().contains(clientThread.nickName)) {
                    clientThread.sendMessage(updateMessage);
                }
            }
        }

        /* 오목 방 리스트 업데이트 */
        private void omokUpdateRoomList() {
            List<String> roomList = new ArrayList<>(omokUserRoomMap.keySet());
            sendAll(new MessageDTO(MessageType.OmokUpdateRoom, "", roomList));
        }

        /* 오목 방 생성 메서드 */
        private void handleOmokCreateRoom(MessageDTO messageDTO) {
            String[] content = messageDTO.getContent().split(":");
            String creator = content[0];
            String roomName = content[1];
            int creatorProfileNumber = db.getUserInfo(creator).getProfileCharacter();

//            if (getAllUniqueUsers().contains(creator)) {
//                sendMessage(new MessageDTO(MessageType.CHAT,
//                        "[서버]: 이미 다른 방에 접속하고 있습니다."));
//                return;
//            }

            if (userRoles.containsKey(nickName)) {
                sendMessage(new MessageDTO(MessageType.CHAT,
                        "[서버]: 이미 다른 방에 접속하고 있습니다."));
                return;
            }

            synchronized (omokUserRoomMap) {
                // 방이 이미 존재하는지 확인
                if (omokUserRoomMap.containsKey(roomName)) {
                    sendMessage(new MessageDTO(MessageType.CHAT,
                            "[서버]: 채팅방 " + roomName + "은(는) 이미 존재하는 채팅방입니다."));
                    return;
                }
                omokUserRoomMap.put(roomName, new OmokRoom(roomName, creator, creatorProfileNumber));
                sendAll(new MessageDTO(MessageType.CHAT,
                        "[서버]: 채팅방 " + roomName + "이(가) 생성되었습니다."));
                sendMessage(new MessageDTO(MessageType.OmokCreateRoom,
                        creator + ":" + roomName, omokUserRoomMap.get(roomName)));
                omokUpdateRoomList();
                omokUpdateUserList(roomName);
            }
        }

        /* 모든 방의 모든 유저 리턴, 현재 오목 방에 들어갔는지 확인하기 위함 */
        public List<String> getAllUniqueUsers() {
            Set<String> allUsers = new HashSet<>();
            synchronized (omokUserRoomMap) {
                for (OmokRoom room : omokUserRoomMap.values()) {
                    allUsers.addAll(room.getUsers());
                }
            }
            System.out.println(new ArrayList<>(allUsers));
            return new ArrayList<>(allUsers); // Set을 List로 변환
        }

        /* 오목 항복 처리 */
        private void handleOmokSurrender(MessageDTO messageDTO) {
            String[] contents = messageDTO.getContent().split(":", 2);
            String nickname = contents[0];
            String roomName = contents[1];

            if (!omokUserRoomMap.containsKey(roomName)) {
                System.err.println("[서버]: " + roomName + " 방이 존재하지 않습니다.");
                return;
            }

            OmokRoom room = omokUserRoomMap.get(roomName);
            synchronized (omokUserRoomMap) {
                /* 사용자가 방에 존재하는지 */
                if (!room.getUsers().contains(nickName)) {
                    return;
                }

                handleGameWin(room, nickName);

                room.initGameData();
            }
        }

        /* 게임 승리 처리 */
        private void handleGameWin(OmokRoom room, String name) {
            OmokRoom cloneRoom = deepClone(room);
            MessageDTO placeStoneMessage = new MessageDTO(MessageType.OmokPlaceStone, "", cloneRoom);

            // 게임 종료 처리
            if (name.equals(room.getOppoName())) {
                placeStoneMessage.setContent(room.creator);
                db.updateWinLose(room.creator, room.oppoName);
            } else {
                placeStoneMessage.setContent(room.oppoName);
                db.updateWinLose(room.oppoName, room.creator);
            }
            for (ReceiveThread clientThread : clientThreadList) {
                if (room.getUsers().contains(clientThread.nickName)) {
                    clientThread.sendMessage(placeStoneMessage);
                }
            }
        }

        /* 오목 방 나갔을 때 처리 */
        private void handleOmokLeaveRoom(MessageDTO messageDTO) {
            String[] content = messageDTO.getContent().split(":");
            String nickName = content[0];
            String roomName = content[1];
            boolean isObserver = content[2].equals(OBSERVER);
            boolean isSurrender = content[2].equals("SURRENDER");

            if (!omokUserRoomMap.containsKey(roomName)) {
                System.err.println("[서버]: " + roomName + " 방이 존재하지 않습니다.");
                return;
            }

            OmokRoom room = omokUserRoomMap.get(roomName);
            synchronized (omokUserRoomMap) {
                // 사용자가 방에 있는지 확인
                if (!room.getUsers().contains(nickName)) {
//                    sendMessage(new MessageDTO(MessageType.CHAT,
//                            "[서버]: " + roomName + " 방에 " + nickName + "님이 존재하지 않습니다."));
                    return;
                }

                /* 상대방이 항복했을 때 게임 종료 */
                if (isSurrender) {
                    handleGameWin(room, nickName);
                    sendAll(new MessageDTO(MessageType.OmokChat,
                            roomName + ":" + nickName + ":" + "게임에서 나감"));
                }

                // 방 생성자가 나가는 경우 방 삭제
                if (room.getCreator().equals(nickName)) {
                    System.out.println("[서버]: 방 생성자 " + nickName + "가 방을 떠났습니다. 방을 삭제합니다.");

                    // 해당 방의 모든 유저의 역할 삭제
                    for (String user : room.getUsers()) {
                        userRoles.remove(user); // 유저 역할 제거
                        System.out.println("[서버]: " + user + "의 역할이 삭제되었습니다.");
                    }
                    System.out.println("[DEBUG] userRoles 상태: " + userRoles);

                    room.removeAllUser();
                    System.out.println(room.getUsers());

                    // 방 제거
                    omokUserRoomMap.remove(roomName);
                    System.out.println(omokUserRoomMap.keySet());
                    //System.out.println(omokUserRoomMap.get(roomName));

                    // 방 삭제 브로드캐스트
                    sendAll(new MessageDTO(MessageType.OmokRemoveRoom, roomName));
                    omokUpdateRoomList();
                    return;
                }

                // 사용자 제거 (getUsers()의 데이터가 지워지는지 잘 확인해야 함)
                room.removeUser(nickName);
                userRoles.remove(nickName);
                room.initGameData();

                /* 상대방이 나갔을 경우 준비 해제 (관전자 X) */
                if (nickName.equals(room.oppoName)) {
                    room.setOppoReady(false);
                    room.setCreatorReady(false);
                }

                System.out.println("[서버]: " + nickName + "님이 " + roomName + " 방에서 나갔습니다.");

                // 플레이어 역할 업데이트 (플레이어가 2명 이상 남았을 때 관전자를 플레이어로 변환)
                if (!isObserver && room.getUsers().size() >= 2) {
                    String newPlayer = promoteObserverToPlayer(room.getUsers(), roomName);
                    if (newPlayer != null) {
                        // "newPlayer"가 플레이어로 역할 변경
                        // 해당 유저에게 역할 변경 메시지 전송
                        sendObserverToPlayerMessage(newPlayer);
                        int oppoProfile = db.getProfileCharacterNumber(newPlayer);
                        room.setOppoProfile(oppoProfile);
                        room.setOppoName(newPlayer);
                    }
                } else {
                    room.setOppoProfile(11);
                    room.setOppoName("[ Empty ]");
                }

                // 방이 비어있으면 삭제
                if (room.getUsers().isEmpty()) {
                    System.out.println("[서버]: " + roomName + " 방이 비어 있어 삭제됩니다.");
                    omokUserRoomMap.remove(roomName);
                    sendAll(new MessageDTO(MessageType.OmokRemoveRoom, roomName));
                    omokUpdateRoomList();
                } else {
                    // 방 유저 목록 업데이트
                    omokUpdateUserList(roomName);
                }
            }
        }

        private void sendObserverToPlayerMessage(String nickName) {
            for (ReceiveThread clientThread : clientThreadList) {
                if (clientThread.nickName.equals(nickName)) {
                    clientThread.sendMessage(new MessageDTO(MessageType.OmokUpdatePlayer, PLAYER));
                    break;
                }
            }
        }

        // 옵저버 중 한 명을 플레이어로 승격
        private String promoteObserverToPlayer(List<String> roomUsers, String roomName) {
            for (String user : roomUsers) {
                String role = userRoles.get(user);
                if (role != null && role.equals(OBSERVER)) {
                    userRoles.put(user, PLAYER);
                    return user;
                }
            }
            return null;
        }

        private void handleEditUserMethod(MessageDTO messageDTO) {
            String[] content = messageDTO.getContent().split(":");
            String editNickname = content[0];
            String nickname = content[1];
            userNameList.remove(nickname);
            if (!userNameList.contains(nickName)) {
                userNameList.add(nickName);
                updateAdminUserList();
            }
            updateUserList();
        }

        private void createRoom(String roomName) {
            if (!roomMap.containsKey(roomName)) {
                sendAll(new MessageDTO(MessageType.CHAT, "[서버]: 채팅방 " + roomName + "이(가) 생성되었습니다."));
                roomNameList.add(roomName);
                ServerApplication server = new ServerApplication(portNum + subPortNum++, false);
                server.setCurrentRoom(roomName);
                roomMap.put(roomName, server);
                updateRoomList();
            } else {
                sendMessage(new MessageDTO(MessageType.CHAT, "[서버]: 채팅방 " + roomName + "은(는) 이미 존재하는 채팅방입니다."));
            }
        }

        private void removeRoom(String roomName) {
            if (roomMap.containsKey(roomName)) {
                if (roomMap.get(roomName).getNickNameList().isEmpty()) {
                    sendAll(new MessageDTO(MessageType.CHAT, "[서버]: 채팅방 " + roomName + "이(가) 제거되었습니다."));
                    sendAll(new MessageDTO(MessageType.RemoveRoom, ""));
                    roomNameList.remove(roomName);
                    roomMap.remove(roomName);
                    updateRoomList();

                    /* 방 목록이 비어있는 경우 추가 처리 */
                    if (roomNameList.isEmpty()) {
                        sendAll(new MessageDTO(MessageType.ResetRoomList, "EMPTY"));
                    }
                } else {
                    sendMessage(new MessageDTO(MessageType.CHAT, "[서버]: 채팅방 " + roomName + "에 사용자가 있어 제거할 수 없습니다."));
                }
            } else {
                sendMessage(new MessageDTO(MessageType.CHAT, "[서버]: 채팅방 " + roomName + "이(가) 존재하지 않습니다."));
            }
        }

        /* 그룹 채팅방 */
        private void enterRoom(String content) {
            String[] parts = content.split(":");
            String enterNickName = parts[0];
            String enterRoomName = parts[1];

            if (!roomMap.containsKey(enterRoomName)) {
                sendMessage(new MessageDTO(MessageType.CHAT, "[서버]: " + enterRoomName + " 방이 존재하지 않습니다."));
                return;
            }

            if (!roomMap.isEmpty()) {
                /* 이미 방에 있는 경우 */
                if (userRoomMap.containsKey(enterNickName)) {
                    String currentRoom = userRoomMap.get(enterNickName);
                    sendMessage(new MessageDTO(MessageType.CHAT, "[서버]: 이미 " + currentRoom + " 방에 접속 중입니다."));
                } else {
                    int enterPortNum = roomMap.get(enterRoomName).getPortNum();
                    userRoomMap.put(enterNickName, enterRoomName);

                    sendAll(new MessageDTO(MessageType.EnterRoom, enterNickName + ":" + enterRoomName + ":" + enterPortNum));
                }
            } else {
                System.out.println("방이 존재하지 않음");
            }
        }

        private void handleWhisper(String content) {
            String[] parts = content.split(":");
            if (parts.length == 3) {
                String sender = parts[0];
                String receiver = parts[1];
                String whisperMessage = parts[2];

                // 대상 클라이언트의 ReceiveThread를 찾음
                for (ReceiveThread clientThread : clientThreadList) {
                    if (clientThread.nickName.equals(receiver)) {
                        clientThread.sendMessage(new MessageDTO(MessageType.CHAT,
                                "[귓속말 from " + sender + "]: " + whisperMessage));
                        break;
                    }
                }

                // 귓속말을 보낸 사람에게도 전송
                for (ReceiveThread clientThread : clientThreadList) {
                    if (clientThread.nickName.equals(sender)) {
                        clientThread.sendMessage(new MessageDTO(MessageType.CHAT,
                                "[" + sender + "] (귓속말): " + whisperMessage));
                        break;
                    }
                }

                if (adminToolUI!=null) {
                    adminToolUI.displayMessage("[귓속말] " + sender + " -> " + receiver + ": " + whisperMessage);
                }
            }
        }

        private void handleSearchRequest(String keyword) {
            List<String> searchResults = db.getMessagesByKeyword(keyword, currentRoom); // 현재 방의 메시지만 검색
            MessageDTO response = new MessageDTO(MessageType.SEARCH_RESPONSE, String.join("\n", searchResults));
            sendMessage(response); // 검색 결과를 클라이언트에게 전송
        }

        public void sendMessage(MessageDTO messageDTO) {
            try {
                /* 메시지 타입이 "CHAT"인 경우에만 데이터베이스에 저장 */
//                if (messageDTO.getType() == MessageType.CHAT) {
//                    db.saveChatLog(nickName, currentRoom, messageDTO.getContent());
//                }
//
//                if (messageDTO.getType() == MessageType.PrivateChat) {
//                    String[] content = messageDTO.getContent().split(":", 3);
//                    String roomName = content[0];
//                    String nickName = content[1];
//                    String message = content[2];
//                    db.saveChatLog(nickName, roomName, message);
//                }
//
//                if (messageDTO.getType() == MessageType.EMOJI) {
//                    String[] content = messageDTO.getContent().split(":", 2);
//                    String message = "[" + nickName + "]" + ": <EmojiCode>-" + content[1];
//                    db.saveChatLog(nickName, currentRoom, message);
//                }
//
//                if (messageDTO.getType() == MessageType.OmokChat) {
//                    String[] content = messageDTO.getContent().split(":", 3);
//                    String roomName = content[0];
//                    String nickname = content[1];
//                    String message = content[2];
//                    db.saveChatLog(nickname, roomName, message);
//                }

                out.writeObject(messageDTO);
                out.flush();
            } catch (IOException e) {
                System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
            }
        }

        public void sendUserInfo(MessageDTO messageDTO) {
            String searchUser = messageDTO.getContent();
            UserInfo userInfo = db.getUserInfo(searchUser);
            sendMessage(new MessageDTO(MessageType.ShowUserInfo, "", userInfo));
        }

        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                System.err.println("연결 닫기 중 오류 발생: " + e.getMessage());
            }
        }
    }
}
