package com.ivaaaak.common.util;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;
import java.util.StringJoiner;


public final class FileManager {

    private static String mainFilePath;

    private FileManager() {

    }

    public static void setMainFilePath(String mainFilePath) {
        FileManager.mainFilePath = mainFilePath;
    }

    public static String getMainFilePath() {
        return mainFilePath;
    }

    public static String read(final String filePath) throws IOException {
        StringJoiner fileData = new StringJoiner("\n");

        try (FileReader reader = new FileReader(filePath);
             Scanner scanner = new Scanner(reader)) {
            while (scanner.hasNextLine()) {
                fileData.add(scanner.nextLine());
            }
        }
        return fileData.toString();
    }


    public static void write(String data, final String filePath) throws IOException {
        try (OutputStream os = new FileOutputStream(filePath);
             OutputStreamWriter osw = new OutputStreamWriter(os)) {
            osw.write(data);
            osw.flush();
        }
    }

}
