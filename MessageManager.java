
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MessageManager implements MessageManagerService {

    public synchronized void sendMessage(User to, User from, Message message) {
        String fileName = to.getUsername().compareTo(from.getUsername()) < 0
                ? String.format("%s_%s.bin", to.getUsername(), from.getUsername())
                : String.format("%s_%s.bin", from.getUsername(), to.getUsername());

        File file = new File(fileName);
        ArrayList<Message> messages = new ArrayList<>();

        if (file.exists()) {
            try (var ois = new ObjectInputStream(new FileInputStream(file))) {
                messages = (ArrayList<Message>) ois.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        messages.add(message);

        try (var oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(messages);
        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayList<User> users = readUsersFromFile();

        if (!to.chats.contains(from)) {
            to.chats.add(from);
        } else {
            to.chats.remove(from);
            to.chats.add(0, from);
        }
        if (!from.chats.contains(to)) {
            from.chats.add(to);
        } else {
            from.chats.remove(to);
            from.chats.add(0, to);
        }
        updateUsersInFile(users, to, from);
    }


    public Message createMessageObject(String message, boolean isImage, User sender, User receiver,
                                       LocalDateTime timestamp, byte[] image) {
        return new Message(message, isImage, sender, receiver, timestamp, image);
    }


    public synchronized boolean block(User userFrom, User userTo) {
        if (!search(userFrom) || !search(userTo)) {
            return false;
        }

        ArrayList<User> users = readUsersFromFile();

        if (userFrom.getBlocked().contains(userTo)) {
            return true;
        }

        userFrom.addBlocked(userTo);
        updateUsersInFile(users, userFrom, userTo);
        return true;
    }


    public synchronized boolean unblock(User userFrom, User userTo) {
        if (!search(userFrom) || !search(userTo)) {
            return false;
        }

        ArrayList<User> users = readUsersFromFile();

        if (userFrom.getBlocked().contains(userTo)) {
            userFrom.removeBlocked(userTo);
            updateUsersInFile(users, userFrom, userTo);
            return true;
        }
        return false;
    }


    public synchronized boolean removeFriend(User userFrom, User userTo) {
        if (!search(userFrom) || !search(userTo)) {
            return false;
        }

        ArrayList<User> users = readUsersFromFile();

        if (userTo.getFriends().contains(userFrom)) {
            userTo.getFriends().remove(userFrom);
            userFrom.getFriends().remove(userTo);
            updateUsersInFile(users, userFrom, userTo);
            return true;
        }
        return false;
    }


    public synchronized boolean search(User user) {
        ArrayList<User> users = readUsersFromFile();
        return users.contains(user);
    }

    public synchronized boolean sendFriendRequest(User userFrom, User userTo) {
        if (!search(userFrom) || !search(userTo)) {
            return false;
        }

        ArrayList<User> users = readUsersFromFile();

        if (userFrom.getBlocked().contains(userTo) ||
                userFrom.getFriends().contains(userTo) ||
                userFrom.getRequestedFriends().contains(userTo)) {
            return false;
        }

        userFrom.getRequestedFriends().add(userTo);
        userTo.getPendingFriendRequests().add(userFrom);
        updateUsersInFile(users, userFrom, userTo);
        return true;
    }


    public synchronized boolean acceptFriendRequest(User userFrom, User userTo) {
        if (!search(userFrom) || !search(userTo)) {
            return false;
        }

        ArrayList<User> users = readUsersFromFile();

        if (userTo.getBlocked().contains(userFrom)) {
            return false;
        }

        userFrom.getRequestedFriends().remove(userTo);
        userTo.getPendingFriendRequests().remove(userFrom);
        userFrom.getFriends().add(userTo);
        userTo.getFriends().add(userFrom);
        updateUsersInFile(users, userFrom, userTo);
        return true;
    }


    public synchronized boolean rejectFriendRequest(User userFrom, User userTo) {
        if (!search(userFrom) || !search(userTo)) {
            return false;
        }

        ArrayList<User> users = readUsersFromFile();

        if (userTo.getBlocked().contains(userFrom)) {
            return false;
        }

        userFrom.getRequestedFriends().remove(userTo);
        userTo.getPendingFriendRequests().remove(userFrom);
        updateUsersInFile(users, userFrom, userTo);
        return true;
    }


    public ArrayList<User> searchUsersFromFile(String s) {
        ArrayList<User> users = readUsersFromFile();
        ArrayList<User> matchedUsers = new ArrayList<>();

        for (User user : users) {
            if (user.getUsername().startsWith(s)) {
                matchedUsers.add(user);
            }
        }
        return matchedUsers;
    }

    public ArrayList<User> readUsersFromFile() {
        ArrayList<User> users = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.bin"))) {
            users = (ArrayList<User>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public void updateUsersInFile(ArrayList<User> users, User... updatedUsers) {
        for (User updatedUser : updatedUsers) {
            users.removeIf(u -> u.getUsername().equals(updatedUser.getUsername()));
            users.add(updatedUser);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.bin"))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void deleteMessage(String fileName, String messageId) {
        ArrayList<Message> messages = new ArrayList<Message>();

        try (var ois = new ObjectInputStream(new FileInputStream(fileName))) {
            messages = (ArrayList<Message>) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        messages.removeIf(message -> String.valueOf(message.getMessageId()).equals(messageId));

        try (var oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
