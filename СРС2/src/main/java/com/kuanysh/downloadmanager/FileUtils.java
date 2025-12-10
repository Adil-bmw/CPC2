package com.kuanysh.downloadmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Вспомогательные методы работы с файлами.
 */
public class FileUtils {

    public static void mergeChunks(String outputFile, int numberOfChunks) throws IOException {
        System.out.println("Сборка файла из частей...");
        try (FileOutputStream output = new FileOutputStream(outputFile)) {
            for (int i = 0; i < numberOfChunks; i++) {
                File part = new File(outputFile + ".part" + i);
                if (!part.exists()) {
                    System.out.println("Предупреждение: chunk-файл не найден: " + part.getName());
                    continue;
                }
                try (FileInputStream input = new FileInputStream(part)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                // удаляем временный файл
                part.delete();
            }
        }
        System.out.println("Сборка завершена: " + outputFile);
    }
}
