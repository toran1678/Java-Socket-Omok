package Function.WeatherSearch;

import javax.swing.*;

public class test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Weather Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);

            WeatherSearchPanel weatherPanel = new WeatherSearchPanel();
            frame.add(weatherPanel);

            frame.setVisible(true);
        });
    }
}
