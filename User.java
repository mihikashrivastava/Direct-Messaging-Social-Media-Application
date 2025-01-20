
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class User implements Serializable, UserService {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImage;
    private LocalDateTime lastLoginAt;
    private ArrayList<User> friends;
    private ArrayList<User> blocked;
    private ArrayList<User> requestedFriend;
    private ArrayList<User> requestedFriends;
    private ArrayList<User> pendingFriendRequests;
    public ArrayList<User> chats;
    private boolean privateAccount = false;

    public User(String userName, String password, String email, String firstName, String lastName, String profileImage) 
    {
        this.username = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profileImage = profileImage;
        this.friends = new ArrayList<>();
        this.blocked = new ArrayList<>();
        this.requestedFriend = new ArrayList<>();
        this.pendingFriendRequests = new ArrayList<>();
        this.chats = new ArrayList<>();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(username, user.username) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, email, password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public boolean addFriend(User friend) {
        if (friends.contains(friend)) {
            return false;
        }
        friends.add(friend);
        return true;
    }

    public int getFriend(User friend) {
        return friends.indexOf(friend);
    }

    public ArrayList<User> getPendingFriendRequests() {
        return pendingFriendRequests;
    }

    public ArrayList<User> getRequestedFriends() {
        return requestedFriend;
    }

    public boolean getVisibility() {
        return privateAccount;
    }

    public boolean removeFriend(User friend) {
        if (friends.contains(friend)) {
            friends.remove(friend);
            return true;
        }
        return false;
    }

    public boolean addBlocked(User blockedUser) {
        if (!blocked.contains(blockedUser)) {
            blocked.add(blockedUser);
            return true;
        }
        return false;
    }

    public ArrayList<User> getBlocked() {
        return blocked;
    }

    public boolean removeBlocked(User blockedUser) {
        if (blocked.contains(blockedUser)) {
            blocked.remove(blockedUser);
            return true;
        }
        return false;
    }

    public void addUserToFile(User user) {
        ArrayList<User> users = new ArrayList<>();

        // Read existing users from file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.bin"))) {
            users = (ArrayList<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            // File might not exist initially; this is fine and expected for first-time write
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        users.add(user);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.bin"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean changeVisibility() {
        privateAccount = !privateAccount;
        return privateAccount;
    }
}
