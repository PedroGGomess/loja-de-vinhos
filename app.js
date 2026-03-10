/**
 * app.js — Interface web da Loja de Vinhos
 *
 * Liga-se ao servidor Java (porta 8080) via REST API para carregar
 * dados reais de vinhos, vendas, stock e equipa.
 */

const API = '';  // mesmo origem — o servidor Java serve estes ficheiros

// ─── Emojis por tipo de vinho ─────────────────────────────────────────────
const TIPO_EMOJI = {
    'Tinto':     '🍷',
    'Branco':    '🥂',
    'Rosé':      '🌸',
    'Espumante': '🍾',
    'Porto':     '🍷'
};

// ─── Carrinho (gerido no cliente) ─────────────────────────────────────────
let carrinho = [];

// ══════════════════════════════════════════════════════════════════════════
//  LOJA — loja.html
// ══════════════════════════════════════════════════════════════════════════

/**
 * Carrega a lista de vinhos da API e renderiza os cartões de produto.
 * @param {string} tipo  Tipo de vinho para filtrar (vazio = todos)
 */
async function loadVinhos(tipo = '') {
    const url = tipo ? `${API}/api/vinhos?tipo=${encodeURIComponent(tipo)}` : `${API}/api/vinhos`;
    const grid = document.getElementById('products-grid');
    if (!grid) return;

    grid.innerHTML = '<p style="padding:20px;color:#888;">A carregar vinhos…</p>';

    try {
        const res  = await fetch(url);
        const data = await res.json();

        if (data.length === 0) {
            grid.innerHTML = '<p style="padding:20px;color:#888;">Sem vinhos disponíveis.</p>';
            return;
        }

        grid.innerHTML = data.map(v => `
            <div class="product-card" data-id="${v.id}" data-preco="${v.preco}"
                 data-nome="${escHtml(v.nome)}" data-tipo="${escHtml(v.tipo)}">
                <div class="product-image">${TIPO_EMOJI[v.tipo] || '🍷'}</div>
                <h3>${escHtml(v.nome)}</h3>
                <p style="font-size:12px;color:#888;margin:5px 0;">
                    ${escHtml(v.tipo)} • ${escHtml(v.regiao)} • ${v.anoColheita}
                </p>
                <p style="font-size:11px;color:#aaa;margin:3px 0;">
                    Stock: ${v.quantidadeStock} un.
                    ${v.quantidadeStock <= 5 ? '<span style="color:#c62828;">⚠ Baixo</span>' : ''}
                </p>
                <div class="product-price">€${v.preco.toFixed(2)}</div>
                <button class="btn-add"
                    onclick="addToCart(${v.id}, '${escJs(v.nome)}', ${v.preco}, ${v.quantidadeStock})"
                    ${v.quantidadeStock === 0 ? 'disabled' : ''}>
                    ${v.quantidadeStock === 0 ? 'Sem stock' : 'Adicionar'}
                </button>
            </div>`).join('');
    } catch (err) {
        grid.innerHTML = `<p style="padding:20px;color:#c62828;">Erro ao carregar vinhos: ${err.message}</p>`;
    }
}

/** Adiciona um item ao carrinho e actualiza o painel. */
function addToCart(id, nome, preco, stock) {
    const existing = carrinho.find(i => i.id === id);
    if (existing) {
        if (existing.qty >= stock) {
            alert(`Stock insuficiente. Máximo: ${stock}`);
            return;
        }
        existing.qty++;
    } else {
        carrinho.push({ id, nome, preco, stock, qty: 1 });
    }
    renderCart();
}

/** Remove um item do carrinho. */
function removeFromCart(id) {
    carrinho = carrinho.filter(i => i.id !== id);
    renderCart();
}

/** Limpa o carrinho. */
function clearCart() {
    carrinho = [];
    renderCart();
}

