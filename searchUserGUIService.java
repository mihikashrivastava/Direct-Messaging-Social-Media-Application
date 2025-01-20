
import java.io.*;

public interface searchUserGUIService {
    public void createSearchUserGUI(String loggedInUsername, PrintWriter pw, BufferedReader bfr);

    public void searchUsers();

    public void showUserActions();
}
