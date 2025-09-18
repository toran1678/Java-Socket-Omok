package TestPack.TestCode.Socket2;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Room {
    private String roomName;
    private ArrayList<Socket> users = new ArrayList<>();
    private ArrayList<String> userNames = new ArrayList<>();

    public Room(String roomName) {
        this.roomName = roomName;
    }

    public void addUser(Socket socket, String nick) {
        users.add(socket);
        userNames.add(nick);
        broadcast("/enter_user " + userNames.size() + " " + nick);
    }

    public void removeUser(Socket socket, String nick) {
        users.remove(socket);
        userNames.remove(nick);
        broadcast("/exit_user " + userNames.size() + " " + nick);
    }

    public void broadcast(String message) {
        for (Socket user : users) {
            try {
                PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(user.getOutputStream(), "UTF-8")), true);
                writer.println(message);
            } catch (IOException e) {
                e.printStackTrace();  // 적절한 로깅 처리 필요
            }
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public int getUserCount() {
        return users.size();
    }

    public ArrayList<String> getUserNames() {
        return userNames;
    }
}
