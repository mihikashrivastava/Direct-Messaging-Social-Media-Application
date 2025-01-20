


import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MainServerTestFinal {

    private static MessageManager messageManager;

    @BeforeAll
    static void setUp() {
        // Create test data
        TestUtility.createTestData();
        messageManager = new MessageManager();
    }

    @AfterAll
    static void tearDown() {
        // Reset the test environment
        TestUtility.clearTestData();
    }

    @Test
    void testSearchUserExists() throws Exception {
        String username = TestUtility.getRandomUsername();
        User user = MainServer.searchUserFromUsername(username);

        assertNotNull(user, "User should be found for username: " + username);
    }

    @Test
    void testSearchUserNotExists() {
        String username = "nonexistentUser";

        assertThrows(
                UserNotFoundException.class,
                () -> MainServer.searchUserFromUsername(username),
                "UserNotFoundException should be thrown for non-existent user"
        );
    }

    @Test
    void testProcessCommandBlockAndUnblock() {
        String user1 = TestUtility.getRandomUsername();
        String user2 = TestUtility.getRandomUsername();

        while (user1.equals(user2)) {
            user2 = TestUtility.getRandomUsername();
        }

        String blockResult = MainServer.processCommand(
                "block," + user1 + "," + user2,
                messageManager
        );
        assertEquals("Your block command has succeeded", blockResult);

        String unblockResult = MainServer.processCommand(
                "unblock," + user1 + "," + user2,
                messageManager
        );
        assertEquals("Your unblock command has succeeded", unblockResult);
    }

    @Test
    void testProcessCommandAddAndRemoveFriend() {
        String user1 = TestUtility.getRandomUsername();
        String user2 = TestUtility.getRandomUsername();

        while (user1.equals(user2)) {
            user2 = TestUtility.getRandomUsername();
        }

        String addResult = MainServer.processCommand(
                "add," + user1 + "," + user2,
                messageManager
        );
        assertEquals("Your friend command has succeeded", addResult);

        String removeResult = MainServer.processCommand(
                "remove," + user1 + "," + user2,
                messageManager
        );
        assertEquals("Your remove command has succeeded", removeResult);
    }

    @Test
    void testProcessCommandAcceptAndRejectFriend() throws UserNotFoundException {
        String user1 = TestUtility.getRandomUsername();
        String user2 = TestUtility.getRandomUsername();

        while (user1.equals(user2)) {
            user2 = TestUtility.getRandomUsername();
        }

        // User 2 sends a friend request to User 1
        messageManager.sendFriendRequest(
                MainServer.searchUserFromUsername(user2),
                MainServer.searchUserFromUsername(user1)
        );

        String acceptResult = MainServer.processCommand(
                "accept friend," + user1 + "," + user2,
                messageManager
        );
        assertEquals("Your friend command has succeeded", acceptResult);

        // Remove friend and test rejection
        messageManager.removeFriend(
                MainServer.searchUserFromUsername(user1),
                MainServer.searchUserFromUsername(user2)
        );

        messageManager.sendFriendRequest(
                MainServer.searchUserFromUsername(user2),
                MainServer.searchUserFromUsername(user1)
        );

        String rejectResult = MainServer.processCommand(
                "reject friend," + user1 + "," + user2,
                messageManager
        );
        assertEquals("Your friend command has succeeded", rejectResult);
    }

    @Test
    void testProcessCommandSendAndReceiveMessage() {
        String user1 = TestUtility.getRandomUsername();
        String user2 = TestUtility.getRandomUsername();

        while (user1.equals(user2)) {
            user2 = TestUtility.getRandomUsername();
        }

        String messageContent = "Hello, this is a test message.";
        String sendResult = MainServer.processCommand(
                "send message," + user1 + "," + user2 + "," + messageContent,
                messageManager
        );
        assertEquals("Your message has succeeded", sendResult);
    }

    @Test
    void testProcessUnknownCommand() {
        String result = MainServer.processCommand(
                "nonexistent_command,"
                        + TestUtility.getRandomUsername()
                        + ","
                        + TestUtility.getRandomUsername(),
                messageManager
        );

        assertEquals("Unknown command", result);
    }
}
