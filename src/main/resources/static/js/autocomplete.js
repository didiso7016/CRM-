// ===== 搜尋框即時建議(autocomplete)=====
// 用法:在 <input> 加上 data-suggest="customers"
//   data-suggest-action="navigate" → 點選後跳到該客戶頁
//   data-suggest-action="fill"(預設)→ 帶入公司名稱並送出表單(篩選)
(function () {
    const DEBOUNCE_MS = 180;

    document.querySelectorAll('input[data-suggest]').forEach(setup);

    function setup(input) {
        const type = input.getAttribute('data-suggest'); // 目前支援 customers
        const action = input.getAttribute('data-suggest-action') || 'fill';
        const parent = input.parentElement;
        parent.style.position = 'relative';

        const menu = document.createElement('div');
        menu.className = 'ac-menu';
        menu.style.display = 'none';
        parent.appendChild(menu);

        input.setAttribute('autocomplete', 'off');

        let timer = null;
        let items = [];
        let active = -1;

        input.addEventListener('input', function () {
            const q = input.value.trim();
            clearTimeout(timer);
            if (q.length < 1) { hide(); return; }
            timer = setTimeout(function () { fetchSuggest(q); }, DEBOUNCE_MS);
        });

        input.addEventListener('keydown', function (e) {
            if (menu.style.display === 'none') return;
            if (e.key === 'ArrowDown') { e.preventDefault(); move(1); }
            else if (e.key === 'ArrowUp') { e.preventDefault(); move(-1); }
            else if (e.key === 'Enter') { if (active >= 0) { e.preventDefault(); choose(items[active]); } }
            else if (e.key === 'Escape') { hide(); }
        });

        document.addEventListener('click', function (e) {
            if (!parent.contains(e.target)) hide();
        });

        function fetchSuggest(q) {
            fetch('/api/suggest/' + type + '?q=' + encodeURIComponent(q))
                .then(function (r) { return r.ok ? r.json() : []; })
                .then(function (data) { items = data || []; render(); })
                .catch(function () { /* 靜默失敗,不影響手動搜尋 */ });
        }

        function render() {
            if (!items.length) { hide(); return; }
            active = -1;
            menu.innerHTML = items.map(function (it, i) {
                return '<div class="ac-item" data-i="' + i + '">' +
                    '<span class="ac-name">' + esc(it.companyName) + '</span>' +
                    '<span class="ac-code">' + esc(it.customerCode) + '</span></div>';
            }).join('');
            menu.querySelectorAll('.ac-item').forEach(function (el) {
                el.addEventListener('mousedown', function (e) {
                    e.preventDefault();
                    choose(items[parseInt(el.getAttribute('data-i'), 10)]);
                });
            });
            menu.style.display = 'block';
        }

        function move(d) {
            const els = menu.querySelectorAll('.ac-item');
            if (!els.length) return;
            active = (active + d + els.length) % els.length;
            els.forEach(function (el, i) { el.classList.toggle('active', i === active); });
        }

        function choose(it) {
            if (!it) return;
            if (action === 'navigate') { window.location.href = '/customers/' + it.id; return; }
            input.value = it.companyName;
            hide();
            const form = input.closest('form');
            if (form) form.submit();
        }

        function hide() { menu.style.display = 'none'; active = -1; }

        function esc(s) {
            return (s == null ? '' : String(s))
                .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
        }
    }
})();
