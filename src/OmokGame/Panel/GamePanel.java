package OmokGame.Panel;

import ChatApp.Client.ClientApplication;

import java.awt.BorderLayout;
import java.io.Serial;
import javax.swing.JPanel;

public class GamePanel extends JPanel {
    @Serial
    private static final long serialVersionUID = 1L;
    JPanel gameBody = new JPanel();
    private BoardCanvas boardCanvas = null;
    private ClientApplication clientApplication;

    public GamePanel() {
        gameBody.add(getBoardCanvas());

        this.setLayout(new BorderLayout());
        this.add(gameBody, BorderLayout.CENTER);
    }

    public GamePanel(ClientApplication clientApplication) {
        this.clientApplication = clientApplication;
        gameBody.add(getBoardCanvas());

        this.setLayout(new BorderLayout());
        this.add(gameBody, BorderLayout.CENTER);
    }

    public BoardCanvas getBoardCanvas() {
        if (boardCanvas == null) {
            boardCanvas = new BoardCanvas(clientApplication);
        }
        return boardCanvas;
    }
}
