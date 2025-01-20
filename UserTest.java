
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserTest {

    private User jinx;
    private User vi;
    private User caitlyn;

    @Before
    public void setUp() {
        jinx = new User("jinx", "powpow", "jinx@piltover.com", "Jinx", "Zaun", "jinx.jpg");
        vi = new User("vi", "punchy", "vi@piltover.com", "Vi", "Enforcer", "vi.jpg");
        caitlyn = new User("caitlyn", "sheriff", "caitlyn@piltover.com", "Caitlyn", "Kiramman", "caitlyn.jpg");
    }

    // Basic Tests
    @Test
    public void testGetUsername() {
        assertEquals("jinx", jinx.getUsername());
    }

    @Test
    public void testSetUsername() {
        jinx.setUsername("chaos_queen");
        assertEquals("chaos_queen", jinx.getUsername());
    }

    @Test
    public void testGetPassword() {
        assertEquals("powpow", jinx.getPassword());
    }

    @Test
    public void testSetPassword() {
        jinx.setPassword("zapZap");
        assertEquals("zapZap", jinx.getPassword());
    }

    @Test
    public void testGetEmail() {
        assertEquals("jinx@piltover.com", jinx.getEmail());
    }

    @Test
    public void testSetEmail() {
        jinx.setEmail("chaos@zaun.com");
        assertEquals("chaos@zaun.com", jinx.getEmail());
    }

    @Test
    public void testSetProfileImageEmptyString() {
        jinx.setProfileImage("");
        assertEquals("", jinx.getProfileImage());
    }

    // Friend List Tests
    @Test
    public void testAddFriend() {
        jinx.addFriend(vi);
        assertTrue(jinx.getFriends().contains(vi));
    }

    @Test
    public void testGetFriend() {
        jinx.addFriend(vi);
        assertEquals(0, jinx.getFriend(vi));
    }

    @Test
    public void testRemoveFriend() {
        jinx.addFriend(vi);
        assertTrue(jinx.removeFriend(vi));
        assertFalse(jinx.getFriends().contains(vi));
    }

    @Test
    public void testAddFriendTwice() {
        jinx.addFriend(vi);
        jinx.addFriend(vi); // Attempt to add the same friend again
        assertEquals(1, jinx.getFriends().size()); // Should still only have one friend
    }

    @Test
    public void testRemoveNonExistentFriend() {
        assertFalse(jinx.removeFriend(vi)); // Vi was never added as a friend
    }

    // Block List Tests
    @Test
    public void testAddBlocked() {
        assertTrue(jinx.addBlocked(caitlyn));
        assertTrue(jinx.getBlocked().contains(caitlyn));
    }

    @Test
    public void testRemoveBlocked() {
        jinx.addBlocked(caitlyn);
        assertTrue(jinx.removeBlocked(caitlyn));
        assertFalse(jinx.getBlocked().contains(caitlyn));
    }

    @Test
    public void testAddBlockedTwice() {
        assertTrue(jinx.addBlocked(caitlyn));
        assertFalse(jinx.addBlocked(caitlyn)); // Trying to block Caitlyn again
        assertEquals(1, jinx.getBlocked().size()); // Should still only have one blocked user
    }

    @Test
    public void testRemoveNonExistentBlockedUser() {
        assertFalse(jinx.removeBlocked(vi)); // Vi was never added to blocked users
    }

    // Visibility Tests
    @Test
    public void testChangeVisibility() {
        boolean initialVisibility = jinx.getVisibility();
        assertNotEquals(initialVisibility, jinx.changeVisibility());
    }

    // File Operation Tests
    @Test
    public void testAddUserToFile() {
        // This test checks if the method can execute without exceptions.
        // It does not verify the content of the file.
        jinx.addUserToFile(jinx);
    }

    @Test
    public void testAddUserToFileMultipleTimes() {
        try {
            jinx.addUserToFile(jinx);
            jinx.addUserToFile(vi);
            // Test passes if no exception occurs
        } catch (Exception e) {
            fail("Exception should not be thrown when adding multiple users to file");
        }
    }

    // Boundary Case Tests
    @Test
    public void testInvalidEmailFormat() {
        jinx.setEmail("invalidEmail");
        assertEquals("invalidEmail", jinx.getEmail()); // Basic test, you might want to validate format in the class
    }
}
