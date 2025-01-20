
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;

public class Message implements Serializable, MessageService {
    int messageId;
    String message;
    boolean isImage;
    User sender;
    User receiver;
    LocalDateTime timestamp;
    byte[] image;

    public Message(String message, boolean isImage, User sender,
                   User receiver, LocalDateTime timestamp, byte[] image) {
        this.isImage = isImage;
        if (isImage) {
            this.message = null;
            this.image = image;
        } else {
            this.message = message;
            this.image = null;
        }
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.messageId = new Random().nextInt(1, 2147483647);
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSenderUsername(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean isImage) {
        this.isImage = isImage;
    }

}
