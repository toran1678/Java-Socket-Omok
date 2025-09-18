package ChatApp.Client.OmokRankingLayout;

import Database.Database;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;
import java.sql.*;

public class OmokRankingFrame extends JFrame {
    Database db = new Database();
    public OmokRankingFrame() {
        setTitle("Omok Ranking");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        SwingCompFunc.setFrameStyle(this);

        // 상단 제목
        // 상단 제목을 담을 패널 생성
        JPanel titlePanel = new JPanel();
        JPanel buttonPanel = new JPanel();

        JButton refreshButton = new JButton("새로고침");
        JButton sortWinsButton = new JButton("승리 기준 정렬");
        JButton sortWinRateButton = new JButton("승률 기준 정렬");
        JButton closeButton = new JButton("닫기");

        SwingCompFunc.setButtonStyle(refreshButton);
        SwingCompFunc.setButtonStyle(closeButton);
        SwingCompFunc.setButtonStyle(sortWinsButton);
        SwingCompFunc.setButtonStyle(sortWinRateButton);

        titlePanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("오목 랭킹", SwingConstants.CENTER);
        titleLabel.setFont(new Font("나눔 고딕 BOLD", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER); // 제목 추가
        add(titlePanel, BorderLayout.NORTH); // 상단에 패널 배치

        SwingCompFunc.setTopPanelStyle(titlePanel);
        SwingCompFunc.setTopPanelStyle(buttonPanel);

        // 중앙 랭킹 공간 (JTable)
        String[] columnNames = {"순위", "닉네임", "승리", "패배", "승률"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // 모든 셀 수정 불가능
                return false;
            }
        };
        JTable rankingTable = new JTable(tableModel);

        /* 가운데 정렬 */
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < rankingTable.getColumnCount(); i++) {
            rankingTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(rankingTable);
        add(scrollPane, BorderLayout.CENTER);

        SwingCompFunc.setRankingTableStyle(rankingTable);

        // 하단 버튼
        buttonPanel.add(refreshButton);
        buttonPanel.add(sortWinsButton);
        buttonPanel.add(sortWinRateButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 버튼 이벤트
        refreshButton.addActionListener(e -> db.loadUserRanking(tableModel));
        closeButton.addActionListener(e -> dispose());

        /* 정렬 기능 추가 */
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        rankingTable.setRowSorter(sorter);

        sortWinsButton.addActionListener(e -> {
            sorter.setSortKeys(List.of(new RowSorter.SortKey(2, SortOrder.DESCENDING))); // 2번째 열(승리) 기준 내림차순
            sorter.sort(); // 정렬 적용
        });

        sortWinRateButton.addActionListener(e -> {
            sorter.setSortKeys(List.of(new RowSorter.SortKey(4, SortOrder.DESCENDING))); // 4번째 열(승률) 기준 내림차순
            sorter.sort(); // 정렬 적용
        });

        // 초기 랭킹 데이터 로드
        db.loadUserRanking(tableModel);
    }

    public static void main(String[] args) {
        new OmokRankingFrame().setVisible(true);
    }
}