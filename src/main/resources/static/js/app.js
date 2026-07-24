// ===== 全站共用 JavaScript =====

// 千分位金額格式化
function formatMoney(value) {
    const n = Number(value) || 0;
    return n.toLocaleString('zh-TW', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
}

// 刪除/停用前二次確認:表單加上 class="confirm-action" 與 data-confirm-message
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.confirm-action').forEach(function (form) {
        form.addEventListener('submit', function (e) {
            const msg = form.getAttribute('data-confirm-message') || '確定要執行此操作嗎?';
            if (!window.confirm(msg)) {
                e.preventDefault();
            }
        });
    });
});
