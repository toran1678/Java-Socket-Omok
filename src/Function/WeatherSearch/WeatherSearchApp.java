package Function.WeatherSearch;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.border.LineBorder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.*;
import java.util.List;
import java.util.Properties;

public class WeatherSearchApp {
    private static Map<String, Map<String, Set<String>>> dataMap = new HashMap<>();
    private static Map<String, int[]> coordinatesMap = new HashMap<>(); /* 좌표 저장용 */

    /* API KEY AND URL */
    private static String PUBLIC_DATA_API_KEY;
    private static String PUBLIC_DATA_API_URL;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        Properties config = new Properties();
        try (InputStream input = WeatherSearchApp.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                try (FileReader reader = new FileReader("config.properties")) {
                    config.load(reader);
                }
            } else {
                config.load(input);
            }
            PUBLIC_DATA_API_KEY = config.getProperty("weather.api.key", "");
            PUBLIC_DATA_API_URL = config.getProperty("weather.api.url", "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst");
            
            if (PUBLIC_DATA_API_KEY.isEmpty() || "YOUR_WEATHER_API_KEY_HERE".equals(PUBLIC_DATA_API_KEY)) {
                System.err.println("⚠️  경고: 날씨 API 키가 설정되지 않았습니다.");
                System.err.println("   config.properties 파일에서 weather.api.key를 설정해주세요.");
                System.err.println("   API 키 신청: https://data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084");
            }
        } catch (IOException e) {
            System.err.println("설정 파일을 로드할 수 없습니다: " + e.getMessage());
            PUBLIC_DATA_API_KEY = "";
            PUBLIC_DATA_API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
        }
    }

    /* Swing Component */
    /* JFrame */
    JFrame frame = new JFrame("날씨 조회");

    /* Panel */
    JPanel mainP = new JPanel();
    JPanel selectP = new JPanel();
    JPanel weatherP = new JPanel();

    /* Label */
    JLabel selectL = new JLabel("주소 선택");
    JLabel tempL = new JLabel();
    JLabel dateL = new JLabel();
    JLabel timeL = new JLabel("지역을 선택해주세요.");
    JLabel weatherL = new JLabel();
    JLabel humidityL = new JLabel();
    JLabel weatherImageL = new JLabel();

    /* Combo Box */
    JComboBox<String> provinceBox = new JComboBox<>();
    JComboBox<String> guBox = new JComboBox<>();
    JComboBox<String> dongBox = new JComboBox<>();

    /* Button */
    JButton searchBtn = new JButton("조회");

    WeatherSearchApp() {
        /* 지역 정보 CSV 파일 읽기 */
        readCSV("src/Function/WeatherSearch/RegionCSV/Regions.csv");

        /* 종료 시 애플리케이션 종료, 프레임 중앙 정렬, 크기 조절 불가능 */
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(440, 320); // 프레임 크기 조정
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        mainP.setBounds(0, 0, 425, 283);
        selectP.setBounds(10, 10, 200, 273);
        weatherP.setBounds(220, 10, 200, 273);

        mainP.setLayout(null);
        selectP.setLayout(null);
        weatherP.setLayout(null);

        frame.getContentPane().add(mainP);
        mainP.add(weatherP);
        mainP.add(selectP);

        /* 1단계(도) 드롭다운 목록 초기화 */
        provinceBox.addItem("1단계(도)");

        /* 도 목록을 정렬하여 드롭다운에 추가 */
        List<String> provinces = new ArrayList<>(dataMap.keySet());
        Collections.sort(provinces);
        for (String province : provinces) {
            provinceBox.addItem(province);
        }

        /* 2단계(시/구) 선택 드롭다운 목록 초기화 */
        guBox.addItem("2단계(시/구)");

        /* 3단계(동/면/읍) 선택 드롭다운 목록 초기화 */
        dongBox.addItem("3단계(동/면/읍)");

        provinceBox.setBounds(30, 68, 160, 25);
        guBox.setBounds(30, 101, 160, 25);
        dongBox.setBounds(30, 134, 160, 25);

        selectP.add(provinceBox);
        selectP.add(guBox);
        selectP.add(dongBox);

        searchBtn.setBounds(70, 168, 70, 26);
        selectP.add(searchBtn);

        weatherImageL.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));

        selectL.setHorizontalAlignment(SwingConstants.CENTER);
        selectL.setBounds(70, 43, 70, 15);

        dateL.setBounds(30, 162, 180, 15);
        timeL.setBounds(30, 187, 180, 15);
        weatherL.setBounds(30, 212, 180, 15);
        tempL.setBounds(30, 137, 180, 15);
        humidityL.setBounds(30, 237, 180, 15);
        weatherImageL.setBounds(50, 15, 100, 100);

        /* Weather Label Add */
        weatherP.add(dateL);
        weatherP.add(timeL);
        weatherP.add(weatherL);
        weatherP.add(tempL);
        weatherP.add(humidityL);
        weatherP.add(weatherImageL);

        selectP.add(selectL);

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

        /* 조회 버튼 클릭 시 날씨 정보 조회 */
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /* 선택된 목록 */
                String selectedProvince = (String) provinceBox.getSelectedItem();
                String selectedGu = (String) guBox.getSelectedItem();
                String selectedDong = (String) dongBox.getSelectedItem();

                /* 선택된 값이 모두 헤더 값이 아닐 경우 */
                if (!"1단계(도)".equals(selectedProvince) &&
                        !"2단계(시/구)".equals(selectedGu) &&
                        !"3단계(동/면/읍)".equals(selectedDong)) {
                    /* 좌표 가져오기 */
                    int[] coords = coordinatesMap.get(selectedDong);
                    if (coords != null) {
                        String[] weatherInfo = new String[5];
                        String errorMessage = getWeather(coords[0], coords[1], weatherInfo);
                        if (errorMessage == null) {
                            dateL.setText("날짜 : " + weatherInfo[0]); // 날짜 표시
                            timeL.setText("시간 : " + weatherInfo[1]); // 시간 표시
                            weatherL.setText("날씨 : " + weatherInfo[2]); // 날씨 상태 표시
                            tempL.setText("기온 : " + weatherInfo[3] + "℃"); // 기온 표시
                            humidityL.setText("습도 : " + weatherInfo[4] + "%"); // 습도 표시

                            ImageIcon originalIcon = new ImageIcon("src/Function/WeatherSearch/Img/"+ weatherInfo[2] +".png");
                            Image originalImage = originalIcon.getImage();
                            Image resizedImage = originalImage.getScaledInstance(weatherImageL.getWidth(), weatherImageL.getHeight(), Image.SCALE_SMOOTH);
                            ImageIcon resizedIcon = new ImageIcon(resizedImage);
                            weatherImageL.setIcon(resizedIcon);
                        } else {
                            /* 오류 메시지 출력 */
                            dateL.setText("Error : " + errorMessage);
                            timeL.setText("");
                            weatherL.setText("");
                            tempL.setText("");
                            humidityL.setText("");
                        }
                    }
                } else { /* 선택을 모두 안 했을 경우 */
                    JOptionPane.showMessageDialog(null, "모든 항목을 선택해주세요.",
                            "경고", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // 프레임을 보이게 함
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        WeatherSearchApp weather = new WeatherSearchApp();
    }

    /* CSV 파일 읽기 메서드 */
    private static void readCSV(String filePath) {
        /* 파일 읽기 */
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            /* 첫 번째 행(헤더) 읽고 무시 */
            br.readLine();
            /* 파일의 끝까지 각 줄을 읽음 */
            while ((line = br.readLine()) != null) {
                /* 읽어온 줄을 쉼표로 분리하여 저장 */
                String[] values = line.split(",");
                String province = values[0]; // 1단계 (도)
                String gu = values[1];       // 2단계 (시/구)
                String dong = values[2];     // 3단계 (동/면/읍)
                int x = Integer.parseInt(values[3]); // X 좌표
                int y = Integer.parseInt(values[4]); // Y 좌표

                /* 도 -> 구 -> 동 구조를 맵에 추가 */
                /* "dataMap"에 도 키가 없으면 새로 "HashMap"을 생성해 추가 */
                dataMap.putIfAbsent(province, new HashMap<>());
                /* 도의 구 정보를 저장할 "guMap"을 가져옴 */
                Map<String, Set<String>> guMap = dataMap.get(province);
                /* "guMap"에  구(시/군/구) 키가 없으면 새로 "HashSet"을 생성해 추가 */
                guMap.putIfAbsent(gu, new HashSet<>());
                /* 동 정보를 "guMap"의 구에 추가 */
                guMap.get(gu).add(dong);

                /* 좌표 추가 (동/면/읍) 이름을 키로 하고 X, Y 좌표를 배열에 저장 */
                coordinatesMap.put(dong, new int[]{x, y});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 날씨 정보를 가져오는 메서드(x, y의 지역 좌표와 날씨 정보를 저장할 문자열) */
    public static String getWeather(int x, int y, String[] v) {
        HttpURLConnection conn = null;
        String s = null; // 에러 메시지

        try {
            /* 현재 시각 30분 전 계산 */
            LocalDateTime t = LocalDateTime.now().minusMinutes(30);
            
            @SuppressWarnings("deprecation")
			URL url = new URL(
                PUBLIC_DATA_API_URL // 초단기예보조회 API
                + "?ServiceKey=" + PUBLIC_DATA_API_KEY // 서비스키
                + "&pageNo=1" // 페이지번호 Default: 1
                + "&numOfRows=60" // 한 페이지 결과 수 (10개 카테고리값 * 6시간)
                + "&dataType=XML" // 요청자료형식(XML/JSON) Default: XML
                + "&base_date=" + t.format(DateTimeFormatter.ofPattern("yyyyMMdd"))  // 발표 날짜
                + "&base_time=" + t.format(DateTimeFormatter.ofPattern("HHmm")) // 발표 시각
                + "&nx=" + x // 예보지점의 X 좌표값
                + "&ny=" + y // 예보지점의 Y 좌표값
            );

            /* url.openConnection()을 통해 HTTP 연결을 설정 */
            conn = (HttpURLConnection) url.openConnection();
            /* getInputStream()을 호출해 API 로부터 응답을 받고 */
            /* Document 객체로 파싱, XML 형식의 응답을 다룰 수 있음 */
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(conn.getInputStream());

            boolean ok = false; // <resultCode>00</resultCode> 획득 여부
            Element e;
            NodeList ns = doc.getElementsByTagName("header");
            if (ns.getLength() > 0) {
                e = (Element) ns.item(0);
                if ("00".equals(e.getElementsByTagName("resultCode").item(0).getTextContent())) {
                    ok = true; // "resultCode"가 "00"이면 요청 성공
                } else { // 에러 메시지
                    s = e.getElementsByTagName("resultMsg").item(0).getTextContent();
                }
            }

            // 가장 빠른 예보 시각의 데이터만 처리
            if (ok) {
                String fd = null, ft = null; // 가장 빠른 예보 시각
                String pty = null; // 강수형태
                String sky = null; // 하늘상태
                String cat; // category
                String val; // fcstValue

                /* "item" 태그를 검색하여 날씨 정보를 추출 */
                ns = doc.getElementsByTagName("item");
                for (int i = 0; i < ns.getLength(); i++) {
                    e = (Element) ns.item(i);
                    
                    if (ft == null) { // 가져올 예보 시간 결정
                        fd = e.getElementsByTagName("fcstDate").item(0).getTextContent(); // 예보 날짜
                        ft = e.getElementsByTagName("fcstTime").item(0).getTextContent(); // 예보 시각
                    } else if (!fd.equals(e.getElementsByTagName("fcstDate").item(0).getTextContent()) ||
                               !ft.equals(e.getElementsByTagName("fcstTime").item(0).getTextContent())) {
                        continue; // 결정된 예보 시각과 같은 시각의 것만 취한다.
                    }
                    
                    cat = e.getElementsByTagName("category").item(0).getTextContent(); // 자료구분코드
                    val = e.getElementsByTagName("fcstValue").item(0).getTextContent(); // 예보 값
                    
                    if ("PTY".equals(cat)) pty = val; // 강수형태
                    else if ("SKY".equals(cat)) sky = val; // 하늘상태
                    else if ("T1H".equals(cat)) v[3] = val; // 기온
                    else if ("REH".equals(cat)) v[4] = val; // 습도
                }

                /* 날짜와 시간 포매팅 */
                v[0] = fd.substring(0, 4) + "-" + fd.substring(4, 6) + "-" + fd.substring(6, 8); // yyyy-MM-dd 형식
                v[1] = ft.substring(0, 2) + ":" + ft.substring(2, 4); // HH:mm 형식

                /* 날씨 상태 판단 후 저장 */
                if ("0".equals(pty)) { // 강수형태 없으면, 하늘상태로 판단
                    if ("1".equals(sky)) { v[2] = "맑음"; }
                    else if ("3".equals(sky)) { v[2] = "구름 많음"; }
                    else if ("4".equals(sky)) v[2] = "흐림"; }
                else if ("1".equals(pty)) { v[2] = "비"; }
                else if ("3".equals(pty)) { v[2] = "눈"; }
                else { v[2] = "정보 없음"; } // 다른 경우에 대한 기본값
            }
        } catch (Exception e) {
            s = e.getMessage();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return s; // 에러 메시지 반환
    }
}
