package ChatApp.Server.AdminToolUI;

import ChatApp.Server.ServerApplication;
import ChatApp.Server.UserEdit.UserEditPage;
import Database.Database;
import Database.UserInfo.UserInfo;
import Database.UserInfo.showUserInfo;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminToolUI extends JFrame {
    JLabel titleLabel;
    JTextArea chatArea;
    JList<String> userList;
    DefaultListModel<String> userListModel;
    JTextField messageField;
    JButton sendButton;
    JButton addUserButton = new JButton("회원 추가");;
    JButton searchAllUsersButton = new JButton("모든 유저 검색");
    JButton searchAllUsersButton2 = new JButton("모든 유저 검색2");
    JButton restoreUserButton = new JButton("유저 복원");
    JButton searchChatButton = new JButton("채팅 검색");
    ServerApplication server;
    Database db;

    Color topPanelColor = SwingCompFunc.TopPanelColor;
    Color buttonColor =new Color (122, 178, 211);

    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    /* 유저 리스트 우클릭 팝업 메뉴 설정 */
    JPopupMenu userPopupMenu = new JPopupMenu();
    JMenuItem viewInfoItem = new JMenuItem("유저 정보 확인");
    JMenuItem editUserItem = new JMenuItem("유저 정보 편집");
    JMenuItem kickUserItem = new JMenuItem("유저 강퇴");

    public AdminToolUI(ServerApplication server) {
        this.server = server;
        db = new Database();
        initUI();
        updateUserList(server.getUserList()); // 초기 유저 리스트 설정
    }

    private void initUI() {
        // 상단 제목
        titleLabel = new JLabel("관리자 모드", SwingConstants.CENTER);
        SwingCompFunc.setTopLabelStyle(titleLabel);

        setIconImage(gameIcon.getImage());

        // 가운데 채팅 영역
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // 오른쪽 버튼 패널
        JPanel rightButtonPanel = new JPanel();
        rightButtonPanel.setBackground(SwingCompFunc.MiddlePanelColor);
        rightButtonPanel.setLayout(new GridLayout(5, 1, 10, 10));
        rightButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        SwingCompFunc.setButtonStyle(searchAllUsersButton);
        SwingCompFunc.setButtonStyle(addUserButton);
        SwingCompFunc.setButtonStyle(searchAllUsersButton2);
        SwingCompFunc.setButtonStyle(restoreUserButton);
        SwingCompFunc.setButtonStyle(searchChatButton);

        rightButtonPanel.add(addUserButton);
        rightButtonPanel.add(searchAllUsersButton);
        rightButtonPanel.add(searchAllUsersButton2);
        rightButtonPanel.add(restoreUserButton);
        rightButtonPanel.add(searchChatButton);

        // 좌측 유저 목록
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setBackground(SwingCompFunc.MiddlePanelColor);

        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBorder(BorderFactory.createTitledBorder("접속 중인 유저"));
        userScrollPane.setBackground(SwingCompFunc.MiddlePanelColor);
        userScrollPane.setPreferredSize(new Dimension(150, 0));

        // 하단 메시지 입력과 전송 및 검색 버튼
        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("메시지 전송");

        SwingCompFunc.setTopPanelStyle(messagePanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(sendButton);

        SwingCompFunc.setButtonStyle(sendButton);

        // 회원 추가 버튼 클릭 이벤트
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddUserDialog();
            }
        });

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.EAST);

        viewInfoItem.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                showUserInfo(selectedUser);
            }
        });

        editUserItem.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                editUser(selectedUser);
            }
        });

        kickUserItem.addActionListener(e -> {
            String selectedUser = userList.getSelectedValue();
            if (selectedUser != null) {
                int confirmation = JOptionPane.showConfirmDialog(this,
                        "정말 " + selectedUser + "님을 강퇴하시겠습니까?", "강퇴 확인",
                        JOptionPane.YES_NO_OPTION);

                if (confirmation == JOptionPane.YES_OPTION) {
                    server.kickUser(selectedUser); // 서버에서 유저 강퇴 메서드 호출
                    chatArea.append("[관리자] " + selectedUser + "님이 강퇴되었습니다.\n");
                    updateUserList(server.getUserList());
                }
            }
        });

        userPopupMenu.add(viewInfoItem);
        userPopupMenu.add(editUserItem);
        userPopupMenu.add(kickUserItem);

        // 유저 목록에 마우스 리스너 추가하여 팝업 메뉴 표시
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && userList.locationToIndex(e.getPoint()) != -1) {
                    userList.setSelectedIndex(userList.locationToIndex(e.getPoint()));
                    userPopupMenu.show(userList, e.getX(), e.getY());
                }
            }
        });

        // 전송 버튼 클릭 및 엔터 키 입력 시 메시지 전송
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendAdminMessage();
            }
        });

        // 엔터 키로 메시지 전송
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendAdminMessage();
            }
        });

        // 모든 유저 검색 버튼 클릭 시
        searchAllUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchAllUsers();
            }
        });

        searchAllUsersButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllUsersFrame();
            }
        });

        restoreUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restoreUserFrame();
            }
        });

        searchChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roomListFrame();
            }
        });

        // 레이아웃 구성
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(chatScrollPane, BorderLayout.CENTER);
        add(userScrollPane, BorderLayout.WEST);
        add(messagePanel, BorderLayout.SOUTH);
        add(rightButtonPanel, BorderLayout.EAST);

        setTitle("관리자 도구");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // 서버에서 받아온 유저 리스트를 업데이트
    public void updateUserList(List<String> users) {
        userListModel.clear();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }

    // 관리자 메시지 전송 메서드
    private void sendAdminMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            server.sendAdminMessage(message); // 서버에 메시지 전송
            // chatArea.append("[관리자]: " + message + "\n"); // 화면에 표시
            scrollToBottom();
            messageField.setText("");
        }
    }

    // 모든 유저 검색 메서드
    private void searchAllUsers() {
        List<String> allUsers = db.getAllUserNicknames(); // 데이터베이스에서 모든 유저 닉네임 가져오기

        // 결과를 리스트로 보여줄 새로운 창 생성
        JFrame resultFrame = new JFrame("모든 유저 닉네임");
        resultFrame.setLayout(new BorderLayout());
        resultFrame.setSize(400, 450);
        resultFrame.setLocationRelativeTo(this);

        // 창 상단 제목 라벨 설정
        JLabel titleLabel = new JLabel("모든 유저 닉네임 목록", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        resultFrame.add(titleLabel, BorderLayout.NORTH);
        titleLabel.setPreferredSize(new Dimension(0, 40));
        titleLabel.setOpaque(true);

        titleLabel.setBackground(SwingCompFunc.TopPanelColor);
        titleLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        // 리스트 설정
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String user : allUsers) {
            listModel.addElement(user);
        }

        JList<String> resultList = new JList<>(listModel);
        resultList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        resultList.setBackground(new Color(245, 245, 245));
        resultList.setSelectionBackground(new Color(200, 230, 255));
        resultList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 팝업 메뉴 생성
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewInfoItem = new JMenuItem("유저 정보 확인");
        JMenuItem editUserItem = new JMenuItem("유저 정보 편집");

        // 팝업 메뉴 아이템에 이벤트 추가
        viewInfoItem.addActionListener(e -> {
            String selectedUser = resultList.getSelectedValue();
            if (selectedUser != null) {
                showUserInfo(selectedUser); // 유저 정보를 확인하는 메서드 호출
            }
        });

        editUserItem.addActionListener(e -> {
            String selectedUser = resultList.getSelectedValue();
            if (selectedUser != null) {
                editUser(selectedUser); // 유저에게 메시지를 보내는 메서드 호출
            }
        });

        popupMenu.add(viewInfoItem);
        popupMenu.add(editUserItem);

        // 리스트에 마우스 리스너 추가하여 우클릭 시 팝업 메뉴 표시
        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && resultList.locationToIndex(e.getPoint()) != -1) {
                    resultList.setSelectedIndex(resultList.locationToIndex(e.getPoint())); // 우클릭한 유저 선택
                    popupMenu.show(resultList, e.getX(), e.getY());
                }
            }
        });

        // 리스트를 스크롤 가능한 패널로 추가
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        resultFrame.add(scrollPane, BorderLayout.CENTER);

        // 닫기 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("닫기");

        SwingCompFunc.setButtonStyle(closeButton);

        closeButton.addActionListener(e -> resultFrame.dispose());
        buttonPanel.add(closeButton);
        resultFrame.add(buttonPanel, BorderLayout.SOUTH);

        // 창 표시
        resultFrame.setVisible(true);
    }

    private void roomListFrame() {
        List<String> roomList = db.getRoomNames(); // 데이터베이스에서 모든 방 이름 가져오기

        Collections.sort(roomList);

        // 결과를 리스트로 보여줄 새로운 창 생성
        JFrame roomListFrame = new JFrame("모든 방 목록");
        roomListFrame.setLayout(new BorderLayout());
        roomListFrame.setSize(400, 450);
        roomListFrame.setLocationRelativeTo(this);

        // 창 상단 제목 라벨 설정
        JLabel titleLabel = new JLabel("모든 방 이름 목록", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        roomListFrame.add(titleLabel, BorderLayout.NORTH);
        titleLabel.setPreferredSize(new Dimension(0, 40));
        titleLabel.setOpaque(true);

        titleLabel.setBackground(SwingCompFunc.TopPanelColor);
        titleLabel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        // 리스트 설정
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String user : roomList) {
            listModel.addElement(user);
        }

        JList<String> resultList = new JList<>(listModel);
        resultList.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        resultList.setBackground(new Color(245, 245, 245));
        resultList.setSelectionBackground(new Color(200, 230, 255));
        resultList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedRoom = resultList.getSelectedValue();
                    if (selectedRoom != null) {
                        new SearchMessageFrame(selectedRoom).setVisible(true);
                    }
                }
            }
        });

        // 리스트를 스크롤 가능한 패널로 추가
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        roomListFrame.add(scrollPane, BorderLayout.CENTER);

        // 닫기 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("닫기");

        SwingCompFunc.setButtonStyle(closeButton);

        closeButton.addActionListener(e -> roomListFrame.dispose());
        buttonPanel.add(closeButton);
        roomListFrame.add(buttonPanel, BorderLayout.SOUTH);

        // 창 표시
        roomListFrame.setVisible(true);
    }

    private void openAddUserDialog() {
        JDialog dialog = new JDialog(this, "회원 추가", true);
        dialog.setLayout(new BorderLayout(10, 10)); // 여백 추가
        dialog.setSize(600, 280);
        dialog.setLocationRelativeTo(this);
        // dialog.setBackground(Color.WHITE);

        // 상단 제목 패널 생성
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(SwingCompFunc.TopPanelColor); // 상단 부분 색상
        JLabel titleLabel = new JLabel("회원 추가", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE); // 텍스트 색상
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        // 입력 패널 생성
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // 아이디, 닉네임, 비밀번호 입력 필드 생성
        JLabel idLabel = new JLabel("아이디");
        JTextField idField = new JTextField(15);
        JLabel nameLabel = new JLabel("닉네임");
        JTextField nameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("비밀번호");
        JPasswordField passwordField = new JPasswordField(15);

        // 입력 필드 레이아웃 배치
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(passwordField, gbc);

        // 버튼 패널 생성
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // 버튼 중앙 정렬
        buttonPanel.setBackground(new Color(240, 240, 240));
        JButton saveButton = new JButton("저장");
        JButton cancelButton = new JButton("취소");

        // 버튼 스타일
        SwingCompFunc.setButtonStyle(saveButton);
        SwingCompFunc.setButtonStyle(cancelButton);

        // 버튼 패널에 추가
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // 다이얼로그에 패널 추가
        dialog.add(titlePanel, BorderLayout.NORTH); // 상단 제목 패널 추가
        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 저장 버튼 이벤트
        saveButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String nickname = nameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (!id.isEmpty() && !nickname.isEmpty() && !password.isEmpty()) {
                addUserToDatabase(id, nickname, password);
                server.updateAdminUserList(); // UI 업데이트
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "모든 필드를 입력해주세요.", "입력 오류", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 취소 버튼 이벤트
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAllUsersFrame() {
        JFrame userFrame = new JFrame("모든 유저 관리");
        userFrame.setSize(1100, 400);

        SwingCompFunc.setFrameStyle(userFrame);

        /* 상단 제목 패널 */
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        SwingCompFunc.setTopPanelStyle(titlePanel);

        userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("유저 관리");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel);

        // 제목 패널 아래 검색 패널 추가
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setBackground(SwingCompFunc.MiddlePanelColor);

        // 드롭다운 메뉴
        JComboBox<String> searchOptionBox = new JComboBox<>(new String[]{"ID", "Name", "Nickname"});
        searchPanel.add(searchOptionBox);

        // 검색 필드
        JTextField searchField = new JTextField(15);
        SwingCompFunc.setDocumentFilter(searchField, 15);
        searchPanel.add(searchField);

        // 검색 버튼
        JButton searchButton = new JButton("검색");
        SwingCompFunc.setButtonStyle(searchButton);
        searchPanel.add(searchButton);

        // 새로고침 버튼
        JButton refreshButton = new JButton("새로고침");
        SwingCompFunc.setButtonStyle(refreshButton);
        searchPanel.add(refreshButton);

        // 제목 패널과 검색 패널을 하나의 상단 패널로 구성
        JPanel topPanel = new JPanel();
        topPanel.setBackground(SwingCompFunc.TopPanelColor);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        /* JTable Model */
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{
                "ID", "Nickname", "Password", "Name", "Phone Number", "Birth Date", "Gender",
                "Email", "Zipcode", "Address", "Detailed Address", "Profile Character", "Win", "Lose"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 특정 열 수정 불가능하게 설정
                // 예: ID 열(column 0)은 수정 불가
                if (column == 0) { // ID 열
                    return false;
                }
                return true; // 나머지 열은 수정 가능
            }
        };

        JTable userTable = new JTable(tableModel);

        List<String[]> userList = db.getAllUserInfo("users");
        for (String[] user : userList) {
            tableModel.addRow(user);
        }

        // TableRowSorter 설정
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        // 열별 Comparator 설정
        sorter.setComparator(0, Comparator.comparing(o -> o.toString())); // ID (문자열 정렬)
        sorter.setComparator(11, Comparator.comparingInt(o -> Integer.parseInt(o.toString())));
        sorter.setComparator(12, Comparator.comparingInt(o -> Integer.parseInt(o.toString()))); // Win (숫자 정렬)
        sorter.setComparator(13, Comparator.comparingInt(o -> Integer.parseInt(o.toString()))); // Lose (숫자 정렬)

        // 기본 정렬 키 설정 (ID 열 기준)
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        sorter.sort(); // 정렬 강제 적용

        /* Button Panel */
        JPanel buttonPanel = new JPanel();
        JButton modifyButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");
        JButton closeButton = new JButton("닫기");

        SwingCompFunc.setTopPanelStyle(buttonPanel);
        SwingCompFunc.setButtonStyle(modifyButton);
        SwingCompFunc.setButtonStyle(deleteButton);
        SwingCompFunc.setButtonStyle(closeButton);

        // 테이블 스타일 설정
        SwingCompFunc.setAdminToolTableStyle(userTable);

        // 삭제 버튼 동작
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userId = tableModel.getValueAt(selectedRow, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(userFrame, "정말 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean isDeleted = db.moveUserToTrash(userId);
                        if (isDeleted) {
                            tableModel.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(userFrame, "삭제되었습니다.");
                        } else {
                            JOptionPane.showMessageDialog(userFrame, "삭제에 실패했습니다.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(userFrame, "삭제할 유저를 선택해주세요.");
                }
            }
        });

        /* 검색 버튼 동작 */
        searchButton.addActionListener(e -> {
            String selectedOption = searchOptionBox.getSelectedItem().toString();
            String searchText = searchField.getText().trim();

            if (!searchText.isEmpty()) {
                RowFilter<DefaultTableModel, Object> filter = null;
                int columnIndex = switch (selectedOption) {
                    case "ID" -> 0;
                    case "Name" -> 3;
                    case "Nickname" -> 1;
                    default -> -1;
                };

                if (columnIndex != -1) {
                    filter = RowFilter.regexFilter("(?i)" + searchText, columnIndex);
                }

                sorter.setRowFilter(filter);
            } else {
                JOptionPane.showMessageDialog(userFrame, "검색어를 입력하세요.", "오류", JOptionPane.WARNING_MESSAGE);
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    // 선택된 행의 데이터를 가져옵니다.
                    String userId = tableModel.getValueAt(selectedRow, 0).toString().trim(); // ID는 고정
                    String nickname = tableModel.getValueAt(selectedRow, 1).toString().trim();
                    String password = tableModel.getValueAt(selectedRow, 2).toString().trim();
                    String name = tableModel.getValueAt(selectedRow, 3).toString().trim();
                    String phoneNumber = tableModel.getValueAt(selectedRow, 4).toString().trim();
                    String birthDate = tableModel.getValueAt(selectedRow, 5).toString().trim();
                    String gender = tableModel.getValueAt(selectedRow, 6).toString().trim().equals("남자") ? "남자" : "여자";
                    String email = tableModel.getValueAt(selectedRow, 7).toString().trim();
                    String zipcode = tableModel.getValueAt(selectedRow, 8).toString().trim();
                    String address = tableModel.getValueAt(selectedRow, 9).toString().trim();
                    String detailedAddress = tableModel.getValueAt(selectedRow, 10).toString().trim();
                    int profileCharacter = Integer.parseInt(tableModel.getValueAt(selectedRow, 11).toString());
                    int win = Integer.parseInt(tableModel.getValueAt(selectedRow, 12).toString());
                    int lose = Integer.parseInt(tableModel.getValueAt(selectedRow, 13).toString());

                    int result = JOptionPane.showConfirmDialog(userFrame, "수정하시겠습니까?",
                            "수정", JOptionPane.OK_CANCEL_OPTION);
                    if (result == JOptionPane.OK_OPTION) {
                        boolean isUpdate = db.updateUserInfo(userId, nickname, password, name, phoneNumber,
                                birthDate, gender, email, zipcode, address, detailedAddress,
                                profileCharacter, win, lose);

                        if (isUpdate) {
                            JOptionPane.showMessageDialog(userFrame, "수정되었습니다.");
                        } else {
                            JOptionPane.showMessageDialog(userFrame, "수정에 실패했습니다.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(userFrame, "수정할 유저를 선택해주세요.");
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userFrame.dispose();
            }
        });

        /* 새로고침 버튼 동작 */
        refreshButton.addActionListener(e -> {
            // 테이블 모델 초기화
            tableModel.setRowCount(0);

            // 데이터베이스에서 데이터를 다시 불러옴
            List<String[]> updatedUserList = db.getAllUserInfo("users");

            // 새 데이터를 테이블 모델에 추가
            for (String[] user : updatedUserList) {
                tableModel.addRow(user);
            }

            // 검색 필드 초기화 및 필터 제거
            searchField.setText("");
            sorter.setRowFilter(null);

            JOptionPane.showMessageDialog(userFrame, "데이터가 새로고침되었습니다.", "새로고침 완료", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(modifyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // 레이아웃 설정
        userFrame.setLayout(new BorderLayout());
        userFrame.add(topPanel, BorderLayout.NORTH);
        // userFrame.add(titlePanel, BorderLayout.NORTH);
        userFrame.add(new JScrollPane(userTable), BorderLayout.CENTER);
        userFrame.add(buttonPanel, BorderLayout.SOUTH);

        userFrame.setVisible(true);
    }

    private void restoreUserFrame() {
        JFrame userFrame = new JFrame("유저 휴지통 관리");
        userFrame.setSize(1100, 400);

        SwingCompFunc.setFrameStyle(userFrame);

        /* 상단 제목 패널 */
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        SwingCompFunc.setTopPanelStyle(titlePanel);

        userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel titleLabel = new JLabel("휴지통 유저 관리");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(titleLabel);

        /* JTable Model */
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{
                "ID", "Nickname", "Password", "Name", "Phone Number", "Birth Date", "Gender",
                "Email", "Zipcode", "Address", "Detailed Address", "Profile Character", "Win", "Lose"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 특정 열 수정 불가능하게 설정
                // 예: ID 열(column 0)은 수정 불가
                if (column == 0) { // ID 열
                    return false;
                }
                return true; // 나머지 열은 수정 가능
            }
        };

        JTable userTable = new JTable(tableModel);
        List<String[]> userList = db.getAllUserInfo("usersTrash");
        for (String[] user : userList) {
            tableModel.addRow(user);
        }

        // TableRowSorter 설정
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        // 열별 Comparator 설정
        sorter.setComparator(0, Comparator.comparing(o -> o.toString())); // ID (문자열 정렬)
        sorter.setComparator(11, Comparator.comparingInt(o -> Integer.parseInt(o.toString())));
        sorter.setComparator(12, Comparator.comparingInt(o -> Integer.parseInt(o.toString()))); // Win (숫자 정렬)
        sorter.setComparator(13, Comparator.comparingInt(o -> Integer.parseInt(o.toString()))); // Lose (숫자 정렬)

        // 기본 정렬 키 설정 (ID 열 기준)
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
        sorter.sort(); // 정렬 강제 적용

        /* Button Panel */
        JPanel buttonPanel = new JPanel();
        JButton restoreButton = new JButton("복원");
        JButton deleteButton = new JButton("삭제");
        JButton closeButton = new JButton("닫기");

        SwingCompFunc.setAdminToolTableStyle(userTable);

        SwingCompFunc.setTopPanelStyle(buttonPanel);
        SwingCompFunc.setButtonStyle(restoreButton);
        SwingCompFunc.setButtonStyle(deleteButton);
        SwingCompFunc.setButtonStyle(closeButton);

        // 삭제 버튼 동작
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userId = tableModel.getValueAt(selectedRow, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(userFrame, "정말 삭제하시겠습니까?",
                            "삭제 확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean isDeleted = db.deleteUserInfo(userId);
                        if (isDeleted) {
                            tableModel.removeRow(selectedRow);
                            JOptionPane.showMessageDialog(userFrame, "삭제되었습니다.");
                        } else {
                            JOptionPane.showMessageDialog(userFrame, "삭제에 실패했습니다.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(userFrame, "삭제할 유저를 선택해주세요.");
                }
            }
        });

        // 복원 버튼 동작 (추후 구현)
        restoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String userId = (String) tableModel.getValueAt(selectedRow, 0); // ID 가져오기
                    int confirm = JOptionPane.showConfirmDialog(userFrame,
                            "정말 이 유저를 복원하시겠습니까?", "복원 확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean isRestored = db.restoreUser(userId);
                        if (isRestored) {
                            JOptionPane.showMessageDialog(userFrame, "유저가 성공적으로 복원되었습니다.");
                            tableModel.removeRow(selectedRow); // 테이블에서 행 제거
                        } else {
                            JOptionPane.showMessageDialog(userFrame, "유저 복원에 실패했습니다.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(userFrame, "복원할 유저를 선택해주세요.");
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userFrame.dispose();
            }
        });

        buttonPanel.add(restoreButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // 레이아웃 설정
        userFrame.setLayout(new BorderLayout());
        userFrame.add(titlePanel, BorderLayout.NORTH);
        userFrame.add(new JScrollPane(userTable), BorderLayout.CENTER);
        userFrame.add(buttonPanel, BorderLayout.SOUTH);

        userFrame.setVisible(true);
    }

    private void addUserToDatabase(String id, String nickname, String password) {
        db.addUser(id, nickname, password); // 데이터베이스에 회원 추가
        JOptionPane.showMessageDialog(this, "회원이 추가되었습니다: " + nickname);
    }

    public void editUser(String nickname) {
        new UserEditPage(nickname).setVisible(true);
    }

    public void showUserInfo(String nickname) {
        UserInfo userInfo = db.getUserInfo(nickname);
        new showUserInfo(userInfo);
    }

    // 서버로부터 받은 메시지를 채팅창에 표시
    public void displayMessage(String message) {
        chatArea.append(message + "\n");
        scrollToBottom();
    }

    // 스크롤을 가장 아래로 이동시키는 메서드
    private void scrollToBottom() {
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}
