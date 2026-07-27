// ===== 報價單表單:動態品項 + 帶入零件 + 即時金額試算 =====
(function () {
    const data = window.QUOTATION_DATA || {items: [], products: [], contacts: [], units: []};
    const products = data.products || [];
    const contacts = data.contacts || [];
    const units = data.units || [];

    let rowIndex = 0; // 持續遞增的索引,刪除列後不重用(後端會忽略空列)

    const body = document.getElementById('itemsBody');
    const customerSelect = document.getElementById('customerSelect');
    const contactSelect = document.getElementById('contactSelect');

    // 建立 HTML 逸出
    function esc(v) {
        if (v === null || v === undefined) return '';
        return String(v).replace(/&/g, '&amp;').replace(/</g, '&lt;')
            .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    }

    // 千分位顯示
    function fmt(n) {
        const num = Number(n) || 0;
        return num.toLocaleString('zh-TW', {minimumFractionDigits: 2, maximumFractionDigits: 2});
    }

    // 零件下拉選項(供每列「帶入零件」)
    const productOptionsHtml = '<option value="">— 選擇 —</option>' +
        products.map(p => `<option value="${p.id}">${esc(p.internalPartNumber)} ${esc(p.name || '')}</option>`).join('');

    // 單位建議清單
    const unitDatalistId = 'unitListQt';
    (function ensureUnitDatalist() {
        if (document.getElementById(unitDatalistId)) return;
        const dl = document.createElement('datalist');
        dl.id = unitDatalistId;
        dl.innerHTML = units.map(u => `<option value="${esc(u)}"></option>`).join('');
        document.body.appendChild(dl);
    })();

    // 建立一列品項
    function addRow(item) {
        const i = rowIndex++;
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="text-center seq"></td>
            <td><select class="form-select form-select-sm product-picker">${productOptionsHtml}</select></td>
            <td><input class="form-control form-control-sm" name="items[${i}].internalPartNumber"></td>
            <td><input class="form-control form-control-sm" name="items[${i}].customerPartNumber"></td>
            <td><input class="form-control form-control-sm" name="items[${i}].productName"></td>
            <td><input class="form-control form-control-sm" name="items[${i}].specification"></td>
            <td><input class="form-control form-control-sm" name="items[${i}].material"></td>
            <td><input class="form-control form-control-sm" name="items[${i}].surfaceTreatment"></td>
            <td><input type="number" step="0.0001" min="0" class="form-control form-control-sm text-end qty" name="items[${i}].quantity"></td>
            <td><input class="form-control form-control-sm" list="${unitDatalistId}" name="items[${i}].unit"></td>
            <td><input type="number" step="0.0001" min="0" class="form-control form-control-sm text-end price" name="items[${i}].unitPrice"></td>
            <td><input type="number" step="0.01" min="0" max="100" class="form-control form-control-sm text-end disc" name="items[${i}].discountRate"></td>
            <td class="text-end amount amount-cell">0.00</td>
            <td><input class="form-control form-control-sm" name="items[${i}].leadTime"></td>
            <td><input class="form-control form-control-sm" name="items[${i}].notes"></td>
            <td class="text-center"><button type="button" class="btn btn-sm btn-outline-danger del-btn"><i data-lucide="trash-2"></i></button></td>
        `;
        // 隱藏的 productId
        const hidden = document.createElement('input');
        hidden.type = 'hidden';
        hidden.name = `items[${i}].productId`;
        hidden.className = 'product-id';
        tr.appendChild(hidden);

        body.appendChild(tr);

        // 回填既有值
        if (item) {
            setVal(tr, 'internalPartNumber', item.internalPartNumber);
            setVal(tr, 'customerPartNumber', item.customerPartNumber);
            setVal(tr, 'productName', item.productName);
            setVal(tr, 'specification', item.specification);
            setVal(tr, 'material', item.material);
            setVal(tr, 'surfaceTreatment', item.surfaceTreatment);
            setVal(tr, 'quantity', item.quantity);
            setVal(tr, 'unit', item.unit || 'PCS');
            setVal(tr, 'unitPrice', item.unitPrice);
            setVal(tr, 'discountRate', item.discountRate != null ? item.discountRate : 0);
            setVal(tr, 'leadTime', item.leadTime);
            setVal(tr, 'notes', item.notes);
            hidden.value = item.productId != null ? item.productId : '';
        } else {
            setVal(tr, 'unit', 'PCS');
            setVal(tr, 'discountRate', 0);
        }

        // 事件:帶入零件
        tr.querySelector('.product-picker').addEventListener('change', function () {
            const p = products.find(x => String(x.id) === this.value);
            if (!p) return;
            hidden.value = p.id;
            setVal(tr, 'internalPartNumber', p.internalPartNumber);
            setVal(tr, 'customerPartNumber', p.customerPartNumber);
            setVal(tr, 'productName', p.name);
            setVal(tr, 'specification', p.specification);
            setVal(tr, 'material', p.material);
            setVal(tr, 'surfaceTreatment', p.surfaceTreatment);
            setVal(tr, 'unit', p.unit || 'PCS');
            setVal(tr, 'unitPrice', p.unitPrice);
            if (p.leadTime) setVal(tr, 'leadTime', p.leadTime);
            recalcRow(tr);
            recalcTotals();
        });

        // 事件:數量/單價/折扣變更 → 重算
        tr.querySelectorAll('.qty, .price, .disc').forEach(inp => {
            inp.addEventListener('input', function () {
                recalcRow(tr);
                recalcTotals();
            });
        });

        // 事件:刪除
        tr.querySelector('.del-btn').addEventListener('click', function () {
            tr.remove();
            renumber();
            recalcTotals();
        });

        recalcRow(tr);
        renumber();
        if (window.lucide) { lucide.createIcons(); } // 讓新列的圖示以 Lucide 渲染
    }

    function setVal(tr, name, value) {
        const el = tr.querySelector(`[name$=".${name}"]`);
        if (el) el.value = (value === null || value === undefined) ? '' : value;
    }

    // 品項金額 = 數量 × 單價 ×(1 - 折扣/100)
    function recalcRow(tr) {
        const qty = parseFloat(tr.querySelector('.qty').value) || 0;
        const price = parseFloat(tr.querySelector('.price').value) || 0;
        const disc = parseFloat(tr.querySelector('.disc').value) || 0;
        const amount = qty * price * (1 - disc / 100);
        tr.querySelector('.amount-cell').textContent = fmt(amount);
        tr._amount = amount;
    }

    // 報價單總計
    function recalcTotals() {
        let subtotal = 0;
        body.querySelectorAll('tr').forEach(tr => {
            subtotal += tr._amount || 0;
        });
        const discount = parseFloat(document.getElementById('overallDiscount').value) || 0;
        const freight = parseFloat(document.getElementById('freight').value) || 0;
        const otherFee = parseFloat(document.getElementById('otherFee').value) || 0;
        const taxRate = parseFloat(document.getElementById('taxRate').value) || 0;

        const taxable = subtotal - discount + freight + otherFee;
        const taxAmount = taxable * taxRate / 100;
        const total = taxable + taxAmount;

        document.getElementById('subtotalCell').textContent = fmt(subtotal);
        document.getElementById('taxCell').textContent = fmt(taxAmount);
        document.getElementById('totalCell').textContent = fmt(total);
    }

    // 重新編號項次
    function renumber() {
        let n = 1;
        body.querySelectorAll('tr').forEach(tr => {
            tr.querySelector('.seq').textContent = n++;
        });
    }

    // 依客戶篩選聯絡人下拉
    function refreshContacts(preselectId) {
        const cid = customerSelect ? customerSelect.value : '';
        const filtered = contacts.filter(c => String(c.customerId) === String(cid));
        contactSelect.innerHTML = '<option value="">(未指定)</option>' +
            filtered.map(c => `<option value="${c.id}">${esc(c.name)}</option>`).join('');
        if (preselectId) {
            contactSelect.value = preselectId;
        } else {
            // 未指定時,預設帶入該客戶的主要聯絡人
            const primary = filtered.find(c => c.primary);
            if (primary) contactSelect.value = primary.id;
        }
    }

    // ===== 初始化 =====
    if (customerSelect) {
        customerSelect.addEventListener('change', function () {
            refreshContacts(null);
        });
        refreshContacts(data.selectedContactId);
    }

    document.getElementById('addRowBtn').addEventListener('click', function () {
        addRow(null);
    });

    ['overallDiscount', 'freight', 'otherFee', 'taxRate'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.addEventListener('input', recalcTotals);
    });

    // 載入既有品項(編輯 / 驗證失敗回填);沒有則給一列空白
    const initItems = (data.items || []).filter(x => x);
    if (initItems.length === 0) {
        addRow(null);
    } else {
        initItems.forEach(it => addRow(it));
    }
    recalcTotals();
})();
