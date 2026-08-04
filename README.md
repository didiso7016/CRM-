# 客戶與報價管理系統(單人地端版)

工廠產品生產與出貨用的輕量級客戶與報價管理系統。單人使用、地端執行、僅監聽本機 `127.0.0.1`,不對外開放。

> 專案裡每個檔案的用途、什麼時候用、能不能刪 → 見 **[FILES.md](FILES.md)**

## 技術

- Java 17 / Spring Boot 3 / Thymeleaf / Bootstrap 5
- SQLite(單一檔案資料庫)/ Maven
- 伺服器端渲染(非前後端分離),分層架構 Controller / Service / Repository / Entity

## 功能

- 客戶資料與聯絡人管理(軟刪除/停用、主要聯絡人、搜尋)
- 產品品項主檔(料號搜尋、停用)
- 報價單:多品項、BigDecimal 金額計算、複製、建立新版、狀態管理
- A4 列印 / 另存 PDF(瀏覽器列印)
- 首頁 Dashboard、公司設定、資料庫備份
- 客戶關懷提醒:超過設定天數未聯絡的客戶會出現在首頁

## 開發環境執行

需安裝 JDK 17(Maven 用內附的 `mvnw` 即可,無需另裝):

```bat
mvnw.cmd spring-boot:run
```

瀏覽器開啟 http://localhost:8080

## 建立免安裝可攜版(給沒有 Java 的電腦)

```bat
build-portable.bat
```

會在 `dist\` 產生 `crm.jar` + 精簡 `jre\` + 啟動腳本。把整個 `dist\` 資料夾複製到任何 Windows 電腦,雙擊 `start-crm.bat` 即可執行,**對方免安裝 Java**。

## 資料與備份

- 資料庫:`data/crm.db`(不進版控)
- 備份:`backup/`(執行 `backup-crm.bat` 或設定頁「立即備份」,自動保留最近 30 份)
- 還原:見 `restore-guide.txt`

## 重要:不會上傳到 GitHub 的內容

`.gitignore` 已排除以下項目,**客戶資料永遠只留在本機**:

- `data/`、`backup/`、`*.db`(資料庫與備份)
- `uploads/`(公司 Logo)
- `target/`、`dist/`、`jre/`(建置產生物)

## 測試

```bat
mvnw.cmd test
```

涵蓋金額計算、報價單號產生、版本建立、客戶編號重複、有效期限與品項驗證等商業邏輯。
