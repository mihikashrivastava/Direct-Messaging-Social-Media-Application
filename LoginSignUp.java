
import java.io.*;
import java.util.ArrayList;

public class LoginSignUp implements LoginSignUpService {
    private String username;
    private String password;
    private final String NO_USERNAME = "Username does not exist";
    private final String PASSWORD_INCORRECT = "Wrong password! Please try again.";
    private final String USERNAME_TAKEN = "Username taken";
    private final String USERNAME_INVALID = "Username contains either spaces or commas";
    private final String PASSWORD_INVALID = "Password contains either spaces or commas";

    public LoginSignUp() {
    }

    public String authorizeLogin(String username, String password) {
        this.username = username;
        this.password = password;
        ArrayList<String> accountList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                accountList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading accounts file";
        }

        for (String account : accountList) {
            String[] parts = account.split(",");
            String accountUsername = parts[0];
            String accountPassword = parts[1];

            if (accountUsername.equals(username)) {
                if (accountPassword.equals(password)) {
                    return account;
                } else {
                    return PASSWORD_INCORRECT;
                }
            }
        }
        return NO_USERNAME;
    }

    public User createNewUser(String username, String password, String email, String firstName, String lastName, 
                              String profileImage) throws BadDataException {
        ArrayList<String> accountList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("accounts.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                accountList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadDataException("Error reading accounts file");
        }

        for (String account : accountList) {
            String[] parts = account.split(",");
            String accountUsername = parts[0];
            if (accountUsername.equals(username)) {
                throw new BadDataException(USERNAME_TAKEN);
            }
        }

        if (username.contains(",") || username.contains(" ")) {
            throw new BadDataException(USERNAME_INVALID);
        }
        if (password.contains(",") || password.contains(" ")) {
            throw new BadDataException(PASSWORD_INVALID);
        }

        User newUser = new User(username, password, email, firstName, lastName, profileImage);

        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt", true))) {
            pw.println(username + "," + password + "," + email + "," + firstName + "," + lastName + "," + profileImage);
            pw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadDataException("Error writing to accounts file");
        }

        var users = new ArrayList<User>();
        File f = new File("users.bin");
        if (f.length() > 0) {
            try (var ois = new ObjectInputStream(new FileInputStream(new File("users.bin")))) {
                users = (ArrayList<User>) ois.readObject();
            } catch (Exception e) {
                throw new BadDataException("Error writing to users file");
            }
        }
        users.add(newUser);

        try (var oos = new ObjectOutputStream(new FileOutputStream(new File("users.bin")))) {
            oos.writeObject(users);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadDataException("Error writing to users file");
        }

        return newUser;
    }
}