/** Renderiza o painel do carrinho. */
function renderCart() {
    const itemsEl = document.getElementById('cart-items');
    const countEl = document.getElementById('cart-count');
    const totalEl = document.getElementById('cart-total');
    if (!itemsEl) return;

    const totalQty  = carrinho.reduce((s, i) => s + i.qty, 0);
    const subtotal  = carrinho.reduce((s, i) => {
        // Desconto de 10% para compras de 6 ou mais garrafas do mesmo artigo
        const t = i.preco * i.qty;
        return s + (i.qty >= 6 ? t * 0.90 : t);
    }, 0);

    if (countEl) countEl.textContent = `${totalQty} ${totalQty === 1 ? 'item' : 'itens'}`;
    if (totalEl) totalEl.textContent = `€${subtotal.toFixed(2)}`;

    if (carrinho.length === 0) {
        itemsEl.innerHTML = '<p style="padding:20px;color:#aaa;text-align:center;">Carrinho vazio</p>';
        return;
    }

    itemsEl.innerHTML = carrinho.map(i => {
        const t = i.preco * i.qty;
        const desc = i.qty >= 6;
        const sub  = desc ? t * 0.90 : t;
        return `
        <div style="background:white;border:2px solid var(--tan);padding:12px;border-radius:8px;margin-bottom:12px;">
            <div style="display:flex;justify-content:space-between;align-items:start;">
                <div style="flex:1;">
                    <strong style="color:var(--wine-dark);display:block;margin-bottom:4px;">
                        ${escHtml(i.nome)}
                    </strong>
                    <div style="font-size:13px;color:#666;margin-top:6px;">
                        ${i.qty} × €${i.preco.toFixed(2)}
                        ${desc ? ' <span style="color:green;font-size:11px;">(-10%)</span>' : ''}
                    </div>
                    <button onclick="removeFromCart(${i.id})"
                        style="margin-top:6px;padding:2px 8px;font-size:11px;cursor:pointer;
                               background:white;border:1px solid #ccc;border-radius:4px;">
                        Remover
                    </button>
                </div>
                <strong style="color:var(--wine-primary);font-size:18px;
                               font-family:'Playfair Display',serif;">
                    €${sub.toFixed(2)}
                </strong>
            </div>
        </div>`;
    }).join('');
}

/** Filtra vinhos por categoria ao clicar nos botões. */
function setupCategoryButtons() {
    document.querySelectorAll('.category-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.category-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const tipo = btn.dataset.tipo || '';
            loadVinhos(tipo);
        });
    });
}

// ══════════════════════════════════════════════════════════════════════════
//  STOCK — stock.html
// ══════════════════════════════════════════════════════════════════════════

/** Carrega estatísticas de stock e preenche os KPIs e a tabela. */
async function loadStock() {
    await loadStockStats();
    await loadStockTable();
}

async function loadStockStats() {
    try {
        const res  = await fetch(`${API}/api/stock/stats`);
        const data = await res.json();

        setText('kpi-total-garrafas', data.totalGarrafas.toLocaleString('pt-PT'));
        setText('kpi-stock-critico',  data.stockCritico);
        setText('kpi-valor-stock',    `€${(data.valorStock / 1000).toFixed(0)}k`);
    } catch (err) {
        console.error('Erro ao carregar stats de stock:', err);
    }
}

async function loadStockTable() {
    const tbody = document.getElementById('stock-tbody');
    if (!tbody) return;

    try {
        const res    = await fetch(`${API}/api/vinhos`);
        const vinhos = await res.json();

        tbody.innerHTML = vinhos.map(v => {
            const statusCls  = v.quantidadeStock <= 5 ? 'status-warning' : 'status-success';
            const statusText = v.quantidadeStock <= 5 ? '⚠ Baixo' : '✓ Normal';
            const stockColor = v.quantidadeStock <= 5 ? '#f57c00' : '#2e7d32';
            return `
            <tr>
                <td><code>#${v.id}</code></td>
                <td><strong>${escHtml(v.nome)}</strong></td>
                <td>${escHtml(v.tipo)}</td>
                <td class="price-col">€${v.preco.toFixed(2)}</td>
                <td><strong style="color:${stockColor};">${v.quantidadeStock} un.</strong></td>
                <td>${escHtml(v.regiao)}</td>
                <td><span class="${statusCls}">${statusText}</span></td>
            </tr>`;
        }).join('');
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" style="color:#c62828;">Erro: ${err.message}</td></tr>`;
    }
}

// ══════════════════════════════════════════════════════════════════════════
//  DASHBOARD DO GERENTE — gerente.html
// ══════════════════════════════════════════════════════════════════════════

async function loadDashboard() {
    try {
        const res  = await fetch(`${API}/api/dashboard`);
        const data = await res.json();

        setText('kpi-faturacao',    `€${data.faturacaoTotal.toLocaleString('pt-PT', {minimumFractionDigits:2})}` );
        setText('kpi-garrafas',     data.totalGarrafas.toLocaleString('pt-PT'));
        setText('kpi-stock-critico', data.stockCritico);
    } catch (err) {
        console.error('Erro ao carregar dashboard:', err);
    }

    await loadVendasRecentes();
}

