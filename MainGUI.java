
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.io.*;

public class MainGUI implements MainGUIService {
    public void createGUI(String loggedInUsername, PrintWriter pw, BufferedReader bfr) {
        JFrame frame = new JFrame(String.format("Currently logged in: %s", loggedInUsername));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        // Left panel (10% width)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(8, 1));
        leftPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.10), frame.getHeight()));

        JButton chatButton = new JButton("Chat");
        JButton searchButton = new JButton("Search");
        JButton profileButton = new JButton("Profile");

        searchButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new searchUserGUI().createSearchUserGUI(loggedInUsername, pw, bfr));
        });

        profileButton.addActionListener(e -> {
            frame.dispose();
            SwingUtilities.invokeLater(() -> new profileGUI().createProfileGUI(loggedInUsername, pw, bfr));
        });

        leftPanel.add(chatButton);
        leftPanel.add(searchButton);
        leftPanel.add(profileButton);

        // Center panel (35% width)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.35), frame.getHeight()));
        JScrollPane scrollableUserChatButtons = new JScrollPane(centerPanel);

        // Right panel (50% width, initially empty)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension((int) (frame.getWidth() * 0.50), frame.getHeight()));

        // Load user data and populate center panel with chat buttons
        User loggedInUser = null;
        try {
            loggedInUser = MainServer.searchUserFromUsername(loggedInUsername);
            if (loggedInUser == null) {
                throw new Exception("User not found");
            }
        } catch (Exception e) {
            System.err.println("Error fetching user: " + e.getMessage());
        }

        if (loggedInUser != null && loggedInUser.chats != null && !loggedInUser.chats.isEmpty()) {
            System.out.println("Chats loaded: " + loggedInUser.chats.size()); // Debug log
            for (User chat : loggedInUser.chats) {
                JButton userChatButton = new JButton(chat.getUsername());
                userChatButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                userChatButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); 
                // Allow full width in BoxLayout
                centerPanel.add(userChatButton);

                userChatButton.addActionListener(e -> {

                    rightPanel.removeAll();

                    // Chat history
                    JPanel chatHistory = new JPanel();
                    chatHistory.setLayout(new BoxLayout(chatHistory, BoxLayout.Y_AXIS));

                    String chatFileName = MessageManager.getCorrectFileName(userChatButton.getText(), loggedInUsername);

                    // Loading and displaying the messages
                    loadAndDisplayMessages(chatFileName, chatHistory, pw);


                    new Thread(() -> monitorFileForChanges(chatFileName, chatHistory, rightPanel, pw)).start();

                    JScrollPane messageScrollPane = new JScrollPane(chatHistory);


                    JPanel inputPanel = new JPanel();
                    JTextField chatField = new JTextField();
                    chatField.setPreferredSize(new Dimension((int) (500), 30));
                    JButton sendButton = new JButton("Send");
                    sendButton.setPreferredSize(new Dimension((int) (100), 30));

                    sendButton.addActionListener(ev -> {
                        String messageText = chatField.getText().trim();
                        if (!messageText.isEmpty()) {
                            pw.println(String.format("send message,%s,%s,%s", loggedInUsername, 
                                                     userChatButton.getText(), chatField.getText()));
                            String respo = "";
                            try {
                                respo = bfr.readLine();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                            if (respo.equals("Message could not be sent!")) {
                                JOptionPane.showMessageDialog(null, 
                                                              "There was an error sending the message.", "Error", 
                                                              JOptionPane.ERROR_MESSAGE);
                            }
                            chatField.setText("");
                        }
                    });

                    inputPanel.setLayout(new BorderLayout());
                    inputPanel.add(chatField, BorderLayout.CENTER);
                    inputPanel.add(sendButton, BorderLayout.EAST);
                    inputPanel.setPreferredSize(new Dimension(rightPanel.getWidth(), 30)); 
                    // Ensure the panel is visible

                    rightPanel.add(messageScrollPane, BorderLayout.CENTER);
                    rightPanel.add(inputPanel, BorderLayout.SOUTH);
                    rightPanel.revalidate();
                    rightPanel.repaint();
                });
            }
        } //else {
        //System.out.println("No chats available or loggedInUser.chats is null.");
        //}

        // Revalidate and repaint center panel
        centerPanel.revalidate();
        centerPanel.repaint();

        // Add panels to the frame
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(scrollableUserChatButtons, BorderLayout.CENTER);
        frame.add(rightPanel, BorderLayout.EAST);

        // Make the frame visible
        frame.setVisible(true);
    }

    public void loadAndDisplayMessages(String chatFileName, JPanel chatHistory, PrintWriter pw) {
        SwingUtilities.invokeLater(() -> {
            chatHistory.removeAll(); // Clear the panel
            ArrayList<Message> messages = MessageManager.getMessagesFromFile(chatFileName);
            for (Message message : messages) {
                JButton messageArea = new JButton(
                        String.format("%s: %s", message.sender.getUsername(), message.message));
                messageArea.setBackground(new Color(238, 238, 238));

                //Aligning buttons' text to the left
                messageArea.setHorizontalAlignment(SwingConstants.LEFT);

                //messageArea.setEditable(false);
                //messageArea.setLineWrap(true);
                //messageArea.setWrapStyleWord(true);
                messageArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
                messageArea.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5)); // Add padding

                messageArea.addActionListener(e -> {
                    int delete = JOptionPane.showConfirmDialog(null, 
                                                               "Do you want to delete this message?", "Confirmation", 
                                                               JOptionPane.YES_NO_OPTION);
                    if (delete == 0) {
                        pw.println(String.format("delete message,%s,%s", chatFileName, message.getMessageId()));
                    }
                });

                chatHistory.add(messageArea);
            }
            chatHistory.revalidate();
            chatHistory.repaint();
        });
    }

    public void monitorFileForChanges(String chatFileName, JPanel chatHistory, JPanel rightPanel, PrintWriter pw) {
        long lastModified = new File(chatFileName).lastModified();

        while (true) {
            try {
                Thread.sleep(200); // Check for changes every second
                File file = new File(chatFileName);
                if (file.lastModified() > lastModified) {
                    lastModified = file.lastModified();
                    // Update the chat history when file is modified
                    loadAndDisplayMessages(chatFileName, chatHistory, pw);
                    SwingUtilities.invokeLater(() -> {
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    });
                }
            } catch (InterruptedException e) {
                System.err.println("File watcher interrupted: " + e.getMessage());
            }
        }
    }
}
