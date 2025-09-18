package Function.WeatherSearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class WeatherAPI {
    public static Map<String, Map<String, Set<String>>> dataMap = new HashMap<>();
    public static Map<String, int[]> coordinatesMap = new HashMap<>();
    
    // 설정 파일에서 API 정보 로드
    private static final Properties config = new Properties();
    private static String API_URL;
    private static String API_KEY;
    
    static {
        loadConfig();
    }
    
    /**
     * config.properties 파일에서 설정을 로드하는 메서드
     */
    private static void loadConfig() {
        try (InputStream input = WeatherAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                // 클래스패스에서 찾을 수 없으면 프로젝트 루트에서 찾기
                try (FileReader reader = new FileReader("config.properties")) {
                    config.load(reader);
                }
            } else {
                config.load(input);
            }
            
            API_URL = config.getProperty("weather.api.url", "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst");
            API_KEY = config.getProperty("weather.api.key", "");
            
            if (API_KEY.isEmpty() || "YOUR_API_KEY_HERE".equals(API_KEY)) {
                System.err.println("경고: 날씨 API 키가 설정되지 않았습니다.");
                System.err.println("   config.properties 파일에서 weather.api.key를 설정해주세요.");
                System.err.println("   API 키 신청: https://data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084");
            }
        } catch (IOException e) {
            System.err.println("설정 파일을 로드할 수 없습니다: " + e.getMessage());
            // 기본값 설정
            API_URL = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtFcst";
            API_KEY = "";
        }
    }

    /**
     * CSV 파일 읽어서 주소 데이터와 좌표 데이터를 메모리에 저장
     */
    public static void loadRegions(Map<String, Map<String, Set<String>>> dataMap, Map<String, int[]> coordinatesMap) {
        String filePath = "src/Function/WeatherSearch/RegionCSV/Regions.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // 헤더 무시
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String province = values[0]; // 도
                String gu = values[1];       // 시/구
                String dong = values[2];     // 동/면/읍
                int x = Integer.parseInt(values[3]); // X 좌표
                int y = Integer.parseInt(values[4]); // Y 좌표

                // 데이터맵 업데이트
                dataMap.putIfAbsent(province, new HashMap<>());
                dataMap.get(province).putIfAbsent(gu, new HashSet<>());
                dataMap.get(province).get(gu).add(dong);

                // 좌표맵 업데이트
                coordinatesMap.put(dong, new int[]{x, y});
            }
        } catch (IOException e) {
            //e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    /**
     * 날씨 데이터를 가져오는 메서드
     */
    public static String getWeather(int x, int y, String[] weatherInfo) {
        HttpURLConnection connection = null;
        String errorMessage = null;

        try {
            LocalDateTime currentTime = LocalDateTime.now().minusMinutes(30); // 발표 시각은 30분 전 기준
            String baseDate = currentTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = currentTime.format(DateTimeFormatter.ofPattern("HHmm"));

            URL url = new URL(
                    API_URL
                            + "?ServiceKey=" + API_KEY
                            + "&pageNo=1"
                            + "&numOfRows=60"
                            + "&dataType=XML"
                            + "&base_date=" + baseDate
                            + "&base_time=" + baseTime
                            + "&nx=" + x
                            + "&ny=" + y
            );

            connection = (HttpURLConnection) url.openConnection();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(connection.getInputStream());

            // 응답 확인
            Element header = (Element) doc.getElementsByTagName("header").item(0);
            String resultCode = header.getElementsByTagName("resultCode").item(0).getTextContent();
            if (!"00".equals(resultCode)) {
                errorMessage = header.getElementsByTagName("resultMsg").item(0).getTextContent();
                return errorMessage;
            }

            // 날씨 정보 추출
            String forecastDate = null, forecastTime = null;
            String pty = null, sky = null; // 강수형태, 하늘상태

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                if (forecastDate == null) {
                    forecastDate = item.getElementsByTagName("fcstDate").item(0).getTextContent();
                    forecastTime = item.getElementsByTagName("fcstTime").item(0).getTextContent();
                }

                String category = item.getElementsByTagName("category").item(0).getTextContent();
                String value = item.getElementsByTagName("fcstValue").item(0).getTextContent();

                switch (category) {
                    case "PTY": pty = value; break; // 강수형태
                    case "SKY": sky = value; break; // 하늘상태
                    case "T1H": weatherInfo[3] = value; break; // 기온
                    case "REH": weatherInfo[4] = value; break; // 습도
                }
            }

            // 날짜 및 시간 설정
            weatherInfo[0] = forecastDate.substring(0, 4) + "-" + forecastDate.substring(4, 6) + "-" + forecastDate.substring(6, 8);
            weatherInfo[1] = forecastTime.substring(0, 2) + ":" + forecastTime.substring(2, 4);

            // 날씨 상태 설정
            if ("0".equals(pty)) {
                if ("1".equals(sky)) weatherInfo[2] = "맑음";
                else if ("3".equals(sky)) weatherInfo[2] = "구름 많음";
                else if ("4".equals(sky)) weatherInfo[2] = "흐림";
            } else if ("1".equals(pty)) {
                weatherInfo[2] = "비";
            } else if ("3".equals(pty)) {
                weatherInfo[2] = "눈";
            } else {
                weatherInfo[2] = "정보 없음";
            }

        } catch (Exception e) {
            errorMessage = e.getMessage();
        } finally {
            if (connection != null) connection.disconnect();
        }

        return errorMessage;
    }
}
