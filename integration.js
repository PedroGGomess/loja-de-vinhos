/**
 * integration.js — Funções de integração com o servidor Java.
 * Delega para as funções implementadas em app.js.
 */

/** Carrega a lista de vinhos e renderiza os cartões de produto. */
function loadProducts(tipo) {
    loadVinhos(tipo || '');
}

/** Gere a adição/remoção de produtos no carrinho. */
function manageCart() {
    renderCart();
}

/** Carrega os dados do painel do gerente. */
function loadDashboardData() {
    loadDashboard();
}

/** Carrega a equipa de funcionários. */
function manageTeam() {
    loadEquipa();
}

/** Carrega os dados de stock. */
function manageStock() {
    loadStock();
}

