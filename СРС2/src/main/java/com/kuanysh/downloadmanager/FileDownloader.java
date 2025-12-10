package com.kuanysh.downloadmanager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Загрузчик одного файла: делит файл на части и скачивает их параллельно.
 */
public class FileDownloader implements Runnable {

    private final String url;
    private final String outputFile;

    // Количество частей (можно вынести в параметры)
    private final int numberOfChunks = 4;

    public FileDownloader(String url, String outputFile) {
        this.url = url;
        this.outputFile = outputFile;
    }

    @Override
    public void run() {
        System.out.println("\n=== Загрузка файла: " + url + " ===");
        try {
            // HEAD-запрос для определения размера файла
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            long fileSize = connection.getContentLengthLong();

            if (fileSize <= 0) {
                System.out.println("Не удалось определить размер файла. Выполняется одиночная загрузка без разбиения.");
                // можно было бы реализовать простой одно-поточный вариант
                return;
            }

            System.out.println("Размер файла: " + fileSize + " байт");

            long chunkSize = fileSize / numberOfChunks;
            ExecutorService executor = Executors.newFixedThreadPool(numberOfChunks);
            CountDownLatch latch = new CountDownLatch(numberOfChunks);

            for (int i = 0; i < numberOfChunks; i++) {
                long start = i * chunkSize;
                long end = (i == numberOfChunks - 1) ? (fileSize - 1) : (start + chunkSize - 1);

                executor.submit(new ChunkDownloader(
                        url,
                        outputFile,
                        start,
                        end,
                        i,
                        latch
                ));
            }

            // Ждем завершения всех частей
            latch.await();
            executor.shutdown();

            // Сборка частей в один файл
            FileUtils.mergeChunks(outputFile, numberOfChunks);

            // Валидация
            FileValidator.validate(outputFile, fileSize);

            System.out.println("Загрузка файла " + outputFile + " завершена.\n");
        } catch (Exception e) {
            System.out.println("Ошибка загрузки файла: " + e.getMessage());
        }
    }
}
