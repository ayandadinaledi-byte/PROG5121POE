package com.mycompany.part1chatapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    private static final String MSG1 = "Hi Mike, can you join us for dinner tonight?";
    private static final String MSG2 = "Hi Keegan, did you receive the payment?";

    @Test
    public void testMessageLengthSuccess() {
        Message m = new Message();
        assertEquals("Message ready to send.", m.checkMessageLength(MSG1));
    }

    @Test
    public void testMessageLengthFailure() {
        Message m = new Message();
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                m.checkMessageLength("a".repeat(260)));
    }

    @Test
    public void testRecipientSuccess() {
        Message m = new Message();
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell("+27718693002"));
    }

    @Test
    public void testRecipientFailure() {
        Message m = new Message();
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.", m.checkRecipientCell("08575975889"));
    }

    @Test
public void testMessageHashTestCase1() {
    Message m = new Message();
    assertEquals("00:0:HITONIGHT", m.createMessageHash("0012345678", 0, MSG1));
}

@Test
public void testMessageHashesInLoop() {
    Message m = new Message();
    String[] texts = {MSG1, MSG2};
    String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};
    for (int i = 0; i < texts.length; i++) {
        assertEquals(expected[i], m.createMessageHash("0012345678", i, texts[i]));
        }
    }

    @Test
    public void testMessageIDCreated() {
        Message m = new Message(1, "+27718693002", MSG1);
        assertTrue(m.getMessageID().matches("\\d{10}"));
        assertTrue(m.checkMessageID(m.getMessageID()));
        System.out.println("Message ID generated: " + m.getMessageID());
    }

    @Test
    public void testSendMessageOptions() {
        Message m = new Message();
        assertEquals("Message successfully sent.", m.sendMessage("1"));
        assertEquals("Press 0 to delete the message.", m.sendMessage("2"));
        assertEquals("Message successfully stored.", m.sendMessage("3"));
    }

    // Test data: message 1 is sent, message 2 is discarded, so the total is 1
    @Test
    public void testTotalMessages() {
        Message.resetTotal();
        new Message(1, "+27718693002", MSG1).processMessage("1");
        new Message(2, "08575975889", MSG2).processMessage("2");
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testPrintMessages() {
        Message.resetTotal();
        new Message(1, "+27718693002", MSG1).processMessage("1");
        assertTrue(Message.printMessages().contains(MSG1));
    }
}