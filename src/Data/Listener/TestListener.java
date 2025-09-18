package Data.Listener;

import ChatApp.Client.ClientApplication;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestListener implements ActionListener {
    ClientApplication clientApplication;
    public TestListener(ClientApplication clientApplication) {
        this.clientApplication = clientApplication;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 임의의 방 이름 리스트
        String[] rooms = {"Room 1", "Room 2", "Room 3", "Room 4"};

        // 새 창 설정
        JFrame roomFrame = new JFrame("Room List");
        roomFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        roomFrame.setSize(300, 300);

        // 리스트 생성
        JList<String> roomList = new JList<>(rooms);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(roomList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("방 목록"));

        // 방 선택 이벤트
        roomList.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                String selectedRoom = roomList.getSelectedValue();
                JOptionPane.showMessageDialog(roomFrame, "You selected: " + selectedRoom);
                clientApplication.sendMessage(new MessageDTO(MessageType.OmokEnterRoom, clientApplication.getNickName() + ":" + selectedRoom));
            }
        });

        // 레이아웃 설정 및 추가
        roomFrame.setLayout(new BorderLayout());
        roomFrame.add(scrollPane, BorderLayout.CENTER);

        // 닫기 버튼
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(ev -> roomFrame.dispose());
        roomFrame.add(closeButton, BorderLayout.SOUTH);

        roomFrame.setLocationRelativeTo(null);
        roomFrame.setVisible(true);
    }
}