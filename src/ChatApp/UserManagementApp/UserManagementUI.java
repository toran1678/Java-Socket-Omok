package ChatApp.UserManagementApp;

import ChatApp.Server.ServerApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class UserManagementUI extends JFrame {
    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JList<String> userList = new JList<>(userListModel);
    private JTextArea messageArea = new JTextArea();
    private ServerApplication serverApplication;

    public UserManagementUI(ServerApplication serverApplication) {
        this.serverApplication = serverApplication;

        setTitle("User Management");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 유저 리스트 패널 설정
        JScrollPane userScrollPane = new JScrollPane(userList);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addMouseListener(new UserListMouseListener());

        // 메시지 표시 영역 설정
        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, userScrollPane, messageScrollPane);
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // 유저 목록 업데이트
    public void updateUserList(List<String> users) {
        userListModel.clear();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }

    // 클라이언트로부터 수신된 메시지 표시
    public void displayMessage(String message) {
        messageArea.append(message + "\n");
    }

    // 유저 리스트에서 오른쪽 클릭 메뉴 처리
    private class UserListMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e) && !userList.isSelectionEmpty()) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem kickMenuItem = new JMenuItem("Kick User");
                JMenuItem editMenuItem = new JMenuItem("Edit User");

                String selectedUser = userList.getSelectedValue();
                kickMenuItem.addActionListener(event -> kickUser(selectedUser));
                editMenuItem.addActionListener(event -> editUser(selectedUser));

                popupMenu.add(kickMenuItem);
                popupMenu.add(editMenuItem);
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    // 유저 강퇴
    private void kickUser(String user) {
        userListModel.removeElement(user);
        messageArea.append("Kicked: " + user + "\n");
        // 서버에 강퇴 메시지 전송

    }

    // 유저 정보 수정
    private void editUser(String user) {
        String newName = JOptionPane.showInputDialog(this, "Edit Username", user);
        if (newName != null && !newName.trim().isEmpty()) {
            userListModel.setElementAt(newName, userList.getSelectedIndex());
            messageArea.append("Edited user to: " + newName + "\n");
        }
    }

    public static void main(String[] args) {
    }
}