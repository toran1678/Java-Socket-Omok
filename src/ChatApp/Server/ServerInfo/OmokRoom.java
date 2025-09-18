package ChatApp.Server.ServerInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.Serial;

public class OmokRoom implements Serializable {
    @Serial     // 직렬화
    private static final long serialVersionUID = 1L;
    public final String roomName;               // 방 이름은 변경되지 않음
    public final String creator;                // 방장 이름은 변경되지 않음
    public int creatorProfileNumber = -1;       // 방장 프로필 번호
    public String oppoName = "[ Empty ]";       // 상대방 이름
    public int oppoProfileNumber = 11;          // 상대방 프로필 번호
    public ArrayList<String> users;             // 사용자 리스트

    public OmokRoom(String roomName, String creator, int creatorProfileNumber) {
        this.roomName = roomName;
        this.creator = creator;
        this.creatorProfileNumber = creatorProfileNumber;
        this.users = new ArrayList<>();
        this.users.add(creator); // 방장은 자동으로 입장
    }

    public synchronized void addUser(String user) {
        if (!users.contains(user)) {
            users.add(user);
            // System.out.println(user + "님이 " + roomName + " 방에 입장했습니다.");
        }
    }

    public synchronized void removeUser(String user) {
        if (users.contains(user)) {
            users.remove(user);
            // System.out.println(user + "님이 " + roomName + " 방을 떠났습니다.");
        }
    }

    public void removeAllUser() {
        users.clear();
    }
    public boolean isEmpty() {
        return users.isEmpty();
    }

    public synchronized ArrayList<String> getUsers() {
        if (users != null) {
            return new ArrayList<>(users);
        }
        users = new ArrayList<>();
        return new ArrayList<>(users);
    }

    // public void setCreator(String creator) { this.creator = creator; }
    public void setOppoName(String oppoName) {
        this.oppoName = oppoName;
    }
    public void setOppoProfile(int oppoProfile) {
        this.oppoProfileNumber = oppoProfile;
    }

    public String getCreator() {
        return creator;
    }
    public int getCreatorProfileNumber() {
        return creatorProfileNumber;
    }
    public String getOppoName() {
        return oppoName;
    }
    public int getOppoProfileNumber() {
        return oppoProfileNumber;
    }

    @Override
    public String toString() {
        return "OmokRoom{" +
                "roomName='" + roomName + '\'' +
                ", creator='" + creator + '\'' +
                ", creatorProfileNumber=" + creatorProfileNumber +
                ", oppoName='" + oppoName + '\'' +
                ", oppoProfileNumber=" + oppoProfileNumber +
                ", users=" + users +
                '}';
    }

    /* 게임 동작 */
    public int last = -1;                   // 마지막에 놓인 돌
    public int BLACK = 1;
    public int WHITE = -1;
    public boolean creatorReady = false;    // 방장 준비
    public boolean oppoReady = false;       // 상대방 준비
    public boolean started = false;         // 게임 시작
    public int turn = 0;                    // 현재 턴을 나타냄 ( 0: 흑돌, 1: 흰돌 )
    public int creatorChess = 0;            // 방장의 돌 색
    public int oppoChess = 0;               // 상대방의 돌 색
    public int[][] chessBoard = new int[15][15];    // 오목판

    public void initGameData() {
        last = -1;
        creatorReady = false;
        oppoReady = false;
        started = false;
        turn = 0;
        creatorChess = 0;
        oppoChess = 0;
        chessBoard = new int[15][15];
    }

    public void setCreatorReady(boolean ready) { this.creatorReady = ready; }
    public void setOppoReady(boolean ready) { this.oppoReady = ready; }
    public void setStarted(boolean start) { this.started = start; }
    public void setTurn(int turn) { this.turn = turn; }
    public void setCreatorChess(int chess) { this.creatorChess = chess; }
    public void setOppoChess(int chess) { this.oppoChess = chess; }
    public void setChessBoard(int[][] chessBoard) {
        this.chessBoard = chessBoard;
    }
    public void setLast(int last) { this.last = last; }

    public boolean getCreatorReady() { return creatorReady; }
    public boolean getOppoReady() { return oppoReady; }
    public boolean getStarted() { return  started; }
    public int getTurn() { return turn; }
    public int getCreatorChess() { return creatorChess; }
    public int getOppoChess() { return oppoChess; }
    public int[][] getChessBoard() { return chessBoard; }
    public int getLast() { return last; }
}