


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

public class MessageTest {

    private Message textMessage;
    private Message imageMessage;
    private User sender;
    private User receiver;
    private byte[] sampleImage;

    @Before
    public void setUp() {
        sender = new User(
                "luffy",
                "gomugomuno",
                "luffy@onepiece.com",
                "Monkey D.",
                "Luffy",
                "luffy.jpg"
        );

        receiver = new User(
                "zoro",
                "santoryu",
                "zoro@onepiece.com",
                "Roronoa",
                "Zoro",
                "zoro.jpg"
        );

        sampleImage = new byte[]{1, 2, 3, 4, 5};

        textMessage = new Message(
                "Hello, Zoro!",
                false,
                sender,
                receiver,
                LocalDateTime.now(),
                null
        );

        imageMessage = new Message(
                null,
                true,
                sender,
                receiver,
                LocalDateTime.now(),
                sampleImage
        );
    }

    // Basic Tests
    @Test
    public void testGetMessageId() {
        textMessage.setMessageId(123);
        assertEquals(123, textMessage.getMessageId());
    }

    @Test
    public void testSetMessageId() {
        imageMessage.setMessageId(456);
        assertEquals(456, imageMessage.getMessageId());
    }

    @Test
    public void testGetMessage() {
        assertEquals("Hello, Zoro!", textMessage.getMessage());
        assertNull(imageMessage.getMessage());
    }

    @Test
    public void testSetMessage() {
        textMessage.setMessage("New Message");
        assertEquals("New Message", textMessage.getMessage());
    }

    @Test
    public void testIsImage() {
        assertFalse(textMessage.isImage());
        assertTrue(imageMessage.isImage());
    }

    // Edge Case Tests
    @Test
    public void testNullMessageForImage() {
        assertNull(imageMessage.getMessage()); // Ensure message is null for an image
    }

    @Test
    public void testNullImageForText() {
        assertNull(textMessage.getImage()); // Ensure image is null for a text message
    }

    @Test
    public void testSetSender() {
        User newSender = new User(
                "sanji",
                "diablejambe",
                "sanji@onepiece.com",
                "Sanji",
                "Vinsmoke",
                "sanji.jpg"
        );

        textMessage.setSenderUsername(newSender);
        assertEquals("sanji", textMessage.getSender().getUsername());
    }

    @Test
    public void testGetTimestamp() {
        LocalDateTime timestamp = LocalDateTime.now();
        textMessage.setTimestamp(timestamp);
        assertEquals(timestamp, textMessage.getTimestamp());
    }

    @Test
    public void testGetImage() {
        assertArrayEquals(sampleImage, imageMessage.getImage());
    }

    @Test
    public void testSetImage() {
        byte[] newImage = new byte[]{10, 20, 30, 40, 50};
        imageMessage.setImage(newImage);
        assertArrayEquals(newImage, imageMessage.getImage());
    }

    // Edge Case: Null Image
    @Test
    public void testSetNullImage() {
        imageMessage.setImage(null);
        assertNull(imageMessage.getImage());
    }

    // Edge Case: Null Timestamp
    @Test
    public void testSetNullTimestamp() {
        textMessage.setTimestamp(null);
        assertNull(textMessage.getTimestamp());
    }
}
