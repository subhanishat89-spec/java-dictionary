package dictionary_final;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            String path = prompt(in, "Dictionary file path [dictionary.txt]: ");
            if (path == null) {
                return;
            }
            if (path.isEmpty()) {
                path = "dictionary.txt";
            }

            Dictionary dict;
            try {
                dict = new Dictionary(path);
            } catch (IOException e) {
                System.out.println("Could not open the dictionary file: " + e.getMessage());
                return;
            }
            System.out.println("Loaded " + dict.size() + " word(s) from " + path);

            while (true) {
                printMenu();
                String choice = prompt(in, "Choose an option: ");
                if (choice == null || choice.equals("8")) {
                    System.out.println("Goodbye!");
                    return;
                }
                try {
                    handle(choice, in, dict);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("Could not save the dictionary: " + e.getMessage());
                }
            }
        }
    }

    private static void handle(String choice, Scanner in, Dictionary dict) throws IOException {
        switch (choice) {
            case "1": {
                String word = prompt(in, "Enter a word: ");
                if (word == null) return;
                String def = dict.get(word);
                if (def != null) {
                    System.out.println(word.trim().toLowerCase() + ": " + def);
                } else {
                    System.out.println("Word not found.");
                    List<String> similar = dict.searchPrefix(word.length() >= 2 ? word.substring(0, 2) : word);
                    if (!similar.isEmpty() && !word.trim().isEmpty()) {
                        System.out.println("Did you mean: " + String.join(", ", similar) + "?");
                    }
                }
                break;
            }
            case "2": {
                String word = prompt(in, "New word: ");
                if (word == null) return;
                if (dict.contains(word)) {
                    String ans = prompt(in, "That word already exists. Overwrite? (y/n): ");
                    if (ans == null || !ans.equalsIgnoreCase("y")) {
                        System.out.println("Cancelled.");
                        return;
                    }
                }
                String def = prompt(in, "Definition: ");
                if (def == null) return;
                dict.put(word, def);
                System.out.println("Saved!");
                break;
            }
            case "3": {
                String word = prompt(in, "Word to update: ");
                if (word == null) return;
                if (!dict.contains(word)) {
                    System.out.println("Word not found.");
                    return;
                }
                System.out.println("Current: " + dict.get(word));
                String def = prompt(in, "New definition: ");
                if (def == null) return;
                dict.put(word, def);
                System.out.println("Updated!");
                break;
            }
            case "4": {
                String word = prompt(in, "Word to delete: ");
                if (word == null) return;
                System.out.println(dict.remove(word) ? "Deleted." : "Word not found.");
                break;
            }
            case "5": {
                String prefix = prompt(in, "Starts with: ");
                if (prefix == null) return;
                List<String> found = dict.searchPrefix(prefix);
                if (found.isEmpty()) {
                    System.out.println("No matches.");
                } else {
                    for (String w : found) {
                        System.out.println("  " + w + ": " + dict.get(w));
                    }
                }
                break;
            }
            case "6": {
                if (dict.size() == 0) {
                    System.out.println("The dictionary is empty.");
                } else {
                    for (Map.Entry<String, String> e : dict.all().entrySet()) {
                        System.out.println("  " + e.getKey() + ": " + e.getValue());
                    }
                }
                break;
            }
            case "7":
                System.out.println("The dictionary has " + dict.size() + " word(s).");
                break;
            default:
                System.out.println("Invalid option. Enter a number from 1 to 8.");
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("===== Dictionary =====");
        System.out.println("1. Look up a word");
        System.out.println("2. Add a word");
        System.out.println("3. Update a definition");
        System.out.println("4. Delete a word");
        System.out.println("5. Search by prefix");
        System.out.println("6. List all words");
        System.out.println("7. Word count");
        System.out.println("8. Exit");
    }

    /** Prints a prompt and returns the trimmed input, or null if input has ended. */
    private static String prompt(Scanner in, String message) {
        System.out.print(message);
        return in.hasNextLine() ? in.nextLine().trim() : null;
    }
}