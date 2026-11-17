package com.mycompany.part1chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StoredMessageTest {

    private String hashMessage2;   // hash of test message 2

    // Loads the Part 3 test data into the arrays before every test
    @BeforeEach
    public void loadTestData() {
        StoredMessage.resetArrays();
        StoredMessage.addMessage("1111111111", 1, "+27834557896", "Did you get the cake?", "Sent");
        hashMessage2 = StoredMessage.addMessage("2222222222", 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.", "Stored");
        StoredMessage.addMessage("3333333333", 3, "+27834484567", "Yohoooo, I am at your gate.", "Disregard");
        StoredMessage.addMessage("0838884567", 4, "0838884567", "It is dinner time!", "Sent");
        StoredMessage.addMessage("5555555555", 5, "+27838884567", "Ok, I am leaving without you.", "Stored");
    }

    @Test
    public void testSentMessagesArrayPopulated() {
        String[] expected = {"Did you get the cake?", "It is dinner time!"};
        assertArrayEquals(expected, StoredMessage.getSentMessages());
    }

    @Test
    public void testDisregardedArrayPopulated() {
        String[] expected = {"Yohoooo, I am at your gate."};
        assertArrayEquals(expected, StoredMessage.getDisregardedMessages());
    }

    @Test
    public void testStoredArrayPopulated() {
        assertEquals(2, StoredMessage.getStoredMessages().length);
    }

    @Test
    public void testHashAndIDArraysPopulated() {
        assertEquals(5, StoredMessage.getMessageHashes().length);
        assertEquals(5, StoredMessage.getMessageIDs().length);
        assertEquals("0838884567", StoredMessage.getMessageIDs()[3]);
    }

    @Test
    public void testLongestMessage() {
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                StoredMessage.longestStoredMessage());
    }

    @Test
    public void testSearchByMessageID() {
        assertEquals("It is dinner time!", StoredMessage.findMessageByID("0838884567"));
    }

    @Test
    public void testSearchByMessageIDNotFound() {
        assertEquals("Message ID not found.", StoredMessage.findMessageByID("9999999999"));
    }

    @Test
    public void testSearchByRecipient() {
        String[] expected = {"Where are you? You are late! I have asked you to be on time.",
                             "Ok, I am leaving without you."};
        assertArrayEquals(expected, StoredMessage.findMessagesByRecipient("+27838884567"));
    }

    @Test
    public void testDeleteByHash() {
        assertEquals("Message: \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.",
                StoredMessage.deleteByHash(hashMessage2));
        assertEquals(1, StoredMessage.getStoredMessages().length);
    }

    @Test
    public void testDeleteUnknownHash() {
        assertEquals("Message hash not found.", StoredMessage.deleteByHash("XX:9:NOTHING"));
    }

    @Test
    public void testDisplayReport() {
        String report = StoredMessage.sentMessagesReport();
        assertTrue(report.contains("11:1:DIDCAKE"));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains("It is dinner time!"));
    }

    @Test
    public void testStoredReport() {
        String report = StoredMessage.storedMessagesReport();
        assertTrue(report.contains("22:2:WHERETIME"));
        assertTrue(report.contains("Ok, I am leaving without you."));
    }
}