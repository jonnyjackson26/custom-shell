import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChangeDirectory {
    public String changeDirectory(String[] commands, String currentDir) {
        String newDir = commands.length > 1 ? commands[1] : System.getProperty("user.home");

        // If the path is relative, resolve it against the current directory
        Path path = Paths.get(newDir).isAbsolute() ? Paths.get(newDir) : Paths.get(currentDir).resolve(newDir);

        // Normalize the path to resolve ".." and other symbols
        path = path.normalize();

        // Ensure we don't go beyond the root directory
        if (!path.startsWith(System.getProperty("user.home"))) {
            System.out.println("Access denied: Cannot go outside the home directory.");
            return currentDir;
        }

        if (Files.exists(path) && Files.isDirectory(path)) {
            return path.toString();  // Return the new directory
        } else {
            System.out.println("Invalid directory: " + newDir);
            return currentDir;  // Return the original current directory if the change fails
        }
    }
}
