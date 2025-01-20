
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface UserService {
    String getUsername();

    void setUsername(String username);

    public String getPassword();

    public void setPassword(String password);

    public String getEmail();

    public void setEmail(String email);

    public String getProfileImage();

    public void setProfileImage(String profileImage);

    public LocalDateTime getLastLoginAt();

    public void setLastLoginAt(LocalDateTime lastLoginAt);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    ArrayList<User> getFriends();

    public boolean addFriend(User friend);

    public int getFriend(User friend);

    public boolean removeFriend(User friend);

    public boolean addBlocked(User blockedUser);

    public ArrayList<User> getBlocked();

    public boolean removeBlocked(User blockedUser);

    public void addUserToFile(User user);

    public boolean changeVisibility();
}
