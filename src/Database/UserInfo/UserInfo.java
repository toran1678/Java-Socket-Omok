package Database.UserInfo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

public class UserInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String nickname;
    private String name;
    private String password;
    private String phoneNum;
    private String birth;
    private String gender;
    private String email;
    private int win;
    private int lose;
    private byte[] image;  // BLOB 형식의 이미지를 byte 배열로 저장
    private int profileCharacter;

    public UserInfo() {
        this.id = "";
        this.nickname = "";
        this.name = "이름 없음";
        this.win = 0;
        this.lose = 0;
        this.image = null;
        this.profileCharacter = 0;
    }

    public UserInfo(String name, int win, int lose, byte[] image) {
        this.name = name;
        this.win = win;
        this.lose = lose;
        this.image = image;
    }

    // Getter 메서드들
    public String getId() { return id; }
    public String getNickname() { return nickname; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getPhoneNum() { return phoneNum; }
    public String getBirth() { return birth; }
    public String getGender() { return gender; }
    public String getEmail() { return email; }
    public int getWin() { return win; }
    public int getLose() { return lose; }
    public byte[] getImage() { return image; }
    public int getProfileCharacter() { return profileCharacter; }

    // Setter 메서드들
    public void setId(String id) { this.id = id; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setPhoneNum(String phoneNum) { this.phoneNum = phoneNum; }
    public void setBirth(String birth) { this.birth = birth; }
    public void setGender(String gender) { this.gender = gender; }
    public void setEmail(String email) { this.email = email; }
    public void setWin(int win) { this.win = win; }
    public void setLose(int lose) { this.lose = lose; }
    public void setImage(byte[] image) { this.image = image; }
    public void setProfileCharacter(int character) { this.profileCharacter = character; }

    @Override
    public String toString() {
        return "Player{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", win='" + win + '\'' +
                ", lose='" + lose + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
