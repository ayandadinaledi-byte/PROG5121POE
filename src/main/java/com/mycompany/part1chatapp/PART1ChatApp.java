package com.mycompany.part1chatapp;

import java.util.Scanner;

public class PART1ChatApp {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.println("--- REGISTER ---");
        System.out.print("Enter First Name: ");
        String firstName = input.nextLine();

        System.out.print("Enter Last Name: ");
        String lastName = input.nextLine();

        System.out.print("Enter Username: ");
        String username = input.nextLine();

        System.out.print("Enter Password: ");
        String password = input.nextLine();

        System.out.print("Enter Cell Phone Number with +27: ");
        String cell = input.nextLine();

        Login login = new Login(firstName, lastName, username, password, cell);

        String result = login.registerUser();
        System.out.println(result);

        // Registration passed when the cell number message reports success
        if (result.contains("Cell") && result.contains("successfully")) {
            System.out.println("\n--- LOGIN ---");
            System.out.print("Enter Username: ");
            String loginUsername = input.nextLine();

            System.out.print("Enter Password: ");
            String loginPassword = input.nextLine();

            boolean loggedIn = login.loginUser(loginUsername, loginPassword);
            System.out.println(login.returnLoginStatus(loginUsername, loginPassword));

            // Messages can only be sent after a successful login
            if (loggedIn) {
                startQuickChat(input, loginUsername);
            }
        }
    }

    // ---------------- MAIN MENU ----------------

    public static void startQuickChat(Scanner input, String sender) {
        System.out.println("\nWelcome to QuickChat.");
        System.out.println(StoredMessage.loadStoredMessagesFromJson());
        boolean running = true;

        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            System.out.print("Choose an option: ");
            String choice = input.nextLine();

            if (choice.equals("1")) {
                sendMessages(input);
            } else if (choice.equals("2")) {
                System.out.println("Coming Soon.");
            } else if (choice.equals("3")) {
                System.out.println("Goodbye.");
                running = false;
            } else if (choice.equals("4")) {
                storedMessagesMenu(input, sender);
            } else {
                System.out.println("Invalid option. Please choose 1, 2, 3 or 4.");
            }
        }
    }

    // ---------------- SEND MESSAGES (Part 2) ----------------

    public static void sendMessages(Scanner input) {
        Message checker = new Message();

        System.out.print("How many messages do you want to enter? ");
        int howMany = Integer.parseInt(input.nextLine());

        for (int i = 1; i <= howMany; i++) {
            System.out.println("\n--- Message " + i + " ---");

            // Ask again until the recipient number is valid
            String recipient = "";
            boolean cellOk = false;
            while (!cellOk) {
                System.out.print("Enter recipient cell number (e.g. +27831234567): ");
                recipient = input.nextLine();
                String cellResult = checker.checkRecipientCell(recipient);
                System.out.println(cellResult);
                if (cellResult.equals("Cell phone number successfully captured.")) {
                    cellOk = true;
                }
            }

            // Ask again until the message is 250 characters or less
            String text = "";
            boolean textOk = false;
            while (!textOk) {
                System.out.print("Enter your message: ");
                text = input.nextLine();
                String lengthResult = checker.checkMessageLength(text);
                System.out.println(lengthResult);
                if (lengthResult.equals("Message ready to send.")) {
                    textOk = true;
                }
            }

            // Message number comes from the loop counter
            Message msg = new Message(i, recipient, text);

            // Ask until the user gives a valid choice
            String option = "";
            boolean optionOk = false;
            while (!optionOk) {
                System.out.println("1) Send Message");
                System.out.println("2) Disregard Message");
                System.out.println("3) Store Message to send later");
                System.out.print("Choose an option: ");
                option = input.nextLine();
                if (option.equals("1") || option.equals("2") || option.equals("3")) {
                    optionOk = true;
                } else {
                    System.out.println("Invalid option. Please choose 1, 2 or 3.");
                }
            }

            System.out.println(msg.processMessage(option));

            if (option.equals("1")) {
                // Details are shown only after the message has been sent
                System.out.println("\n" + msg.getMessageDetails());
            } else if (option.equals("2")) {
                String zero = input.nextLine();
                if (zero.equals("0")) {
                    System.out.println("Message deleted.");
                }
            }
        }

        System.out.println("\nTotal messages sent: " + Message.returnTotalMessages());
    }

    // ---------------- STORED MESSAGES (Part 3) ----------------

    public static void storedMessagesMenu(Scanner input, String sender) {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- STORED MESSAGES ---");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search messages for a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display report of all stored messages");
            System.out.println("7) Back to main menu");
            System.out.print("Choose an option: ");
            String choice = input.nextLine();

            if (choice.equals("1")) {
                System.out.println(StoredMessage.storedSendersAndRecipients(sender));
            } else if (choice.equals("2")) {
                System.out.println("Longest stored message: " + StoredMessage.longestStoredMessage());
            } else if (choice.equals("3")) {
                System.out.print("Enter message ID: ");
                System.out.println(StoredMessage.searchByID(input.nextLine().trim()));
            } else if (choice.equals("4")) {
                System.out.print("Enter recipient cell number: ");
                String[] found = StoredMessage.findMessagesByRecipient(input.nextLine().trim());
                if (found.length == 0) {
                    System.out.println("No messages found for that recipient.");
                } else {
                    for (int i = 0; i < found.length; i++) {
                        System.out.println((i + 1) + ") " + found[i]);
                    }
                }
            } else if (choice.equals("5")) {
                System.out.print("Enter message hash: ");
                String outcome = StoredMessage.deleteByHash(input.nextLine().trim().toUpperCase());
                System.out.println(outcome);
                if (outcome.contains("successfully deleted")) {
                    StoredMessage.rewriteJsonFile();   // keep messages.json in step with the arrays
                }
            } else if (choice.equals("6")) {
                System.out.println(StoredMessage.storedMessagesReport());
            } else if (choice.equals("7")) {
                back = true;
            } else {
                System.out.println("Invalid option. Please choose 1 to 7.");
            }
        }
    }
}