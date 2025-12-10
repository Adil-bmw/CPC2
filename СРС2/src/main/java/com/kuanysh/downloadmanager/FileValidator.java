package com.kuanysh.downloadmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Проверка размера файла и вычисление SHA-512.
 */
public class FileValidator {

    public static void validate(String fileName, long expectedSize) throws Exception {
        File file = new File(fileName);
        long actualSize = file.length();

        System.out.println("Проверка размера файла...");
        System.out.println("Ожидаемый размер: " + expectedSize + " байт");
        System.out.println("Фактический размер: " + actualSize + " байт");
        System.out.println("Результат: " + (expectedSize == actualSize ? "OK" : "FAILED"));

        System.out.println("Вычисление SHA-512 хеша...");
        MessageDigest digest = MessageDigest.getInstance("SHA-512");

        try (InputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[512]; 
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hash = digest.digest();
        System.out.println("SHA-512: " + bytesToHex(hash));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
