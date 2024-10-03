import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Assn3 {
    private static final List<String> commandHistory = new ArrayList<>();
    private static double totalExecutionTime = 0.0;
    private static String currentDir = System.getProperty("user.dir");

    public static void main(String[] args) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input;

        try {
            while (true) {
                System.out.print("[" + currentDir + "]: ");
                input = reader.readLine();

                // Split the command
                String[] commands = splitCommand(input);
                if (commands.length == 0) continue;


                // Handle the "^ <number>" command
                if (commands[0].equals("^") && commands.length == 2) {
                    try {
                        int commandIndex = Integer.parseInt(commands[1]) - 1;  // Convert to 0-based index
                        if (commandIndex >= 0 && commandIndex < commandHistory.size()) {
                            String historyCommand = commandHistory.get(commandIndex);
                            commands = splitCommand(historyCommand);  // Split and re-execute the command
                        } else {
                            System.out.println("Invalid history command number.");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format for history command.");
                        continue;
                    }
                }

                commandHistory.add(input);

                switch (commands[0]) {
                    case "exit":
                        return;
                    case "cd":
                        changeDirectory(commands);
                        break;
                    case "mdir":
                        createDirectory(commands);
                        break;
                    case "rdir":
                        removeDirectory(commands);
                        break;
                    case "list":
                        list();
                        break;
                    case "ptime":
                        printExecutionTime();
                        break;
                    case "history":
                        printHistory();
                        break;
                    default:
                        if (commands[commands.length - 1].equals("&")) { // Check for the & at the end of the command
                            // Remove the "&" from the command array
                            String[] finalCommands = new String[commands.length - 1];
                            System.arraycopy(commands, 0, finalCommands, 0, commands.length - 1);

                            // Run external command in a background thread
                            new Thread(() -> ExecuteCommand.executeCommand(finalCommands)).start();
                        } else {
                            // Run external command and wait for it to finish
                            totalExecutionTime += ExecuteCommand.executeCommand(commands);
                        }
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
        }
    }

    private static void changeDirectory(String[] commands) {
        ChangeDirectory cd = new ChangeDirectory();
        currentDir = cd.changeDirectory(commands, currentDir);  // Update currentDir
    }

    private static void list() {
        ListFiles list = new ListFiles();
        list.listFiles(currentDir);  // Use the tracked currentDir
    }

    // Method to create directories
    private static void createDirectory(String[] commands) {
        CreateDirectory.createDirectory(commands, currentDir);
    }

    // Method to remove directories
    private static void removeDirectory(String[] commands) {
        RemoveDirectory.removeDirectory(commands,currentDir);
    }

    // Method to print execution time
    private static void printExecutionTime() {
        System.out.printf("Total time in child processes: %.4f seconds%n", totalExecutionTime);
    }

    // Method to print command history
    private static void printHistory() {
        System.out.println("-- Command History --");
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + ": " + commandHistory.get(i));
        }
    }

    private static void executeCommand(String[] commands) {
        totalExecutionTime+= ExecuteCommand.executeCommand(commands);
    }

    // Method to split command input
    public static String[] splitCommand(String command) {
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                matchList.add(regexMatcher.group(2));
            } else {
                matchList.add(regexMatcher.group());
            }
        }
        return matchList.toArray(new String[0]);
    }
}
