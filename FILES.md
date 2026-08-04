# 專案檔案說明

這份文件說明專案根目錄裡每個檔案的用途、什麼時候會用到、以及能不能刪。

一句話總覽:**程式碼全部在 `src\` 裡，其他的 `.bat` 都只是「幫你自動打指令的小抄」，本身沒有任何程式功能。**

---

## 快速查表:我現在想做某件事，該點哪個？

| 我想做的事 | 該做什麼 |
|---|---|
| 改程式、看修改後的效果 | 雙擊 `run-dev.bat`，瀏覽器打 `http://localhost:8080` |
| 做一份免安裝版給 Windows 使用者 | 雙擊 `build-portable.bat`，把產生的 `dist\` 整包給對方 |
| 做一份給 Mac 使用者 | 見下方「交付給 Mac」段落 |
| 備份資料 | 雙擊 `backup-crm.bat`，或在系統「設定」頁按「立即備份」 |
| 從備份還原 | 看 `restore-guide.txt` |
| 在新電腦上繼續開發 | 雙擊 `setup-dev.bat` 裝 Java，關掉視窗，再點 `run-dev.bat` |
| 清掉編譯產物 | 命令列跑 `.\mvnw.cmd clean` |

---

## 一、程式骨架（刪了系統就壞了）

| 檔案 / 資料夾 | 說明 |
|---|---|
| `src\` | **全部的程式碼與畫面**。整個系統就是這個資料夾，其他都是輔助 |
| `pom.xml` | 專案定義檔。寫著用哪個 Java 版本、要下載哪些套件 |
| `mvnw.cmd` | Maven 包裝器（Windows 版）。讓你不必另外安裝 Maven，所有 build 指令都靠它 |
| `mvnw` | 同上的 Mac / Linux 版。在 Windows 用不到，但將來若要在 Mac 上編譯會需要 |
| `.mvn\` | `mvnw` 的設定檔，記錄要抓哪個版本的 Maven |
| `.gitignore` | 指定哪些檔案不上傳 GitHub。**客戶資料靠這個擋住**，別亂改 |
| `.gitattributes` | 強制 `.command` 用 LF 換行。少了它，Mac 啟動器會壞掉並報 `bad interpreter: /bin/bash^M` |

## 二、你的資料（刪了資料就沒了，且不進版控）

| 資料夾 | 說明 |
|---|---|
| `data\` | **資料庫本體**。所有客戶、聯絡人、產品、報價單都在 `data\crm.db` 這一個檔案裡 |
| `backup\` | 備份檔存放處。檔名格式 `crm-backup-日期-時間.db`，自動保留最近 30 份 |
| `uploads\` | 使用者上傳的檔案，目前是公司 Logo |

> 這三個資料夾都被 `.gitignore` 排除，永遠不會上傳到 GitHub。

## 三、自動產生（可以放心刪，下次 build 會再長出來）

| 資料夾 | 說明 |
|---|---|
| `target\` | Maven 編譯產物，`crm-1.0.0.jar` 就是在這裡產生的 |
| `dist\` | `build-portable.bat` 產生的 Windows 免安裝包 |

---

## 四、腳本（.bat）

### `run-dev.bat` — 開發時每天用

從原始碼直接啟動系統，不需要打包。實際執行的是 `mvnw.cmd spring-boot:run`。

改完程式碼 → 重跑這支 → 立刻看到效果。啟動後**要自己開瀏覽器**輸入 `http://localhost:8080`。停止按 `Ctrl + C`。

### `build-portable.bat` — 要交付 Windows 版時用

