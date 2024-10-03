import java.io.IOException;
import java.nio.file.*;

public class CreateDirectory {
    public static void createDirectory(String[] commands, String currentDir) {
        if (commands.length < 2) {
            System.out.println("Error: Directory name not provided.");
            return;
        }

        String dirName = commands[1];
        Path dirPath = Paths.get(currentDir, dirName);

        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
                System.out.println("Directory created: " + dirPath);
            } else {
                System.out.println("Error: Directory already exists.");
            }
        } catch (IOException e) {
            System.out.println("Error: Could not create directory - " + e.getMessage());
        }
    }
}
