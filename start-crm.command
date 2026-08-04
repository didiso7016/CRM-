#!/bin/bash
# =====================================================
#  CRM 報價管理系統 - macOS 啟動器
#  對應 Windows 版的 start-crm.bat
#  用法:在 Finder 對這個檔案按右鍵 -> 打開
# =====================================================

# 切換到這個腳本所在的資料夾(資料庫會建立在這裡)
cd "$(dirname "$0")" || exit 1

echo "============================================"
echo "  CRM 報價管理系統 - 啟動中"
echo "============================================"
echo

# ---- 1. 找 Java:優先用隨附的 jre,其次找系統的 Java 17 ----
JAVA_CMD=""
if [ -x "./jre/bin/java" ]; then
    JAVA_CMD="./jre/bin/java"
elif JH=$(/usr/libexec/java_home -v 17 2>/dev/null); then
    JAVA_CMD="$JH/bin/java"
elif JH=$(/usr/libexec/java_home 2>/dev/null); then
    JAVA_CMD="$JH/bin/java"
fi

if [ -z "$JAVA_CMD" ] || ! "$JAVA_CMD" -version >/dev/null 2>&1; then
    echo "[錯誤] 這台電腦找不到 Java。"
    echo
    echo "請先安裝 Java 17:"
    echo "  1. 打開 https://adoptium.net/temurin/releases/?version=17"
    echo "  2. Operating System 選 macOS"
    echo "  3. Architecture:"
    echo "       Apple 晶片 (M1/M2/M3/M4) 選 aarch64"
    echo "       Intel 晶片            選 x64"
    echo "     (查晶片:左上角蘋果圖示 -> 關於這台 Mac)"
    echo "  4. 下載 .pkg 檔,雙擊安裝,一路按繼續"
    echo "  5. 裝好後再打開這個檔案一次"
    echo
    read -n 1 -s -r -p "按任意鍵關閉..."
    exit 1
fi

echo "使用的 Java:$("$JAVA_CMD" -version 2>&1 | head -1)"

# ---- 2. 找程式主檔 ----
JAR="./crm.jar"
if [ ! -f "$JAR" ]; then
    JAR=$(ls -1 ./*.jar 2>/dev/null | head -1)
fi
if [ -z "$JAR" ] || [ ! -f "$JAR" ]; then
    echo "[錯誤] 找不到程式檔 crm.jar。"
    echo "       請確認 crm.jar 和這個啟動檔放在同一個資料夾。"
    echo
    read -n 1 -s -r -p "按任意鍵關閉..."
    exit 1
fi

# ---- 3. 背景等待伺服器就緒,好了就自動開瀏覽器 ----
(
    for _ in $(seq 1 60); do
        if curl -s -o /dev/null --max-time 2 http://localhost:8080/ ; then
            open "http://localhost:8080"
            exit 0
        fi
        sleep 1
    done
    echo
    echo "[提醒] 伺服器比較慢還沒起來,請手動打開瀏覽器輸入:http://localhost:8080"
) &

echo
echo "伺服器啟動中,約 5-15 秒後會自動打開瀏覽器..."
echo "網址:http://localhost:8080"
echo
echo "※ 要停止系統:在這個視窗按 Control + C,或直接關掉視窗。"
echo "============================================"
echo

# ---- 4. 啟動(佔用這個視窗,關掉視窗即停止) ----
"$JAVA_CMD" -jar "$JAR"

echo
echo "系統已停止。"
read -n 1 -s -r -p "按任意鍵關閉視窗..."
