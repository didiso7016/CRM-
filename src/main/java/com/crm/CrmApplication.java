package com.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 系統進入點。
 * 在啟動 Spring 之前先確保 data 與 backup 資料夾存在,
 * 因為 SQLite 只會建立資料庫檔案,不會自動建立上層資料夾。
 */
@SpringBootApplication
public class CrmApplication {

    public static void main(String[] args) {
        ensureDir("data");    // 資料庫存放處
        ensureDir("backup");  // 備份存放處
        ensureDir("uploads"); // 公司 Logo 等上傳檔存放處
        SpringApplication.run(CrmApplication.class, args);
    }

    /** 若資料夾不存在則建立 */
    private static void ensureDir(String name) {
        try {
            Path dir = Path.of(name);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException e) {
            throw new IllegalStateException("無法建立資料夾:" + name, e);
        }
    }
}
