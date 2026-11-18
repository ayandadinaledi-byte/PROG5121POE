package com.mycompany.part1chatapp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * StoredMessage class (Part 3).
 * Holds the message arrays (plain arrays, no ArrayList) and the
 * search, delete, longest-message and report features.
 */
public class StoredMessage {

    private static final int MAX = 100;

    // Parallel arrays: index i in each array describes the same message
    private static String[] messageIDs = new String[MAX];
    private static String[] messageHashes = new String[MAX];
    private static String[] recipients = new String[MAX];
    private static String[] messageTexts = new String[MAX];
    private static String[] flags = new String[MAX];     // "Sent", "Stored" or "Disregard"
    private static int messageCount = 0;

    // Message text for each category
    private static String[] sentMessages = new String[MAX];
    private static String[] disregardedMessages = new String[MAX];
    private static String[] storedMessages = new String[MAX];
    private static int sentCount = 0;
    private static int disregardedCount = 0;
    private static int storedCount = 0;

    // ---------- Filling the arrays ----------

    // Adds a message to the parallel arrays and to its category array
    public static boolean addRecord(String id, String hash, String recipient, String text, String flag) {
        if (messageCount >= MAX) {
            return false;   // arrays are full
        }
        messageIDs[messageCount] = id;
        messageHashes[messageCount] = hash;
        recipients[messageCount] = recipient;
        messageTexts[messageCount] = text;
        flags[messageCount] = flag;
        messageCount++;

        if (flag.equals("Sent")) {
            sentMessages[sentCount] = text;
            sentCount++;
        } else if (flag.equals("Stored")) {
            storedMessages[storedCount] = text;
            storedCount++;
        } else {
            disregardedMessages[disregardedCount] = text;
            disregardedCount++;
        }
        return true;
    }

    // Creates the hash, adds the message and returns the hash (used by the unit tests)
    public static String addMessage(String id, int number, String recipient, String text, String flag) {
        String hash = Message.createMessageHash(id, number, text);
        addRecord(id, hash, recipient, text, flag);
        return hash;
    }

    public static void resetArrays() {
        messageCount = 0;
        sentCount = 0;
        disregardedCount = 0;
        storedCount = 0;
    }

    // Exact-size copies of the arrays
    public static String[] getSentMessages() {
        String[] copy = new String[sentCount];
        for (int i = 0; i < sentCount; i++) {
            copy[i] = sentMessages[i];
        }
        return copy;
    }

    public static String[] getStoredMessages() {
        String[] copy = new String[storedCount];
        for (int i = 0; i < storedCount; i++) {
            copy[i] = storedMessages[i];
        }
        return copy;
    }

    public static String[] getDisregardedMessages() {
        String[] copy = new String[disregardedCount];
        for (int i = 0; i < disregardedCount; i++) {
            copy[i] = disregardedMessages[i];
        }
        return copy;
    }

    public static String[] getMessageHashes() {
        String[] copy = new String[messageCount];
        for (int i = 0; i < messageCount; i++) {
            copy[i] = messageHashes[i];
        }
        return copy;
    }

    public static String[] getMessageIDs() {
        String[] copy = new String[messageCount];
        for (int i = 0; i < messageCount; i++) {
            copy[i] = messageIDs[i];
        }
        return copy;
    }

    // ---------- Stored messages menu features ----------

    // a. Sender and recipient of all stored messages
    public static String storedSendersAndRecipients(String sender) {
        String output = "";
        for (int i = 0; i < messageCount; i++) {
            if (flags[i].equals("Stored")) {
                output += "Sender: " + sender + " | Recipient: " + recipients[i] + "\n";
            }
        }
        if (output.isEmpty()) {
            return "No stored messages.";
        }
        return output;
    }

