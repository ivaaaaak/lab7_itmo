package com.ivaaaak.client;


import com.ivaaaak.common.util.FileManager;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Scanner;

public class InputManager {

    /**
     * This class reads user input from keyboard or from file with the script
     */

    private final ArrayDeque<ArrayDeque<String>> allScripts = new ArrayDeque<>();
    private final ArrayDeque<String> filePaths = new ArrayDeque<>();
    private final Scanner scanner =  new Scanner(System.in);


    public String readLine() {
        if (!allScripts.isEmpty()) {
            ArrayDeque<String> currenScript = allScripts.peek();
            if (!currenScript.isEmpty()) {
                return currenScript.pop();
            }
            allScripts.pop();
            filePaths.pop();
            return readLine();
        }
        return scanner.nextLine();
    }

    public void connectToFile(String filePath) {
        if (filePaths.contains(filePath)) {
            System.err.println("The file contains recursion");
        } else {
            try {
                String script = FileManager.read(filePath);
                try (Scanner sc = new Scanner(script)) {
                    ArrayDeque<String> commands = new ArrayDeque<>();
                    while (sc.hasNextLine()) {
                        commands.add(sc.nextLine());
                    }
                    filePaths.push(filePath);
                    allScripts.push(commands);
                }
            } catch (IOException e) {
                System.err.println("Some problems with file has occurred");
                e.printStackTrace();
            }
        }
    }
}