唯一一支「有在生產東西」的腳本，跑完會產生 `dist\` 資料夾。四個階段：

1. 編譯 + **跑測試** + 打包成 jar（測試沒過就不會產出）
2. 複製成 `dist\crm.jar`
3. 用 `jlink` 挖一份精簡版 Java 放進 `dist\jre\`
4. 複製啟動腳本與說明檔

因為把 Java 一起打包進去了，**對方電腦不用裝任何東西**，整個 `dist\` 複製過去雙擊就能跑。

> 重新打包**不會刪掉 `dist\data\`**，所以改完程式重新 build，使用者的資料不會不見。

### `start-crm.bat` — 給 Windows 使用者的啟動器

啟動打包好的 `crm.jar`，等伺服器就緒後**自動打開瀏覽器**。

⚠️ **這支要放在 `dist\` 裡執行。** 它會找「跟自己同一層的 `crm.jar`」，所以直接點根目錄那份一定會報 `Program jar not found` —— 這是設計如此，不是壞掉。

停止方式：關掉標題為 `CRM Server - do not close` 的視窗。

### `backup-crm.bat` — 備份資料庫

把 `data\crm.db` 複製到 `backup\`，檔名加上日期時間，並刪除超過 30 份的舊備份。跟啟動完全無關。

### `setup-dev.bat` — 換新電腦時跑一次

檢查有沒有 Java 17，沒有就用 `winget` 自動安裝 Eclipse Temurin JDK 17，並設定 `MAVEN_OPTS`（避開公司 SSL 代理問題）。

裝完**要關掉視窗重開**才會吃到新的 PATH。這台電腦裝過一次就不用再跑。

---

## 五、說明文件

| 檔案 | 給誰看 |
|---|---|
| `README.md` | **開發者**（你、未來接手的人）。技術架構、如何執行與打包 |
| `FILES.md` | 就是這份。專案檔案清單 |
| `restore-guide.txt` | **Windows 使用者**。資料庫還原步驟，會被打包進 `dist\` |
| `START-HERE.txt` | **Mac 使用者**。從安裝 Java 到啟動的完整步驟 |

---

## 六、交付給 Mac 使用者

程式碼本身**完全跨平台**（路徑都用相對寫法、沒有呼叫任何 Windows 指令、SQLite 驅動內含 Mac 原生函式庫），所以 Mac 上是原生執行，不是勉強能跑。

不能用的只有 `dist\jre\`（Windows 版 Java）和 `.bat`。因此 **Mac 交付不走 `build-portable.bat`**，改用下面的流程：

### 打包步驟

```bat
.\mvnw.cmd clean package
```

然後手動組出這個資料夾結構：

```
CRM-MAC\
  ├─ crm.jar                 <- 從 target\crm-1.0.0.jar 複製並改名
  ├─ start-crm.command       <- Mac 啟動器
  ├─ START-HERE.txt          <- 使用說明
  ├─ data\crm.db             <- 要附資料才複製
  └─ uploads\                <- 公司 Logo，漏了報價單會破圖
```

改名建議用指令，避免 Windows 隱藏副檔名造成 `crm.jar.jar`：

```powershell
Rename-Item crm-1.0.0.jar crm.jar
```

最後整個資料夾壓縮成 zip。約 60MB，**Email 寄不出去**（Gmail 上限 25MB），要用雲端硬碟。

### 三個踩過的坑

1. **檔名一律用英文。** Windows 壓縮 zip 時中文檔名到 Mac 上會變亂碼，所以說明檔叫 `START-HERE.txt` 而不是中文名。
2. **`.txt` 要有 UTF-8 BOM。** 少了它，Mac 的 TextEdit 有機率把中文判讀成亂碼。
3. **Mac 使用者一定要先跑 `chmod +x` 和 `xattr -c`。** Windows 無法設定 Mac 的執行權限，壓縮也留不住；而從網路下載的腳本會被 macOS 標記隔離。這兩件事 `START-HERE.txt` 第三步有教（用拖曳檔案的方式，不需要會打路徑）。

### `start-crm.command` 做什麼

Mac 版的啟動器，等同 `start-crm.bat`：找 Java（隨附 jre → 系統 Java 17 → 任何版本，三層備援）→ 找 jar → 背景等伺服器就緒後自動開瀏覽器 → 啟動。找不到 Java 時會直接印出安裝步驟。

停止方式：關掉終端機視窗，或按 `Control + C`。

---

## 七、目前已知待辦

- `restore-guide.txt` 只有 Windows 版，用語（`data\crm.db`、`start-crm.bat`）不適用 Mac。正式交付 Mac 使用者前需補一份。
- Mac 交付包裡沒有備份腳本，使用者只能用系統「設定」頁的「立即備份」。
