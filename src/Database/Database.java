package Database;

import Database.UserInfo.UserInfo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class Database {
    // 설정 파일에서 데이터베이스 정보 로드
    private static final Properties config = new Properties();
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    
    static {
        loadConfig();
    }
    
    /**
     * config.properties 파일에서 데이터베이스 설정을 로드하는 메서드
     */
    private static void loadConfig() {
        try (InputStream input = Database.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                // 클래스패스에서 찾을 수 없으면 프로젝트 루트에서 찾기
                try (java.io.FileReader reader = new java.io.FileReader("config.properties")) {
                    config.load(reader);
                }
            } else {
                config.load(input);
            }
            
            URL = config.getProperty("db.url", "jdbc:mysql://localhost:3307/Omok?useUnicode=true&characterEncoding=UTF-8");
            USER = config.getProperty("db.user", "root");
            PASSWORD = config.getProperty("db.password", "123456");
            
        } catch (IOException e) {
            System.err.println("설정 파일을 로드할 수 없습니다: " + e.getMessage());
            // 기본값 설정
            URL = "jdbc:mysql://localhost:3307/Omok?useUnicode=true&characterEncoding=UTF-8";
            USER = "root";
            PASSWORD = "123456";
        }
    }

    Connection conn = null;
    Statement stmt = null;
    ResultSet result = null;
    PreparedStatement pstmt = null;

    /* Database Connect */
    public Database() {}

    /* 로그인 정보를 확인 */
    public boolean loginCheck(String _id, String _password) {
        try {
            connectDatabase();

            String loginCheckSql = "SELECT password FROM users WHERE id='" + _id + "'";
            result = stmt.executeQuery(loginCheckSql);

            while(result.next()) {
                if(_password.equals(result.getString("password"))) {
                    System.out.println("< 로그인 성공 >");
                    return true;
                } else {
                    System.out.println("< 로그인 실패 >");
                    return false;
                }
            }
        } catch(Exception e) {
            System.out.println("로그인 실패 > " + e.toString());
            return false;
        } finally {
            closeResources();
        }
        return false;
    }

    /* 아이디 중복 확인 */
    boolean idCheck(String id, boolean idChecked) {
            if (idChecked) {
            JOptionPane.showMessageDialog(null, "이미 확인된 아이디입니다.");
            return true;
        }

        try {
            connectDatabase();

            String idCheckSql = "SELECT COUNT(*) FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(idCheckSql);
            pstmt.setString(1, id);

            result = pstmt.executeQuery();
            if (result.next()) {
                int count = result.getInt(1);
                if (count > 0) {
                    // 중복
                    JOptionPane.showMessageDialog(null, "중복된 아이디입니다.");
                    return false;
                } else {
                    JOptionPane.showMessageDialog(null, "사용 가능한 아이디입니다.");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            // 데이터베이스 오류
            JOptionPane.showMessageDialog(null, "데이터베이스 오류");
        } finally {
            closeResources();
        }
        // 오류
        JOptionPane.showMessageDialog(null, "오류");
        return false;
    }

    /* 유저 정보 업데이트 */
    public boolean updateUserInfo(String _id, String id, String name, String nickname,
                                  String password, String phone, String birth, String gender,
                                  String email, int profileCharacter) {
        try {
            connectDatabase();

            String updateQuery = "UPDATE users SET id = ?, name = ?, nickname = ?, password = ?," +
                    "phone_number = ?, birth_date = ?, gender = ?, email = ?, profile_character = ? WHERE id = ?";
            pstmt = conn.prepareStatement(updateQuery);

            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, nickname);
            pstmt.setString(4, password);
            pstmt.setString(5, phone);
            pstmt.setString(6, birth);
            pstmt.setString(7, gender);
            pstmt.setString(8, email);
            pstmt.setInt(9, profileCharacter);
            pstmt.setString(10, _id);

            int rowsUpdated = pstmt.executeUpdate();

            return rowsUpdated > 0; // 업데이트 성공 여부 반환
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    /* 유저 정보 삭제 */
    public boolean deleteUserInfo(String userId) {
        try {
            connectDatabase();

            String deleteQuery = "DELETE FROM usersTrash WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(deleteQuery);
            pstmt.setString(1, userId);

            int rowsDeleted = pstmt.executeUpdate();
            return rowsDeleted > 0; // 삭제 성공 여부 반환
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean moveUserToTrash(String userId) {
        try {
            connectDatabase();

            /* 1. 휴지통 테이블에서 동일한 아이디를 가진 데이터 삭제 */
            String deleteFromTrashQuery = "DELETE FROM usersTrash WHERE id = ?";
            PreparedStatement deleteFromTrashStmt = conn.prepareStatement(deleteFromTrashQuery);
            deleteFromTrashStmt.setString(1, userId);
            deleteFromTrashStmt.executeUpdate();

            /* 2. users 테이블에서 "usersTrash"로 데이터 복사 */
            String moveQuery = "INSERT INTO usersTrash (id, nickname, password, name, phone_number, " +
                    "birth_date, gender, email, zipcode, address, detailedAddress, " +
                    "profile_picture, profile_character, win, lose) " +
                    "SELECT id, nickname, password, name, phone_number, " +
                    "birth_date, gender, email, zipcode, address, detailedAddress, " +
                    "profile_picture, profile_character, win, lose FROM users WHERE id = ?";
            PreparedStatement moveStmt = conn.prepareStatement(moveQuery);
            moveStmt.setString(1, userId);

            int rowsInserted = moveStmt.executeUpdate(); // 데이터 복사

            if (rowsInserted > 0) {
                /* 3. 복사가 성공했으면 원본 테이블에서 데이터 삭제 */
                String deleteQuery = "DELETE FROM users WHERE id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                deleteStmt.setString(1, userId);

                int rowsDeleted = deleteStmt.executeUpdate(); // 원본 데이터 삭제
                return rowsDeleted > 0; // 삭제 성공 여부 반환
            }

            return false; // 데이터 복사 실패
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /* 유저 복원 기능 */
    public boolean restoreUser(String userId) {
        try {
            connectDatabase();

            // 1. 휴지통에서 원본 테이블로 데이터 복사
            String restoreQuery = "INSERT INTO users (id, nickname, password, name, phone_number, " +
                    "birth_date, gender, email, zipcode, address, detailedAddress, " +
                    "profile_picture, profile_character, win, lose) " +
                    "SELECT id, nickname, password, name, phone_number, " +
                    "birth_date, gender, email, zipcode, address, detailedAddress, " +
                    "profile_picture, profile_character, win, lose FROM usersTrash WHERE id = ?";
            PreparedStatement restoreStmt = conn.prepareStatement(restoreQuery);
            restoreStmt.setString(1, userId);

            int rowsInserted = restoreStmt.executeUpdate(); // 데이터 복사

            if (rowsInserted > 0) {
                // 2. 복사가 성공했으면 휴지통 테이블에서 데이터 삭제
                String deleteQuery = "DELETE FROM usersTrash WHERE id = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                deleteStmt.setString(1, userId);

                int rowsDeleted = deleteStmt.executeUpdate(); // 휴지통 데이터 삭제
                return rowsDeleted > 0; // 삭제 성공 여부 반환
            }

            return false; // 복사 실패 시
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            closeResources();
        }
    }

    /* 닉네임 중복 확인 */
    public boolean nicknameCheck(String nickname, boolean idChecked) {
        if (idChecked) {
            JOptionPane.showMessageDialog(null, "이미 확인된 닉네임입니다.");
            return true;
        }

        try {
            connectDatabase();

            String idCheckSql = "SELECT COUNT(*) FROM users WHERE nickname = ?";
            pstmt = conn.prepareStatement(idCheckSql);
            pstmt.setString(1, nickname);

            result = pstmt.executeQuery();
            if (result.next()) {
                int count = result.getInt(1);
                if (count > 0) {
                    // 중복
                    JOptionPane.showMessageDialog(null, "중복된 닉네임입니다.");
                    return false;
                } else {
                    JOptionPane.showMessageDialog(null, "사용 가능한 닉네임입니다.");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            // 데이터베이스 오류
            JOptionPane.showMessageDialog(null, "데이터베이스 오류");
        } finally {
            closeResources();
        }
        // 오류
        JOptionPane.showMessageDialog(null, "오류");
        return false;
    }

    /* 유저 프로필 정보 가져오기 */
    public UserInfo getUserInfo(String _nickname) {
        if (_nickname.isEmpty()) {
            JOptionPane.showMessageDialog(null, "아이디 값이 없습니다.");
            return null;
        }
        UserInfo userInfo = new UserInfo();

        try {
            connectDatabase();
            String idCheckSql = "SELECT * FROM users WHERE nickname = ?";
            pstmt = conn.prepareStatement(idCheckSql);
            pstmt.setString(1, _nickname);

            result = pstmt.executeQuery();
            result.next();

            userInfo.setId(result.getString("id"));
            userInfo.setNickname(result.getString("nickname"));
            userInfo.setName(result.getString("name"));
            userInfo.setPassword(result.getString("password"));
            userInfo.setPhoneNum(result.getString("phone_number"));
            userInfo.setBirth(result.getString("birth_date"));
            userInfo.setGender(result.getString("gender"));
            userInfo.setEmail(result.getString("email"));

            userInfo.setWin(result.getInt("win"));
            userInfo.setLose(result.getInt("lose"));
            userInfo.setImage(result.getBytes("profile_picture"));
            userInfo.setProfileCharacter(result.getInt("profile_character"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "데이터베이스 오류");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "오류");
        } finally {
            closeResources();
        }

        return userInfo;
    }

    public int getProfileCharacterNumber(String nickname) {
        if (nickname.isEmpty()) System.err.println("닉네임 값이 없습니다.");
        String idCheckSql = "SELECT profile_character FROM users WHERE nickname = ?";
        int characterNumber = -1;
        try {
            connectDatabase();

            pstmt = conn.prepareStatement(idCheckSql);
            pstmt.setString(1, nickname);

            result = pstmt.executeQuery();
            result.next();

            characterNumber = result.getInt("profile_character");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }
        return characterNumber;
    }

    /* 프로필 캐릭터 존재 확인 */
    public boolean profileCharacterCheck(String userId) {
        try {
            connectDatabase();
            String sql = "SELECT profile_character FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            result = pstmt.executeQuery();

            if (result.next()) {
                int profileCharacter = result.getInt("profile_character");
                /* 프로필 캐릭터 이미지가 없을 경우 */
                if (profileCharacter == 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "데이터베이스 오류");
        } finally {
            closeResources();
        }
        return false;
    }

    /* 닉네임 가져오기 */
    public String getNickname(String userId) {
        String nickname = null;

        try {
            connectDatabase();
            String sql = "SELECT nickname FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            result = pstmt.executeQuery();
            if (result.next()) {
                nickname = result.getString("nickname");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }

        return nickname;
    }

    /* 특정 조건으로 유저 검색 */


    /* 닉네임 가져오기 */
    public String getEmail(String userId) {
        String email = null;

        try {
            connectDatabase();
            String sql = "SELECT email FROM users WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);

            result = pstmt.executeQuery();
            if (result.next()) {
                email = result.getString("email");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }

        return email;
    }

    /* profile_character 값을 수정하는 메서드 */
    public boolean updateProfileCharacter(String userId, int characterId) {
        try {
            connectDatabase();
            String sql = "UPDATE users SET profile_character = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, characterId);
            pstmt.setString(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("프로필 캐릭터가 업데이트되었습니다.");
                return true;
            } else {
                System.out.println("프로필 캐릭터 업데이트 실패");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "데이터베이스 오류");
            return false;
        } finally {
            closeResources();
        }
    }

    /* 비밀번호 변경 */
    public boolean updatePassword(String userId, String newPassword) {
        try {
            connectDatabase();
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("비밀번호가 변경되었습니다.");
                return true;
            } else {
                System.out.println("비밀번호 변경에 실패했습니다.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /* 닉네임 변경 */
    public boolean updateNickname(String userId, String newNickname) {
        try {
            connectDatabase();
            String sql = "UPDATE users SET nickname = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newNickname);
            pstmt.setString(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("닉네임이 변경되었습니다.");
                return true;
            } else {
                System.out.println("닉네임 변경에 실패했습니다.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /* 메시지 저장 */
    public void saveChatLog(String nickname, String roomName, String message) {
        String query = "INSERT INTO chat_logs (nickname, room_name, message) VALUES (?, ?, ?)";
        try {
            connectDatabase();

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, nickname);
            pstmt.setString(2, roomName);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    /* 메시지 검색 (특정 문자를 포함한 메시지 검색) */
    public List<String> getMessagesByKeyword(String keyword, String roomName) {
        List<String> messages = new ArrayList<>();
        String query = "SELECT message FROM chat_logs WHERE message LIKE ? AND room_name = ? ORDER BY timestamp ASC";
        try {
            connectDatabase();

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, "%" + keyword + "%");  // 키워드가 포함된 메시지 검색
            pstmt.setString(2, roomName);  // 특정 방 이름으로 필터링
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                messages.add(rs.getString("message"));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
        return messages;
    }

    /* 모든 유저의 닉네임 가져오기 */
    public List<String> getAllUserNicknames() {
        List<String> nicknames = new ArrayList<>();
        String query = "SELECT nickname FROM users";

        try {
            connectDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                nicknames.add(rs.getString("nickname"));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }
        return nicknames;
    }

    /* 모든 유저 정보 가져오기 */
    public List<String[]> getAllUserInfo(String table) {
        List<String[]> userList = new ArrayList<>();
        String query = "SELECT * FROM " + table;

        try {
            connectDatabase();
            pstmt = conn.prepareStatement(query);
            result = pstmt.executeQuery();

            while (result.next()) {
                String id = result.getString("id");
                String nickname = result.getString("nickname");
                String password = result.getString("password");
                String name = result.getString("name");
                String phoneNumber = result.getString("phone_number");
                String birthDate = result.getString("birth_date");
                String gender = result.getString("gender");
                String email = result.getString("email");
                String zipcode = result.getString("zipcode");
                String address = result.getString("address");
                String detailedAddress = result.getString("detailedAddress");
                String profileCharacter = String.valueOf(result.getInt("profile_character"));
                String win = String.valueOf(result.getInt("win"));
                String lose = String.valueOf(result.getInt("lose"));
                userList.add(new String[]{id, nickname, password, name, phoneNumber,
                birthDate, gender, email, zipcode, address, detailedAddress, profileCharacter, win, lose});
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }

        return userList;
    }

    /* 유저 정보 업데이트 */
    public boolean updateUserInfo(
            String id, String nickname, String password, String name,
            String phoneNumber, String birthDate, String gender,
            String email, String zipcode, String address, String detailedAddress,
            int profileCharacter, int win, int lose
    ) {
        try {
            connectDatabase();

            String query = "UPDATE users SET nickname = ?, password = ?, name = ?, " +
                    "phone_number = ?, birth_date = ?, gender = ?, " +
                    "email = ?, zipcode = ?, address = ?, detailedAddress = ?, " +
                    "profile_character = ?, win = ?, lose = ? WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, nickname);
            pstmt.setString(2, password);
            pstmt.setString(3, name);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, birthDate);
            pstmt.setString(6, gender);
            pstmt.setString(7, email);
            pstmt.setString(8, zipcode);
            pstmt.setString(9, address);
            pstmt.setString(10, detailedAddress);
            pstmt.setInt(11, profileCharacter);
            pstmt.setInt(12, win);
            pstmt.setInt(13, lose);
            pstmt.setString(14, id);

            return pstmt.executeUpdate() > 0;
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    /* 관리자 UI 회원 추가 */
    public void addUser(String id, String nickname, String password) {
        connectDatabase();
        String query = "INSERT INTO users (id, nickname, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, id);
            pstmt.setString(2, nickname);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            System.out.println("회원이 성공적으로 추가되었습니다.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    /* 랭킹 검색 기능 */
    public void loadUserRanking(DefaultTableModel tableModel) {
        // 기존 데이터 제거
        tableModel.setRowCount(0);
        connectDatabase();

        String query = "SELECT nickname, win, lose FROM users ORDER BY win DESC LIMIT 10";
        try {
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            int rank = 1;
            while (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                int wins = resultSet.getInt("win");
                int losses = resultSet.getInt("lose");
                double winRate = (wins + losses > 0) ? ((double) wins / (wins + losses)) * 100 : 0.0;

                // 테이블에 데이터 추가
                tableModel.addRow(new Object[]{
                        rank,
                        nickname,
                        wins,
                        losses,
                        String.format("%.2f%%", winRate)
                });
                rank++;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "랭킹 데이터를 불러오는데 실패했습니다.\n" + e.getMessage(),
                    "오류", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources();
        }
    }

    /* 관리자 모드 채팅 검색 프로그램 */
    /* 방 검색 */
    public ArrayList<String> getRoomNames() {
        ArrayList<String> roomNames = new ArrayList<>();
        String query = "SELECT DISTINCT room_name FROM chat_logs";

        try {
            connectDatabase();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                roomNames.add(resultSet.getString("room_name"));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }

        return  roomNames;
    }

    /* 특정 방에서 메시지 검색 */
    public ArrayList<String> searchMessages(String roomName, String searchText) {
        ArrayList<String> messages = new ArrayList<>();
        String query;

        if (searchText == null || searchText.isEmpty()) {
            // 검색어가 없을 때, 방의 전체 메시지를 가져오는 쿼리
            query = "SELECT nickname, message, timestamp FROM chat_logs WHERE room_name = ?";
        } else {
            // 검색어가 있을 때, 키워드가 포함된 메시지를 검색하는 쿼리
            query = "SELECT nickname, message, timestamp FROM chat_logs WHERE room_name = ? AND message LIKE ?";
        }

        try {
            connectDatabase();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomName);

            if (searchText != null && !searchText.isEmpty()) {
                pstmt.setString(2, "%" + searchText + "%");
            }

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                messages.add("[" + timestamp + "] " + nickname + ": " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return messages;
    }

    /* 1대1 채팅 대화 내용 불러오기 */
    public ArrayList<String> searchMessages(String roomName) {
        ArrayList<String> messages = new ArrayList<>();
        String query;

        query = "SELECT nickname, message, timestamp FROM chat_logs WHERE room_name = ?";

        try {
            connectDatabase();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomName);

            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                String message = resultSet.getString("message");
                Timestamp timestamp = resultSet.getTimestamp("timestamp");

                LocalDateTime localDateTime = timestamp.toLocalDateTime();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTime = localDateTime.format(formatter);

                messages.add("[" + nickname + "]:" + message + ":" + formattedTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }

        return messages;
    }

    /* 승리/패배 업데이트 */
    public boolean updateWinLose(String winnerNickname, String loserNickname) {
        try {
            connectDatabase();

            // 승자 업데이트
            String updateWinnerSql = "UPDATE users SET win = win + 1 WHERE nickname = ?";
            pstmt = conn.prepareStatement(updateWinnerSql);
            pstmt.setString(1, winnerNickname);
            int winnerUpdated = pstmt.executeUpdate();

            // 패자 업데이트
            String updateLoserSql = "UPDATE users SET lose = lose + 1 WHERE nickname = ?";
            pstmt = conn.prepareStatement(updateLoserSql);
            pstmt.setString(1, loserNickname);
            int loserUpdated = pstmt.executeUpdate();

            return winnerUpdated > 0 && loserUpdated > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }
    
    /* 오목판 저장 */
    public void saveOmokMove(String roomName, String player, int x, int y, int stoneColor) {
        String query = "INSERT INTO omok_moves (room_name, player, x, y, stone_color) VALUES (?, ?, ?, ?, ?)";
        try {
            connectDatabase();
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, roomName);
            pstmt.setString(2, player);
            pstmt.setInt(3, x);
            pstmt.setInt(4, y);
            pstmt.setInt(5, stoneColor);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }


    /* 아이디 찾기 */
    public String findID(String name, String phone) {
        try {
            connectDatabase();

            String query = "SELECT id FROM users WHERE name = ? AND phone_number = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, name);
            pstmt.setString(2, phone);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return result.getString("id");
            }
            return null;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }
        return null;
    }

    /* 비밀번호 찾기 */
    public String findPW(String id, String phone) {
        try {
            connectDatabase();

            String query = "SELECT password FROM users WHERE id = ? AND phone_number = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);

            pstmt.setString(1, id);
            pstmt.setString(2, phone);

            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return result.getString("password");
            }
            return null;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }
        return null;
    }

    /**
     * 회원가입
     * @param _id - 아이디
     * @param _password - 비밀번호
     * @param _name - 이름
     * @param _phoneNum - 핸드폰 번호
     * @param _birth - 생일
     * @param _gender - 성별
     * @param _email - 이메일
     * @param _imageData - 이미지
     * @param _zipcode - 우편번호
     * @param _address - 주소
     * @param _detailedAddress - 상세주소
     * @return - true or false
     */
    boolean joinCheck(String _id, String _nickname, String _password, String _name,
                      String _phoneNum, String _birth, String _gender,
                      String _email, byte[] _imageData, String _zipcode,
                      String _address, String _detailedAddress ) {

        String sql = "INSERT INTO users (id, nickname, password, name, phone_number, birth_date," +
                " gender, email, profile_picture, zipcode, address, detailedAddress)" + 
        		" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connectDatabase();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, _id);
            pstmt.setString(2, _nickname);
            pstmt.setString(3, _password);
            pstmt.setString(4, _name);
            pstmt.setString(5, _phoneNum);
            pstmt.setString(6, _birth);
            pstmt.setString(7, _gender);
            pstmt.setString(8, _email);
            pstmt.setBytes(9, _imageData);
            pstmt.setString(10, _zipcode);
            pstmt.setString(11, _address);
            pstmt.setString(12, _detailedAddress);

            pstmt.executeUpdate();

            System.out.println("회원가입 성공");
            return true;
        } catch (Exception e) {
            System.out.println("회원가입 실패 > " + e.toString());
            return false;
        } finally {
            closeResources();
        }
    }

    /* 임의 데이터 삽입 */
    public void insertRandomUsers() {
        // SQL INSERT 쿼리
        String insertQuery = "INSERT INTO users " +
                "(id, nickname, password, name, phone_number, birth_date, gender, email, zipcode, address, " +
                "detailedAddress, profile_picture, profile_character, win, lose) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Random random = new Random();

        try {
            connectDatabase();
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);

            // 다수의 데이터를 생성 (예: 50개의 레코드)
            for (int i = 1; i <= 50; i++) {
                // 임의 데이터 생성
                String id = "user" + i;
                String nickname = "user" + i;
                String password = "password" + i;
                String name = "Name" + i;
                String phoneNumber = "010-" + (1000 + random.nextInt(9000)) + "-" + (1000 + random.nextInt(9000));
                String birthDate = "19" + (70 + random.nextInt(30)) + "/" + (1 + random.nextInt(12)) + "/" + (1 + random.nextInt(28));
                String gender = random.nextBoolean() ? "남자" : "여자";
                String email = "user" + i + "@example.com";
                String zipcode = String.valueOf(10000 + random.nextInt(90000));
                String address = "Address " + i;
                String detailedAddress = "Detailed Address " + i;
                byte[] profilePicture = null; // BLOB 데이터는 "null"로 처리
                int profileCharacter = random.nextInt(10 + 1); // 0~9 임의 값
                int win = random.nextInt(100); // 0~99 임의 값
                int lose = random.nextInt(100); // 0~99 임의 값

                // 데이터 매핑
                pstmt.setString(1, id);
                pstmt.setString(2, nickname);
                pstmt.setString(3, password);
                pstmt.setString(4, name);
                pstmt.setString(5, phoneNumber);
                pstmt.setString(6, birthDate);
                pstmt.setString(7, gender);
                pstmt.setString(8, email);
                pstmt.setString(9, zipcode);
                pstmt.setString(10, address);
                pstmt.setString(11, detailedAddress);
                pstmt.setBytes(12, profilePicture); // 프로필 사진은 null로 삽입
                pstmt.setInt(13, profileCharacter);
                pstmt.setInt(14, win);
                pstmt.setInt(15, lose);

                // SQL 실행
                pstmt.executeUpdate();
                System.out.println("Inserted: " + id);
            }

            System.out.println("50개의 사용자 데이터 삽입 완료!");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            closeResources();
        }
    }

    /* 데이터베이스 연결 */
    public void connectDatabase() {
    	try {
            Class.forName(DRIVER); // 필수 X 명시적
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = conn.createStatement();
            // System.out.println("MySQL 서버 연동 성공");
        } catch(SQLException e) {
            System.out.println("MySQL 서버 연동 실패 > " + e.toString());
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC 드라이버를 찾을 수 없습니다.");
            System.err.println(e.getMessage());
        }
    }

    /* 리소스 닫기 */
    private void closeResources() {
        try {
            if (result != null) result.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}