package TestPack.TestCode.Socket;

import java.net.Socket;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ChatClient {
    public static void main(String[] args) {
        new StartFrame();
    }
}

class StartFrame extends JFrame {
    JTextField ipnum, name;
    ImageIcon icon;
    String imgURL;
    JPanel down_panel;

    public StartFrame() {
        setTitle("connect");
        // setSize(500,300);
        setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 3,
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
        setLocation(
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2)
                        - (int) (this.getSize().getWidth() / 2),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)
                        - (int) (this.getSize().getHeight() / 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        imgURL = "src/Function/EmotionDisplay/img/삐짐.jpg";

        // 수정한 부분
        if (imgURL == null) {
            System.err.println("이미지 파일을 찾을 수 없습니다.");
            // 대체 이미지 또는 기본 처리
        } else {
            icon = new ImageIcon(imgURL);
            // 이미지 크기 조정 및 설정
        }
        Image img = icon.getImage();
        img = img.getScaledInstance((int) getSize().getWidth(),
                (int) getSize().getHeight() - (int) (getSize().getHeight() / 10), java.awt.Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        JPanel background = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(icon.getImage(), 0, 0, null);
                setOpaque(false); // 그림을 표시하게 설정,투명하게 조절
                super.paintComponent(g);
            }
        };

        background.setBounds(0, 0, (int) this.getSize().getWidth(),
                (int) (this.getSize().getHeight()) - (int) (getSize().getHeight() / 5));
        background.setLayout(null);
        down_panel = new JPanel();
        JLabel ipnum_label = new JLabel("IP: ");
        ipnum = new JTextField(15);
        ipnum.setText("0.0.0.0");
        JLabel name_label = new JLabel("NickName: ");
        name = new JTextField(15);
        name.setText("Name");
        JButton start_button = new JButton("Connect");
        int down_panel_X = (int) (background.getSize().getWidth() / 3) - (int) (background.getSize().getWidth() / 9);
        int down_panel_Y = (int) (background.getSize().getHeight() - (int) background.getSize().getHeight() / 10);
        int down_panel_width = (int) background.getSize().getWidth() / 2;
        int down_panel_height = (int) background.getSize().getHeight() / 5;
        down_panel.setBounds(down_panel_X, down_panel_Y, down_panel_width, down_panel_height);
        down_panel.setLayout(new FlowLayout());
        down_panel.add(ipnum);
        down_panel.add(name);
        down_panel.add(start_button);
        down_panel.setBackground(new Color(255, 0, 0, 0));
        background.add(down_panel);
        add(background);
        start_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ip = ipnum.getText();
                String nick_name = name.getText();
                if (isIPv4(ip)) {
                    dispose();
                    new Chater(ip, nick_name);
                } else {
                    JOptionPane.showMessageDialog(null, "ip를 제대로 입력해주세요.");
                }
            }
        });
        // setUndecorated(true);

        setVisible(true);
    }

    public static boolean isIPv4(String str) {
        return Pattern.matches("((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])([.](?!$)|$)){4}", str);
    }
}

class Chater {
    Receiver receiver;
    Socket socket;
    ChatFrame frame;

    public Chater(String ip, String name) {
        try {

            socket = new Socket(ip, 2400);
            frame = new ChatFrame(socket, name);
            receiver = new Receiver(socket, frame);
            receiver.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("서버 접속 실패");
        }
    }
}

class ChatFrame extends JFrame {
    Socket socket;
    Sender sender;
    JTextArea receiveArea, usersListArea, sendArea;
    JLabel users_label;

