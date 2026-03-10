/**
 * ApiServer — servidor HTTP que expõe a loja de vinhos via REST API
 * e serve os ficheiros estáticos (HTML/CSS/JS) da interface web.
 *
 * Utiliza a API interna do JDK (com.sun.net.httpserver) sem dependências externas.
 * Porta por omissão: 8080
 */
package main.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.model.*;
import main.repository.FuncionarioRepository;
import main.repository.VendaRepository;
import main.repository.VinhoRepository;
import main.service.EquipaService;
import main.service.LojaService;
import main.service.VendaService;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class ApiServer {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final HttpServer server;
    private final VinhoRepository vinhoRepo;
    private final VendaRepository vendaRepo;
    private final FuncionarioRepository funcionarioRepo;
    private final LojaService lojaService;
    private final VendaService vendaService;
    private final EquipaService equipaService;

    /**
     * Cria e configura o servidor HTTP na porta indicada.
     */
    public ApiServer(int port,
                     VinhoRepository vinhoRepo,
                     VendaRepository vendaRepo,
                     FuncionarioRepository funcionarioRepo,
                     LojaService lojaService,
                     VendaService vendaService,
                     EquipaService equipaService) throws IOException {
        this.vinhoRepo = vinhoRepo;
        this.vendaRepo = vendaRepo;
        this.funcionarioRepo = funcionarioRepo;
        this.lojaService = lojaService;
        this.vendaService = vendaService;
        this.equipaService = equipaService;

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(4));

        // Rotas da API REST
        server.createContext("/api/vinhos",       this::handleVinhos);
        server.createContext("/api/vendas",        this::handleVendas);
        server.createContext("/api/funcionarios",  this::handleFuncionarios);
        server.createContext("/api/dashboard",     this::handleDashboard);
        server.createContext("/api/stock/stats",   this::handleStockStats);

        // Ficheiros estáticos (HTML, CSS, JS)
        server.createContext("/", this::handleStatic);
    }

    /** Inicia o servidor e imprime o URL de acesso. */
    public void start() {
        server.start();
        System.out.println("🌐 Interface web disponível em: http://localhost:"
                + server.getAddress().getPort());
    }

    /** Para o servidor de forma limpa. */
    public void stop() {
        server.stop(0);
    }

    // ------------------------------------------------------------------ //
    //  Handlers da API REST                                                //
    // ------------------------------------------------------------------ //

    /** GET /api/vinhos[?tipo=Tinto] — lista de vinhos */
    private void handleVinhos(HttpExchange ex) throws IOException {
        if (!isGet(ex)) { sendError(ex, 405, "Method Not Allowed"); return; }

        String tipo = queryParam(ex, "tipo");
        List<Vinho> vinhos = (tipo != null && !tipo.isEmpty())
                ? lojaService.listarVinhosPorTipo(tipo)
                : vinhoRepo.findAll();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < vinhos.size(); i++) {
            if (i > 0) json.append(",");
            json.append(vinhoToJson(vinhos.get(i)));
        }
        json.append("]");

        sendJson(ex, 200, json.toString());
    }

    /** GET /api/vendas — lista completa de vendas */
    private void handleVendas(HttpExchange ex) throws IOException {
        if (!isGet(ex)) { sendError(ex, 405, "Method Not Allowed"); return; }

        List<Venda> vendas = vendaService.listarTodasVendas();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < vendas.size(); i++) {
            if (i > 0) json.append(",");
            json.append(vendaToJson(vendas.get(i)));
        }
        json.append("]");

        sendJson(ex, 200, json.toString());
    }

    /** GET /api/funcionarios — lista de funcionários ativos */
    private void handleFuncionarios(HttpExchange ex) throws IOException {
        if (!isGet(ex)) { sendError(ex, 405, "Method Not Allowed"); return; }

        List<Funcionario> lista = equipaService.listarFuncionariosAtivos();

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) json.append(",");
            json.append(funcionarioToJson(lista.get(i)));
        }
        json.append("]");

        sendJson(ex, 200, json.toString());
    }

    /** GET /api/dashboard — estatísticas para o painel do gerente */
    private void handleDashboard(HttpExchange ex) throws IOException {
        if (!isGet(ex)) { sendError(ex, 405, "Method Not Allowed"); return; }

        double faturacao = vendaService.calcularFaturacaoTotal();
        long totalVendas = vendaRepo.findAll().stream()
                .filter(v -> Venda.STATUS_CONCLUIDA.equals(v.getStatus())).count();
        int totalGarrafas = vendaRepo.findAll().stream()
                .filter(v -> Venda.STATUS_CONCLUIDA.equals(v.getStatus()))
                .flatMap(v -> v.getItens().stream())
                .mapToInt(ItemCarrinho::getQuantidade).sum();
        int stockCritico = (int) vinhoRepo.findAll().stream()
                .filter(v -> v.getQuantidadeStock() <= 5).count();
        double massaSalarial = equipaService.calcularMassaSalarial();

        String json = String.format(Locale.US,
                "{\"faturacaoTotal\":%.2f,\"totalVendas\":%d,\"totalGarrafas\":%d,"
                + "\"stockCritico\":%d,\"massaSalarial\":%.2f}",
                faturacao, totalVendas, totalGarrafas, stockCritico, massaSalarial);

        sendJson(ex, 200, json);
    }

    /** GET /api/stock/stats — estatísticas de stock */
    private void handleStockStats(HttpExchange ex) throws IOException {
        if (!isGet(ex)) { sendError(ex, 405, "Method Not Allowed"); return; }

        List<Vinho> vinhos = vinhoRepo.findAll();
        int totalGarrafas  = vinhos.stream().mapToInt(Vinho::getQuantidadeStock).sum();
        int stockCritico   = (int) vinhos.stream().filter(v -> v.getQuantidadeStock() <= 5).count();
        double valorStock  = vinhos.stream()
                .mapToDouble(v -> v.getPreco() * v.getQuantidadeStock()).sum();

        String json = String.format(Locale.US,
                "{\"totalGarrafas\":%d,\"stockCritico\":%d,\"valorStock\":%.2f}",
                totalGarrafas, stockCritico, valorStock);

        sendJson(ex, 200, json);
    }

    // ------------------------------------------------------------------ //
    //  Handler para ficheiros estáticos                                    //
    // ------------------------------------------------------------------ //

    private void handleStatic(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";

        // Previne path traversal
        if (path.contains("..")) {
            sendError(ex, 403, "Forbidden");
            return;
        }

        File file = new File("." + path);
        if (!file.exists() || !file.isFile()) {
            sendError(ex, 404, "Not Found: " + path);
            return;
        }

        String contentType = contentTypeFor(path);
        byte[] content = Files.readAllBytes(file.toPath());

        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(200, content.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(content);
        }
    }

    // ------------------------------------------------------------------ //
    //  Serialização manual para JSON                                       //
    // ------------------------------------------------------------------ //

    private String vinhoToJson(Vinho v) {
        return String.format(Locale.US,
                "{\"id\":%d,\"nome\":\"%s\",\"tipo\":\"%s\",\"regiao\":\"%s\","
                + "\"anoColheita\":%d,\"preco\":%.2f,\"quantidadeStock\":%d,"
                + "\"descricao\":\"%s\",\"teorAlcoolico\":%.1f,\"produtor\":\"%s\","
                + "\"mediaAvaliacoes\":%.1f}",
                v.getId(), esc(v.getNome()), esc(v.getTipo()), esc(v.getRegiao()),
                v.getAnoColheita(), v.getPreco(), v.getQuantidadeStock(),
                esc(v.getDescricao()), v.getTeorAlcoolico(), esc(v.getProdutor()),
                v.getMediaAvaliacoes());
    }

    private String vendaToJson(Venda v) {
        String clienteNome = v.getCliente() != null ? esc(v.getCliente().getNome()) : "";
        String data = v.getDataVenda() != null ? esc(v.getDataVenda().format(FMT)) : "";

        StringBuilder itens = new StringBuilder("[");
        List<ItemCarrinho> items = v.getItens();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) itens.append(",");
            ItemCarrinho item = items.get(i);
            itens.append(String.format(Locale.US,
                    "{\"nome\":\"%s\",\"quantidade\":%d,\"subtotal\":%.2f}",
                    esc(item.getVinho().getNome()),
                    item.getQuantidade(),
                    item.getSubtotal()));
        }
        itens.append("]");

        return String.format(Locale.US,
                "{\"id\":%d,\"clienteNome\":\"%s\",\"data\":\"%s\","
                + "\"total\":%.2f,\"metodoPagamento\":\"%s\",\"status\":\"%s\","
                + "\"itens\":%s}",
                v.getId(), clienteNome, data,
                v.getTotalVenda(), esc(v.getMetodoPagamento()), esc(v.getStatus()),
                itens);
    }

    private String funcionarioToJson(Funcionario f) {
        String tipo = (f instanceof Gerente) ? "Gerente" : "Funcionário";
        String dataAdmissao = f.getDataAdmissao() != null ? f.getDataAdmissao().toString() : "";
        return String.format(Locale.US,
                "{\"id\":%d,\"nome\":\"%s\",\"email\":\"%s\",\"cargo\":\"%s\","
                + "\"salario\":%.2f,\"dataAdmissao\":\"%s\",\"ativo\":%s,\"tipo\":\"%s\"}",
                f.getId(), esc(f.getNome()), esc(f.getEmail()), esc(f.getCargo()),
                f.getSalario(), dataAdmissao, f.isAtivo(), tipo);
    }

    // ------------------------------------------------------------------ //
    //  Utilitários                                                         //
    // ------------------------------------------------------------------ //

    private boolean isGet(HttpExchange ex) {
        return "GET".equalsIgnoreCase(ex.getRequestMethod());
    }

    private String queryParam(HttpExchange ex, String key) {
        String query = ex.getRequestURI().getQuery();
        if (query == null) return null;
        for (String part : query.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                try { return URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()); }
                catch (UnsupportedEncodingException e) {
                    // UTF-8 is always supported — this should never happen
                    System.err.println("Aviso: falha ao descodificar parâmetro '" + key + "': " + e.getMessage());
                    return kv[1];
                }
            }
        }
        return null;
    }

    private void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private void sendError(HttpExchange ex, int code, String msg) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); }
    }

    private String contentTypeFor(String path) {
        if (path.endsWith(".html")) return "text/html; charset=UTF-8";
        if (path.endsWith(".css"))  return "text/css; charset=UTF-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=UTF-8";
        return "application/octet-stream";
    }

    /** Escapa caracteres especiais para JSON. */
    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
