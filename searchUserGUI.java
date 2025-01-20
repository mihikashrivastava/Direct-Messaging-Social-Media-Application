
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;


public class searchUserGUI implements searchUserGUIService {

    private JTextField searchField;
    private JList<String> userList;
    private DefaultListModel<String> userModel;
    private JPanel buttonPanel = new JPanel();
    private JButton friendRequestButton;
    private JButton blockButton;
    private JButton unblockButton;
    ArrayList<User> users = new MessageManager().readUsersFromFile();

    public void createSearchUserGUI(String loggedInUsername, PrintWriter pw, BufferedReader bfr) {
        JFrame frame = new JFrame(String.format("Currently logged in: %s", loggedInUsername));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Set the layout for the frame
        frame.setLayout(new BorderLayout());

        // Left panel (15% width)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(8, 1)); // 2 buttons in a column

        //ImageIcon chaticon = new ImageIcon("chaticon.png");
        JButton chatButton = new JButton("Chat");
        JButton searchButton = new JButton("Search");
        JButton profileButton = new JButton("Profile");

        searchButton.addActionListener(e -> {
            //Add code for search button
        });

        chatButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new MainGUI().createGUI(loggedInUsername, pw, bfr));
        });

        profileButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new profileGUI().createProfileGUI(loggedInUsername, pw, bfr));
        });

        leftPanel.add(chatButton);
        leftPanel.add(searchButton);
        leftPanel.add(profileButton);

        leftPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.10), frame.getHeight()));

        //CENTER PANEL
        JPanel centerPanel = new JPanel();
        centerPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.35), frame.getHeight()));
        centerPanel.setLayout(new BorderLayout());

        //Search field
        searchField = new JTextField();
        searchField.setToolTipText("Search for users...");
        searchField.addActionListener(e -> searchUsers());
        centerPanel.add(searchField, BorderLayout.NORTH);

        //User List
        userModel = new DefaultListModel<>();
        userList = new JList<>(userModel);
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.addListSelectionListener(e -> showUserActions());
        JScrollPane scrollPane = new JScrollPane(userList);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        //Right Panel
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.50), frame.getHeight()));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setVisible(false); // Initially hidden
        buttonPanel.setLayout(new GridLayout(5, 1));

        friendRequestButton = new JButton("Send Friend Request");
        friendRequestButton.addActionListener(e -> {
            pw.println(String.format("add friend,%s,%s", loggedInUsername, userList.getSelectedValue()));
            pw.flush();
            JOptionPane.showMessageDialog(null, "Friend request sent successfully!");
        });

        blockButton = new JButton("Block User");
        blockButton.addActionListener(e -> {
            pw.println(String.format("block,%s,%s", loggedInUsername, userList.getSelectedValue()));
            pw.flush();
            JOptionPane.showMessageDialog(null, "User blocked successfully.");
        });

        unblockButton = new JButton("Unblock User");
        unblockButton.addActionListener(e -> {
            pw.println(String.format("unblock,%s,%s", loggedInUsername, userList.getSelectedValue()));
            pw.flush();
            JOptionPane.showMessageDialog(null, "User unblocked successfully.");
        });

        JButton sendMessageButton = new JButton("Start Conversation");
        sendMessageButton.addActionListener(e -> {
            String textBox = JOptionPane.showInputDialog(null, "What is your message?",
                    "Type here...");
            if (textBox != null) {
                pw.println(String.format("send message,%s,%s,%s", loggedInUsername, userList.getSelectedValue(), 
                        textBox));
            }
            String response = "";
            try {
                response = bfr.readLine();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (response.startsWith("Message could not succeed")) {
                JOptionPane.showMessageDialog(null, "Error sending Message, " +
                        "User has a private account", "Error sending message", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton viewUserDetailsButton = new JButton("View User Details");
        viewUserDetailsButton.addActionListener(e -> {

            User viewUserUser = null;
            try {
                viewUserUser = MainServer.searchUserFromUsername(userList.getSelectedValue());
            } catch (UserNotFoundException ex) {
                ex.printStackTrace();
            }

            JFrame viewUserFrame = new JFrame(viewUserUser.getUsername());
            viewUserFrame.setLayout(new GridLayout(4, 1));
            viewUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            viewUserFrame.setSize(300, 150);

            JLabel viewUserUsernameField = new JLabel(String.format("Username: %s", viewUserUser.getUsername()));
            JLabel viewUserName = new JLabel(String.format("Name: %s %s", viewUserUser.getFirstName(), 
                    viewUserUser.getLastName()));
            JLabel viewUserEmail = new JLabel(String.format("Email: %s", viewUserUser.getEmail()));
            JLabel viewUserPrivacy = new JLabel(String.format("Privacy: %s", 
                    viewUserUser.getVisibility() ? "Private" : "Public"));

            viewUserFrame.add(viewUserUsernameField);
            viewUserFrame.add(viewUserName);
            viewUserFrame.add(viewUserEmail);
            viewUserFrame.add(viewUserPrivacy);
            viewUserFrame.setVisible(true);
        });

        buttonPanel.add(friendRequestButton);
        buttonPanel.add(blockButton);
        buttonPanel.add(unblockButton);
        buttonPanel.add(sendMessageButton);
        buttonPanel.add(viewUserDetailsButton);
        rightPanel.add(buttonPanel);


        //Adding all the panels to the frame
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);

        // Make the frame visible
        frame.setVisible(true);
    }

    public void searchUsers() {
        String query = searchField.getText().trim().toLowerCase();
        userModel.clear();
        if (!query.isEmpty()) {
            for (User user : users) {
                if (user.getUsername().toLowerCase().startsWith(query)) {
                    userModel.addElement(user.getUsername());
                }
            }
        }
    }

    public void showUserActions() {
        if (!userList.isSelectionEmpty()) {
            buttonPanel.setVisible(true);
        } else {
            buttonPanel.setVisible(false);
        }
    }
}