    // b. Longest stored message
    public static String longestStoredMessage() {
        String longest = "";
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].length() > longest.length()) {
                longest = storedMessages[i];
            }
        }
        if (longest.isEmpty()) {
            return "No stored messages.";
        }
        return longest;
    }

    private static int indexOfID(String id) {
        for (int i = 0; i < messageCount; i++) {
            if (messageIDs[i].equals(id)) {
                return i;
            }
        }
        return -1;
    }

    // c. Search by message ID: message text only
    public static String findMessageByID(String id) {
        int index = indexOfID(id);
        if (index == -1) {
            return "Message ID not found.";
        }
        return messageTexts[index];
    }

    // c. Search by message ID: recipient and message
    public static String searchByID(String id) {
        int index = indexOfID(id);
        if (index == -1) {
            return "Message ID not found.";
        }
        return "Recipient: " + recipients[index] + "\nMessage: " + messageTexts[index];
    }

    // d. All sent or stored messages for one recipient (exact-size array)
    public static String[] findMessagesByRecipient(String recipient) {
        int matches = 0;
        for (int i = 0; i < messageCount; i++) {
            if (recipients[i].equals(recipient) && !flags[i].equals("Disregard")) {
                matches++;
            }
        }
        String[] found = new String[matches];
        int position = 0;
        for (int i = 0; i < messageCount; i++) {
            if (recipients[i].equals(recipient) && !flags[i].equals("Disregard")) {
                found[position] = messageTexts[i];
                position++;
            }
        }
        return found;
    }

    // e. Delete a message using its hash
    public static String deleteByHash(String hash) {
        for (int i = 0; i < messageCount; i++) {
            if (messageHashes[i].equals(hash)) {
                String text = messageTexts[i];
                String flag = flags[i];

                if (flag.equals("Sent")) {
                    sentCount = removeText(sentMessages, sentCount, text);
                } else if (flag.equals("Stored")) {
                    storedCount = removeText(storedMessages, storedCount, text);
                } else {
                    disregardedCount = removeText(disregardedMessages, disregardedCount, text);
                }

                // shift the parallel arrays left to close the gap
                for (int j = i; j < messageCount - 1; j++) {
                    messageIDs[j] = messageIDs[j + 1];
                    messageHashes[j] = messageHashes[j + 1];
                    recipients[j] = recipients[j + 1];
                    messageTexts[j] = messageTexts[j + 1];
                    flags[j] = flags[j + 1];
                }
                messageCount--;
                return "Message: \"" + text + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    private static int removeText(String[] array, int count, String text) {
        for (int i = 0; i < count; i++) {
            if (array[i].equals(text)) {
                for (int j = i; j < count - 1; j++) {
                    array[j] = array[j + 1];
                }
                return count - 1;
            }
        }
        return count;
    }

    // Report of all SENT messages: Message Hash, Recipient, Message
    public static String sentMessagesReport() {
        String output = "";
        for (int i = 0; i < messageCount; i++) {
            if (flags[i].equals("Sent")) {
                output += "Message Hash: " + messageHashes[i]
                        + "\nRecipient: " + recipients[i]
                        + "\nMessage: " + messageTexts[i] + "\n\n";
            }
        }
        if (output.isEmpty()) {
            return "No messages sent.";
        }
        return output;
    }

    // f. Report with the full details of all STORED messages
    public static String storedMessagesReport() {
        String output = "";
        for (int i = 0; i < messageCount; i++) {
            if (flags[i].equals("Stored")) {
                output += "Message ID: " + messageIDs[i]
                        + "\nMessage Hash: " + messageHashes[i]
                        + "\nRecipient: " + recipients[i]
                        + "\nMessage: " + messageTexts[i] + "\n\n";
            }
        }
        if (output.isEmpty()) {
            return "No stored messages.";
        }
        return output;
    }

    // ---------- Reading and updating the JSON file ----------
  
    // Reads messages.json into the arrays (flag "Stored")
    public static String loadStoredMessagesFromJson() {
        String id = "";
        String rec = "";
        String msg = "";
        String hash = "";
        int loaded = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("messages.json"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"messageID\"")) {
                    id = extractValue(line);
                } else if (line.startsWith("\"recipient\"")) {
                    rec = extractValue(line);
                } else if (line.startsWith("\"message\"")) {
                    msg = extractValue(line);
                } else if (line.startsWith("\"messageHash\"")) {
                    hash = extractValue(line);
                } else if (line.startsWith("}")) {
                    // end of one JSON object (the line may be "}" or "},")
                    if (addRecord(id, hash, rec, msg, "Stored")) {
                        loaded++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            return "No stored messages file found yet.";
        } catch (IOException e) {
            return "Error reading messages.json: " + e.getMessage();
        }
        return loaded + " stored message(s) loaded from messages.json.";
    }

    private static String extractValue(String line) {
        int start = line.indexOf(": \"") + 3;
        int end = line.lastIndexOf("\"");
        String value = line.substring(start, end);
        return value.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    // Rewrites messages.json so it matches the stored messages currently in the arrays
    public static String rewriteJsonFile() {
        String content = "[\n";
        boolean first = true;
        for (int i = 0; i < messageCount; i++) {
            if (flags[i].equals("Stored")) {
                if (!first) {
                    content += ",\n";
                }
                content += Message.buildJsonEntry(messageIDs[i], recipients[i], messageTexts[i], messageHashes[i]);
                first = false;
            }
        }
        content += "\n]";

        try {
            Files.writeString(Paths.get("messages.json"), content);
            return "messages.json updated.";
        } catch (IOException e) {
            return "Error updating messages.json: " + e.getMessage();
        }
    }
}