package com.crm.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 資料庫備份服務。
 * 將 SQLite 資料庫複製到 backup 資料夾,檔名含日期時間,並僅保留最近 30 份。
 */
@Service
public class BackupService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final int KEEP = 30;

    private final Path dbPath;
    private final Path backupDir = Path.of("backup");

    public BackupService(@Value("${spring.datasource.url}") String datasourceUrl) {
        // 由 jdbc:sqlite:data/crm.db 解析出實際檔案路徑
        String file = datasourceUrl.replaceFirst("^jdbc:sqlite:", "");
        this.dbPath = Path.of(file);
    }

    /** 立即備份,回傳備份檔名 */
    public synchronized String backupNow() {
        try {
            if (!Files.exists(dbPath)) {
                throw new IllegalStateException("找不到資料庫檔案:" + dbPath.toAbsolutePath());
            }
            Files.createDirectories(backupDir);
            String name = "crm-backup-" + LocalDateTime.now().format(TS) + ".db";
            Path target = backupDir.resolve(name);
            // 複製資料庫檔(單人使用、備份時無並發寫入,直接複製即可)
            Files.copy(dbPath, target, StandardCopyOption.REPLACE_EXISTING);
            cleanupOldBackups();
            return name;
        } catch (IOException e) {
            throw new UncheckedIOException("備份失敗", e);
        }
    }

    /** 僅保留最近 KEEP 份,刪除較舊的 */
    private void cleanupOldBackups() throws IOException {
        List<Path> backups = listBackupFiles();
        if (backups.size() > KEEP) {
            for (Path old : backups.subList(KEEP, backups.size())) {
                Files.deleteIfExists(old);
            }
        }
    }

    /** 依時間新到舊列出備份檔 */
    public List<Path> listBackupFiles() {
        List<Path> result = new ArrayList<>();
        if (!Files.exists(backupDir)) {
            return result;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(backupDir, "crm-backup-*.db")) {
            for (Path p : stream) {
                result.add(p);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("讀取備份清單失敗", e);
        }
        result.sort(Comparator.comparing((Path p) -> p.getFileName().toString()).reversed());
        return result;
    }

    /** 最近一次備份的檔名,無則回傳 null */
    public String lastBackupName() {
        List<Path> backups = listBackupFiles();
        return backups.isEmpty() ? null : backups.get(0).getFileName().toString();
    }

    /** 備份資料夾絕對路徑(顯示給使用者) */
    public String backupDirAbsolutePath() {
        return backupDir.toAbsolutePath().toString();
    }
}
