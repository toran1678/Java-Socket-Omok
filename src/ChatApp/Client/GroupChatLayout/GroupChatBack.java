package ChatApp.Client.GroupChatLayout;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import Database.UserInfo.showUserInfo;
import Database.UserInfo.UserInfo;

public class GroupChatBack extends Thread {
    private String nickName, roomName, message, ipAddress;
    private int portNum;
    public Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private GroupChatLayout groupChatLayout;
    ArrayList<String> nickNameList = new ArrayList<>(); // 유저목록을 저장합니다.

    public void setGui(GroupChatLayout groupChatLayout) {
        this.groupChatLayout = groupChatLayout;
        // 실행했던 ClientGUI 그 자체의 정보를 들고옵니다.
        this.groupChatLayout.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                closeConnection();
            }
        });
    }

    public void setUserInfo(String nickName, String roomName, String ipAddress, int portNum) {
        /* "ClientGUI"로부터 닉네임, 아이피, 포트 값을 받아옵니다. */
        this.nickName = nickName;
        this.roomName = roomName;
        this.ipAddress = ipAddress;
        this.portNum = portNum;
    }

    @Override
    public void run() {
        try {
            connectToServer();
            while (in != null) {
                MessageDTO messageDTO = (MessageDTO) in.readObject();
                handleIncomingMessage(messageDTO);
            }
        } catch (EOFException e) {
            System.out.println("서버와 연결이 종료되었습니다.");
        } catch (Exception e) {
            System.err.println("오류 발생: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void connectToServer() throws IOException {
        socket = new Socket(ipAddress, portNum);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        out.writeObject(new MessageDTO(MessageType.CHAT, nickName));
        out.flush();
    }

    private void handleIncomingMessage(MessageDTO messageDTO) {
        String message = messageDTO.getContent();
        switch (messageDTO.getType()) {
            case ResetUserList:
                updateUserList(messageDTO.getContent());
                break;
            case UserLeft:
                nickNameList.remove(message);
                groupChatLayout.removeUser(message);
                groupChatLayout.resetUserList(nickNameList);
                break;
            case SEARCH_RESPONSE:
                List<String> results = Arrays.asList(messageDTO.getContent().split("\n"));
                groupChatLayout.displaySearchResults(results);
                break;
            case EnterUser:
                updateUserList(messageDTO.getContent());
                break;
            case LeaveUser:
                nickNameList.remove(message);
                groupChatLayout.removeUser(message);
                groupChatLayout.resetUserList(nickNameList);
                break;
            case ShowUserInfo:
                showUserInfo(messageDTO);
                break;
            case EMOJI:
                handleEmojiChat(messageDTO.getContent());
                break;
            case CHAT:
                groupChatLayout.appendMessage(message);
                break;
            default:
                groupChatLayout.appendMessage("[알림]: 알 수 없는 메시지 타입입니다.");
        }
    }

    private void showUserInfo(MessageDTO messageDTO) {
        UserInfo userinfo = (UserInfo) messageDTO.getObject();
        new showUserInfo(userinfo);
    }

    public void handleEmojiChat(String _content) {
        String[] content = _content.split(":", 2);
        String sender = content[0];
        int emoji = Integer.parseInt(content[1]);

        groupChatLayout.appendEmoji(emoji, sender);
    }

    private void updateUserList(String newUser) {
        groupChatLayout.userModel.removeAllElements();
        groupChatLayout.userList.removeAll();

        nickNameList.add(newUser);
        groupChatLayout.resetUserList(nickNameList);
    }

    public void sendMessage(MessageDTO messageDTO) {
        try {
            out.writeObject(messageDTO);
            out.flush();
        } catch (IOException e) {
            System.err.println("메시지 전송 중 오류 발생: " + e.getMessage());
        }
    }

    private void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("연결 종료 중 오류 발생: " + e.getMessage());
        }
    }
}
