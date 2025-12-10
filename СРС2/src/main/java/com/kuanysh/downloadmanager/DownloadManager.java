package com.kuanysh.downloadmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Менеджер очереди файлов.
 * Ограничивает количество одновременно загружаемых файлов.
 */
public class DownloadManager {

    private final ExecutorService fileExecutor;
    private final List<FileDownloader> tasks = new ArrayList<>();

    public DownloadManager(int maxParallelFiles) {
        this.fileExecutor = Executors.newFixedThreadPool(maxParallelFiles);
    }

    public void addDownload(String url, String outputFile) {
        tasks.add(new FileDownloader(url, outputFile));
    }

    public void start() {
        System.out.println("Старт менеджера загрузок. Всего файлов: " + tasks.size());
        for (FileDownloader task : tasks) {
            fileExecutor.submit(task);
        }
        fileExecutor.shutdown();
    }
}
