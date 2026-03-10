# 🍷 Loja de Vinhos — Java OOP

Projeto universitário de uma loja de vinhos implementado em **Java puro (OOP)** com interface de terminal.

---

## Estrutura do Projeto

```
src/
├── main/
│   ├── model/          → Entidades do domínio (Vinho, Cliente, Funcionario, Gerente, Venda, ...)
│   ├── service/        → Lógica de negócio (LojaService, StockService, VendaService, ...)
│   ├── repository/     → Persistência em ficheiros .dat (serialização Java)
│   ├── ui/             → Menus interativos no terminal
│   └── Main.java       → Ponto de entrada da aplicação
└── test/               → Testes unitários (opcional)
```

---

## Como Compilar e Executar

### Opção 1 — Script automático (Linux/macOS)
```bash
chmod +x compile.sh
./compile.sh
```

### Opção 2 — Manual
```bash
# Compilar
mkdir -p out
javac -d out -sourcepath src $(find src -name "*.java")

# Executar
java -cp out main.Main
```

**Requisito:** Java 17 ou superior.

---

## Credenciais de Acesso (Dados de Exemplo)

| Perfil      | Email               | Password     |
|-------------|---------------------|--------------|
| Gerente     | admin@loja.pt       | admin123     |
| Funcionário | joao@loja.pt        | func123      |
| Cliente     | cliente@email.pt    | cliente123   |

---

## Funcionalidades

### Cliente
- Ver catálogo completo de vinhos
- Filtrar por tipo, região, preço, nome
- Pesquisa avançada por múltiplos critérios
- Ordenar vinhos (preço, nome, ano, região)
- Adicionar ao carrinho (desconto 10% para ≥ 6 garrafas)
- Finalizar compra com método de pagamento
- Ver histórico de compras
- Avaliar vinhos comprados (1-5 ⭐)

### Gerente / Funcionário
- **Stock:** listar, adicionar, editar, remover vinhos; alertas de stock crítico
- **Equipa:** gerir funcionários, ver massa salarial
- **Relatórios:** por período, top produtos, faturação mensal, exportar .txt
- **Vendas:** listar, consultar, cancelar vendas

---

## Persistência

Os dados são guardados em ficheiros `.dat` (serialização Java) na pasta onde a aplicação é executada:
- `vinhos.dat` — catálogo de vinhos
- `clientes.dat` — clientes registados
- `vendas.dat` — histórico de vendas
- `funcionarios.dat` — equipa
- `log.txt` — registo de todas as vendas com timestamp
