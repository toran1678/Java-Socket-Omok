package Function.WeatherSearch;

import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class WeatherSearchPanel extends JPanel {
    private static final int[] SEOUL_COORDS = {60, 127}; // 서울의 기본 좌표
    JLabel locationLabel, dateLabel, timeLabel, weatherLabel, tempLabel, humidityLabel, weatherImageLabel;

    public WeatherSearchPanel() {
        setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(BorderFactory.createLineBorder(new Color(162, 210, 223)));
        setPreferredSize(new Dimension(270, 120));
        setLayout(new BorderLayout());

        // 날씨 정보 패널
        JPanel weatherPanel = new JPanel();
        weatherPanel.setBounds(0, 0, 270, 120);
        weatherPanel.setLayout(null);
        // weatherPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        locationLabel = createLabel("지역: 서울", 5, 5, 150, 20);
        dateLabel = createLabel("날짜: -", 5, 35, 150, 20);
        timeLabel = createLabel("시간: -", 5, 65, 100, 20);
        weatherLabel = createLabel("날씨: -", 100, 65, 100, 20);
        tempLabel = createLabel("기온: -", 5, 95, 100, 20);
        humidityLabel = createLabel("습도: -", 100, 95, 80, 20);

        weatherImageLabel = new JLabel();
        weatherImageLabel.setBounds(190, 10, 60, 60);
        weatherPanel.setBackground(new Color(120, 157, 188));

        weatherPanel.add(locationLabel);
        weatherPanel.add(dateLabel);
        weatherPanel.add(timeLabel);
        weatherPanel.add(weatherLabel);
        weatherPanel.add(tempLabel);
        weatherPanel.add(humidityLabel);
        weatherPanel.add(weatherImageLabel);

        // 날씨 검색 버튼
        JButton searchButton = new JButton("날씨 검색");
        searchButton.setBounds(190, 85, 75, 30);
        searchButton.setFont(new Font("나눔고딕", Font.BOLD, 9));
        weatherPanel.add(searchButton);
        SwingCompFunc.setButtonStyle(searchButton);

        add(weatherPanel);

        // 버튼 클릭 시 검색 프레임 열기
        searchButton.addActionListener(e ->
                new WeatherFrame(this::updateWeather).setVisible(true)
        );

        // 기본 서울 날씨 표시
        displayWeather("서울", SEOUL_COORDS);
    }

    public JPanel getInternalPanel() {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                return (JPanel) comp;
            }
        }
        return null;
    }

    /* 라벨 생성 메서드 */
    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        label.setFont(new Font("나눔고딕", Font.PLAIN, 14));
        return label;
    }

    /* 날씨 정보 표시 메서드 */
    public void displayWeather(String location, int[] coords) {
        String[] weatherInfo = new String[5];
        String error = WeatherAPI.getWeather(coords[0], coords[1], weatherInfo);

        if (error == null) {
            locationLabel.setText("지역: " + location); // 선택 위치 업데이트
            dateLabel.setText("날짜: " + weatherInfo[0]);
            timeLabel.setText("시간: " + weatherInfo[1]);
            weatherLabel.setText("날씨: " + weatherInfo[2]);
            tempLabel.setText("기온: " + weatherInfo[3] + "℃");
            humidityLabel.setText("습도: " + weatherInfo[4] + "%");

            // 날씨 아이콘 업데이트
            ImageIcon originalIcon = new ImageIcon("src/Function/WeatherSearch/Img/" + weatherInfo[2] + ".png");
            Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            weatherImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            JOptionPane.showMessageDialog(this, "날씨 데이터를 가져오는 데 실패했습니다.\n" + error,
                    "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* 날씨 업데이트 콜백 메서드 */
    private void updateWeather(String location, int[] coords) {
        displayWeather(location, coords);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WeatherSearchPanel::new);
    }
}