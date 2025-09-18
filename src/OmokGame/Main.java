package OmokGame;

import ChatApp.Client.ClientApplication;
import Data.Data;
import OmokGame.Panel.BoardCanvas;
import OmokGame.Panel.GamePanel;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Data.chessBoard[5][5] = 1;
        Data.chessBoard[10][10] = -1;

        GameFrame gameFrame = new GameFrame("name", new ClientApplication());

        BoardCanvas mapCanvas = gameFrame.getGamePanel().getBoardCanvas();
        mapCanvas.paintBoardImage();
        mapCanvas.repaint();

        //JLabel myProfileLabel = GameFrame.getInstance().getRightPanel().getMyProfileLabel();
        //myProfileLabel.setBorder(BorderFactory.createTitledBorder("플레이어3"));

        //System.out.println(Data.chessBoard[5][5]);
        //new GameFrame("Name").setVisible(true);
    }
}
