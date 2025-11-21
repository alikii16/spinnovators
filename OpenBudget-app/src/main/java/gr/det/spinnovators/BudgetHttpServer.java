package gr.det.spinnovators;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * Lightweight HTTP server that exposes the existing MinistryDataInput data via JSON.
 * It prevents duplication of the static arrays inside HTML/JS.
 */
public class BudgetHttpServer {

    private static final int DEFAULT_PORT = 8081;
    private static final Gson GSON = new Gson();
    private static final NumberFormat CURRENCY_FORMAT =
        NumberFormat.getCurrencyInstance(new Locale("el", "GR"));

    private final MinistryDataInput data;
    private final EnvMinistryDataInput envData;
    private HttpServer server;

    public BudgetHttpServer(MinistryDataInput data) {
        this.data = data;
        // Load environment data once at startup (like MinistryDataInput)
        this.envData = new EnvMinistryDataInput();
    }
    

    public void start() throws IOException {
        if (server != null) {
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(DEFAULT_PORT), 0);
            server.createContext("/api/budget", new BudgetHandler());
            server.createContext("/budget", new BudgetPageHandler());
            server.createContext("/login", new LoginHandler());
            server.createContext("/portal", new PortalHandler());
            server.createContext("/budget-portal", new BudgetPortalHandler());
            server.createContext("/employee-budget-portal", new EmployeeBudgetPortalHandler());
            server.createContext("/env-portal", new EnvPortalHandler());
            server.createContext("/env-portal-employee", new EnvPortalEmployeeHandler());
            server.createContext("/env-budget", new EnvBudgetHandler());
            server.createContext("/env-budget-employee", new EnvBudgetEmployeeHandler());
            server.createContext("/test-env", new TestEnvHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("HTTP Server started on port " + DEFAULT_PORT);
        } catch (java.net.BindException e) {
            System.err.println("==========================================");
            System.err.println("ΣΦΑΛΜΑ: Η θύρα " + DEFAULT_PORT + " είναι ήδη σε χρήση!");
            System.err.println("==========================================");
            System.err.println("Πιθανές λύσεις:");
            System.err.println("1. Κλείσε την προηγούμενη εκτέλεση της εφαρμογής");
            System.err.println("2. Ή τερμάτισε τη διαδικασία που χρησιμοποιεί τη θύρα " + DEFAULT_PORT);
            System.err.println("3. Ή περίμενε λίγα δευτερόλεπτα και ξαναπροσπάθησε");
            System.err.println("==========================================");
            throw new IOException("Η θύρα " + DEFAULT_PORT + " είναι ήδη σε χρήση. Κλείσε την προηγούμενη εκτέλεση.", e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private class BudgetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, Map.of("error", "Method Not Allowed"));
                return;
            }

            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String year = queryParams.get("year");

            if (year == null || year.isBlank()) {
                sendJsonResponse(exchange, 400, Map.of("error", "Missing 'year' parameter"));
                return;
            }

            BudgetResponse response = buildResponse(year);

            if (response == null) {
                sendJsonResponse(exchange, 404, Map.of("error", "Δεν υπάρχουν στοιχεία για το έτος " + year));
                return;
            }

            sendJsonResponse(exchange, 200, response);
        }
    }

    private class BudgetPageHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("========================================");
            System.out.println("NATIONAL BUDGET REQUEST");
            System.out.println("========================================");
            
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                System.out.println("ERROR: Invalid request method: " + exchange.getRequestMethod());
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET για την προβολή των προϋπολογισμών."));
                return;
            }

            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String year = queryParams.get("year");

            System.out.println("Requested year: " + (year != null ? year : "(missing)"));
            System.out.println("Request URI: " + exchange.getRequestURI());

            if (year == null || year.isBlank()) {
                System.out.println("✗ ERROR: Year parameter is missing");
                sendHtmlResponse(exchange, 400, buildMessagePage(
                        "Ελλιπή στοιχεία",
                        "Παρακαλώ συμπληρώστε πρώτα το έτος στο πλαίσιο της φόρμας."));
                System.out.println("========================================");
                return;
            }

            // Handle "0000" as invalid year (should show error)
            if ("0000".equals(year)) {
                System.out.println("✗ ERROR: Invalid year '0000' (not allowed)");
                sendHtmlResponse(exchange, 404, buildMessagePage(
                        "Δεν βρέθηκαν στοιχεία",
                        "Δεν έχουμε δεδομένα για το έτος " + year + ". Δοκιμάστε ξανά."));
                System.out.println("========================================");
                return;
            }

            System.out.println("Building budget response for year: " + year);
            BudgetResponse response = buildResponse(year);

            if (response == null) {
                System.out.println("✗ ERROR: No data found for year: " + year);
                sendHtmlResponse(exchange, 404, buildMessagePage(
                        "Δεν βρέθηκαν στοιχεία",
                        "Δεν έχουμε δεδομένα για το έτος " + year + ". Δοκιμάστε ξανά."));
                System.out.println("========================================");
                return;
            }

            System.out.println("✓ Data found for year " + year);
            System.out.println("  Total entries: " + response.entries.size());
            System.out.println("  Total amount: " + CURRENCY_FORMAT.format(response.total));
            System.out.println("Building HTML...");
            sendHtmlResponse(exchange, 200, buildBudgetHtml(response));
            System.out.println("✓ National budget HTML sent successfully");
            System.out.println("========================================");
        }
    }

    private class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                System.out.println("========================================");
                System.out.println("LOGIN REQUEST RECEIVED");
                System.out.println("========================================");
                
                if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    System.out.println("ERROR: Invalid request method: " + exchange.getRequestMethod());
                    sendHtmlResponse(exchange, 405, buildMessagePage(
                            "Μη υποστηριζόμενη ενέργεια",
                            "Η φόρμα σύνδεσης πρέπει να αποστέλλεται με μέθοδο POST."));
                    return;
                }

                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("Raw body: " + body);
                
                Map<String, String> formData = parseFormBody(body);
                System.out.println("Parsed form data: " + formData);

                String username = formData.getOrDefault("username", "").trim();
                String password = formData.getOrDefault("password", "");

                System.out.println("Login attempt:");
                System.out.println("  Username: '" + username + "' (length: " + username.length() + ")");
                System.out.println("  Password: '" + password + "' (length: " + password.length() + ")");
                System.out.println("  Checking against:");
                System.out.println("    MINISTER_USERNAME: 'Minister'");
                System.out.println("    MINISTER_PASSWORD: 'm1n1st3r'");
                System.out.println("    EMPLOYEE_PASSWORD: '3mploy33'");

                FirstLogin.LoginResult result = FirstLogin.authenticate(username, password);

                System.out.println("Authentication result: " + result);
                
                switch (result) {
                    case MINISTER:
                        System.out.println("→ Redirecting to MINISTER portal (MinisterNationalBudget.html)");
                        sendStaticFile(exchange, "OpenBudget-app/src/main/webapp/MinisterNationalBudget.html");
                        System.out.println("✓ Minister portal served successfully");
                        break;
                    case EMPLOYEE:
                        System.out.println("→ Redirecting to EMPLOYEE portal (EmployeeNationalBudget.html)");
                        System.out.println("  Employee username: " + username);
                        sendEmployeePortal(exchange, username);
                        System.out.println("✓ Employee portal served successfully");
                        break;
                    default:
                        System.out.println("✗ INVALID credentials - showing error page");
                        sendLoginErrorPage(exchange);
                }
                System.out.println("========================================");
            } catch (Exception e) {
                System.err.println("✗ EXCEPTION in LoginHandler:");
                e.printStackTrace();
                try {
                    sendHtmlResponse(exchange, 500, buildMessagePage(
                            "Σφάλμα",
                            "Προέκυψε σφάλμα: " + e.getMessage()));
                } catch (IOException ioException) {
                    System.err.println("Failed to send error response");
                }
            }
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object body) throws IOException {
        String json = GSON.toJson(body);
        byte[] payload = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, payload.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(payload);
        }
    }

    private void sendHtmlResponse(HttpExchange exchange, int statusCode, String html) throws IOException {
        byte[] payload = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.getResponseHeaders().set("X-Frame-Options", "SAMEORIGIN");
        exchange.sendResponseHeaders(statusCode, payload.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(payload);
        }
    }

    private void sendStaticFile(HttpExchange exchange, String relativePath) throws IOException {
        Path path = new File(relativePath).getAbsoluteFile().toPath();
        byte[] payload = Files.readAllBytes(path);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, payload.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(payload);
        }
    }

    private void sendEmployeePortal(HttpExchange exchange, String username) throws IOException {
        Path path = new File("OpenBudget-app/src/main/webapp/EmployeeNationalBudget.html").getAbsoluteFile().toPath();
        String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        
        // Replace the username placeholder with the actual username
        String welcomeMessage = "Καλώς ήρθατε, " + (username != null && !username.isBlank() ? username : "Χρήστη") + ".";
        content = content.replace("<!--USERNAME_PLACEHOLDER-->", welcomeMessage);
        
        // Replace all links to employee-budget-portal and env-portal-employee with username parameter
        String encodedUsername = java.net.URLEncoder.encode(username != null ? username : "", StandardCharsets.UTF_8);
        content = content.replace("http://localhost:8081/employee-budget-portal", 
                                  "http://localhost:8081/employee-budget-portal?username=" + encodedUsername);
        content = content.replace("http://localhost:8081/env-portal-employee", 
                                  "http://localhost:8081/env-portal-employee?username=" + encodedUsername);
        
        byte[] payload = content.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, payload.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(payload);
        }
    }

    private Map<String, String> parseQuery(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getQuery();

        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = pair.substring(0, idx);
                String value = pair.substring(idx + 1);
                params.put(key, decodeComponent(value));
            } else if (idx == -1) {
                params.put(pair, "");
            }
        }
        return params;
    }

    private Map<String, String> parseFormBody(String body) {
        Map<String, String> params = new HashMap<>();
        if (body == null || body.isBlank()) {
            return params;
        }
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if (idx >= 0) {
                String key = decodeComponent(pair.substring(0, idx));
                String value = decodeComponent(pair.substring(idx + 1));
                params.put(key, value);
            }
        }
        return params;
    }

    private String decodeComponent(String value) {
        return java.net.URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private BudgetResponse buildResponse(String year) {
        switch (year) {
            case "2025":
                return createResponse(year, data.getNames25(), data.getBudgetAmount25(), data.getSize25());
            case "2024":
                return createResponse(year, data.getNames24(), data.getBudgetAmount24(), data.getSize24());
            case "2023":
                return createResponse(year, data.getNames23(), data.getBudgetAmount23(), data.getSize23());
            default:
                return null;
        }
    }

    private BudgetResponse createResponse(String year, String[] names, double[] amounts, int size) {
        System.out.println("  createResponse called for year: " + year);
        System.out.println("  names array: " + (names != null ? "not null, length: " + names.length : "NULL"));
        System.out.println("  amounts array: " + (amounts != null ? "not null, length: " + amounts.length : "NULL"));
        System.out.println("  size: " + size);
        
        List<BudgetEntry> entries = new ArrayList<>();
        double total = 0;

        if (names == null || amounts == null || size <= 0) {
            System.err.println("  ✗ ERROR: Invalid data arrays!");
            return new BudgetResponse(year, entries, total);
        }

        for (int i = 0; i < size && i < names.length && i < amounts.length; i++) {
            String name = names[i];
            double amount = amounts[i];

            entries.add(new BudgetEntry(name, amount));
            total += amount;
        }

        System.out.println("  Created " + entries.size() + " entries, total: " + total);
        return new BudgetResponse(year, entries, total);
    }

    private String buildBudgetHtml(BudgetResponse response) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang=\"el\"><head><meta charset=\"UTF-8\">");
        sb.append("<style>");
        sb.append("body{font-family:'Segoe UI',Arial,sans-serif;background:#e8f1ec;padding:32px;margin:0;}");
        sb.append("h2{text-align:center;color:#0f3a24;font-size:26px;margin-bottom:20px;letter-spacing:0.4px;}");
        sb.append("table{width:100%;border-collapse:collapse;margin-top:18px;font-size:14px;background:#fff;border-radius:8px;overflow:hidden;}");
        sb.append("th,td{padding:12px 15px;border-bottom:1px solid #e5eee7;}");
        sb.append("th{text-align:left;background:linear-gradient(135deg, #0f4c2c, #1b8a54);color:#fff;font-size:14px;font-weight:600;letter-spacing:0.3px;}");
        sb.append("td{color:#2a4a36;font-size:14px;}");
        sb.append("td.amount{text-align:right;font-weight:600;color:#0b542f;}");
        sb.append("tr:hover{background:#f0f7f3;}");
        sb.append("tr.total-row td{font-weight:bold;border-top:2px solid #d9e6dc;font-size:15px;background:#f9fcfa;}");
        sb.append("</style></head><body>");
        sb.append("<h2>Κρατικός Προϋπολογισμός ").append(response.year).append("</h2>");
        sb.append("<table><thead><tr><th>Φορέας / Υπουργείο</th><th style=\"text-align:right;\">Ποσό</th></tr></thead><tbody>");

        for (BudgetEntry entry : response.entries) {
            sb.append("<tr><td>").append(entry.name).append("</td><td class=\"amount\">")
              .append(CURRENCY_FORMAT.format(entry.amount)).append("</td></tr>");
        }

        sb.append("<tr class=\"total-row\"><td>Συνολικός Προϋπολογισμός</td><td class=\"amount\">")
          .append(CURRENCY_FORMAT.format(response.total)).append("</td></tr>");
        sb.append("</tbody></table>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String buildMessagePage(String title, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html lang=\"el\"><head><meta charset=\"UTF-8\">");
        sb.append("<style>body{font-family:Arial, sans-serif;background:#fefefe;padding:25px;text-align:center;}");
        sb.append(".card{background:#fff;border-radius:12px;padding:30px;max-width:480px;margin:40px auto;box-shadow:0 4px 18px rgba(0,0,0,0.08);}");
        sb.append("h2{color:#1f2a1c;margin-bottom:12px;}p{color:#4a4a4a;margin-bottom:6px;}</style></head><body>");
        sb.append("<div class=\"card\"><h2>").append(title).append("</h2><p>").append(message).append("</p></div>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private class PortalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("========================================");
            System.out.println("LOGIN PAGE REQUEST");
            System.out.println("========================================");
            System.out.println("Serving: LoginPage.html");
            
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                System.out.println("ERROR: Invalid request method: " + exchange.getRequestMethod());
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET."));
                return;
            }
            sendStaticFile(exchange, "OpenBudget-app/src/main/webapp/LoginPage.html");
            System.out.println("✓ LoginPage.html served successfully");
            System.out.println("========================================");
        }
    }

    private class BudgetPortalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("========================================");
            System.out.println("MINISTER NATIONAL BUDGET PORTAL");
            System.out.println("========================================");
            System.out.println("Serving: MinisterNationalBudget.html");
            
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                System.out.println("ERROR: Invalid request method: " + exchange.getRequestMethod());
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET."));
                return;
            }
            sendStaticFile(exchange, "OpenBudget-app/src/main/webapp/MinisterNationalBudget.html");
            System.out.println("✓ MinisterNationalBudget.html served successfully");
            System.out.println("========================================");
        }
    }

    private class EmployeeBudgetPortalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("========================================");
            System.out.println("EMPLOYEE NATIONAL BUDGET PORTAL");
            System.out.println("========================================");
            
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                System.out.println("ERROR: Invalid request method: " + exchange.getRequestMethod());
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET."));
                return;
            }
            
            // Get username from query parameter
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String username = queryParams.get("username");
            
            System.out.println("Serving: EmployeeNationalBudget.html");
            System.out.println("Employee username: " + (username != null ? username : "(not provided)"));
            
            Path path = new File("OpenBudget-app/src/main/webapp/EmployeeNationalBudget.html").getAbsoluteFile().toPath();
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            
            // Replace the username placeholder with the actual username
            String welcomeMessage = "Καλώς ήρθατε, " + (username != null && !username.isBlank() ? username : "Χρήστη") + ".";
            content = content.replace("<!--USERNAME_PLACEHOLDER-->", welcomeMessage);
            System.out.println("Welcome message: " + welcomeMessage);
            
            // Replace all links with username parameter
            String encodedUsername = java.net.URLEncoder.encode(username != null ? username : "", StandardCharsets.UTF_8);
            content = content.replace("http://localhost:8081/employee-budget-portal", 
                                      "http://localhost:8081/employee-budget-portal?username=" + encodedUsername);
            content = content.replace("http://localhost:8081/env-portal-employee", 
                                      "http://localhost:8081/env-portal-employee?username=" + encodedUsername);
            
            byte[] payload = content.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
            System.out.println("✓ EmployeeNationalBudget.html served successfully");
            System.out.println("========================================");
        }
    }

    private class EnvPortalHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET."));
                return;
            }
            
            System.out.println("========================================");
            System.out.println("MINISTER ENV BUDGET PORTAL");
            System.out.println("========================================");
            System.out.println("Serving: MinisterEnvBudget.html");
            
            sendStaticFile(exchange, "OpenBudget-app/src/main/webapp/MinisterEnvBudget.html");
            System.out.println("✓ MinisterEnvBudget.html served successfully");
            System.out.println("========================================");
        }
    }

    private class EnvPortalEmployeeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET."));
                return;
            }
            
            // Get username from query parameter
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String username = queryParams.get("username");
            
            System.out.println("========================================");
            System.out.println("EMPLOYEE ENV BUDGET PORTAL");
            System.out.println("========================================");
            System.out.println("Serving: EmployeeEnvBudget.html");
            System.out.println("Employee username: " + (username != null ? username : "(not provided)"));
            
            Path path = new File("OpenBudget-app/src/main/webapp/EmployeeEnvBudget.html").getAbsoluteFile().toPath();
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            
            // Replace links with username parameter
            String encodedUsername = java.net.URLEncoder.encode(username != null ? username : "", StandardCharsets.UTF_8);
            content = content.replace("http://localhost:8081/employee-budget-portal", 
                                      "http://localhost:8081/employee-budget-portal?username=" + encodedUsername);
            content = content.replace("http://localhost:8081/env-portal-employee", 
                                      "http://localhost:8081/env-portal-employee?username=" + encodedUsername);
            content = content.replace("http://localhost:8081/env-budget-employee", 
                                      "http://localhost:8081/env-budget-employee?username=" + encodedUsername);
            
            byte[] payload = content.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
            
            System.out.println("✓ EmployeeEnvBudget.html served successfully");
            System.out.println("========================================");
        }
    }

    private class TestEnvHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("X-Frame-Options", "SAMEORIGIN");
            
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String year = queryParams.get("year");
            
            String testHtml = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head><body style=\"padding:20px;font-family:Arial;\"><h1>Test - Iframe Works!</h1><p>If you see this, the iframe is loading correctly.</p>";
            if (year != null) {
                testHtml += "<p>Year parameter received: " + year + "</p>";
            }
            testHtml += "</body></html>";
            sendHtmlResponse(exchange, 200, testHtml);
        }
    }

    private class EnvBudgetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("========================================");
            System.out.println("ENV BUDGET REQUEST (MINISTER)");
            System.out.println("========================================");
            
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("X-Frame-Options", "SAMEORIGIN");
            
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                System.out.println("ERROR: Invalid request method: " + exchange.getRequestMethod());
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET."));
                return;
            }

            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String year = queryParams.get("year");

            System.out.println("Requested year: " + (year != null ? year : "(missing)"));
            System.out.println("Request URI: " + exchange.getRequestURI());

            if (year == null || year.isBlank()) {
                System.out.println("✗ ERROR: Year parameter is missing");
                sendHtmlResponse(exchange, 400, buildMessagePage(
                        "Ελλιπή στοιχεία",
                        "Παρακαλώ συμπληρώστε πρώτα το έτος στο πλαίσιο της φόρμας."));
                System.out.println("========================================");
                return;
            }

            // Handle "0000" as invalid year
            if ("0000".equals(year)) {
                System.out.println("✗ ERROR: Invalid year '0000' (not allowed)");
                sendHtmlResponse(exchange, 404, buildMessagePage(
                        "Δεν βρέθηκαν στοιχεία",
                        "Δεν έχουμε δεδομένα για το έτος " + year + ". Δοκιμάστε ξανά."));
                System.out.println("========================================");
                return;
            }

            System.out.println("Loading environment budget data for year: " + year);
            // Get data for the requested year (like BudgetPageHandler does)
            List<EnvMinistryDataInput.EnvBudgetEntry> yearData = envData.getDataForYear(year);
            
            if (yearData == null || yearData.isEmpty()) {
                System.out.println("✗ ERROR: No data found for year: " + year);
                sendHtmlResponse(exchange, 404, buildMessagePage(
                        "Δεν βρέθηκαν στοιχεία",
                        "Δεν έχουμε δεδομένα για το έτος " + year + ". Δοκιμάστε ξανά."));
                System.out.println("========================================");
                return;
            }

            System.out.println("✓ Data found for year " + year);
            System.out.println("  Total entries: " + yearData.size());
            System.out.println("Building HTML...");
            String html = buildEnvBudgetHtml(year, yearData, "http://localhost:8081/budget-portal");
            System.out.println("  HTML length: " + html.length() + " characters");
            sendHtmlResponse(exchange, 200, html);
            System.out.println("✓ Env budget HTML sent successfully");
            System.out.println("========================================");
        }
    }

    private class EnvBudgetEmployeeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("========================================");
            System.out.println("ENV BUDGET REQUEST (EMPLOYEE)");
            System.out.println("========================================");
            
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("X-Frame-Options", "SAMEORIGIN");
            
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                System.out.println("ERROR: Invalid request method: " + exchange.getRequestMethod());
                sendHtmlResponse(exchange, 405, buildMessagePage(
                        "Μη υποστηριζόμενη ενέργεια",
                        "Παρακαλώ χρησιμοποιήστε αίτημα GET."));
                return;
            }

            Map<String, String> queryParams = parseQuery(exchange.getRequestURI());
            String year = queryParams.get("year");
            String username = queryParams.get("username");

            System.out.println("Requested year: " + (year != null ? year : "(missing)"));
            System.out.println("Employee username: " + (username != null ? username : "(missing)"));
            System.out.println("Request URI: " + exchange.getRequestURI());

            if (year == null || year.isBlank()) {
                System.out.println("✗ ERROR: Year parameter is missing");
                sendHtmlResponse(exchange, 400, buildMessagePage(
                        "Ελλιπή στοιχεία",
                        "Παρακαλώ συμπληρώστε πρώτα το έτος στο πλαίσιο της φόρμας."));
                System.out.println("========================================");
                return;
            }

            if ("0000".equals(year)) {
                System.out.println("✗ ERROR: Invalid year '0000' (not allowed)");
                sendHtmlResponse(exchange, 404, buildMessagePage(
                        "Δεν βρέθηκαν στοιχεία",
                        "Δεν έχουμε δεδομένα για το έτος " + year + ". Δοκιμάστε ξανά."));
                System.out.println("========================================");
                return;
            }

            System.out.println("Loading environment budget data for year: " + year);
            List<EnvMinistryDataInput.EnvBudgetEntry> yearData = envData.getDataForYear(year);
            
            if (yearData == null || yearData.isEmpty()) {
                System.out.println("✗ ERROR: No data found for year: " + year);
                sendHtmlResponse(exchange, 404, buildMessagePage(
                        "Δεν βρέθηκαν στοιχεία",
                        "Δεν έχουμε δεδομένα για το έτος " + year + ". Δοκιμάστε ξανά."));
                System.out.println("========================================");
                return;
            }

            System.out.println("✓ Data found for year " + year);
            System.out.println("  Total entries: " + yearData.size());
            System.out.println("Building HTML...");
            String backUrl = "http://localhost:8081/employee-budget-portal";
            if (username != null && !username.isBlank()) {
                String encodedUsername = java.net.URLEncoder.encode(username, StandardCharsets.UTF_8);
                backUrl += "?username=" + encodedUsername;
                System.out.println("  Back URL includes username: " + username);
            }
            String html = buildEnvBudgetHtml(year, yearData, backUrl);
            System.out.println("  HTML length: " + html.length() + " characters");
            sendHtmlResponse(exchange, 200, html);
            System.out.println("✓ Env budget HTML sent successfully");
            System.out.println("========================================");
        }
    }

    private String buildEnvBudgetHtml(String year, List<EnvMinistryDataInput.EnvBudgetEntry> entries, String backUrl) {
        try {
            System.out.println("  Processing " + entries.size() + " budget entries...");
            
            // Group entries by sector -> unit -> entry
            Map<String, Map<String, List<EnvMinistryDataInput.EnvBudgetEntry>>> grouped = new HashMap<>();
            
            for (EnvMinistryDataInput.EnvBudgetEntry entry : entries) {
                String sector = entry.getSector();
                String unit = entry.getUnit();
                
                grouped.computeIfAbsent(sector, k -> new HashMap<>())
                       .computeIfAbsent(unit, k -> new ArrayList<>())
                       .add(entry);
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html><html lang=\"el\"><head><meta charset=\"UTF-8\">");
            sb.append("<style>");
            sb.append("body{font-family:'Segoe UI',Arial,sans-serif;background:#e8f1ec;padding:32px;margin:0;}");
            sb.append("h2{text-align:center;color:#0f3a24;font-size:26px;margin-bottom:20px;letter-spacing:0.4px;}");
            sb.append(".container{max-width:1200px;margin:0 auto;}");
            sb.append(".sector{background:#fff;border-radius:18px;padding:24px 28px;margin-bottom:24px;");
            sb.append("box-shadow:0 20px 45px rgba(0,0,0,0.08);border:1px solid #d9e6dc;}");
            sb.append(".sector h3{margin:0 0 12px 0;color:#114025;font-size:20px;}");
            sb.append(".sector-total{color:#0b542f;font-weight:600;font-size:15px;margin-bottom:12px;}");
            sb.append("table{width:100%;border-collapse:collapse;margin-top:18px;font-size:14px;background:#fff;border-radius:8px;overflow:hidden;}");
            sb.append("th,td{padding:12px 15px;border-bottom:1px solid #e5eee7;}");
            sb.append("th{text-align:left;background:linear-gradient(135deg, #0f4c2c, #1b8a54);color:#fff;font-size:14px;font-weight:600;letter-spacing:0.3px;}");
            sb.append("td{color:#2a4a36;font-size:14px;}");
            sb.append("td.amount{text-align:right;font-weight:600;color:#0b542f;}");
            sb.append("tr.unit-row{background:#f9fcfa;font-weight:600;}");
            sb.append("tr.unit-row td{color:#114025;}");
            sb.append("tr.entry-row td{font-size:13px;color:#476250;padding-left:24px;}");
            sb.append("tr:hover{background:#f0f7f3;}");
            sb.append("tr.unit-row:hover{background:#e8f1ec;}");
            sb.append(".back-section{margin-top:40px;text-align:center;padding-top:30px;border-top:2px solid #d9e6dc;}");
            sb.append(".back-text{font-size:16px;color:#2a4a36;margin-bottom:16px;font-weight:500;}");
            sb.append(".back-btn{display:inline-block;padding:13px 32px;background:linear-gradient(120deg, #0f4c2c, #1b8a54);");
            sb.append("color:#fff;border:none;border-radius:14px;font-size:16px;cursor:pointer;text-decoration:none;");
            sb.append("letter-spacing:0.4px;transition:transform 0.2s, box-shadow 0.2s;}");
            sb.append(".back-btn:hover{transform:translateY(-2px);box-shadow:0 14px 32px rgba(19, 94, 58, 0.35);}");
            sb.append("</style></head><body>");
            sb.append("<div class=\"container\">");
            sb.append("<h2>ΥΠΕΝ – Προϋπολογισμός ").append(year).append("</h2>");

            // Iterate through sectors
            for (Map.Entry<String, Map<String, List<EnvMinistryDataInput.EnvBudgetEntry>>> sectorEntry : grouped.entrySet()) {
                String sectorName = sectorEntry.getKey();
                Map<String, List<EnvMinistryDataInput.EnvBudgetEntry>> units = sectorEntry.getValue();
                
                double sectorTotal = 0;
                Map<String, Double> unitTotals = new HashMap<>();
                
                // Calculate totals
                for (Map.Entry<String, List<EnvMinistryDataInput.EnvBudgetEntry>> unitEntry : units.entrySet()) {
                    double unitTotal = 0;
                    for (EnvMinistryDataInput.EnvBudgetEntry entry : unitEntry.getValue()) {
                        unitTotal += entry.getAmount();
                    }
                    unitTotals.put(unitEntry.getKey(), unitTotal);
                    sectorTotal += unitTotal;
                }
                
                sb.append("<div class=\"sector\">");
                sb.append("<h3>").append(sectorName).append("</h3>");
                sb.append("<div class=\"sector-total\">Σύνολο Τομέα: ").append(CURRENCY_FORMAT.format(sectorTotal)).append("</div>");
                sb.append("<table><thead><tr><th>Μονάδα / Δαπάνη</th><th style=\"text-align:right;\">Ποσό</th></tr></thead><tbody>");

                // Iterate through units
                for (Map.Entry<String, List<EnvMinistryDataInput.EnvBudgetEntry>> unitEntry : units.entrySet()) {
                    String unitName = unitEntry.getKey();
                    double unitTotal = unitTotals.get(unitName);
                    
                    sb.append("<tr class=\"unit-row\"><td>").append(unitName).append("</td>")
                      .append("<td class=\"amount\">").append(CURRENCY_FORMAT.format(unitTotal)).append("</td></tr>");
                    
                    // Iterate through entries
                    for (EnvMinistryDataInput.EnvBudgetEntry entry : unitEntry.getValue()) {
                        sb.append("<tr class=\"entry-row\"><td>").append("— ").append(entry.getEntry()).append("</td>")
                          .append("<td class=\"amount\">").append(CURRENCY_FORMAT.format(entry.getAmount())).append("</td></tr>");
                    }
                }

            sb.append("</tbody></table>");
            sb.append("</div>");
            }

            sb.append("</div>");
            sb.append("</body></html>");
            
            String result = sb.toString();
            System.out.println("  HTML generation completed");
            return result;
        } catch (Exception e) {
            System.err.println("buildEnvBudgetHtml: Exception occurred:");
            e.printStackTrace();
            return buildMessagePage("Σφάλμα", "Προέκυψε σφάλμα κατά την κατασκευή της HTML: " + e.getMessage() + "<br><br>Exception: " + e.getClass().getSimpleName());
        }
    }

    private void sendLoginErrorPage(HttpExchange exchange) throws IOException {
        Path portalPath = new File("OpenBudget-app/src/main/webapp/LoginPage.html").getAbsoluteFile().toPath();
        String portalContent = new String(Files.readAllBytes(portalPath), StandardCharsets.UTF_8);
        
        String errorMessage = "<div style=\"background-color:#ffebee;border:1px solid #c62828;border-radius:8px;padding:12px;margin-bottom:18px;text-align:center;\"><p style=\"color:#c62828;font-size:15px;font-weight:600;margin:0;\">Λάθος όνομα χρήστη ή κωδικός. Προσπαθήστε ξανά.</p></div>";
        
        String updatedContent = portalContent.replace("<!--SERVER_MESSAGE-->", errorMessage);
        
        sendHtmlResponse(exchange, 401, updatedContent);
    }

    private static class BudgetResponse {
        private final String year;
        private final List<BudgetEntry> entries;
        private final double total;

        BudgetResponse(String year, List<BudgetEntry> entries, double total) {
            this.year = year;
            this.entries = entries;
            this.total = total;
        }
    }

    private static class BudgetEntry {
        private final String name;
        private final double amount;

        BudgetEntry(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}

