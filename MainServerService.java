
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;

public interface MainServerService {

    static User searchUserFromUsername(String username) throws UserNotFoundException {
        try (var ois = new ObjectInputStream(new FileInputStream("users.bin"))) {
            ArrayList<User> users = (ArrayList<User>) ois.readObject();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new UserNotFoundException("User not found");
    }

    static String processCommand(String inputFromClient, MessageManager messageManager) {
        String[] inputArr = inputFromClient.split(",");
        String outputToClient = "";

        try {
            switch (inputArr[0].toLowerCase()) {
                case "block": //Block a user
                    messageManager.block(
                            MainServer.searchUserFromUsername(inputArr[1]),
                            MainServer.searchUserFromUsername(inputArr[2])
                    );
                    outputToClient = "Your block command has succeeded";
                    break;
                case "unblock": // Unblock a user
                    messageManager.unblock(
                            MainServer.searchUserFromUsername(inputArr[1]),
                            MainServer.searchUserFromUsername(inputArr[2])
                    );
                    outputToClient = "Your unblock command has succeeded";
                    break;
                case "remove friend": // Remove a user from friend list
                case "remove": // Alternative command syntax for removing a friend
                    messageManager.removeFriend(
                            MainServer.searchUserFromUsername(inputArr[1]),
                            MainServer.searchUserFromUsername(inputArr[2])
                    );
                    outputToClient = "Your remove command has succeeded";
                    break;
                case "add friend": // Add a user as a friend
                case "add": // Alternative command syntax for adding a friend
                    messageManager.sendFriendRequest(
                            MainServer.searchUserFromUsername(inputArr[1]),
                            MainServer.searchUserFromUsername(inputArr[2])
                    );
                    outputToClient = "Your friend command has succeeded";
                    break;
                case "accept friend": // Accept a friend request
                    messageManager.acceptFriendRequest(
                            MainServer.searchUserFromUsername(inputArr[1]),
                            MainServer.searchUserFromUsername(inputArr[2])
                    );
                    outputToClient = "Your friend command has succeeded";
                    break;
                case "reject friend": // Reject a friend request
                    messageManager.rejectFriendRequest(
                            MainServer.searchUserFromUsername(inputArr[1]),
                            MainServer.searchUserFromUsername(inputArr[2])
                    );
                    outputToClient = "Your friend command has succeeded";
                    break;
                case "send message": // Send a message
                    messageManager.sendMessage(
                            MainServer.searchUserFromUsername(inputArr[1]),
                            MainServer.searchUserFromUsername(inputArr[2]),
                            messageManager.createMessageObject(
                                    inputArr[3],
                                    false,
                                    MainServer.searchUserFromUsername(inputArr[1]),
                                    MainServer.searchUserFromUsername(inputArr[2]),
                                    LocalDateTime.now(),
                                    null
                            )
                    );
                    outputToClient = "Your message has succeeded";
                    break;
                default: // Handle unknown commands
                    outputToClient = "Unknown command";
            }
        } catch (UserNotFoundException e) {
            // Handle case when a user is not found
            outputToClient = "User not found, Command Did not Succeed";
        }

        return outputToClient; // Return the response to the client
    }
}
