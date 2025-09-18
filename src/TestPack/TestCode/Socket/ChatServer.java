package TestPack.TestCode.Socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ChatServer extends Thread {
    /* 접속한 소켓 리스트 */
    static ArrayList<Socket> users = new ArrayList<Socket>();
    /* 접속한 사용자들의 닉네임 리스트 */
    static ArrayList<String> userNames = new ArrayList<String>();
    /* 현재 클라이언트의 소켓 */
    Socket socket;
    /* 현재 클라이언트의 닉네임 */
    String nick="";

    /* 생성자 */
    public ChatServer(Socket socket) {
        this.socket = socket;

        users.add(socket);
        String nicks="";
        for (int i = 0; i < userNames.size(); i++) {
            nicks += userNames.get(i);
            if (i != userNames.size()-1) nicks+=",";
        }

        if (!nicks.isEmpty()){
            try{
                OutputStream out3 = socket.getOutputStream(); //
                PrintWriter writer3= new PrintWriter(new BufferedWriter(new OutputStreamWriter(out3,"UTF-8")),true);
                writer3.println("/update_user_list "+nicks);

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input,"UTF-8"));
            OutputStream out;
            PrintWriter writer;

            while (true) {
                String s = null;
                if (reader.ready()) {
                    if ((s = reader.readLine()) != null) {

                        if (s.equals("/quit")) {

                            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),true);
                            writer.println(s);

                            break;
                        }
                        if (s.startsWith("/init_name ")) {
                            nick=s.split(" ")[1];
                            userNames.add(nick);
                            for (int i = 0; i < users.size(); i++) {

                                out = users.get(i).getOutputStream(); //
                                writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out,"UTF-8")),true);
                                writer.println("/enter_user "+users.size()+" "+nick);
                            }

                            continue;
                        }
                        for (int i = 0; i < users.size(); i++) {

                            out = users.get(i).getOutputStream();
                            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out,"UTF-8")),true);
                            //PrintWriter writer = new PrintWriter(out, true); //
                            writer.println(s);
                            // writer.flush();
                        }}

                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{
            System.out.println("socket disconnect");
            users.remove(socket);
            userNames.remove(nick);
            for (int i = 0; i < users.size(); i++) {

                OutputStream out;
                try {
                    out = users.get(i).getOutputStream();
                    PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out,"UTF-8")),true);
                    //PrintWriter writer = new PrintWriter(out, true); //
                    writer.println("/exit_user "+users.size()+" "+nick);
                    //socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // writer.flush();
            }
        }
    }

    public static void main(String[] args) {


        // TODO Auto-generated method stub
        int socket = 2400;
        try {
            ServerSocket ss = new ServerSocket(socket);
            System.out.println("서버가 실행되었습니다.");
            while (true) {

                Socket user = ss.accept();
                System.out.println("주소 : " + user.getInetAddress() + " : " + user.getLocalPort());
                Thread serverThread = new ChatServer(user);
                serverThread.start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}