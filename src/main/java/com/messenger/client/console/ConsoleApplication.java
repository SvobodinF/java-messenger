package com.messenger.client.console;

import com.messenger.backend.domain.Message;
import com.messenger.backend.friendship.FriendDto;
import com.messenger.backend.registration.RegistrationRequest;
import com.messenger.backend.auth.AuthRequest;
import com.messenger.backend.auth.AuthResponse;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

@Component
public class ConsoleApplication implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String AUTH_URL = "http://localhost:8080/auth";
    private static final String REGISTER_URL = "http://localhost:8080/register";
    private static final String FRIENDS_URL = "http://localhost:8080/api/friends";
    private static final String MESSAGES_URL = "http://localhost:8080/api/messages";

    private String currentToken = null;
    private Long currentUserId = null;

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            if (currentToken == null) {
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> register(scanner);
                    case 2 -> login(scanner);
                    case 3 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice, try again.");
                }
            } else {
                System.out.println("1. View Friends");
                System.out.println("2. Add Friend");
                System.out.println("3. View Friend Requests");
                System.out.println("4. Update Friend Request Status");
                System.out.println("5. Send Message");
                System.out.println("6. View Dialogues");
                System.out.println("7. View Messages With Friend");
                System.out.println("8. Logout");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1 -> viewFriends();
                    case 2 -> addFriendByName(scanner);
                    case 3 -> viewPendingRequests();
                    case 4 -> updateRequestStatus(scanner);
                    case 5 -> sendMessage(scanner);
                    case 6 -> viewDialogues();
                    case 7 -> viewMessagesWithFriend(scanner);
                    case 8 -> logout();
                    default -> System.out.println("Invalid choice, try again.");
                }
            }
        }
    }

    private void register(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            String response = restTemplate.postForObject(REGISTER_URL,
                    new RegistrationRequest(username, password), String.class);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    AUTH_URL,
                    new AuthRequest(username, password),
                    AuthResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                currentToken = response.getBody().token();
                currentUserId = extractUserIdFromToken(currentToken);
                System.out.println("Login successful!");
            } else {
                System.out.println("Login failed.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private Long extractUserIdFromToken(String token) {
        // Extract user ID from the token payload (if encoded as JSON Web Token)
        return 1L; // Placeholder; implement JWT decoding logic
    }

    private void logout() {
        currentToken = null;
        currentUserId = null;
        System.out.println("Logged out successfully.");
    }

    private void viewFriends() {
        try {
            String url = FRIENDS_URL + "/list?userId=" + currentUserId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + currentToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<FriendDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    FriendDto[].class
            );

            FriendDto[] friends = response.getBody();
            if (friends != null) {
                System.out.println("Your Friends:");
                for (FriendDto friend : friends) {
                    System.out.println(friend.friendName() + " - " + friend.status());
                }
            } else {
                System.out.println("You have no friends.");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving friends: " + e.getMessage());
        }
    }

    private void addFriendByName(Scanner scanner) {
        System.out.print("Enter friend's name: ");
        String friendName = scanner.nextLine();

        try {
            String url = FRIENDS_URL + "/add-by-name?userId=" + currentUserId + "&friendName=" + friendName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + currentToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println(response.getBody());
        } catch (Exception e) {
            System.out.println("Error adding friend: " + e.getMessage());
        }
    }

    private void viewPendingRequests() {
        try {
            String url = FRIENDS_URL + "/requests?userId=" + currentUserId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + currentToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<FriendDto[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    FriendDto[].class
            );

            FriendDto[] requests = response.getBody();
            if (requests != null) {
                System.out.println("Pending Friend Requests:");
                for (FriendDto request : requests) {
                    System.out.println("ID: " + request.id() + ", Name: " + request.friendName() + ", Status: " + request.status());
                }
            } else {
                System.out.println("No pending requests.");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving pending requests: " + e.getMessage());
        }
    }

    private void updateRequestStatus(Scanner scanner) {
        System.out.print("Enter friendship request ID: ");
        Long friendshipId = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter new status (ACCEPTED or REJECTED): ");
        String status = scanner.nextLine();

        try {
            String url = FRIENDS_URL + "/update-status?friendshipId=" + friendshipId + "&status=" + status;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + currentToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            System.out.println(response.getBody());
        } catch (Exception e) {
            System.out.println("Error updating friendship status: " + e.getMessage());
        }
    }

    private void sendMessage(Scanner scanner) {
        viewFriends(); // Выводим список друзей

        System.out.print("Enter friend's name: ");
        String friendName = scanner.nextLine();

        System.out.print("Enter your message: ");
        String text = scanner.nextLine();

        try {
            String url = MESSAGES_URL + "/send?senderId=" + currentUserId + "&friendName=" + friendName + "&text=" + text;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + currentToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println(response.getBody());
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    private void viewDialogues() {
        try {
            String url = MESSAGES_URL + "/dialogues?userId=" + currentUserId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + currentToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<String[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String[].class
            );

            String[] dialogues = response.getBody();
            if (dialogues != null) {
                System.out.println("Your Dialogues:");
                for (String dialogue : dialogues) {
                    System.out.println("- " + dialogue);
                }
            } else {
                System.out.println("No dialogues found.");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving dialogues: " + e.getMessage());
        }
    }

    private void viewMessagesWithFriend(Scanner scanner) {
        System.out.print("Enter friend's name: ");
        String friendName = scanner.nextLine();

        try {
            String url = MESSAGES_URL + "/with-friend?userId=" + currentUserId + "&friendName=" + friendName;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + currentToken);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Message[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Message[].class
            );

            Message[] messages = response.getBody();
            if (messages != null) {
                System.out.println("Messages with " + friendName + ":");
                for (Message message : messages) {
                    System.out.println((message.getSenderId().equals(currentUserId) ? "You: " : friendName + ": ") + message.getText());
                }
            } else {
                System.out.println("No messages found.");
            }
        } catch (Exception e) {
            System.out.println("Error retrieving messages: " + e.getMessage());
        }
    }
}