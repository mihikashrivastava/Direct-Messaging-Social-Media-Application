
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws BadDataException, UserNotFoundException {
        User dhruv = MainServer.searchUserFromUsername("dhruv123");
        User garv = MainServer.searchUserFromUsername("garv123");
        //User andrew = MainServer.searchUserFromUsername("andrew123");

        MessageManager messageManager = new MessageManager();

        messageManager.sendMessage(dhruv, garv, messageManager.createMessageObject("hello garv", false, dhruv, garv, LocalDateTime.now(), null));
        //messageManager.sendMessage(dhruv, andrew, messageManager.createMessageObject("hello andrew", false, dhruv, andrew, LocalDateTime.now(), null));
        //messageManager.sendMessage(dhruv, garv, messageManager.createMessageObject("hello garv num 2", false, dhruv, garv, LocalDateTime.now(), null));
        //messageManager.sendMessage(andrew, dhruv, messageManager.createMessageObject("hello youre so sigma dhruv", false, andrew, dhruv, LocalDateTime.now(), null));

        /*
        for (User user : users) {
            if (user.getUsername().equals("dhruv123")) {
                System.out.println(user.getBlocked().size());
                for (User blocked : user.getBlocked()) {
                    System.out.println(blocked.getUsername());
                }
            }
        }
        System.out.println(messageManager.getCorrectFileName("dhruv123", "garv123"));


         */
    }
}
