import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RemoveDirectory {
    public static void removeDirectory(String[] commands, String currentDir) {
        if (commands.length < 2) {
            System.out.println("Error: Directory name not provided.");
            return;
        }

        String dirName = commands[1];
        Path dirPath = Paths.get(currentDir, dirName);

        try {
            if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
                if (Files.list(dirPath).findAny().isPresent()) {
                    System.out.println("Error: Directory is not empty.");
                } else {
                    Files.delete(dirPath);
                    System.out.println("Directory removed: " + dirPath);
                }
            } else {
                System.out.println("Error: Directory does not exist.");
            }
        } catch (IOException e) {
            System.out.println("Error: Could not remove directory - " + e.getMessage());
        }
    }

}
