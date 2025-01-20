import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;

public class LoginSignUpTest {

    private LoginSignUp loginSignUp;

    @Before
    public void setUp() {
        loginSignUp = new LoginSignUp();

        // Clear the "accounts.txt" file before each test
        try (PrintWriter writer = new PrintWriter(new FileWriter("accounts.txt"))) {
            writer.write(""); // Clear the file
        } catch (IOException e) {
            fail("Setup failed: Unable to clear accounts.txt");
        }
    }

    // Basic Tests
    @Test
    public void testAuthorizeLoginCorrectCredentials() {
        // Simulate correct credentials
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt", true))) {
            pw.println("luffy,pirateking");
        } catch (IOException e) {
            fail("Setup failed: Unable to write to accounts.txt");
        }

        String result = loginSignUp.authorizeLogin("luffy", "pirateking");
        assertEquals("luffy,pirateking", result);
    }

    @Test
    public void testAuthorizeLoginIncorrectPassword() {
        // Simulate incorrect password
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt", true))) {
            pw.println("zoro,swordsman");
        } catch (IOException e) {
            fail("Setup failed: Unable to write to accounts.txt");
        }

        String result = loginSignUp.authorizeLogin("zoro", "wrongpassword");
        assertEquals("Wrong password! Please try again.", result);
    }

    @Test
    public void testAuthorizeLoginUsernameNotFound() {
        // Simulate a missing username
        String result = loginSignUp.authorizeLogin("nami", "navigator");
        assertEquals("Username does not exist", result);
    }

    // Edge Case Tests
    @Test
    public void testAuthorizeLoginWithEmptyUsername() {
        String result = loginSignUp.authorizeLogin("", "password");
        assertEquals("Username does not exist", result);
    }

    @Test
    public void testAuthorizeLoginWithEmptyPassword() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt", true))) {
            pw.println("usopp,sniperking");
        } catch (IOException e) {
            fail("Setup failed: Unable to write to accounts.txt");
        }

        String result = loginSignUp.authorizeLogin("usopp", "");
        assertEquals("Wrong password! Please try again.", result);
    }

    @Test
    public void testAuthorizeLoginWithNullUsername() {
        String result = loginSignUp.authorizeLogin(null, "password");
        assertEquals("Username does not exist", result);
    }

    @Test
    public void testAuthorizeLoginWithNullPassword() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt", true))) {
            pw.println("franky,shipwright");
        } catch (IOException e) {
            fail("Setup failed: Unable to write to accounts.txt");
        }

        String result = loginSignUp.authorizeLogin("franky", null);
        assertEquals("Wrong password! Please try again.", result);
    }

    // Exception Handling Tests for createNewUser
    @Test
    public void testCreateNewUserUsernameTaken() {
        // Simulate a username already taken
        try (PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt", true))) {
            pw.println("sanji,chef");
        } catch (IOException e) {
            fail("Setup failed: Unable to write to accounts.txt");
        }

        try {
            loginSignUp.createNewUser(
                    "sanji",
                    "cookingmaster",
                    "sanji@onepiece.com",
                    "Sanji",
                    "Vinsmoke",
                    "sanji.jpg"
            );
            fail("Expected BadDataException for username taken");
        } catch (BadDataException e) {
            assertEquals("Username taken", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testCreateNewUserInvalidUsername() {
        // Test for username containing spaces or commas
        try {
            loginSignUp.createNewUser(
                    "bad username",
                    "password123",
                    "bad@user.com",
                    "Bad",
                    "User",
                    "bad.jpg"
            );
            fail("Expected BadDataException for invalid username");
        } catch (BadDataException e) {
            assertEquals("Username contains either spaces or commas", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testCreateNewUserSuccess() {
        // Test for successful user creation
        try {
            User newUser = loginSignUp.createNewUser(
                    "brook",
                    "soulking",
                    "brook@onepiece.com",
                    "Brook",
                    "Soul",
                    "brook.jpg"
            );
            String username = newUser.getUsername();
            assertEquals("brook", username);
        } catch (BadDataException e) {
            fail("Unexpected BadDataException: " + e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
