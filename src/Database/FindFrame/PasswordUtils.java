package Database.FindFrame;

import java.security.SecureRandom;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.io.*;
import Database.Database;

public class PasswordUtils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom RANDOM = new SecureRandom();
    Database db = new Database();
    
    // 설정 파일에서 로드할 변수들
    private static String SMTP_HOST;
    private static String SMTP_PORT;
    private static String SENDER_EMAIL;
    private static String SENDER_PASSWORD;
    
    static {
        loadConfig();
    }
    
    private static void loadConfig() {
        Properties config = new Properties();
        try (InputStream input = PasswordUtils.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                try (FileReader reader = new FileReader("config.properties")) {
                    config.load(reader);
                }
            } else {
                config.load(input);
            }
            SMTP_HOST = config.getProperty("email.smtp.host", "smtp.gmail.com");
            SMTP_PORT = config.getProperty("email.smtp.port", "587");
            SENDER_EMAIL = config.getProperty("email.sender.address", "");
            SENDER_PASSWORD = config.getProperty("email.sender.password", "");
            
            if (SENDER_EMAIL.isEmpty() || "YOUR_EMAIL_HERE".equals(SENDER_EMAIL)) {
                System.err.println("경고: 이메일 주소가 설정되지 않았습니다.");
                System.err.println("   config.properties 파일에서 email.sender.address를 설정해주세요.");
            }
            if (SENDER_PASSWORD.isEmpty() || "YOUR_APP_PASSWORD_HERE".equals(SENDER_PASSWORD)) {
                System.err.println("경고: 이메일 앱 비밀번호가 설정되지 않았습니다.");
                System.err.println("   config.properties 파일에서 email.sender.password를 설정해주세요.");
            }
        } catch (IOException e) {
            System.err.println("설정 파일을 로드할 수 없습니다: " + e.getMessage());
            SMTP_HOST = "smtp.gmail.com";
            SMTP_PORT = "587";
            SENDER_EMAIL = "";
            SENDER_PASSWORD = "";
        }
    }

    public static String generateTemporaryPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    public static void sendEmail(String recipientEmail, String subject, String body) {
        if (SENDER_EMAIL.isEmpty() || SENDER_PASSWORD.isEmpty()) {
            System.err.println("이메일 설정이 올바르지 않습니다. config.properties 파일을 확인해주세요.");
            return;
        }

        // SMTP 설정
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // 세션 생성
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            // 메시지 작성
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setText(body);

            // 이메일 전송
            Transport.send(message);
            System.out.println("Email sent successfully!");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void passwordReset(String id) {
        String tempPassword = generateTemporaryPassword(10);
        String userEmail = db.getEmail(id);

        if (db.updatePassword(id, tempPassword)) {
            // 3. 이메일 전송
            String subject = "임시 비밀번호 발급";
            String body = "안녕하세요,\n\n귀하의 임시 비밀번호는 다음과 같습니다:\n\n" + tempPassword +
                    "\n\n로그인 후 반드시 비밀번호를 변경해주세요.";
            sendEmail(userEmail, subject, body);
            System.out.println("임시 비밀번호가 이메일로 전송되었습니다.");
        } else {
            System.out.println("비밀번호 업데이트에 실패했습니다.");
        }
    }

    public static void main(String[] args) {
        PasswordUtils pu = new PasswordUtils();
        pu.passwordReset("toran1678");
    }
}
