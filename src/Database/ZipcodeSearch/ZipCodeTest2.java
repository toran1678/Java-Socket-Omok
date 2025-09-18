package Database.ZipcodeSearch;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import org.xml.sax.InputSource;
import javax.swing.text.BadLocationException;

public class ZipCodeTest2 extends JFrame {
    private JTextField queryField;
    private JTextArea resultArea;
    private JTextField postalField;
    private JTextField addressField;

    // 공공 데이터 API 인증키 및 URL
    private static String PUBLIC_DATA_API_KEY;
    private static String PUBLIC_DATA_API_URL;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        Properties config = new Properties();
        try (InputStream input = ZipCodeTest2.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                try (FileReader reader = new FileReader("config.properties")) {
                    config.load(reader);
                }
            } else {
                config.load(input);
            }
            PUBLIC_DATA_API_KEY = config.getProperty("postal.api.key", "");
            PUBLIC_DATA_API_URL = config.getProperty("postal.api.url", "http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll");
            
            if (PUBLIC_DATA_API_KEY.isEmpty() || "YOUR_POSTAL_API_KEY_HERE".equals(PUBLIC_DATA_API_KEY)) {
                System.err.println("⚠️  경고: 우편번호 API 키가 설정되지 않았습니다.");
                System.err.println("   config.properties 파일에서 postal.api.key를 설정해주세요.");
                System.err.println("   API 키 신청: https://www.epost.go.kr/search/zipcode/zipcodeApi.jsp");
            }
        } catch (IOException e) {
            System.err.println("설정 파일을 로드할 수 없습니다: " + e.getMessage());
            PUBLIC_DATA_API_KEY = "";
            PUBLIC_DATA_API_URL = "http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll";
        }
    }

    public ZipCodeTest2(JTextField zipcodeT, JTextField addressT) {
        this.postalField = zipcodeT;
        this.addressField = addressT;

        setTitle("주소 검색");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        queryField = new JTextField();
        JButton searchButton = new JButton("검색");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(queryField, BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.EAST);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    String[] lines = resultArea.getText().split("\n");
                    int index = resultArea.getLineOfOffset(evt.getY());
                    if (index >= 0 && index < lines.length) {
                        String selectedLine = lines[index];
                        String[] parts = selectedLine.split(" ", 2);
                        if (parts.length == 2) {
                            zipcodeT.setText(parts[0]);
                            addressT.setText(parts[1]);
                            dispose(); // 창 닫기
                        }
                    }
                } catch (BadLocationException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ZipCodeTest2.this, "주소 선택 중 오류가 발생했습니다.");
                }
            }
        });

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = queryField.getText();
                if (query.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "주소를 입력하세요.");
                    return;
                }

                try {
                    // 쿼리 문자열을 인코딩
                    String encodedQuery = URLEncoder.encode(query, "UTF-8");
                    String apiUrl = PUBLIC_DATA_API_URL + "?serviceKey=" + PUBLIC_DATA_API_KEY + "&srchwrd=" + encodedQuery + "&countPerPage=20&currentPage=1";

                    HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-type", "application/xml");

                    // 응답 코드 확인 ( 추가
                    int responseCode = conn.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        JOptionPane.showMessageDialog(null, "API 호출 오류: " + responseCode + " - " + conn.getResponseMessage());
                        return;
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    // XML 응답 처리
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new InputSource(new StringReader(response.toString())));

                    // XML에서 필요한 데이터 추출
                    NodeList zipNoList = document.getElementsByTagName("zipNo");
                    NodeList lnmAdresList = document.getElementsByTagName("lnmAdres"); // 도로명 주소
                    // NodeList rnAdresList = document.getElementsByTagName("rnAdres"); // 지번 주소

                    if (zipNoList.getLength() > 0 && lnmAdresList.getLength() > 0) {
                        StringBuilder result = new StringBuilder();
                        for (int i = 0; i < zipNoList.getLength(); i++) {
                            String postalCode = zipNoList.item(i).getTextContent().trim();
                            String address = lnmAdresList.item(i).getTextContent().trim();
                            result.append(postalCode).append(" ").append(address).append("\n");
                        }
                        resultArea.setText(result.toString());
                    } else {
                        resultArea.setText("주소를 찾을 수 없습니다.");
                    }
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(null, "주소 검색 중 오류가 발생했습니다. 다시 시도해 주세요.");
                }
            }
        });
    }
}