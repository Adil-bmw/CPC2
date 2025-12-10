package com.kuanysh.downloadmanager;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 * Загрузчик одного chunk'а с использованием HTTP Range.
 */
public class ChunkDownloader implements Runnable {

    private final String url;
    private final String outputFile;
    private final long startByte;
    private final long endByte;
    private final int chunkId;
    private final CountDownLatch latch;

    public ChunkDownloader(String url,
                           String outputFile,
                           long startByte,
                           long endByte,
                           int chunkId,
                           CountDownLatch latch) {
        this.url = url;
        this.outputFile = outputFile;
        this.startByte = startByte;
        this.endByte = endByte;
        this.chunkId = chunkId;
        this.latch = latch;
    }

    @Override
    public void run() {
        String partFileName = outputFile + ".part" + chunkId;
        System.out.println("Chunk " + chunkId + " старт: байты " + startByte + "-" + endByte);
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_PARTIAL
                    && responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Chunk " + chunkId + " получил код " + responseCode + ". Создаю пустой файл-часть.");
                // создаем пустую часть, чтобы не падала сборка
                new FileOutputStream(partFileName).close();
                return;
            }

            try (InputStream input = connection.getInputStream();
                 FileOutputStream output = new FileOutputStream(partFileName)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalRead = 0;

                while ((bytesRead = input.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }

                System.out.println("Chunk " + chunkId + " завершен. Скачано: " + totalRead + " байт.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка в chunk " + chunkId + ": " + e.getMessage());
            // создаем пустой файл, чтобы FileUtils.mergeChunks не падал на FileNotFoundException
            try {
                new FileOutputStream(partFileName).close();
            } catch (Exception ignored) {}
        } finally {
            latch.countDown();
        }
    }
}