async function loadVendasRecentes() {
    const tbody = document.getElementById('vendas-recentes-tbody');
    if (!tbody) return;

    try {
        const res    = await fetch(`${API}/api/vendas`);
        const vendas = await res.json();
        // Mostra as últimas 5 vendas
        const recentes = vendas.slice(-5).reverse();

        tbody.innerHTML = recentes.map(v => {
            const produtos = v.itens.map(i => `${i.quantidade}× ${escHtml(i.nome)}`).join(', ') || '—';
            const stCls  = v.status === 'CONCLUIDA' ? 'status-success' : 'status-pending';
            const stTxt  = v.status === 'CONCLUIDA' ? '✓ Concluído'   : '⏳ Pendente';
            return `
            <tr>
                <td><code>#VD-${v.id}</code></td>
                <td><strong>${escHtml(v.clienteNome)}</strong></td>
                <td>${produtos}</td>
                <td class="price-col">€${v.total.toFixed(2)}</td>
                <td>${escHtml(v.metodoPagamento)}</td>
                <td><span class="${stCls}">${stTxt}</span></td>
            </tr>`;
        }).join('');

        if (recentes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="color:#aaa;">Sem vendas registadas.</td></tr>';
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="6" style="color:#c62828;">Erro: ${err.message}</td></tr>`;
    }
}

// ══════════════════════════════════════════════════════════════════════════
//  VENDAS — gerente-vendas.html
// ══════════════════════════════════════════════════════════════════════════

async function loadVendas() {
    await loadVendasStats();
    await loadVendasTable();
}

async function loadVendasStats() {
    try {
        const res  = await fetch(`${API}/api/dashboard`);
        const data = await res.json();
        setText('kpi-total-vendas',   `€${data.faturacaoTotal.toLocaleString('pt-PT', {minimumFractionDigits:2})}`);
        setText('kpi-num-transacoes', data.totalVendas);
    } catch (err) {
        console.error('Erro ao carregar stats de vendas:', err);
    }
}

async function loadVendasTable() {
    const tbody = document.getElementById('vendas-tbody');
    if (!tbody) return;

    try {
        const res    = await fetch(`${API}/api/vendas`);
        const vendas = await res.json();
        const sorted = [...vendas].reverse();

        tbody.innerHTML = sorted.map(v => {
            const produtos  = v.itens.map(i => escHtml(i.nome)).join(', ') || '—';
            const qtdTotal  = v.itens.reduce((s, i) => s + i.quantidade, 0);
            const stCls     = v.status === 'CONCLUIDA' ? 'status-success'
                            : v.status === 'CANCELADA' ? 'status-warning'
                            : 'status-pending';
            const stTxt     = v.status === 'CONCLUIDA' ? '✓ Concluído'
                            : v.status === 'CANCELADA' ? '✗ Cancelado'
                            : '⏳ Pendente';
            return `
            <tr>
                <td><code>#VD-${v.id}</code></td>
                <td>${v.data}</td>
                <td><strong>${escHtml(v.clienteNome)}</strong></td>
                <td>${produtos}</td>
                <td>${qtdTotal}</td>
                <td class="price-col">€${v.total.toFixed(2)}</td>
                <td>${escHtml(v.metodoPagamento)}</td>
                <td><span class="${stCls}">${stTxt}</span></td>
            </tr>`;
        }).join('');

        if (sorted.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="color:#aaa;">Sem vendas registadas.</td></tr>';
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="8" style="color:#c62828;">Erro: ${err.message}</td></tr>`;
    }
}

// ══════════════════════════════════════════════════════════════════════════
//  EQUIPA — gerente-equipa.html
// ══════════════════════════════════════════════════════════════════════════

async function loadEquipa() {
    const tbody = document.getElementById('equipa-tbody');
    if (!tbody) return;

    try {
        const res    = await fetch(`${API}/api/funcionarios`);
        const equipa = await res.json();

        tbody.innerHTML = equipa.map(f => `
            <tr>
                <td><code>#EMP-${String(f.id).padStart(3,'0')}</code></td>
                <td><strong>${escHtml(f.nome)}</strong></td>
                <td>${escHtml(f.cargo)}</td>
                <td>${escHtml(f.email)}</td>
                <td>—</td>
                <td class="price-col">€${f.salario.toLocaleString('pt-PT', {minimumFractionDigits:0})}</td>
                <td><span class="status-success">✓ Ativo</span></td>
            </tr>`).join('');

        if (equipa.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="color:#aaa;">Sem funcionários registados.</td></tr>';
        }
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" style="color:#c62828;">Erro: ${err.message}</td></tr>`;
    }
}

// ══════════════════════════════════════════════════════════════════════════
//  UTILITÁRIOS
// ══════════════════════════════════════════════════════════════════════════

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) el.textContent = value;
}

function escHtml(str) {
    if (str == null) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function escJs(str) {
    if (str == null) return '';
    // Escape backslashes first, then single quotes
    return String(str).replace(/\\/g, '\\\\').replace(/'/g, "\\'");
}

// ══════════════════════════════════════════════════════════════════════════
//  INICIALIZAÇÃO AUTOMÁTICA (baseada na página actual)
// ══════════════════════════════════════════════════════════════════════════

document.addEventListener('DOMContentLoaded', () => {
    const page = window.location.pathname.split('/').pop();

    if (page === 'loja.html' || page === '') {
        loadVinhos();
        setupCategoryButtons();
        renderCart();
    } else if (page === 'stock.html') {
        loadStock();
    } else if (page === 'gerente.html') {
        loadDashboard();
    } else if (page === 'gerente-vendas.html') {
        loadVendas();
    } else if (page === 'gerente-equipa.html') {
        loadEquipa();
    }
});

