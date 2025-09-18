package Function.DTO;

import java.io.Serial;
import java.io.Serializable;

public class MessageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // 직렬화 ID 추가
    private MessageType type;
    private String content;
    private Object object;

    public MessageDTO(MessageType type, String content) {
        this.type = type;
        this.content = content;
    }

    public MessageDTO(MessageType type, String content, Object object) {
        this.type = type;
        this.content = content;
        this.object = object;
    }

    public MessageType getType() { return type; }
    public String getContent() { return content; }
    public Object getObject() { return object; }

    public void setContent(String content) {
        this.content = content;
    }
}
