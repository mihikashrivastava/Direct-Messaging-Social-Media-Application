
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.*;

public class MainClient {

    static Socket socket = null;
    static BufferedReader bfr = null;
    static PrintWriter pw = null;

    public static void main(String[] args) {
        // The following try-catch is creating the socket, bufferedreader,
        // and printwriter while catching an IOException
        try {

            Socket socket = new Socket("localhost", 8080);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            AtomicReference<String> loggedInUsername = new AtomicReference<>("");
            LoginSignUp loginSignUp = new LoginSignUp();

            // Login

            JFrame frame = new JFrame("Purdue Social Media");
            frame.setSize(400, 450);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new CardLayout());

            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(null);
            loginPanel.setBackground(new Color(173, 216, 230));

            JLabel loginTitle = new JLabel("Login");
            loginTitle.setBounds(150, 20, 100, 30);
            loginTitle.setFont(new Font("Arial", Font.BOLD, 24));
            loginPanel.add(loginTitle);

            JLabel usernameLabel = new JLabel("User Name");
            usernameLabel.setBounds(50, 80, 100, 30);
            loginPanel.add(usernameLabel);

            JTextField usernameField = new JTextField();
            usernameField.setBounds(150, 80, 200, 30);
            loginPanel.add(usernameField);

            JLabel passwordLabel = new JLabel("Password");
            passwordLabel.setBounds(50, 130, 100, 30);
            loginPanel.add(passwordLabel);

            JPasswordField passwordField = new JPasswordField();
            passwordField.setBounds(150, 130, 200, 30);
            loginPanel.add(passwordField);

            JButton loginButton = new JButton("Login");
            loginButton.setBounds(150, 180, 100, 30);
            loginPanel.add(loginButton);

            JButton goToRegisterButton = new JButton("Register Now");
            goToRegisterButton.setBounds(140, 220, 120, 30);
            loginPanel.add(goToRegisterButton);

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Fields cannot be empty!", "Error", 
                                                  JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String loginResponse = loginSignUp.authorizeLogin(username, password);


                if (loginResponse.equals("Wrong password! Please try again.")) {
                    JOptionPane.showMessageDialog(null, "Wrong password! Please " +
                            "try again.", "Invalid Password", JOptionPane.ERROR_MESSAGE);

                } else if (loginResponse.equals("Username does not exist")) {
                    JOptionPane.showMessageDialog(null, "Username does " +
                            "not exist! Please try again.", "Invalid Username", JOptionPane.ERROR_MESSAGE);

                } else {
                    String[] loginInfo = loginSignUp.authorizeLogin(username, password).split(",");

                    String authorizedUser = loginInfo[0];
                    String authorizedPass = loginInfo[1];
                    loggedInUsername.set(username);
                    System.out.println(loggedInUsername);
                    // Additional logic for successful login actions
                    frame.dispose();
                    SwingUtilities.invokeLater(() -> new MainGUI().createGUI(String.valueOf(loggedInUsername), pw, 
                                                                             bfr));
                }

            });

            goToRegisterButton.addActionListener(e -> {
                frame.setVisible(false); //Setting login frame invisible
                JFrame registerFrame = new JFrame();
                registerFrame.setSize(400, 450);
                registerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                registerFrame.setLayout(new CardLayout());

                JPanel registerPanel = new JPanel();
                registerPanel.setLayout(null);
                registerPanel.setBackground(new Color(173, 216, 230));

                JLabel registerTitle = new JLabel("Register");
                registerTitle.setBounds(150, 20, 100, 30);
                registerTitle.setFont(new Font("Arial", Font.BOLD, 24));
                registerPanel.add(registerTitle);

                JLabel regUsernameLabel = new JLabel("User Name");
                regUsernameLabel.setBounds(50, 80, 100, 30);
                registerPanel.add(regUsernameLabel);

                JTextField regUsernameField = new JTextField();
                regUsernameField.setBounds(150, 80, 200, 30);
                registerPanel.add(regUsernameField);

                JLabel regPasswordLabel = new JLabel("Password");
                regPasswordLabel.setBounds(50, 130, 100, 30);
                registerPanel.add(regPasswordLabel);

                JPasswordField regPasswordField = new JPasswordField();
                regPasswordField.setBounds(150, 130, 200, 30);
                registerPanel.add(regPasswordField);

                JLabel emailLabel = new JLabel("Email");
                emailLabel.setBounds(50, 180, 100, 30);
                registerPanel.add(emailLabel);

                JTextField emailField = new JTextField();
                emailField.setBounds(150, 180, 200, 30);
                registerPanel.add(emailField);

                JLabel firstNameLabel = new JLabel("First Name");
                firstNameLabel.setBounds(50, 230, 100, 30);
                registerPanel.add(firstNameLabel);

                JTextField firstNameField = new JTextField();
                firstNameField.setBounds(150, 230, 200, 30);
                registerPanel.add(firstNameField);

                JLabel lastNameLabel = new JLabel("Last Name");
                lastNameLabel.setBounds(50, 280, 100, 30);
                registerPanel.add(lastNameLabel);

                JTextField lastNameField = new JTextField();
                lastNameField.setBounds(150, 280, 200, 30);
                registerPanel.add(lastNameField);

                JButton registerButton = new JButton("Register");
                registerButton.setBounds(150, 330, 100, 30);
                registerPanel.add(registerButton);

                JButton backToLoginButton = new JButton("Back to Login");
                backToLoginButton.setBounds(140, 380, 120, 30);
                registerPanel.add(backToLoginButton);


                registerButton.addActionListener(ae -> {
                    String username = regUsernameField.getText();
                    String password = new String(regPasswordField.getPassword());
                    String email = emailField.getText();
                    String firstName = firstNameField.getText();
                    String lastName = lastNameField.getText();

                    boolean usernameExists = false;
                    List<String> accountList = new ArrayList<>();
                    try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            accountList.add(line);
                        }
                        for (String account : accountList) {
                            String[] parts = account.split(",");
                            String existingUsername = parts[0];
                            if (existingUsername.equals(username)) {
                                usernameExists = true;
                                JOptionPane.showMessageDialog(null, "Username already exists." +
                                        " Please choose another.", "Username Taken", JOptionPane.ERROR_MESSAGE);
                                break;
                            }
                        }
                    } catch (IOException err) {
                        err.printStackTrace();
                    }

                    if (username.isEmpty() || password.isEmpty() || email.isEmpty() || firstName.isEmpty() || 
                        lastName.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Fields cannot be empty!", "Error", 
                                                      JOptionPane.ERROR_MESSAGE);
                        return;
                    } else if (password.contains(",")) {
                        JOptionPane.showMessageDialog(null, "Password cannot " +
                                "contain commas.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                    } else if (password.length() < 8) {
                        JOptionPane.showMessageDialog(null, "Password must" +
                                " be at least 8 characters.", "Invalid Password", JOptionPane.ERROR_MESSAGE);
                    } else {
                        try {
                            loginSignUp.createNewUser(username, password, email, firstName, lastName,
                                                      "default.jpg");
                            JOptionPane.showMessageDialog(null, "Account created successfully!");
                            registerFrame.dispose();
                            frame.setVisible(true);
                        } catch (BadDataException bde) {
                            JOptionPane.showMessageDialog(null,
                                    bde.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

                backToLoginButton.addActionListener(backToLoginAction -> {
                    registerFrame.dispose();
                    frame.setVisible(true);
                });

                registerFrame.add(registerPanel);
                registerFrame.setVisible(true);
            });

            frame.add(loginPanel);
            frame.setVisible(true);

            // End login

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pw != null) pw.close();
                if (bfr != null) bfr.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
