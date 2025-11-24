package gr.det.spinnovators;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web server class to handle login functionality via HTTP.
 * Serves HTML pages and processes login requests.
 */
public final class LoginWebServer {
    
    private static final int PORT = 8080;
    private static final String MINISTER = "Minister";
    private static final String PASSWORD_MINISTER = "m1n1st3r";
    private static final String PASSWORD_EMPLOYEE = "3mpl0y33";
    
    /**
     * Private constructor to prevent instantiation.
     */
    private LoginWebServer() {
    }
    
    /**
     * Starts the web server.
     * @param frontendPath Path to the frontend directory containing HTML files
     * @throws IOException if server cannot start
     */
    public static void startServer(final String frontendPath) throws IOException {
        System.out.println("[DEBUG] LoginWebServer: Starting server with frontend path: " + frontendPath);
        
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Serve login.html at root and /login.html
        server.createContext("/", exchange -> {
            System.out.println("[DEBUG] Request to / : " + exchange.getRequestMethod());
            if ("GET".equals(exchange.getRequestMethod())) {
                serveLoginPage(exchange, frontendPath, null);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handleLoginPost(exchange, frontendPath);
            }
        });
        
        server.createContext("/login.html", exchange -> {
            System.out.println("[DEBUG] Request to /login.html : " + exchange.getRequestMethod());
            if ("GET".equals(exchange.getRequestMethod())) {
                serveLoginPage(exchange, frontendPath, null);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handleLoginPost(exchange, frontendPath);
            }
        });
        
        // Handle POST to /login
        server.createContext("/login", exchange -> {
            System.out.println("[DEBUG] Request to /login : " + exchange.getRequestMethod());
            if ("POST".equals(exchange.getRequestMethod())) {
                handleLoginPost(exchange, frontendPath);
            } else {
                // Redirect GET /login to login.html
                redirect(exchange, "/login.html");
            }
        });
        
        // Serve static HTML files
        server.createContext("/minister_statebudget.html", exchange -> {
            System.out.println("[DEBUG] Request to /minister_statebudget.html");
            serveStaticFile(exchange, frontendPath, "minister_statebudget.html");
        });
        server.createContext("/employee_statebudget.html", exchange -> {
            System.out.println("[DEBUG] Request to /employee_statebudget.html");
            serveStaticFile(exchange, frontendPath, "employee_statebudget.html");
        });
        
        server.setExecutor(null); // Use default executor
        server.start();
        
        System.out.println("Web server started on http://localhost:" + PORT);
        System.out.println("Open http://localhost:" + PORT + "/login.html in your browser");
    }
    
