package com.mycompany.part1chatapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

/**
 * Message class for QuickChat (Part 2).
 * Holds one message, the checks, hash, send/disregard/store logic and JSON storing.
 */
public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private int messageNumber;

    private static int totalSent = 0;       // number of messages sent so far
    private static String sentLog = "";     // all messages sent while the program runs

    public Message() {
    }

    public Message(int messageNumber, String recipient, String messageText) {
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageID = generateMessageID();
        this.messageHash = createMessageHash(messageID, messageNumber, messageText);
    }

    // Builds a random 10-digit ID one digit at a time using substring
    public String generateMessageID() {
        Random rand = new Random();
        String digits = "0123456789";
        String id = "";
        for (int i = 0; i < 10; i++) {
            int position = rand.nextInt(10);
            id += digits.substring(position, position + 1);
        }
        return id;
    }

    public boolean checkMessageID(String id) {
        return id.length() <= 10;
    }

    public String checkRecipientCell(String cell) {
        if (cell != null && cell.startsWith("+") && cell.length() >= 10 && cell.length() <= 13) {
            String digits = cell.substring(1);
            if (digits.matches("\\d+")) {
                return "Cell phone number successfully captured.";
            }
        }
        return "Cell phone number is incorrectly formatted or does not contain an international code. "
                + "Please correct the number and try again.";
    }

    public String checkMessageLength(String message) {
        if (message.length() <= 250) {
            return "Message ready to send.";
        }
        int excess = message.length() - 250;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    // First two digits of ID : message number : first + last word (punctuation removed), all caps
    public static String createMessageHash(String id, int num, String message) {
        String trimmed = message.trim();
        int firstSpace = trimmed.indexOf(' ');
        int lastSpace = trimmed.lastIndexOf(' ');

        String firstWord = (firstSpace == -1) ? trimmed : trimmed.substring(0, firstSpace);
        String lastWord = (lastSpace == -1) ? trimmed : trimmed.substring(lastSpace + 1);

        firstWord = firstWord.replaceAll("[^A-Za-z0-9]", "");
        lastWord = lastWord.replaceAll("[^A-Za-z0-9]", "");

        return (id.substring(0, 2) + ":" + num + ":" + firstWord + lastWord).toUpperCase();
    }

    // 1 = Send, 2 = Disregard, 3 = Store
    public String sendMessage(String option) {
        if (option.equals("1")) {
            return "Message successfully sent.";
        } else if (option.equals("2")) {
            return "Press 0 to delete the message.";
        } else {
            return "Message successfully stored.";
        }
    }

    // Carries out the user's choice and passes the message to StoredMessage (Part 3 arrays)
    public String processMessage(String option) {
        String result = sendMessage(option);

        if (option.equals("1")) {
            totalSent++;
            sentLog += getMessageDetails() + "\n\n";
            StoredMessage.addRecord(messageID, messageHash, recipient, messageText, "Sent");
        } else if (option.equals("2")) {
            StoredMessage.addRecord(messageID, messageHash, recipient, messageText, "Disregard");
        } else {
            StoredMessage.addRecord(messageID, messageHash, recipient, messageText, "Stored");
            storeMessage(messageID, recipient, messageText, messageHash);
        }
        return result;
    }

    // Message ID, Message Hash, Recipient, Message (the order the brief asks for)
    public String getMessageDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + messageText;
    }

    // All messages sent while the program is running
    public static String printMessages() {
        if (sentLog.isEmpty()) {
            return "No messages sent.";
        }
        return sentLog;
    }

    public static int returnTotalMessages() {
        return totalSent;
    }

    public static void resetTotal() {
        totalSent = 0;
        sentLog = "";
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    // ---------------- JSON STORAGE ----------------
    // Reference: add the source(s) you actually used here (Harvard style), for example:
    // Author/Organisation (Year) Title of page. Available at: URL (Accessed: day month year).

    // One message as a JSON object (also used by StoredMessage when it rewrites the file)
    public static String buildJsonEntry(String id, String recipient, String message, String hash) {
        return "  {\n"
                + "    \"messageID\": \"" + escape(id) + "\",\n"
                + "    \"recipient\": \"" + escape(recipient) + "\",\n"
                + "    \"message\": \"" + escape(message) + "\",\n"
                + "    \"messageHash\": \"" + escape(hash) + "\"\n"
                + "  }";
    }

    // Adds the message to messages.json as an element of a JSON array: [ {...}, {...} ]
    public String storeMessage(String id, String recipient, String message, String hash) {
        String entry = buildJsonEntry(id, recipient, message, hash);
        Path path = Paths.get("messages.json");
        try {
            String content = "";
            if (Files.exists(path)) {
                content = Files.readString(path).trim();
            }

            String updated;
            if (content.isEmpty() || content.equals("[]")) {
                updated = "[\n" + entry + "\n]";
            } else {
                String withoutEnd = content.substring(0, content.lastIndexOf("]")).trim();
                updated = withoutEnd + ",\n" + entry + "\n]";
            }

            Files.writeString(path, updated);
            return "Message stored in messages.json.";
        } catch (IOException e) {
            return "Error storing message: " + e.getMessage();
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}