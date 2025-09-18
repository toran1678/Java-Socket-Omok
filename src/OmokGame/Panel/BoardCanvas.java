package OmokGame.Panel;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

import javax.imageio.ImageIO;

import ChatApp.Client.ClientApplication;
import Data.Data;
import Data.Listener.BoardListener;

// 오목 판 GUI

public class BoardCanvas extends Canvas {
    @Serial
    private static final long serialVersionUID = 1L;
    // 오목 판 크기
    private final int MAP_WIDTH = 531; // 가로
    private final int MAP_HEIGHT = 531; // 세로

    // 이미지 캐시를 저장할 BufferedImage 객체 생성
    BufferedImage chessBoardImage = new BufferedImage(MAP_WIDTH, MAP_HEIGHT, ColorSpace.TYPE_RGB);
    // Graphics2D 객체 생성 및 "BufferedImage"에 그래픽을 그리기 위한 설정
    Graphics2D g = chessBoardImage.createGraphics();
    ClientApplication clientApplication;

    // 생성자
    public BoardCanvas(ClientApplication client) {
        this.clientApplication = client;
        // 오목 판 이미지 그리기 메소드 호출
        this.paintBoardImage();
        // Canvas 크기 설정
        this.setSize(MAP_WIDTH, MAP_HEIGHT);
        // 마우스 리스너 등록
        this.addMouseListener(new BoardListener(clientApplication));
    }

    // 오목 판의 그래픽을 그리는 메소드
    @Override
    public void paint(Graphics g) {
        // chessBoardImage 이미지를 "Canvas"에 그림
        g.drawImage(chessBoardImage, 0, 0, null);
    }

    // 오목 판 이미지를 그리는 메소드
    public void paintBoardImage() {
        // 오목 판의 배경과 돌을 그리는 메소드 호출
        this.paintBackground();
        this.paintChess();
    }

    // 오목 판의 배경 이미지를 그리는 메소드
    public void paintBackground() {
        try {
            // 이미지 경로에서 오목 판 이미지를 가져와 그림
            BufferedImage background = ImageIO.read(new File("src/Data/OmokImage/map.png"));
            g.drawImage(background, 0, 0, null);

        } catch (IOException e) {

            // 예외 발생 시, 기본 배경 출력
            this.setBackground(new Color(210, 180, 140));
            g.setColor(Color.black);

            // 가로, 세로 라인
            for (int i = 0; i < 15; i++) {
                g.drawLine((35 * i + 20), 20, (35 * i + 20), 510);
                g.drawLine(20, (35 * i + 20), 510, (35 * i + 20));
            }
            // 바둑 판 위치를 쉽게 알기 위한 검은 점 5개
            g.fillRect(122, 122, 7, 7);
            g.fillRect(402, 122, 7, 7);
            g.fillRect(122, 402, 7, 7);
            g.fillRect(402, 402, 7, 7);
            g.fillRect(262, 262, 7, 7);

            e.printStackTrace();
        }
    }

    // 오목 판에 돌을 그리는 메소드
    public void paintChess() {
        BufferedImage black = null;
        BufferedImage white = null;

        // 오목 판의 모든 위치를 확인
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                // 흑 돌
                if (Data.chessBoard[i][j] == Data.BLACK) {
                    try {
                        // 놓은 돌 black, 최근 놓은 돌 black2 이미지 가져오기
                        if (15 * j + i == Data.last) {
                            black = ImageIO.read(new File("src/Data/OmokImage/black2.png"));
                        } else {
                            black = ImageIO.read(new File("src/Data/OmokImage/black.png"));
                        }
                        // 돌 이미지를 그림
                        g.drawImage(black, i * 35 + 4, j * 35 + 4, null);

                    } catch (IOException e) {
                        // 이미지가 없을 경우 흑 돌을 기본으로 그림
                        g.fillOval(i * 35 + 4, j * 35 + 4, 33, 33);

                        e.printStackTrace();
                    }
                }
                // 백 돌
                else if (Data.chessBoard[i][j] == Data.WHITE) {
                    try {
                        // 놓은 돌 white, 최근 놓은 돌 white2 이미지 가져오기
                        if (15 * j + i == Data.last) {
                            white = ImageIO.read(new File("src/Data/OmokImage/white2.png"));
                        } else {
                            white = ImageIO.read(new File("src/Data/OmokImage/white.png"));
                        }
                        // 돌 이미지를 그림
                        g.drawImage(white, i * 35 + 4, j * 35 + 4, null);
                    } catch (IOException e) {
                        // 이미지가 없을 경우 백 돌을 기본으로 그림
                        g.setColor(Color.white);
                        g.fillOval(i * 35 + 4, j * 35 + 4, 33, 33);
                        g.setColor(Color.black);

                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    // 오목 판의 가로 크기를 반환하는 메소드
    public int getMapWidth() {
        return MAP_WIDTH;
    }

    // 오목 판의 세로 크기를 반환하는 메소드
    public int getMapHeight() {
        return MAP_HEIGHT;
    }
}