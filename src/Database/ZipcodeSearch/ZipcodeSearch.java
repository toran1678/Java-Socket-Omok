package Database.ZipcodeSearch;

import Function.SwingCompFunc.SwingCompFunc;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.xml.sax.InputSource;

public class ZipcodeSearch extends JFrame {
    private JPanel resultP;
    private List<String> searchResults;

    private JTextField addressInputT;
    private JTextField zipcodeT;
    private JTextField fullAddressT;
    private JButton prevBtn;
    private JButton nextBtn;
    ImageIcon gameIcon = new ImageIcon("src/Database/Image/아이콘.jpg");

    /* Page */
    private int currentPage = 1;
    private int resultsPerPage = 10;

    /* API KEY AND URL */
    private static String PUBLIC_DATA_API_KEY;
    private static String PUBLIC_DATA_API_URL;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        Properties config = new Properties();
        try (InputStream input = ZipcodeSearch.class.getClassLoader().getResourceAsStream("config.properties")) {
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

    public ZipcodeSearch(JTextField zipcodeT, JTextField addressT) {
        this.zipcodeT = zipcodeT;
        this.fullAddressT = addressT;

        setTitle("우편번호 검색");
        setSize(550, 315);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false); /* 창 크기 조절 불가능 */
        setIconImage(gameIcon.getImage());

        addressInputT = new JTextField();
        JButton searchButton = new JButton("검색");
        SwingCompFunc.setButtonStyle(searchButton);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputPanel.setBorder(BorderFactory.createTitledBorder("주소 입력"));

        inputPanel.add(addressInputT, BorderLayout.CENTER);
        inputPanel.add(searchButton, BorderLayout.EAST);

        resultP = new JPanel();
        /* 수직 박스 레이아웃 설정 */
        resultP.setLayout(new BoxLayout(resultP, BoxLayout.Y_AXIS));

        prevBtn = new JButton("이전");
        nextBtn = new JButton("다음");
        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);

        SwingCompFunc.setButtonStyle(prevBtn);
        SwingCompFunc.setButtonStyle(nextBtn);

        UIManager.put("Button.disabledText", new Color(179, 200, 207));

        JPanel navigationPanel = new JPanel();
        navigationPanel.add(prevBtn);
        navigationPanel.add(nextBtn);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultP), BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = addressInputT.getText();
                if (query.isEmpty() || query.equals("시/군/구 + 도로명, 동명 또는 건물명")) {
                    JOptionPane.showMessageDialog(null, "주소를 입력하세요.",
                            "오류", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                /* API 호출, 검색 결과 가져오기 */
                searchResults = fetchAddressData(query);
                if (searchResults.isEmpty()) {
                    resultP.removeAll();
                    resultP.add(new JLabel("주소를 찾을 수 없습니다."));
                    resultP.revalidate();
                    resultP.repaint();
                    nextBtn.setEnabled(false);
                    prevBtn.setEnabled(false);
                } else {
                    currentPage = 1;
                    updateResults();
                    nextBtn.setEnabled(searchResults.size() > resultsPerPage);
                    prevBtn.setEnabled(false);
                }
            }
        });

        prevBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    updateResults();
                }
            }
        });

        nextBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((currentPage * resultsPerPage) < searchResults.size()) {
                    currentPage++;
                    updateResults();
                }
            }
        });

        /* Placeholder */
        String placeholder = "시/군/구 + 도로명, 동명 또는 건물명";
        addressInputT.setText(placeholder);
        addressInputT.setForeground(Color.GRAY);

        addressInputT.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(addressInputT.getText().equals(placeholder)) {
                    addressInputT.setText("");
                    addressInputT.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(addressInputT.getText().isEmpty()) {
                    addressInputT.setForeground(Color.GRAY);
                    addressInputT.setText(placeholder);
                }
            }
        });
    }

    private List<String> fetchAddressData(String query) {
        List<String> results = new ArrayList<>();
        try {
            String encodedQuery = URLEncoder.encode(query, "UTF-8");
            String apiUrl = PUBLIC_DATA_API_URL + "?serviceKey=" + PUBLIC_DATA_API_KEY + "&srchwrd=" + encodedQuery +
                    "&countPerPage=50&currentPage=1";

            HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/xml");

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(null, "API 호출 오류: " + responseCode
                        + " - " + conn.getResponseMessage());
                return results;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            /* XML 문서로 변환 */
            Document document = builder.parse(new InputSource(new StringReader(response.toString())));

            /* 우편번호 */
            NodeList zipNoList = document.getElementsByTagName("zipNo");
            /* 도로명 주소 */
            NodeList lnmAdresList = document.getElementsByTagName("lnmAdres");

            for (int i = 0; i < zipNoList.getLength(); i++) {
                String postalCode = zipNoList.item(i).getTextContent().trim();
                String address = lnmAdresList.item(i).getTextContent().trim();
                results.add(postalCode + " " + address);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "주소 검색 중 오류가 발생했습니다.");
        }
        return results;
    }

    private void updateResults() {
        resultP.removeAll();
        int start = (currentPage - 1) * resultsPerPage;
        int end = Math.min(start + resultsPerPage, searchResults.size());

        for (int i = start; i < end; i++) {
            String result = searchResults.get(i);
            JLabel label = new JLabel(result);
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    String[] parts = result.split(" ", 2);
                    if (parts.length == 2) {
                        zipcodeT.setText(parts[0]);
                        fullAddressT.setText(parts[1]);
                        dispose(); // 창 닫기
                    }
                }
            });
            resultP.add(label);
        }

        resultP.revalidate();
        resultP.repaint();

        /* 버튼 활성화 여부 */
        prevBtn.setEnabled(currentPage > 1);
        nextBtn.setEnabled((currentPage * resultsPerPage) < searchResults.size());
    }
}