package ChatApp.Server;

import ChatApp.Server.ServerInfo.ServerInfo;

public class Main {
    public static void main(String[] args) {
        new ServerApplication(ServerInfo.PORTNUMBER, true);
    }
}
