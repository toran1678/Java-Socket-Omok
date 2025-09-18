package Data;

public final class Data {
    /* 마지막으로 놓인 돌의 위치를 저장하는 변수 설정 */
    public static int last = -1;
    /* 현재 턴을 나타내는 변수 (-1: 흑돌, 1: 백돌) 설정 */
    public static int turn = 0;
    /* 관전자 처리 */
    public static boolean observer = false;
    public static int BLACK = 1;
    public static int WHITE = -1;
    /* 나의 돌 색상과 상대방의 돌 색상을 저장하는 변수 설정 */
    public static int myChess = 0;
    public static int oppoChess = 0;
    /* 게임 준비 여부, 게임 시작 여부를 저장하는 변수 설정 */
    public static boolean ready = false;
    public static boolean started = false;
    /* 오목 게임의 판을 나타내는 2차원 배열 설정 */
    public static int[][] chessBoard = new int[15][15];

    public static void initData() {
        last = -1;
        myChess = 0;
        oppoChess = 0;
        turn = 0;
        ready = false;
        started = false;
        chessBoard = new int[15][15];
    }
}