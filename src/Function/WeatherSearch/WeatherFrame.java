package Function.WeatherSearch;

import Function.SwingCompFunc.SwingCompFunc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

public class WeatherFrame extends JFrame {
    public static Map<String, Map<String, Set<String>>> dataMap = new HashMap<>();
    public static Map<String, int[]> coordinatesMap = new HashMap<>();
    JComboBox<String> provinceBox, guBox, dongBox;
    BiConsumer<String, int[]> updateCallback;
    WeatherAPI weatherAPI = new WeatherAPI();

    public WeatherFrame(BiConsumer<String, int[]> updateCallback) {
        this.updateCallback = updateCallback;

        setTitle("날씨 조회");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        WeatherAPI.loadRegions(dataMap, coordinatesMap);

        // 메인 패널
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        // 상단 제목 패널
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("날씨 조회", SwingConstants.CENTER);
        titleLabel.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        SwingCompFunc.setTopPanelStyle(titlePanel);

        // 중앙 선택 패널
        JPanel centerPanel = new JPanel(new GridBagLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel provinceLabel = new JLabel("1단계(도):");
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(provinceLabel, gbc);

        provinceBox = new JComboBox<>();
        gbc.gridx = 1;
        centerPanel.add(provinceBox, gbc);

        JLabel guLabel = new JLabel("2단계(시/구):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(guLabel, gbc);

        guBox = new JComboBox<>();
        gbc.gridx = 1;
        centerPanel.add(guBox, gbc);

        JLabel dongLabel = new JLabel("3단계(동/면/읍):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(dongLabel, gbc);

        dongBox = new JComboBox<>();
        gbc.gridx = 1;
        centerPanel.add(dongBox, gbc);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton searchButton = new JButton("확인");
        searchButton.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(searchButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        SwingCompFunc.setTopPanelStyle(buttonPanel);
        SwingCompFunc.setButtonStyle(searchButton);

        /* 데이터 로드 */
        loadProvinces();

        /* 2단계(시/구) 선택 드롭다운 목록 초기화 */
        guBox.addItem("2단계(시/구)");

        /* 3단계(동/면/읍) 선택 드롭다운 목록 초기화 */
        dongBox.addItem("3단계(동/면/읍)");

        // 버튼 클릭 이벤트
        searchButton.addActionListener(e -> {
            String selectedDong = (String) dongBox.getSelectedItem();
            if (selectedDong != null && coordinatesMap.containsKey(selectedDong)) {
                int[] coords = coordinatesMap.get(selectedDong);
                updateCallback.accept(selectedDong, coords);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "주소를 올바르게 선택하세요.",
                        "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        /* (도) 콤보박스 선택 시 동작 정의 */
        provinceBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* 선택된 도 */
                Object selectedItem = provinceBox.getSelectedItem();

                /* 헤더 값을 눌렀을 경우 */
                if (selectedItem == null || selectedItem.equals("1단계(도)")) {
                    /* (시/구), (동/면/읍) 목록 초기화 */
                    guBox.removeAllItems();
                    dongBox.removeAllItems();
                    /* 헤더 추가 */
                    guBox.addItem("2단계(시/구)");
                    dongBox.addItem("3단계(동/면/읍)");
                    JOptionPane.showMessageDialog(null, "주소를 선택하세요!",
                            "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                /* (시/구), (동/면/읍) 목록 초기화 */
                guBox.removeAllItems();
                dongBox.removeAllItems();
                /* 헤더 추가 */
                guBox.addItem("2단계(시/구)");
                dongBox.addItem("3단계(동/면/읍)");

                /* 선택된 (도)에 해당하는 (시/구) 목록 추가 */
                String selectedProvince = (String) provinceBox.getSelectedItem();
                Map<String, Set<String>> guMap = dataMap.get(selectedProvince);

                if (guMap != null) {
                    /* (시/구) 목록을 정렬하여 드롭다운에 추가 */
                    List<String> guList = new ArrayList<>(guMap.keySet());
                    /* (시/구) 목록 정렬 */
                    Collections.sort(guList);
                    for (String gu : guList) {
                        guBox.addItem(gu);
                    }
                }
            }
        });

        /* (시/구) 콤보박스 선택 시 동작 정의 */
        guBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* 사용자가 선택하지 않았을 경우는 처리하지 않음 */
                /* (도) 콤보박스 선택 이벤트로 변경될 때 오류가 발생하여 추가 */
                if (!guBox.isPopupVisible()) {
                    return;
                }

                Object selectedItem = guBox.getSelectedItem();

                /* 헤더 값을 눌렀을 경우 */
                if (selectedItem == null || selectedItem.equals("2단계(시/구)")) {
                    /* (동/면/읍) 목록 초기화 */
                    dongBox.removeAllItems();
                    /* 헤더 추가 */
                    dongBox.addItem("3단계(동/면/읍)");
                    JOptionPane.showMessageDialog(null, "주소를 선택하세요!",
                            "경고", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                /* (동/면/읍) 목록 초기화 */
                dongBox.removeAllItems();
                /* 헤더 추가 */
                dongBox.addItem("3단계(동/면/읍)");

                /* 선택된 도에서 (시/구)에 해당하는 (동/면/읍) 목록 추가 */
                String selectedProvince = (String) provinceBox.getSelectedItem();
                String selectedGu = (String) guBox.getSelectedItem();
                Set<String> dongSet = dataMap.get(selectedProvince).get(selectedGu);

                if (dongSet != null) {
                    /* (동/면/읍) 목록을 정렬하여 드롭다운에 추가 */
                    List<String> dongList = new ArrayList<>(dongSet);
                    /* (동/면/읍) 목록 정렬 */
                    Collections.sort(dongList);
                    for (String dong : dongList) {
                        dongBox.addItem(dong);
                    }
                }
            }
        });
    }

    private void loadProvinces() {
        provinceBox.addItem("1단계(도)");
        List<String> provinces = new ArrayList<>(dataMap.keySet());
        Collections.sort(provinces);
        provinces.forEach(provinceBox::addItem);
        for (String province : provinces) {
            provinceBox.addItem(province);
        }
    }
}
