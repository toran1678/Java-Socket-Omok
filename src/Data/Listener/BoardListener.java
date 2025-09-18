package Data.Listener;

import ChatApp.Client.ClientApplication;
import Function.DTO.MessageDTO;
import Function.DTO.MessageType;
import OmokGame.Panel.BoardCanvas;
import Data.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardListener extends MouseAdapter {
    ClientApplication client;
    public BoardListener(ClientApplication clientApplication) {
        this.client = clientApplication;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 이벤트가 발생한 캔버스를 가져옴
        BoardCanvas canvas = (BoardCanvas) e.getSource();

        if (client == null) {
            System.out.println("[Null]");
            return;
        }

        if (Data.observer) {
            JOptionPane.showMessageDialog(null, "당신은 관전자입니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!Data.started) {
            // 게임 시작되지 않았을 경우
            JOptionPane.showMessageDialog(null, "게임이 시작되지 않았습니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Data.turn != Data.myChess) {
            // 내 턴이 아닐 때
            JOptionPane.showMessageDialog(null, "당신의 차례가 아닙니다.",
                    "경고", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (e.getX() < canvas.getMapWidth() - 6 && e.getY() < canvas.getHeight() - 7) {
            // 클릭한 위치를 오목 보드 상의 좌표로 변환
            int x = e.getX() / 35;
            int y = e.getY() / 35;

            // 돌 위치 계산
            int lastPosition = 15 * y + x;
            // 해당 위치에 이미 돌이 없는지 확인
            if (Data.chessBoard[x][y] == 0) {
                // 해당 위치에 돌을 놓고 서버에 플레이 메시지 전송
                // 마지막에 놓은 위치를 업데이트
                Data.last = lastPosition;

                Data.chessBoard[x][y] = Data.myChess;

                // 상대방 턴으로 변경
                Data.turn = Data.oppoChess;

                MessageDTO placeStoneMessage = new MessageDTO(
                        MessageType.OmokPlaceStone, client.gameFrame.getRoomName() + ":" + client.getNickName() + ":" + Data.last +
                        ":" + x + ":" + y,
                        Data.chessBoard
                );
                client.sendMessage(placeStoneMessage);
                client.gameFrame.getRightPanel().displayMessage("[서버]: 돌을 두었습니다.");
            } else {
                JOptionPane.showMessageDialog(null, "이곳에는 놓을 수 없습니다.",
                        "경고", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    @Override /* 마우스가 컴포넌트에 들어왔을 때 이벤트 */
    public void mouseEntered(MouseEvent e) {
        BoardCanvas canvas = (BoardCanvas) e.getSource();
        // 커서를 손가락 모양으로 변경
        canvas.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override /* 마우스가 컴포넌트에서 나갔을 때 이벤트 */
    public void mouseExited(MouseEvent e) {
        BoardCanvas canvas = (BoardCanvas) e.getSource();
        // 커서를 기본 화살표 모양으로 변경
        canvas.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
