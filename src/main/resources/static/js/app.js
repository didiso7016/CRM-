// ===== 全站共用 JavaScript =====

// 深色 / 淺色主題切換(同時切 Bootstrap 主題,並記住選擇)
function toggleTheme() {
    var el = document.documentElement;
    var next = el.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
    el.setAttribute('data-theme', next);
    el.setAttribute('data-bs-theme', next);
    try { localStorage.setItem('crm-theme', next); } catch (e) {}
}

// 千分位金額格式化
function formatMoney(value) {
    const n = Number(value) || 0;
    return n.toLocaleString('zh-TW', { minimumFractionDigits: 0, maximumFractionDigits: 2 });
}

// 浮動通知 toast:數秒後自動淡出消失
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.flash-toast').forEach(function (t) {
        setTimeout(function () {
            t.classList.add('hide');
            setTimeout(function () { t.remove(); }, 320);
        }, 3500);
    });
});

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
