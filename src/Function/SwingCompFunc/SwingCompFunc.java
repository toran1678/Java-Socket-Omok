package Function.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwingCompFunc {
    public static Color TopPanelColor = new Color(153, 204, 255);
    public static Color MiddlePanelColor = new Color(251, 251, 251);
    public static Color ButtonColor = new Color (122, 178, 211);
    public static Color LoginButtonColor = new Color (73, 54, 40);

    public static Color LobbyPanelColor = Color.WHITE;
    public static Color LobbyButtonColor = Color.WHITE;
    public static Color LobbyMainColor = new Color(153, 204, 255);
    public static Color LobbyWeatherColor = new Color(120, 157, 188);
    public static Color LobbyWeatherBtnColor = new Color(122, 178, 211);

    public static void setDarkMode() {
        LobbyPanelColor = new Color(183, 183, 183);
        LobbyMainColor = new Color(74, 73, 71);
        LobbyButtonColor = new Color(183, 183, 183);
        LobbyWeatherColor = new Color(183, 183, 183);
        LobbyWeatherBtnColor = new Color(183, 183, 183);

        TopPanelColor = new Color(74, 73, 71);
        ButtonColor = new Color(183, 183, 183);
    }

    public static void setDefaultMode() {
        LobbyPanelColor = Color.WHITE;
        LobbyButtonColor = Color.WHITE;
        LobbyMainColor = new Color(153, 204, 255);
        LobbyWeatherColor = new Color(120, 157, 188);
        LobbyWeatherBtnColor = new Color(122, 178, 211);

        TopPanelColor = new Color(153, 204, 255);
        ButtonColor = new Color (122, 178, 211);
    }

    /* Set Label Border Style */
    public static void setLabelBorder(JLabel label) {
        label.setBorder(BorderFactory.createLineBorder(LobbyWeatherColor, 2));
    }

    /* Set Component Style */
    public static void setButtonStyle(JButton button) {
        button.setBackground(ButtonColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    /* Login Frame Button Style */
    public static void setLoginButtonStyle(JButton button) {
        button.setBackground(LoginButtonColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(214, 192, 179), 2));
        button.addMouseListener(new ButtonHoverHandler(button));
    }

    /* 오목 Ranking Table Style */
    public static void setRankingTableStyle(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("나눔 고딕", Font.BOLD, 16));
        header.setBackground(new Color(122, 178, 211)); // 파란색 계열
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false); // 열 재정렬 비활성화
        header.setResizingAllowed(false); // 열 크기 조정 비활성화

        // 테이블 경계선 설정
        table.setGridColor(new Color(200, 200, 200)); // 테두리 색상
        table.setShowGrid(true); // 그리드 표시
        // table.setSelectionBackground(new Color(173, 216, 230)); // 선택 배경색
        table.setSelectionForeground(Color.BLACK); // 선택된 텍스트 색상
        table.setFont(new Font("나눔 고딕", Font.BOLD, 14));
        table.setRowHeight(30);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                setHorizontalAlignment(SwingConstants.CENTER);

                if (isSelected) {
                    cell.setBackground(new Color(173, 216, 230));
                } else {
                    // 특정 행 스타일
                    if (row == 0) {
                        cell.setBackground(new Color(255, 178, 0)); // 첫 번째 행(0번 행) 핑크색
                    } else if (row == 1) {
                        cell.setBackground(new Color(229, 225, 218)); // 두 번째 행(1번 행) 파란색
                    } else if (row == 2) {
                        cell.setBackground(new Color(222, 170, 121)); // 세 번째 행(2번 행) 초록색
                    } else {
                        // 기본 교차 행 색상
                        cell.setBackground(new Color(241, 240, 232));
                    }
                    cell.setForeground(new Color(51, 55, 44)); // 기본 텍스트 색상
                }
                return cell;
            }
        });

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(table.getDefaultRenderer(Object.class));
        }
    }

    /* Admin Tool Table Style */
    public static void setAdminToolTableStyle(JTable table) {
        // 테이블 스타일 설정
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(122, 178, 211));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(new Color(200, 230, 255));
        table.setSelectionForeground(Color.BLACK);

        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(9).setPreferredWidth(120);
        table.getColumnModel().getColumn(10).setPreferredWidth(120);
    }

    /* Set TopPanel Style */
    public static void setTopPanelStyle(JPanel panel) {
        panel.setBackground(TopPanelColor);
        panel.setForeground(Color.WHITE);
        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    /* Set TopLabel Style */
    public static void setTopLabelStyle(JLabel label) {
        label.setFont(new Font("나눔 고딕", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(0, 40));
        label.setOpaque(true);
        label.setBackground(SwingCompFunc.TopPanelColor);
        label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    public static void setLabelStyle(JLabel label) {
        label.setFont(new Font("나눔 고딕", Font.BOLD, 17));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(0, 20));
    }

    /* set Frame Style */
    public static void setFrameStyle(JFrame frame) {
        ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");
        frame.setIconImage(gameIcon.getImage());

        /* 화면 중앙에 위치 */
        frame.setLocationRelativeTo(null);
        /* 크기 조정 불가 */
        frame.setResizable(false);
    }

    /* 비밀번호 Placeholder 기능 */
    public static void setPlaceholderPasswordField(JPasswordField passwordField, String placeholder) {
        passwordField.setText(placeholder);
        passwordField.setEchoChar((char) 0);
        passwordField.setForeground(Color.GRAY);

        passwordField.addFocusListener(new FocusAdapter() {
            @Override /* 비밀번호 필드를 클릭하거나 포커스를 주었을 때 */
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('•');
                }
            }

            @Override /* 다른 필드로 이동하거나 포커스를 잃었을 때 */
            public void focusLost(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).isEmpty()) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0);
                }
            }
        });
    }

    /* 텍스트필드 Placeholder 기능 */
    public static void setPlaceholderTextField(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        textField.addFocusListener(new FocusAdapter() {
            @Override /* 비밀번호 필드를 클릭하거나 포커스를 주었을 때 */
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override /* 다른 필드로 이동하거나 포커스를 잃었을 때 */
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    /* 입력 제한 설정 */
    public static void setDocumentFilter(JTextComponent textComponent, int maxLength) {
        ((AbstractDocument) textComponent.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override // 새로운 문자열을 삽입할 때
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override // 텍스트 일부를 대체할 때
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if ((fb.getDocument().getLength() + text.length() - length) <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    /* 숫자만 입력 제한 설정 */
    public static void setNumericInputFilter(JTextComponent textComponent, int maxLength) {
        ((AbstractDocument) textComponent.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override // 새로운 문자열을 삽입할 때
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.isEmpty() || isNumeric(string) && (fb.getDocument().getLength() + string.length() <= maxLength)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override // 텍스트 일부를 대체할 때
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.isEmpty() || isNumeric(text) && (fb.getDocument().getLength() + text.length() - length <= maxLength)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    /* 숫자 문자열인지 확인 */
    public static boolean isNumeric(String text) {
        /* 정규 표현식을 사용 (\d+, 하나 이상의 숫자) */
        return text.matches("\\d+");
    }

    /* 비밀번호 유효성 검사 */
    public static boolean isValidPassword(String password) {
        if (password.length() < 8 || password.length() > 16) {
            return false;
        }

        // 영문자, 숫자, 특수문자를 포함하는지 확인하는 정규 표현식
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public static int calculateStrength(String password) {
        int strength = 0;
        String placeholder = "8~16자, 특수문자 포함";

        if (password.length() >= 8 & !password.equals(placeholder)) strength += 20;
        if (password.matches(".*\\d.*") & !password.equals(placeholder)) strength += 20; // 숫자가 포함되어 있으면
        if (password.matches(".*[a-z].*") & !password.equals(placeholder)) strength += 20; // 소문자가 포함되어 있으면
        if (password.matches(".*[A-Z].*") & !password.equals(placeholder)) strength += 20; // 대문자가 포함되어 있으면
        if (password.matches(".*[!@#\\$%^&*].*") & !password.equals(placeholder)) strength += 20; // 특수 문자가 포함되어 있으면

        return strength;
    }
}

class ButtonHoverHandler extends MouseAdapter {
    private final JButton button;
    private final LineBorder normalBorder = new LineBorder(new Color(214, 192, 179), 2);
    private final LineBorder hoverBorder = new LineBorder(new Color(171, 136, 109), 3);

    public ButtonHoverHandler(JButton button) {
        this.button = button;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        button.setBorder(hoverBorder); // 마우스가 버튼 위로 올려졌을 때
    }

    @Override
    public void mouseExited(MouseEvent e) {
        button.setBorder(normalBorder); // 마우스가 버튼에서 벗어났을 때
    }
}