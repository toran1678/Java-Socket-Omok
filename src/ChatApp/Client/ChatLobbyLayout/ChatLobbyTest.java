package ChatApp.Client.ChatLobbyLayout;

import ChatApp.Server.ServerInfo.ServerInfo;
import Database.Database;

public class ChatLobbyTest {
    public static void main(String[] args) {
        new ChatLobbyLayout("user2", ServerInfo.IPNUMBER,  ServerInfo.PORTNUMBER);
        //new ChatLobbyLayout("user2", ServerInfo.IPNUMBER, ServerInfo.PORTNUMBER);
        //new ChatLobbyLayout("user3", ServerInfo.IPNUMBER, ServerInfo.PORTNUMBER);
    }
}
