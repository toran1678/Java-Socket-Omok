package OmokGame;

import ChatApp.Client.ClientApplication;
import Function.SwingCompFunc.SwingCompFunc;
import OmokGame.Panel.GamePanel;
import OmokGame.Panel.RightPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

/* 게임 화면 */
public class GameFrame extends JFrame {
    @Serial
    private static final long serialVersionUID = 1L;

    GameFrame instance = null;
    GamePanel gamePanel = null;
    RightPanel rightPanel = null;
    public String roomName;
    public ClientApplication clientApplication;

    public GameFrame() {
        super("오목");
    }

    public GameFrame(String roomName, ClientApplication clientApplication) {
        this.roomName = roomName;
        this.clientApplication = clientApplication;
        setTitle("방: " + roomName);
        SwingCompFunc.setFrameStyle(this);

        this.rightPanel = new RightPanel(clientApplication, this);
        this.gamePanel = new GamePanel(clientApplication);

        this.setVisible(false);

        this.setLayout(new BorderLayout());
        this.add(gamePanel, BorderLayout.CENTER);
        this.add(rightPanel, BorderLayout.EAST);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 연결 종료 메서드 추가해야 함
                super.windowClosed(e);
            }
        });

        this.setVisible(true);
    }

    public GamePanel getGamePanel() {
        if (gamePanel == null) {
            System.out.println("초기화가 안됨");
            gamePanel = new GamePanel(clientApplication);
        }
        return gamePanel;
    }

    public RightPanel getRightPanel() {
        if (rightPanel == null) {
            System.out.println("초기화가 안됨");
            rightPanel = new RightPanel(clientApplication, this);
        }
        return rightPanel;
    }

    public String getRoomName() {
        return roomName;
    }
}
