
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface MessageService {
    public int getMessageId();

    public void setMessageId(int messageId);

    public String getMessage();

    public void setMessage(String message);

    public User getSender();

    public void setSenderUsername(User sender);

    public User getReceiver();

    //public void setReceiverUsername(String receiverUsername);
    public LocalDateTime getTimestamp();

    public void setTimestamp(LocalDateTime timestamp);

    public byte[] getImage();

    public void setImage(byte[] image);

    public boolean isImage();

    public void setImage(boolean isImage);

}
