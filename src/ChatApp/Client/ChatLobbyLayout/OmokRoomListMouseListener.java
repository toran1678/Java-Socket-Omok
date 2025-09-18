package ChatApp.Client.ChatLobbyLayout;

import ChatApp.Client.ClientApplication;

import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OmokRoomListMouseListener extends MouseAdapter {
    private final JList<String> roomList;
    private final String nickName;
    private final ClientApplication clientApplication;

    public OmokRoomListMouseListener(JList<String> roomList, String nickName, ClientApplication clientApplication) {
        this.roomList = roomList;
        this.nickName = nickName;
        this.clientApplication = clientApplication;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        String selectedRoom = roomList.getSelectedValue();

        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            // 더블 클릭으로 방 입장
            if (selectedRoom != null) {
                clientApplication.sendMessage(
                        new MessageDTO(MessageType.OmokEnterRoom, nickName + ":" + selectedRoom));
            } else {
                JOptionPane.showMessageDialog(null, "방을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // 우클릭 메뉴 생성
            if (selectedRoom != null) {
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem enterRoomMenu = new JMenuItem("방 입장");
                JMenuItem removeRoomMenu = new JMenuItem("방 제거");

                enterRoomMenu.addActionListener(event -> clientApplication.sendMessage(
                        new MessageDTO(MessageType.OmokEnterRoom, nickName + ":" + selectedRoom)));

                removeRoomMenu.addActionListener(event -> {
                    int confirm = JOptionPane.showConfirmDialog(
                            null,
                            "정말로 방을 제거하시겠습니까?",
                            "방 제거",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        clientApplication.sendMessage(new MessageDTO(MessageType.OmokRemoveRoom, selectedRoom));
                    }
                });

                popupMenu.add(enterRoomMenu);
                popupMenu.add(removeRoomMenu);
                popupMenu.show(roomList, e.getX(), e.getY());
            }
        }
    }
}