    public ChatFrame(Socket s, String name) {
        socket = s;
        sender = new Sender(socket, name);

        setTitle("ChatRoom");
        setLayout(new BorderLayout());
        setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 4,
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);
        setLocation(
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2)
                        - (int) (this.getSize().getWidth() / 2),
                (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2)
                        - (int) (this.getSize().getHeight() / 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowClosed(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowIconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowActivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                // TODO Auto-generated method stub

            }

        });
        JButton send_button = new JButton("Send");
        users_label = new JLabel("접속자 수: ");
        receiveArea = new JTextArea(19, 24);
        receiveArea.setEditable(false);
        receiveArea.setLineWrap(true);
        usersListArea = new JTextArea(19, 10);
        usersListArea.setEditable(false);
        usersListArea.setLineWrap(true);
        sendArea = new JTextArea(1, 20);
        sendArea.setLineWrap(true);
        JPanel topPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(receiveArea);
        JScrollPane scrollPane2 = new JScrollPane(usersListArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(users_label, BorderLayout.NORTH);
        topPanel.add(scrollPane2, BorderLayout.WEST);
        topPanel.add(scrollPane, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        bottomPanel.add(sendArea);
        bottomPanel.add(send_button);
        add(bottomPanel, BorderLayout.SOUTH);
        sendArea.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    sendChat();
                    e.consume();
                }
            }
        });

        send_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendChat();
            }

        });

        setVisible(true);
    }

    void updateChat(String s) {
        receiveArea.append(s + "\n");
        receiveArea.setCaretPosition(receiveArea.getDocument().getLength());
    }

    void updateUserList(String s) {
        String[] userList = s.split(",");
        usersListArea.setText("");
        if (userList.length == 0 && !s.equals("")) {
            users_label.setText("접속자 수: " + 2);
            usersListArea.append(s + "\n");
            return;
        }
        users_label.setText("접속자 수: " + (userList.length + 1));
        for (int i = 0; i < userList.length; i++) {

            usersListArea.append(userList[i] + "\n");
        }

    }

    void enterUser(String n, String user) {
        users_label.setText("접속자 수: " + n);
        usersListArea.append(user + "\n");
    }

    void exitUser(String n, String user) {
        users_label.setText("접속자 수: " + n);
        usersListArea.setText(usersListArea.getText().replaceFirst(user + "\n", ""));
    }

    void sendChat() {
        if (sendArea.getText().isEmpty()) {
            return;
        }
        sender.sendMessage(sendArea.getText());
        sendArea.setText(null);
    }

    void quit() {
        sender.quit();
    }
}

class Receiver extends Thread {
    Socket socket;
    ChatFrame cFrame;
    ArrayList<String> chatLogs = new ArrayList<String>();

    public Receiver(Socket s, ChatFrame frame) {
        socket = s;
        cFrame = frame;
    }

    @Override
    public void run() {
        InputStream input;// 읽는 stream
        BufferedReader reader;// input 내용을 buffer로 받아옴
        try {
            while (true) {
                String s = null;
                input = socket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(input, "UTF-8")); // 읽기
                if ((s = reader.readLine()) != null) {
                    if (s.equals("/quit")) {
                        socket.close();
                        break;
                    } else if (s.startsWith("/enter_user "))
                    {
                        cFrame.enterUser(s.split(" ")[1], s.split(" ")[2]);
                    } else if (s.startsWith("/exit_user ")) {
                        cFrame.exitUser(s.split(" ")[1], s.split(" ")[2]);
                    } else if (s.startsWith("/update_user_list ")) {
                        cFrame.updateUserList(s.split(" ")[1]);
                        s = reader.readLine();
                        cFrame.enterUser(s.split(" ")[1], s.split(" ")[2]);
                    } else {
                        chatLogs.add(s);
                        cFrame.updateChat(s);
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}

class Sender {
    Socket socket;
    String name;

    public Sender(Socket s, String n) {
        socket = s;
        initName(n);
    }

    void quit() {
        try {
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), true);
            writer.println("/quit");
        } catch (Exception e) {
        }
    }

    void sendMessage(String s) {
        try {
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), true);
            writer.println(this.name + ": " + s);
        } catch (Exception e) {

        }
    }

    public void initName(String s) {
        try {
            this.name = s;
            OutputStream out = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "UTF-8")), true);
            writer.println("/init_name " + s);
        } catch (Exception e) {

        }
    }
}