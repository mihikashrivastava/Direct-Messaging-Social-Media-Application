
import javax.swing.*;
import java.io.*;

public interface MainGUIService {
    public void createGUI(String loggedInUsername, PrintWriter pw, BufferedReader bfr);

    public void loadAndDisplayMessages(String chatFileName, JPanel chatHistory, PrintWriter pw);

    public void monitorFileForChanges(String chatFileName, JPanel chatHistory, JPanel rightPanel, PrintWriter pw);
}
