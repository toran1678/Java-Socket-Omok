package TestPack.SocketExam02;

import java.io.*;
import java.net.*;
import java.util.*;

public class OmokServer {
    private static Map<String, List<ClientHandler>> rooms = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("ChatApp.Server is running...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ClientHandler(socket)).start();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String username;
        private String roomName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Get username
                out.println("Enter your username: ");
                username = in.readLine();

                // Join or create a room
                out.println("Enter room name to join or create: ");
                roomName = in.readLine();

                synchronized (rooms) {
                    rooms.putIfAbsent(roomName, new ArrayList<>());
                    rooms.get(roomName).add(this);
                }

                sendMessageToRoom(roomName, username + " has joined the room.");

                String message;
                while ((message = in.readLine()) != null) {
                    sendMessageToRoom(roomName, username + ": " + message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                leaveRoom();
            }
        }

        private void sendMessageToRoom(String roomName, String message) {
            List<ClientHandler> roomClients;
            synchronized (rooms) {
                roomClients = rooms.get(roomName);
            }
            for (ClientHandler client : roomClients) {
                client.out.println(message);
            }
        }

        private void leaveRoom() {
            if (roomName != null && username != null) {
                synchronized (rooms) {
                    rooms.get(roomName).remove(this);
                    if (rooms.get(roomName).isEmpty()) {
                        rooms.remove(roomName);
                    }
                }
                sendMessageToRoom(roomName, username + " has left the room.");
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
