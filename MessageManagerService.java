
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface MessageManagerService {
    public void sendMessage(User to, User from, Message message);

    public Message createMessageObject(String message, boolean isImage, User sender, User receiver, 
                                       LocalDateTime timestamp, byte[] image);

    public boolean block(User userFrom, User userTo);

    public boolean unblock(User userFrom, User userTo) throws UserNotFoundException;

    public boolean removeFriend(User userFrom, User userTo);

    public boolean search(User user);

    public boolean sendFriendRequest(User userFrom, User userTo) throws UserNotFoundException;

    public boolean acceptFriendRequest(User userFrom, User userTo) throws UserNotFoundException;

    public boolean rejectFriendRequest(User userFrom, User userTo) throws UserNotFoundException;

    public ArrayList<User> searchUsersFromFile(String s);

    public ArrayList<User> readUsersFromFile();

    public void updateUsersInFile(ArrayList<User> users, User... updatedUsers);

    public static String getCorrectFileName(String user1, String user2) {
        String toReturn = user1.compareTo(user2) < 0
                ? String.format("%s_%s.bin", user1, user2)
                : String.format("%s_%s.bin", user2, user1);
        return toReturn;
    }

    public static ArrayList<Message> getMessagesFromFile(String fileName) {
        File f = new File(fileName);
        ArrayList<Message> messages = new ArrayList<Message>();

        try (var ois = new ObjectInputStream(new FileInputStream(fileName))) {
            messages = (ArrayList<Message>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return messages;
    }

    public void deleteMessage(String fileName, String messageId);
}
