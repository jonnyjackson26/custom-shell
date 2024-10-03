import java.io.*;
public class ExecuteCommand {

    //tasklist | findstr chrome is a good test for this
    //this runs an executed command like ipconfig, tasklist, ping, systeminfo. it handles piping.
    //it returns a double representing execution time and this will be accumulated to totalExecutionTime in my main file
    //note: i could have only had the executionTime be handled within this 'executeCommand' function but then it would not be as accurate, so I define and return it in both of the helper functions
    public static double executeCommand(String[] commands) {
        // Check if the command contains a pipe ("|")
        int pipeIndex = -1;
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals("|")) {
                pipeIndex = i;
                break;
            }
        }

        if (pipeIndex != -1) {
            // Split the command into two parts: before and after the pipe
            String[] firstCommand = new String[pipeIndex];
            String[] secondCommand = new String[commands.length - pipeIndex - 1];
            System.arraycopy(commands, 0, firstCommand, 0, pipeIndex);
            System.arraycopy(commands, pipeIndex + 1, secondCommand, 0, secondCommand.length);

            return runPipedCommands(firstCommand, secondCommand);
        } else {
            // Regular execution
            return runCommand(commands);
        }
    }

    // Helper method to execute two piped commands
    public static double runPipedCommands(String[] firstCommand, String[] secondCommand) {
        double executionTime = 0;
        try {
            long startTime = System.nanoTime();

            // First command process
            ProcessBuilder firstBuilder = new ProcessBuilder(firstCommand);
            Process firstProcess = firstBuilder.start();

            // Capture the output of the first command
            BufferedReader firstReader = new BufferedReader(new InputStreamReader(firstProcess.getInputStream()));

            // Second command process
            ProcessBuilder secondBuilder = new ProcessBuilder(secondCommand);
            Process secondProcess = secondBuilder.start();

            // Feed the output of the first command into the second command's input
            BufferedWriter secondWriter = new BufferedWriter(new OutputStreamWriter(secondProcess.getOutputStream()));

            String line;
            while ((line = firstReader.readLine()) != null) {
                secondWriter.write(line);
                secondWriter.newLine();
            }
            secondWriter.flush();
            secondWriter.close();

            // Read the output of the second command
            BufferedReader secondReader = new BufferedReader(new InputStreamReader(secondProcess.getInputStream()));
            while ((line = secondReader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the processes to finish
            firstProcess.waitFor();
            secondProcess.waitFor();

            long endTime = System.nanoTime();
            executionTime = (endTime - startTime) / 1_000_000_000.0;

        } catch (IOException | InterruptedException e) {
            System.out.println("Error: Could not execute piped commands - " + e.getMessage());
        }
        return executionTime;
    }

    // Helper method to run a single command
    public static double runCommand(String[] commands) {
        double executionTime=0;
        long startTime = System.nanoTime();

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();

            long endTime = System.nanoTime();
            executionTime = (endTime - startTime) / 1_000_000_000.0;

            if (exitCode != 0) {
                System.out.println("Error: Command exited with code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: Command not found or failed to execute.");
        }
        return executionTime;
    }

}
