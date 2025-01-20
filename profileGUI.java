
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class profileGUI implements ProfileGUIService {
    public void createProfileGUI(String loggedInUsername, PrintWriter pw, BufferedReader bfr) {
        JFrame frame = new JFrame("Profile");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Left panel (navigation)
        JPanel leftPanel = new JPanel(new GridLayout(8, 1));
        leftPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.10), frame.getHeight()));

        JButton chatButton = new JButton("Chat");
        JButton searchButton = new JButton("Search");
        JButton profileButton = new JButton("Profile");

        chatButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new MainGUI().createGUI(loggedInUsername, pw, bfr));
        });

        searchButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new searchUserGUI().createSearchUserGUI(loggedInUsername, pw, bfr));
        });

        profileButton.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "You are already on the Profile page!"));

        leftPanel.add(chatButton);
        leftPanel.add(searchButton);
        leftPanel.add(profileButton);

        // Center panel (Profile options)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.35), frame.getHeight()));

        JButton myProfileButton = new JButton("My Profile: " + loggedInUsername);
        JButton pendingRequestsButton = new JButton("View Pending Friend Requests");
        JButton blockedButton = new JButton("View Blocked");
        JButton friendsButton = new JButton("View Friends");
        JButton sentRequestsButton = new JButton("View Sent Friend Requests");
        JButton changePrivacyButton = new JButton("Change Privacy");

        changePrivacyButton.addActionListener(e -> {
            try {
                User loggedInUser = MainServer.searchUserFromUsername(loggedInUsername);
                loggedInUser.changeVisibility();
                updateUsersBin(loggedInUser);
                JOptionPane.showMessageDialog(null, String.format("Your Privacy Setting is now %s", 
                        loggedInUser.getVisibility() ? "Private" : "Public"), 
                        "Privacy Changed", JOptionPane.INFORMATION_MESSAGE);
            } catch (UserNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        centerPanel.add(myProfileButton);
        centerPanel.add(pendingRequestsButton);
        centerPanel.add(blockedButton);
        centerPanel.add(friendsButton);
        centerPanel.add(sentRequestsButton);
        centerPanel.add(changePrivacyButton);

        // Right panel (dynamic content)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.50), frame.getHeight()));

        // Event handlers for buttons
        myProfileButton.addActionListener(e -> updateRightPanelForMyProfile(loggedInUsername, rightPanel, frame));
        pendingRequestsButton.addActionListener(e -> updateRightPanelForPendingRequests(loggedInUsername, rightPanel, 
                frame));
        blockedButton.addActionListener(e -> updateRightPanelForBlockedUsers(loggedInUsername, rightPanel, frame));
        friendsButton.addActionListener(e -> updateRightPanelForFriends(loggedInUsername, rightPanel, frame));
        sentRequestsButton.addActionListener(e -> updateRightPanelForSentRequests(loggedInUsername, rightPanel, frame));

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(new JScrollPane(centerPanel), BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }

    public void updateRightPanelForMyProfile(String loggedInUsername, JPanel rightPanel, JFrame frame) {
        rightPanel.removeAll();

        User loggedInUser = getUser(loggedInUsername, frame);
        if (loggedInUser == null) return;

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel profilePicLabel = new JLabel();
        profilePicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profilePicLabel.setBorder(BorderFactory.createTitledBorder("Profile Picture"));
        profilePicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            String profilePicPath = loggedInUser.getProfileImage();
            if (profilePicPath != null && !profilePicPath.isEmpty()) {
                ImageIcon profilePic = new ImageIcon(profilePicPath);
                Image scaledImage = profilePic.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                profilePicLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                profilePicLabel.setText("No Picture Available");
            }
        } catch (Exception ex) {
            profilePicLabel.setText("No Picture Available");
        }

        userInfoPanel.add(profilePicLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
/*
        addEditableField("Username:", loggedInUser.getUsername(), userInfoPanel, newUsername -> {
            loggedInUser.setUsername(newUsername);
            updateFiles(loggedInUser);
        });
*/
        addEditableField("Password:", "********", userInfoPanel, newPassword -> {
            loggedInUser.setPassword(newPassword);
            updateFiles(loggedInUser);
        });

        addEditableField("First Name:", loggedInUser.getFirstName(), userInfoPanel, newFirstName -> {
            loggedInUser.setFirstName(newFirstName);
            updateFiles(loggedInUser);
        });

        addEditableField("Last Name:", loggedInUser.getLastName(), userInfoPanel, newLastName -> {
            loggedInUser.setLastName(newLastName);
            updateFiles(loggedInUser);
        });

        addEditableField("Email:", loggedInUser.getEmail(), userInfoPanel, newEmail -> {
            loggedInUser.setEmail(newEmail);
            updateFiles(loggedInUser);
        });

        rightPanel.add(new JScrollPane(userInfoPanel), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }


    public void updateRightPanelForPendingRequests(String loggedInUsername, JPanel rightPanel, JFrame frame) {
        rightPanel.removeAll();

        User loggedInUser = getUser(loggedInUsername, frame);
        if (loggedInUser == null) return;

        JPanel pendingRequestsPanel = new JPanel();
        pendingRequestsPanel.setLayout(new BoxLayout(pendingRequestsPanel, BoxLayout.Y_AXIS));

        for (User requestor : loggedInUser.getPendingFriendRequests()) {
            JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(requestor.getUsername());

            JButton acceptButton = new JButton("Accept");
            JButton declineButton = new JButton("Decline");

            acceptButton.setPreferredSize(new Dimension(80, 25));
            declineButton.setPreferredSize(new Dimension(80, 25));

            acceptButton.addActionListener(acceptEvent -> {
                loggedInUser.addFriend(requestor);
                requestor.addFriend(loggedInUser);
                loggedInUser.getPendingFriendRequests().remove(requestor);

                updateUsersBin(loggedInUser, requestor);
                updateRightPanelForPendingRequests(loggedInUsername, rightPanel, frame);

                JOptionPane.showMessageDialog(frame, "Friend request accepted!");
            });

            declineButton.addActionListener(declineEvent -> {
                loggedInUser.getPendingFriendRequests().remove(requestor);
                updateUsersBin(loggedInUser);

                updateRightPanelForPendingRequests(loggedInUsername, rightPanel, frame);
                JOptionPane.showMessageDialog(frame, "Friend request declined.");
            });

            requestPanel.add(nameLabel);
            requestPanel.add(acceptButton);
            requestPanel.add(declineButton);

            pendingRequestsPanel.add(requestPanel);
        }

        if (loggedInUser.getPendingFriendRequests().isEmpty()) {
            pendingRequestsPanel.add(new JLabel("No pending friend requests."));
        }

        rightPanel.add(new JScrollPane(pendingRequestsPanel), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void updateRightPanelForBlockedUsers(String loggedInUsername, JPanel rightPanel, JFrame frame) {
        rightPanel.removeAll();

        User loggedInUser = getUser(loggedInUsername, frame);
        if (loggedInUser == null) return;

        JPanel blockedPanel = new JPanel();
        blockedPanel.setLayout(new BoxLayout(blockedPanel, BoxLayout.Y_AXIS));

        for (User blocked : loggedInUser.getBlocked()) {
            JPanel blockedUserPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(blocked.getUsername());

            JButton unblockButton = new JButton("Unblock");
            unblockButton.setPreferredSize(new Dimension(80, 25));

            unblockButton.addActionListener(unblockEvent -> {
                loggedInUser.getBlocked().remove(blocked);
                updateUsersBin(loggedInUser);

                blockedPanel.remove(blockedUserPanel);
                blockedPanel.revalidate();
                blockedPanel.repaint();

                JOptionPane.showMessageDialog(frame, blocked.getUsername() + " has been unblocked.");
            });

            blockedUserPanel.add(nameLabel);
            blockedUserPanel.add(unblockButton);
            blockedPanel.add(blockedUserPanel);
        }

        if (loggedInUser.getBlocked().isEmpty()) {
            blockedPanel.add(new JLabel("No blocked users."));
        }

        rightPanel.add(new JScrollPane(blockedPanel), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void updateRightPanelForFriends(String loggedInUsername, JPanel rightPanel, JFrame frame) {
        rightPanel.removeAll();

        User loggedInUser = getUser(loggedInUsername, frame);
        if (loggedInUser == null) return;

        JPanel friendsPanel = new JPanel();
        friendsPanel.setLayout(new BoxLayout(friendsPanel, BoxLayout.Y_AXIS));

        for (User friend : loggedInUser.getFriends()) {
            JPanel friendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(friend.getUsername());

            JButton viewProfileButton = new JButton("View Profile");
            JButton unfriendButton = new JButton("Unfriend");
            JButton blockButton = new JButton("Block");

            viewProfileButton.setPreferredSize(new Dimension(120, 25));
            unfriendButton.setPreferredSize(new Dimension(80, 25));
            blockButton.setPreferredSize(new Dimension(80, 25));

            viewProfileButton.addActionListener(viewEvent -> {
                JOptionPane.showMessageDialog(
                        frame,
                        "Profile of " + friend.getUsername() + ":\n" +
                                "First Name: " + friend.getFirstName() + "\n" +
                                "Last Name: " + friend.getLastName() + "\n" +
                                "Email: " + friend.getEmail(),
                        "View Profile",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });

            unfriendButton.addActionListener(unfriendEvent -> {
                loggedInUser.getFriends().remove(friend);
                friend.getFriends().remove(loggedInUser);

                updateUsersBin(loggedInUser, friend);

                friendsPanel.remove(friendPanel);
                friendsPanel.revalidate();
                friendsPanel.repaint();

                JOptionPane.showMessageDialog(frame, friend.getUsername() + 
                        " has been removed from your friends.");
            });

            blockButton.addActionListener(blockEvent -> {
                loggedInUser.getFriends().remove(friend);
                friend.getFriends().remove(loggedInUser);
                loggedInUser.getBlocked().add(friend);

                updateUsersBin(loggedInUser, friend);

                friendsPanel.remove(friendPanel);
                friendsPanel.revalidate();
                friendsPanel.repaint();

                JOptionPane.showMessageDialog(frame, friend.getUsername() + 
                        " has been blocked and removed from your friends.");
            });

            friendPanel.add(nameLabel);
            friendPanel.add(viewProfileButton);
            friendPanel.add(unfriendButton);
            friendPanel.add(blockButton);

            friendsPanel.add(friendPanel);
        }

        if (loggedInUser.getFriends().isEmpty()) {
            friendsPanel.add(new JLabel("You have no friends."));
        }

        rightPanel.add(new JScrollPane(friendsPanel), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public void updateRightPanelForSentRequests(String loggedInUsername, JPanel rightPanel, JFrame frame) {
        rightPanel.removeAll();

        User loggedInUser = getUser(loggedInUsername, frame);
        if (loggedInUser == null) return;

        JPanel sentRequestsPanel = new JPanel();
        sentRequestsPanel.setLayout(new BoxLayout(sentRequestsPanel, BoxLayout.Y_AXIS));

        for (User recipient : loggedInUser.getRequestedFriends()) {
            JPanel requestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(recipient.getUsername());

            JButton cancelRequestButton = new JButton("Cancel");
            cancelRequestButton.setPreferredSize(new Dimension(80, 25));

            cancelRequestButton.addActionListener(cancelEvent -> {
                loggedInUser.getRequestedFriends().remove(recipient);
                updateUsersBin(loggedInUser);

                sentRequestsPanel.remove(requestPanel);
                sentRequestsPanel.revalidate();
                sentRequestsPanel.repaint();

                JOptionPane.showMessageDialog(frame, "Friend request to " + recipient.getUsername() +
                        " has been canceled.");
            });

            requestPanel.add(nameLabel);
            requestPanel.add(cancelRequestButton);
            sentRequestsPanel.add(requestPanel);
        }

        if (loggedInUser.getRequestedFriends().isEmpty()) {
            sentRequestsPanel.add(new JLabel("No sent friend requests."));
        }

        rightPanel.add(new JScrollPane(sentRequestsPanel), BorderLayout.CENTER);
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public User getUser(String username, JFrame frame) {
        try {
            return MainServer.searchUserFromUsername(username);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error fetching user: " + ex.getMessage(), "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void updateFiles(User user) {
        // Update users.bin
        ArrayList<User> allUsers = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.bin"))) {
            allUsers = (ArrayList<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }

        boolean userUpdated = false;
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getUsername().equals(user.getUsername())) {
                allUsers.set(i, user);
                userUpdated = true;
                break;
            }
        }

        if (!userUpdated) {
            allUsers.add(user);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.bin"))) {
            oos.writeObject(allUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update accounts.txt
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(user.getUsername())) {
                    lines.add(user.getUsername() + "," + user.getPassword() + "," + user.getEmail() + ","
                            + user.getFirstName() + "," + user.getLastName() + "," + user.getProfileImage());
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("accounts.txt"))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateUsersBin(User... users) {
        ArrayList<User> allUsers = new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.bin"))) {
            allUsers = (ArrayList<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
        }

        for (User user : users) {
            boolean updated = false;
            for (int i = 0; i < allUsers.size(); i++) {
                if (allUsers.get(i).getUsername().equals(user.getUsername())) {
                    allUsers.set(i, user);
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                allUsers.add(user);
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.bin"))) {
            oos.writeObject(allUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addEditableField(String label, String value, JPanel panel, FieldUpdater updater) {
        JPanel fieldPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel fieldLabel = new JLabel(label);
        JTextField textField = new JTextField(value);
        textField.setPreferredSize(new Dimension(150, 25));
        JButton updateButton = new JButton("Update");
        updateButton.setPreferredSize(new Dimension(75, 25));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        fieldPanel.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldPanel.add(textField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        fieldPanel.add(updateButton, gbc);

        updateButton.addActionListener(e -> {
            String newValue = textField.getText().trim();
            if (!newValue.isEmpty()) {
                updater.update(newValue);
                JOptionPane.showMessageDialog(panel, label + " updated successfully!");
            } else {
                JOptionPane.showMessageDialog(panel, label + " cannot be empty!", "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    @FunctionalInterface
    public interface FieldUpdater {
        void update(String newValue);
    }
}
