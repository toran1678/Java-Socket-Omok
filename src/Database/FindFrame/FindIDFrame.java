package Database.FindFrame;

import Database.Database;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindIDFrame extends JFrame {
    JTextField nameField;
    JTextField phoneField;
    JButton findIdButton;
    JLabel resultLabel;

    Database db = new Database();

    public FindIDFrame() {
        setTitle("아이디 찾기");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 250);
        setLayout(new BorderLayout());

        SwingCompFunc.setFrameStyle(this);

        // 상단 제목 패널
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("아이디 찾기");
        titleLabel.setFont(new Font("나눔 고딕", Font.BOLD, 20)); // 글꼴 설정
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        titleLabel.setForeground(Color.WHITE);

        SwingCompFunc.setTopPanelStyle(titlePanel);

        // 중앙 패널 (이름, 핸드폰 번호, 버튼)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // 컴포넌트 간 여백 설정
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 이름 입력
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0; // 열 번호
        gbc.gridy = 0; // 행 번호
        gbc.weightx = 0.3; // 레이블 너비 비율
        centerPanel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        nameField.setPreferredSize(new Dimension(150, 30)); // 텍스트 필드 크기 설정
        gbc.gridx = 1; // 텍스트 필드 열 번호
        gbc.gridy = 0; // 동일한 행 번호
        gbc.weightx = 0.7; // 텍스트 필드 너비 비율
        centerPanel.add(nameField, gbc);

        // 핸드폰 번호 입력
        JLabel phoneLabel = new JLabel("휴대폰 번호:");
        phoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        centerPanel.add(phoneLabel, gbc);

        phoneField = new JTextField(15);
        phoneField.setPreferredSize(new Dimension(150, 30)); // 텍스트 필드 크기 설정
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        centerPanel.add(phoneField, gbc);

        String placeholder = "'-'를 포함하여 입력하세요.";
        SwingCompFunc.setPlaceholderTextField(phoneField, placeholder);

        // 아이디 찾기 버튼
        findIdButton = new JButton("아이디 찾기");
        findIdButton.setPreferredSize(new Dimension(100, 30)); // 버튼 크기 설정
        findIdButton.addActionListener(new FindIdActionListener());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // 버튼은 두 열을 차지
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER; // 버튼 중앙 정렬
        centerPanel.add(findIdButton, gbc);

        SwingCompFunc.setButtonStyle(findIdButton);

        // 결과 출력
        resultLabel = new JLabel("이름과 핸드폰 번호를 입력하세요.");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        centerPanel.add(resultLabel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // 보여주기
        setVisible(true);
    }

    private class FindIdActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                resultLabel.setText("모든 필드를 입력해주세요.");
                return;
            }

            String userId = db.findID(name, phone);
            if (userId != null) {
                resultLabel.setText("찾은 아이디: " + userId);
            } else {
                resultLabel.setText("입력한 정보로 아이디를 찾을 수 없습니다.");
            }
        }
    }

    public static void main(String[] args) {
        new FindIDFrame().setVisible(true);
    }
}
