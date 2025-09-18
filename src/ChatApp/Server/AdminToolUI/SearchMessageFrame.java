package ChatApp.Server.AdminToolUI;

import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchMessageFrame extends JFrame {
    private JTextField searchField;
    private JButton searchButton;
    private String roomName;

    public SearchMessageFrame(String roomName) {
        this.roomName = roomName;

        // 기본 설정
        setTitle("메시지 검색");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 제목 패널
        JPanel titlePanel = new JPanel(new BorderLayout());
        SwingCompFunc.setTopPanelStyle(titlePanel);
        titlePanel.setPreferredSize(new Dimension(500, 60));

        JLabel titleLabel = new JLabel("선택한 방: " + roomName, SwingConstants.CENTER);
        titleLabel.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 검색 입력 및 버튼 패널
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));
        inputPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("검색어:");
        searchLabel.setFont(new Font("나눔고딕", Font.BOLD, 14));

        searchField = new JTextField(20);
        searchField.setFont(new Font("나눔고딕", Font.PLAIN, 14));

        searchButton = new JButton("검색");
        searchButton.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 14));
        searchButton.setBackground(new Color(255, 255, 255));

        inputPanel.add(searchLabel);
        inputPanel.add(searchField);
        inputPanel.add(searchButton);

        // 프레임에 패널 추가
        add(titlePanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);

        // 검색 버튼 클릭 이벤트
        searchButton.addActionListener(e -> handleSearch());
        searchField.addActionListener(e -> handleSearch());

        setLocationRelativeTo(null);
    }

    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (!searchText.isEmpty()) {
            new SearchMessageResultFrame(roomName, searchText).setVisible(true);
            this.dispose();
        } else {
            new SearchMessageResultFrame(roomName, "").setVisible(true);
            this.dispose();
        }
    }
}
