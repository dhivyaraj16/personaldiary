import java.io.*; 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.InputMismatchException;

public class DiaryManagement{
    private static final String PASSWORD_FILE = "password.txt";
    private static final String DIARY_FOLDER = "DiaryEntries";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ensure diary folder exists
        new File(DIARY_FOLDER).mkdir();

        // Password setup/check
        if (!checkPassword(scanner)) {
            return;
        }

        while (true) {
            System.out.println("\nPersonal Diary");
            System.out.println("1. Write a new entry");
            System.out.println("2. View an existing entry");
            System.out.println("3. Search in diary entries");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> writeNewEntry(scanner);
                case 2 -> viewEntry(scanner);
                case 3 -> searchEntries(scanner);
                case 4 -> {
                    System.out.println("Thank You for using the Personal Diary!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static boolean checkPassword(Scanner scanner) {
        try {
            File passwordFile = new File(PASSWORD_FILE);
            if (!passwordFile.exists()) {
                // Set up new password
                System.out.print("No password found. Set up a new password: ");
                String newPassword = scanner.nextLine();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD_FILE))) {
                    writer.write(newPassword);
                }
                System.out.println("Password set successfully!");
            } else {
                // Verify password
                System.out.print("Enter your password: ");
                String inputPassword = scanner.nextLine();
                try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE))) {
                    String savedPassword = reader.readLine();
                    if (!inputPassword.equals(savedPassword)) {
                        System.out.println("Incorrect password. Access denied.");
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while handling the password.");
            return false;
        }
        return true;
    }

    private static void writeNewEntry(Scanner scanner) {
        System.out.print("Enter the title of your entry: ");
        String title = scanner.nextLine();

        System.out.print("Enter today's date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        String fileName = DIARY_FOLDER + "/" + date + ".txt";
        System.out.print("Enter the content of your entry: ");
        String content = scanner.nextLine();

        System.out.print("Do you want to add an image? (Yes/No): ");
        String imageResponse = scanner.nextLine().trim().toLowerCase();
        String imagePath = "";
        if (imageResponse.equals("yes")) {
            System.out.print("Enter the path to the image: ");
            imagePath = scanner.nextLine();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            // Add a timestamp
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            writer.write("\n[" + timeStamp + "]\n");

            // Write the entry details
            writer.write("Title: " + title + "\n");
            writer.write("Content: " + content + "\n");
            if (!imagePath.isEmpty()) {
                writer.write("Image: " + imagePath + "\n");
            }
            writer.write("----------------------\n");

            System.out.println("Your entry has been saved to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while saving your entry.");
        }
    }

    private static void viewEntry(Scanner scanner) {
        System.out.print("Enter the date of the entry you want to view (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        String fileName = DIARY_FOLDER + "/" + date + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            System.out.println("\nDiary Entry for " + date + ":");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No entry found for " + date + ".");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the entry.");
        }
    }

    private static void searchEntries(Scanner scanner) {
        System.out.print("Enter a keyword to search in your diary entries: ");
        String keyword = scanner.nextLine();

        File folder = new File(DIARY_FOLDER);
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No diary entries found.");
            return;
        }

        boolean found = false;
        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(keyword)) {
                        System.out.println("Found in " + file.getName() + ": " + line);
                        found = true;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred while searching " + file.getName());
            }
        }

        if (!found) {
            System.out.println("No entries found with the keyword \"" + keyword + "\".");
        }
    }
}

class DiaryEntry {
    private String title;
    private String content;
    private String imagePath; // Store the path to the image
    private LocalDateTime timestamp;

    public DiaryEntry(String title, String content, String imagePath, LocalDateTime timestamp) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Title: " + title +
               "\nDate: " + timestamp +
               "\nContent: " + content +
               (imagePath.isEmpty() ? "" : ("\nImage: " + imagePath)) +
               "\n-----------------------";
    }
}