    /**
     * Serves the login page with optional error message.
     * @param exchange HTTP exchange
     * @param frontendPath Path to frontend directory
     * @param errorMessage Error message to display (null if no error)
     */
    private static void serveLoginPage(final HttpExchange exchange, 
                                       final String frontendPath, 
                                       final String errorMessage) throws IOException {
        try {
            Path htmlPath = Paths.get(frontendPath, "login.html");
            System.out.println("[DEBUG] Serving login page from: " + htmlPath);
            System.out.println("[DEBUG] File exists: " + Files.exists(htmlPath));
            
            if (!Files.exists(htmlPath)) {
                sendErrorResponse(exchange, 404, "login.html not found at: " + htmlPath);
                return;
            }
            
            String htmlContent = new String(Files.readAllBytes(htmlPath), StandardCharsets.UTF_8);
            System.out.println("[DEBUG] HTML content length: " + htmlContent.length() + " characters");
            
            // If there's an error message, inject it into the HTML
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Remove error-message-hidden class and update the message text
                htmlContent = htmlContent.replace(
                    "class=\"message error-message error-message-hidden\"",
                    "class=\"message error-message\""
                );
                htmlContent = htmlContent.replace(
                    "Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά.",
                    errorMessage
                );
                System.out.println("[DEBUG] Error message injected into HTML: " + errorMessage);
            } else {
                // Ensure error message is hidden if no error
                if (!htmlContent.contains("error-message-hidden")) {
                    htmlContent = htmlContent.replace(
                        "class=\"message error-message\"",
                        "class=\"message error-message error-message-hidden\""
                    );
                }
            }
            
            sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
        } catch (IOException e) {
            sendErrorResponse(exchange, 500, "Error loading login page: " + e.getMessage());
        }
    }
    
    /**
     * Handles POST request from login form.
     * @param exchange HTTP exchange
     * @param frontendPath Path to frontend directory
     */
    private static void handleLoginPost(final HttpExchange exchange, 
                                        final String frontendPath) throws IOException {
        try {
            // Read all form data byte-by-byte to ensure we get everything
            java.io.InputStream requestBody = exchange.getRequestBody();
            byte[] buffer = new byte[1024];
            StringBuilder formDataBuilder = new StringBuilder();
            int bytesRead;
            while ((bytesRead = requestBody.read(buffer)) != -1) {
                formDataBuilder.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }
            String formData = formDataBuilder.toString();
            
            System.out.println("[DEBUG] Raw form data received: " + formData);
            
            // Parse username and password from form data
            String username = null;
            String password = null;
            
            if (formData != null && !formData.isEmpty()) {
                String[] pairs = formData.split("&");
                System.out.println("[DEBUG] Number of form pairs: " + pairs.length);
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                        System.out.println("[DEBUG] Parsed - Key: '" + key + "', Value: '" + value + "'");
                        if ("username".equals(key)) {
                            username = value;
                        } else if ("password".equals(key)) {
                            password = value;
                        }
                    } else if (keyValue.length == 1) {
                        // Handle case where value might be empty
                        String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                        System.out.println("[DEBUG] Parsed - Key: '" + key + "', Value: (empty)");
                        if ("username".equals(key)) {
                            username = "";
                        } else if ("password".equals(key)) {
                            password = "";
                        }
                    }
                }
            }
            
            // Validate credentials using FirstLogin logic
            System.out.println("========================================");
            System.out.println("[WEB LOGIN] Login attempt received");
            System.out.println("[WEB LOGIN] Username: " + (username != null ? username : "null"));
            System.out.println("[WEB LOGIN] Checking credentials...");
            
            if (username != null && password != null) {
                if (username.equals(MINISTER) && password.equals(PASSWORD_MINISTER)) {
                    // Minister login successful - redirect to minister page
                    System.out.println("[WEB LOGIN] ✓ Login SUCCESSFUL - Minister");
                    System.out.println("[WEB LOGIN] Redirecting to: /minister_statebudget.html");
                    System.out.println("========================================");
                    redirect(exchange, "/minister_statebudget.html");
                    return;
                } else if (!username.equals(MINISTER) && password.equals(PASSWORD_EMPLOYEE)) {
                    // Employee login successful - redirect to employee page
                    System.out.println("[WEB LOGIN] ✓ Login SUCCESSFUL - Employee: " + username);
                    System.out.println("[WEB LOGIN] Redirecting to: /employee_statebudget.html");
                    System.out.println("========================================");
                    redirect(exchange, "/employee_statebudget.html");
                    return;
                }
            }
            
            // Login failed - show error message on same page
            System.out.println("[WEB LOGIN] ✗ Login FAILED - Wrong credentials");
            System.out.println("[WEB LOGIN] Showing error message on login page");
            System.out.println("========================================");
            String errorMessage = "Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά.";
            serveLoginPage(exchange, frontendPath, errorMessage);
            
        } catch (Exception e) {
            sendErrorResponse(exchange, 500, "Error processing login: " + e.getMessage());
        }
    }
    
    /**
     * Serves a static HTML file.
     * @param exchange HTTP exchange
     * @param frontendPath Path to frontend directory
     * @param filename Name of the file to serve
     */
    private static void serveStaticFile(final HttpExchange exchange, 
                                        final String frontendPath, 
                                        final String filename) throws IOException {
        try {
            Path filePath = Paths.get(frontendPath, filename);
            if (!Files.exists(filePath)) {
                sendErrorResponse(exchange, 404, "File not found: " + filename);
                return;
            }
            
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            sendResponse(exchange, content, 200, "text/html; charset=UTF-8");
        } catch (IOException e) {
            sendErrorResponse(exchange, 500, "Error loading file: " + e.getMessage());
        }
    }
    
    /**
     * Sends a redirect response.
     * @param exchange HTTP exchange
     * @param location URL to redirect to
     */
    private static void redirect(final HttpExchange exchange, final String location) throws IOException {
        exchange.getResponseHeaders().set("Location", location);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
    
    /**
     * Sends an HTTP response.
     * @param exchange HTTP exchange
     * @param response Response body
     * @param statusCode HTTP status code
     * @param contentType Content type header
     */
    private static void sendResponse(final HttpExchange exchange, 
                                     final String response, 
                                     final int statusCode, 
                                     final String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
    
    /**
     * Sends an error response.
     * @param exchange HTTP exchange
     * @param statusCode HTTP status code
     * @param message Error message
     */
    private static void sendErrorResponse(final HttpExchange exchange, 
                                         final int statusCode, 
                                         final String message) throws IOException {
        String errorHtml = "<html><body><h1>Error " + statusCode + "</h1><p>" + message + "</p></body></html>";
        sendResponse(exchange, errorHtml, statusCode, "text/html; charset=UTF-8");
    }
}

