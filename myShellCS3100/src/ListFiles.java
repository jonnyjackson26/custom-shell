import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

public class ListFiles {
    public void listFiles(String dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path entry : stream) {
                BasicFileAttributes attr = Files.readAttributes(entry, BasicFileAttributes.class);
                String permissions = (Files.isDirectory(entry) ? "d" : "-") +
                        (Files.isReadable(entry) ? "r" : "-") +
                        (Files.isWritable(entry) ? "w" : "-") +
                        (Files.isExecutable(entry) ? "x" : "-");
                System.out.printf("%s %10d %s %s%n",
                        permissions,
                        attr.size(),
                        new SimpleDateFormat("MMM dd HH:mm").format(attr.lastModifiedTime().toMillis()),
                        entry.getFileName());
            }
        } catch (IOException e) {
            System.out.println("Error listing files: " + e.getMessage());
        }
    }
}
