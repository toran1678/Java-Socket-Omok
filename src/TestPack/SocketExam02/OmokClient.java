package TestPack.SocketExam02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class OmokClient {
    private String username;
    private String roomName;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private DefaultListModel<String> roomListModel;

    public OmokClient(String serverAddress) throws IOException {
        socket = new Socket(serverAddress, 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        username = JOptionPane.showInputDialog(frame, "Enter your username:");
        roomName = JOptionPane.showInputDialog(frame, "Enter room name to join or create:");

        out.println(username); // send username to server
        out.println(roomName); // send room name to server

        setupGUI();
        new Thread(new MessageListener()).start();
    }

    private void setupGUI() {
        frame = new JFrame("Omok Chat Room: " + roomName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(200, 30));

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.getContentPane().add(chatScrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        inputField.addActionListener(e -> sendMessage()); // Send on Enter key
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.trim().isEmpty()) {
            out.println(message);
            inputField.setText("");
        }
    }

    private class MessageListener implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String serverAddress = JOptionPane.showInputDialog(
                null, "Enter server IP:", "Welcome to Omok Chat", JOptionPane.QUESTION_MESSAGE);
        if (serverAddress != null && !serverAddress.isEmpty()) {
            new OmokClient(serverAddress);
        }
    }
}
