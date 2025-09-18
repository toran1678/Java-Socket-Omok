package Database.FindFrame;

import Database.Database;
import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PasswordChangeFrame extends JFrame {
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton confirmButton;
    private JLabel validationLabel;
    private String ID;
    Database db = new Database();

    public PasswordChangeFrame(String id) {
        setTitle(id + "님의 비밀번호 변경");
        this.ID = id;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(370, 270);
        setLayout(new BorderLayout());

        SwingCompFunc.setFrameStyle(this);

        // 상단 제목 패널
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("비밀번호 변경");
        titleLabel.setFont(new Font("나눔 고딕", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        titleLabel.setForeground(Color.WHITE);

        SwingCompFunc.setTopPanelStyle(titlePanel);

        // 중앙 패널 (비밀번호, 비밀번호 확인 입력)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 비밀번호 입력
        JLabel passwordLabel = new JLabel("비밀번호:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        centerPanel.add(passwordField, gbc);

        // 비밀번호 확인 입력
        JLabel confirmPasswordLabel = new JLabel("비밀번호 확인:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        centerPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        centerPanel.add(confirmPasswordField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // 하단 패널 (확인 버튼 및 검증 라벨)
        JPanel bottomPanel = new JPanel(new GridBagLayout());

        confirmButton = new JButton("확인");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // 버튼이 두 열을 차지하도록 설정
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        bottomPanel.add(confirmButton, gbc);

        SwingCompFunc.setButtonStyle(confirmButton);

        validationLabel = new JLabel("비밀번호를 입력하세요.");
        validationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomPanel.add(validationLabel, gbc);

        add(bottomPanel, BorderLayout.SOUTH);

        // 버튼 클릭 이벤트
        confirmButton.addActionListener(new ConfirmButtonActionListener());

        // 보여주기
        setVisible(true);
    }

    private class ConfirmButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                validationLabel.setText("비밀번호를 입력해주세요.");
                validationLabel.setForeground(Color.RED);
                return;
            }

            if (!password.equals(confirmPassword)) {
                validationLabel.setText("비밀번호가 일치하지 않습니다.");
                validationLabel.setForeground(Color.RED);
                return;

            }

            if (isValidPassword(password)) {
                validationLabel.setText("사용 가능한 비밀번호입니다.");
                validationLabel.setForeground(Color.GRAY);
                boolean changePW = db.updatePassword(ID, password);
                if (changePW) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 변경되었습니다.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "비밀번호 변경에 실패했습니다.");
                }
            } else {
                validationLabel.setText("비밀번호는 최소 8자, 대소문자, 숫자 및 특수 문자를 포함");
                validationLabel.setForeground(Color.RED);
            }
        }
    }

    private boolean isValidPassword(String password) {
        // 비밀번호 유효성 검사: 8자 이상, 대소문자, 숫자, 특수문자 포함
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*[0-9].*") &&
                password.matches(".*[!@#$%^&*].*");
    }

    public static void main(String[] args) {
        new PasswordChangeFrame("name");
    }
}
