package com.kuanysh.downloadmanager;

public class Main {

    // Пример запуска без аргументов (для скриншота / проверки)
    public static void main(String[] args) {
        // Максимум 2 файла одновременно
        DownloadManager manager = new DownloadManager(2);

        // Если аргументы не переданы — используем тестовые ссылки
        if (args.length == 0) {
            System.out.println("Используются тестовые загрузки.");
            manager.addDownload(
                    "https://dlcdn.apache.org//jmeter/source/apache-jmeter-5.6.3_src.tgz",
                    "file1.txt"
            );

            manager.addDownload(
                    "https://raw.githubusercontent.com/git/git/master/COPYING",
                    "file2.txt"
            );

            manager.addDownload(
                    "https://raw.githubusercontent.com/vim/vim/master/runtime/defaults.vim",
                    "file3.txt"
            );
        } else {
            // Если переданы URL — добавляем все в очередь
            for (int i = 0; i < args.length; i++) {
                String url = args[i];
                String fileName = "download_" + (i + 1);
                manager.addDownload(url, fileName);
            }
        }

        manager.start();
    }
}
