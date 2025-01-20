
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public interface ProfileGUIService {
    public void createProfileGUI(String loggedInUsername, PrintWriter pw, BufferedReader bfr);
    public void updateRightPanelForMyProfile(String loggedInUsername, JPanel rightPanel, JFrame frame);
    public void updateRightPanelForPendingRequests(String loggedInUsername, JPanel rightPanel, JFrame frame);
    public void updateRightPanelForBlockedUsers(String loggedInUsername, JPanel rightPanel, JFrame frame);
    public void updateRightPanelForFriends(String loggedInUsername, JPanel rightPanel, JFrame frame);
    public void updateRightPanelForSentRequests(String loggedInUsername, JPanel rightPanel, JFrame frame);
    public User getUser(String username, JFrame frame);
    public void updateFiles(User user);
    public void updateUsersBin(User... users);
    public void addEditableField(String label, String value, JPanel panel, profileGUI.FieldUpdater updater);
}
