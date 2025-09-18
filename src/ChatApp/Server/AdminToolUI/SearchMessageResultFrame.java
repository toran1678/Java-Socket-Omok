package ChatApp.Server.AdminToolUI;

import Database.Database;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SearchMessageResultFrame extends JFrame {
    private JTextArea resultArea;
    private Database db = new Database();

    public SearchMessageResultFrame(String roomName, String searchText) {
        setTitle("검색 결과");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 상단 제목 패널
        JPanel titlePanel = new JPanel(new BorderLayout());
        SwingCompFunc.setTopPanelStyle(titlePanel);
        titlePanel.setPreferredSize(new Dimension(600, 50));

        JLabel titleLabel = new JLabel("검색한 텍스트 <" + searchText + "> , 방: <" + roomName + ">", JLabel.CENTER);
        titleLabel.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // 결과 표시 영역
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("나눔고딕", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // 패널 추가
        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 검색 결과 로드
        loadSearchResults(roomName, searchText);

        setLocationRelativeTo(null);
    }

    private void loadSearchResults(String roomName, String searchText) {
        ArrayList<String> messages;

        if (searchText.isEmpty()) {
            // 검색어가 없으면 방의 전체 메시지를 가져옴
            messages = db.searchMessages(roomName, null);
        } else {
            // 검색어가 있으면 해당 키워드로 검색
            messages = db.searchMessages(roomName, searchText);
        }

        if (messages.isEmpty()) {
            resultArea.setText("검색 결과를 찾을 수 없습니다.");
        } else {
            for (String message : messages) {
                resultArea.append(message + "\n");
            }
        }
    }
}
