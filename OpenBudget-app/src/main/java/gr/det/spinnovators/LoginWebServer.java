package gr.det.spinnovators;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import gr.det.spinnovators.envdatamodel.EnvBudgetData;

/**
 * Web server class to handle login functionality via HTTP.
 * Serves HTML pages and processes login requests.
 */

public final class LoginWebServer {
    
  private static final int PORT = 8080;
  private static final String MINISTER = "Minister";
  private static final String PASSWORD_MINISTER = "m1n1st3r";
  private static final String PASSWORD_EMPLOYEE = "3mpl0y33";
    
  // Budget data loaded from JSON (for budget.html pages)
  private static EnvBudgetData envBudgetData = null;
  private static EnvBudgetTranslator translator = null;
  private static EnvBudgetPrinter envPrinter = null;
    
  /**
   * Private constructor to prevent instantiation.
   */
  private LoginWebServer() {
  }
    
  /**
   * Initializes budget data from JSON file.
   */
  private static void initializeBudgetData() {
    if (envBudgetData == null) {
      EnvBudgetLoader envLoader = new EnvBudgetLoader();
      envBudgetData = envLoader.loadBudget();
      translator = new EnvBudgetTranslator();
      envPrinter = new EnvBudgetPrinter(envBudgetData, translator);
    }
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
      
    // Serve static HTML files and handle year submission
    server.createContext("/minister_statebudget.html", exchange -> {
      System.out.println("[DEBUG] Request to /minister_statebudget.html : " + exchange.getRequestMethod());
      if ("POST".equals(exchange.getRequestMethod())) {
        handleYearSubmission(exchange, frontendPath, "minister_statebudget.html", null);
      } else {
        serveStaticFile(exchange, frontendPath, "minister_statebudget.html");
      }
    });
    server.createContext("/employee_statebudget.html", exchange -> {
      System.out.println("[DEBUG] Request to /employee_statebudget.html : " + exchange.getRequestMethod());
      String usernameParam = getQueryParam(exchange, "user");
      if ("POST".equals(exchange.getRequestMethod())) {
        // For POST, preserve the username from query string
        handleYearSubmission(exchange, frontendPath, "employee_statebudget.html", usernameParam);
      } else {
        serveHtmlWithUsername(exchange, frontendPath, "employee_statebudget.html", usernameParam);
      }
    });
    server.createContext("/minister_budget.html", exchange -> {
      System.out.println("[DEBUG] Request to /minister_budget.html : " + exchange.getRequestMethod());
      if ("POST".equals(exchange.getRequestMethod())) {
        handleYearSubmission(exchange, frontendPath, "minister_budget.html", null);
      } else {
        serveStaticFile(exchange, frontendPath, "minister_budget.html");
      }
    });
    server.createContext("/employee_budget.html", exchange -> {
    System.out.println("[DEBUG] Request to /employee_budget.html : " + exchange.getRequestMethod());
    String usernameParam = getQueryParam(exchange, "user");
    if ("POST".equals(exchange.getRequestMethod())) {
      // For POST, get username from form data or query string
      handleYearSubmission(exchange, frontendPath, "employee_budget.html", usernameParam);
    } else {
      serveHtmlWithUsername(exchange, frontendPath, "employee_budget.html", usernameParam);
    }
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

          
      String htmlContent = new String(Files.readAllBytes(htmlPath), 
          StandardCharsets.UTF_8);
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
            String key = java.net.URLDecoder.decode(keyValue[0], 
                StandardCharsets.UTF_8);
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
          String encodedUser = URLEncoder.encode(username, StandardCharsets.UTF_8);
          redirect(exchange, "/employee_statebudget.html?user=" + encodedUser);
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
      */ private static void serveStaticFile(final HttpExchange exchange, 
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
   * Serves HTML file replacing username placeholders.
   
   * @param exchange HTTP exchange

   * @param frontendPath path to frontend directory
   * @param filename html file name
   * @param username value from query string
   * @throws IOException if file missing
   */
  private static void serveHtmlWithUsername(final HttpExchange exchange,
          final String frontendPath, final String filename,
              final String username) throws IOException {
    try {
      Path filePath = Paths.get(frontendPath, filename);
      if (!Files.exists(filePath)) {
        sendErrorResponse(exchange, 404, "File not found: " + filename);
        return;
      }

      String htmlContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
      String safeUsername = (username == null || username.isBlank()) ? "Υπάλληλε" : username;
      String encodedUsername = URLEncoder.encode(safeUsername, StandardCharsets.UTF_8);

      htmlContent = htmlContent
              .replace("{{username}}", safeUsername)
              .replace("{{usernameEncoded}}", encodedUsername);

      sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
    } catch (IOException e) {
      sendErrorResponse(exchange, 500, "Error loading file: " + e.getMessage());
    }
  }

  /**
   * Extracts query parameter from request.
   * @param exchange HTTP exchange
   * @param key parameter key
   * @return decoded value or null
   */
  private static String getQueryParam(final HttpExchange exchange, final String key) {
    String rawQuery = exchange.getRequestURI().getRawQuery();
    if (rawQuery == null || rawQuery.isEmpty()) {
      return null;
    }
    String[] pairs = rawQuery.split("&");
    for (String pair : pairs) {
      String[] keyValue = pair.split("=", 2);
      if (keyValue.length == 2) {
        String decodedKey = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
        if (decodedKey.equals(key)) {
          return java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
        }
      }
    }
    return null;
  }
    
    /**
     * Sends a redirect response.
     * @param exchange HTTP exchange
     * @param location URL to redirect to
     */
  private static void redirect(final HttpExchange exchange, 
      final String location) throws IOException {
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
     * Handles year submission from the budget form.
     * @param exchange HTTP exchange
     * @param frontendPath Path to frontend directory
     * @param filename HTML file name (minister_statebudget.html or employee_statebudget.html)
     * @param username Username for employee page (null for minister)
     */
  private static void handleYearSubmission(final HttpExchange exchange,
                                            final String frontendPath,
                                            final String filename,
                                            final String username) throws IOException {
    try {
      System.out.println("[DEBUG] ========== handleYearSubmission START ==========");
      System.out.println("[DEBUG] handleYearSubmission called for: " + filename);
      System.out.println("[DEBUG] Request method: " + exchange.getRequestMethod());
      System.out.println("[DEBUG] Username from query: " + username);
            
      // Read form data
      java.io.InputStream requestBody = exchange.getRequestBody();
      byte[] buffer = new byte[1024];
      StringBuilder formDataBuilder = new StringBuilder();
      int bytesRead;
      while ((bytesRead = requestBody.read(buffer)) != -1) {
        formDataBuilder.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
      }
      String formData = formDataBuilder.toString();
      System.out.println("[DEBUG] Raw form data: " + formData);
            
      // Parse year and username from form data
      String year = null;
      String formUsername = username; // Default to query param username
      String formDataUsername = null;
      if (formData != null && !formData.isEmpty()) {
        String[] pairs = formData.split("&");
        System.out.println("[DEBUG] Number of form pairs: " + pairs.length);
        for (String pair : pairs) {
          String[] keyValue = pair.split("=", 2);
          if (keyValue.length == 2) {
            String key = java.net.URLDecoder.decode(keyValue[0],
                StandardCharsets.UTF_8);
            String value = java.net.URLDecoder.decode(keyValue[1],
                StandardCharsets.UTF_8);
            System.out.println("[DEBUG] Parsed - Key: '" + key + "', Value: '" + value + "'");
            if ("year".equals(key)) {
              year = value;
            } else if ("user".equals(key)) {
              formDataUsername = value;
            }
          }
        }
      }
            
            // Determine final username: prefer form data if it's not a placeholder, 
            // otherwise use query param
            // Note: formDataUsername is already decoded, so we can use it directly
      if (formDataUsername != null && !formDataUsername.equals("{{usernameEncoded}}") && !formDataUsername.isEmpty()) {
                // Form data contains the decoded username (from the hidden input that was already replaced)
        formUsername = formDataUsername;
        System.out.println("[DEBUG] Using username from form data (decoded): " + formUsername);
      } else if (username != null && !username.isEmpty()) {
                // Use username from query string (already decoded)
        formUsername = username;
        System.out.println("[DEBUG] Using username from query string: " + formUsername);
      } else {
        // Last resort: use default, but this should not happen if the page was loaded correctly
        formUsername = "Υπάλληλε";
        System.out.println("[DEBUG] WARNING: No username found, using default: " + formUsername);
      }
            
      System.out.println("[DEBUG] Parsed year: " + year);
      System.out.println("[DEBUG] Final username: " + formUsername);
      System.out.println("[DEBUG] Username from query string: " + username);
      System.out.println("[DEBUG] Username from form data: " + formDataUsername);
            
      // Validate year
      if (year == null || year.isEmpty()) {
        System.out.println("[DEBUG] Year validation failed - year is null or empty");
        serveYearPageWithError(exchange, frontendPath, filename, formUsername, "Παρακαλώ εισάγετε ένα έτος.");
        return;
      }
            
      // Check if this is a budget.html page (uses EnvBudgetPrinter) or statebudget.html (uses FullBudgetPrinter)
      boolean isBudgetPage = filename.contains("budget.html") && !filename.contains("statebudget");
            
      if (isBudgetPage) {
        // For budget.html pages: use EnvBudgetPrinter (supports 2023, 2024, 2025, 2026)
        if (!year.equals("2023") && !year.equals("2024") && !year.equals("2025") && !year.equals("2026")) {
          serveYearPageWithError(exchange, frontendPath, filename, formUsername, "Δεν υπάρχουν δεδομένα για το έτος " + year + ". Παρακαλώ επιλέξτε 2023, 2024, 2025 ή 2026.");
          return;
        }
                
        // Initialize budget data from JSON
        initializeBudgetData();
                
        // Capture System.out to get budget output
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        java.io.PrintStream capturedOut = new java.io.PrintStream(baos, true, StandardCharsets.UTF_8);
        System.setOut(capturedOut);
                
        try {
          envPrinter.printYearlyBudget(year);
        } finally {
          System.setOut(originalOut);
        }
                
        String budgetOutput = baos.toString(StandardCharsets.UTF_8);
                
        // Serve page with budget results
        serveYearPageWithBudget(exchange, frontendPath, filename, formUsername, year, budgetOutput);
      } else {
        // For statebudget.html pages: use FullBudgetPrinter (supports 2023, 2024, 2025, 2026)
        if (!year.equals("2023") && !year.equals("2024") && !year.equals("2025") && !year.equals("2026")) {
          serveYearPageWithError(exchange, frontendPath, filename, formUsername, "Δεν υπάρχουν δεδομένα για το έτος " + year + ". Παρακαλώ επιλέξτε 2023, 2024, 2025 ή 2026.");
          return;
        }
                
        // Get budget data
        MinistryDataInput allData = new MinistryDataInput();
        FullBudgetPrinter printer = new FullBudgetPrinter(allData);
                
        // Capture System.out to get budget output
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        java.io.PrintStream capturedOut = new java.io.PrintStream(baos, true, StandardCharsets.UTF_8);
        System.setOut(capturedOut);
                
        try {
          printer.showBudget(year);
        } finally {
          System.setOut(originalOut);
        }
                
        String budgetOutput = baos.toString(StandardCharsets.UTF_8);
                
        // Serve page with budget results
        serveYearPageWithBudget(exchange, frontendPath, filename, formUsername, year, budgetOutput);
      }
            
    } catch (Exception e) {
      System.out.println("[ERROR] Exception in handleYearSubmission: " + e.getMessage());
      e.printStackTrace();
      sendErrorResponse(exchange, 500, "Error processing year submission: " + e.getMessage());
    }
  }
    
    /**
     * Serves the year selection page with budget results.
     * @param exchange HTTP exchange
     * @param frontendPath Path to frontend directory
     * @param filename HTML file name
     * @param username Username for employee page (null for minister)
     * @param year Selected year
     * @param budgetOutput Budget output from FullBudgetPrinter
     */
  private static void serveYearPageWithBudget(final HttpExchange exchange,
                                                final String frontendPath,
                                                final String filename,
                                                final String username,
                                                final String year,
                                                final String budgetOutput) throws IOException {
    try {
      Path filePath = Paths.get(frontendPath, filename);
      if (!Files.exists(filePath)) {
        sendErrorResponse(exchange, 404, "File not found: " + filename);
        return;
      }
            
      String htmlContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            
      // Replace username placeholders if employee page (always replace, even if null)
      String safeUsername = (username == null || username.isBlank()) ? "Υπάλληλε" : username;
      String encodedUsername = URLEncoder.encode(safeUsername, StandardCharsets.UTF_8);
      System.out.println("[DEBUG] serveYearPageWithBudget - filename: " + filename + ", username param: " + username + ", safeUsername: " + safeUsername + ", encodedUsername: " + encodedUsername);
      htmlContent = htmlContent.replace("{{username}}", safeUsername);
      htmlContent = htmlContent.replace("{{usernameEncoded}}", encodedUsername);
            
      // Debug: check if the link was replaced correctly
      if (htmlContent.contains("employee_statebudget.html?user=")) {
        int linkIndex = htmlContent.indexOf("employee_statebudget.html?user=");
        int linkEnd = htmlContent.indexOf("\"", linkIndex);
        if (linkEnd > linkIndex) {
          String link = htmlContent.substring(linkIndex, linkEnd);
          System.out.println("[DEBUG] Link after replacement: " + link);
        }
      }
            
      // Convert budget output to HTML format with beautiful table
      String budgetHtml = "<div style='margin-top: 32px; padding: 0; background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%); border-radius: 12px; border: 2px solid #0d4f1c; overflow: hidden; box-shadow: 0 4px 12px rgba(13, 79, 28, 0.2);'>";
      budgetHtml += "<div style='background: linear-gradient(135deg, #0d4f1c 0%, #1b5e20 100%); padding: 20px; text-align: center;'>";
      budgetHtml += "<h3 style='color: #ffffff; margin: 0; font-size: 22px; font-weight: 600; letter-spacing: 1px;'>ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "</h3>";
      budgetHtml += "</div>";
      budgetHtml += "<div style='padding: 24px;'>";
            
      // Check if this is EnvBudgetPrinter format (has "ΤΟΜΕΑΣ" or "ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ") or FullBudgetPrinter format
      boolean isEnvFormat = budgetOutput.contains("ΤΟΜΕΑΣ:") || budgetOutput.contains("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ:");
            
      if (isEnvFormat) {
        // Parse EnvBudgetPrinter format (structured with sectors, units, entries)
        String[] lines = budgetOutput.split("\n");
        budgetHtml += "<div style='font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif;'>";
                
        String currentSector = null;
        String currentUnit = null;
        boolean inUnit = false;
                
        for (String line : lines) {
          line = line.trim();
          if (line.isEmpty() || line.startsWith("---") || line.startsWith("==") || line.contains
              ("ΑΝΑΛΥΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ")) {
            continue;
          }
                    
          // Sector header
          if (line.startsWith("ΤΟΜΕΑΣ:")) {
            if (currentSector != null) {
              budgetHtml += "</div></div>"; // Close previous sector
            }
            currentSector = line.replace("ΤΟΜΕΑΣ:", "").trim();
            budgetHtml += "<div style='margin-bottom: 24px; border: 1px solid #c8e6c9; border-radius: 8px; overflow: hidden;'>";
            budgetHtml += "<div style='background: linear-gradient(135deg, #1b5e20 0%, #0d4f1c 100%); padding: 16px; color: #ffffff; font-weight: 600; font-size: 18px;'>";
            budgetHtml += currentSector;
            budgetHtml += "</div><div style='padding: 16px;'>";
            inUnit = false;
          }
          // Unit header
          else if (line.startsWith("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ:")) {
            if (currentUnit != null && inUnit) {
              budgetHtml += "</table></div>"; // Close previous unit table
            }
            currentUnit = line.replace("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ:", "").trim();
            budgetHtml += "<div style='margin-top: 16px; margin-bottom: 12px;'>";
            budgetHtml += "<h4 style='color: #0d4f1c; font-size: 16px; font-weight: 600; margin-bottom: 8px;'>" + currentUnit + "</h4>";
            budgetHtml += "<table style='width: 100%; border-collapse: collapse;'>";
            inUnit = true;
          }
          // Entry line (starts with "-")
          else if (line.startsWith("-") && line.contains(":") && inUnit) {
            String[] parts = line.substring(1).split(":", 2);
            if (parts.length == 2) {
              String entryName = parts[0].trim();
              String amount = parts[1].trim();
              budgetHtml += "<tr style='border-bottom: 1px solid #e8e8e8;'>";
              budgetHtml += "<td style='padding: 10px 12px; color: #2e7d32; font-size: 14px;'>" + entryName + "</td>";
              budgetHtml += "<td style='padding: 10px 12px; text-align: right; color: #1b5e20; font-weight: 600; font-size: 14px;'>" + amount + "</td>";
              budgetHtml += "</tr>";
            }
          
        }
          // Unit total
          else if (line.startsWith("ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ:") && inUnit) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
              String amount = parts[1].trim();
              budgetHtml += "<tr style='background-color: #f1f8e9; border-top: 2px solid #0d4f1c;'>";
              budgetHtml += "<td style='padding: 12px; font-weight: 700; color: #0d4f1c; font-size: 15px;'>Σύνολο Μονάδας</td>";
              budgetHtml += "<td style='padding: 12px; text-align: right; font-weight: 700; color: #1b5e20; font-size: 15px;'>" + amount + "</td>";
              budgetHtml += "</tr></table></div>";
              inUnit = false;
            }
          }
          else if (line.startsWith("ΣΥΝΟΛΙΚΟ ΠΟΣΟ ΤΟΜΕΑ")) {

    String amount = line.split(":")[1].trim();

    budgetHtml += "<div style='margin-top: 16px; "
                + "padding: 12px 16px; "
                + "background-color: #f1f8e9; "
                + "border: 2px solid #0d4f1c; "
                + "border-radius: 8px; "
                + "font-weight: 700; "
                + "color: #0d4f1c; "
                + "font-size: 15px;'>"
                + "Συνολικό ποσό τομέα: "
                + "<span style='float:right; color:#1b5e20;'>"
                + amount
                + "</span>"
                + "</div>";
}

        }
                
        // Close last sector if open
        if (currentSector != null) {
          budgetHtml += "</div></div>";
        }
                
        budgetHtml += "</div>";
      } else {
        // Parse FullBudgetPrinter format (simple list format)
        String[] lines = budgetOutput.split("\n");
        budgetHtml += "<table style='width: 100%; border-collapse: collapse; font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif;'>";
        for (String line : lines) {
          line = line.trim();
          if (line.isEmpty() || line.startsWith("---") || line.startsWith("==") || line.startsWith
              ("--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ")) {
            continue;
          }
                    
          if (line.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ")) {
            budgetHtml += "<tr style='background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%); border-top: 3px solid #0d4f1c;'>";
            String[] parts = line.split(":", 2);
            if (parts.length >= 2) {
              String label = parts[0].trim().replace("*", "").trim();
              String amount = parts[1].trim();
              budgetHtml += "<td style='padding: 18px 20px; font-weight: 700; font-size: 17px; color: #0d4f1c;'>" + label + "</td>";
              budgetHtml += "<td style='padding: 18px 20px; text-align: right; font-weight: 700; font-size: 17px; color: #1b5e20;'>" + amount + "</td>";
            } else {
              budgetHtml += "<td colspan='2' style='padding: 18px 20px; font-weight: 700; font-size: 17px; color: #0d4f1c; text-align: center;'>" + line.replace("*", "").trim() + "</td>";
            }
            budgetHtml += "</tr>";
          } else if (line.contains(":") && !line.startsWith("---")) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
              String ministryName = parts[0].trim().replace("*", "").trim();
              String amount = parts[1].trim();
              budgetHtml += "<tr style='border-bottom: 1px solid #e8e8e8; transition: background-color 0.2s;'>";
              budgetHtml += "<td style='padding: 14px 20px; color: #2e7d32; font-size: 15px; line-height: 1.5;'>" + ministryName + "</td>";
              budgetHtml += "<td style='padding: 14px 20px; text-align: right; color: #1b5e20; font-weight: 600; font-size: 15px;'>" + amount + "</td>";
              budgetHtml += "</tr>";
            }
          }
        }
      budgetHtml += "</table>";
      }
      budgetHtml += "</div></div>";
            
      // Insert budget results before the closing card div 
      // (before </form> or before </div> that closes card)
      // Try multiple patterns to ensure we find the right place
      if (htmlContent.contains("</form>")) {
        htmlContent = htmlContent.replace("</form>", "</form>" + budgetHtml);
      } else if (htmlContent.contains("        </div>\n    </div>")) {
        htmlContent = htmlContent.replace("        </div>\n    </div>", budgetHtml 
            + "\n        </div>\n    </div>");
      } else if (htmlContent.contains("    </div>\n\n    <div class=\"container\"")) {
        htmlContent = htmlContent.replace("    </div>\n\n    <div class=\"container\"", budgetHtml 
            + "\n    </div>\n\n    <div class=\"container\"");
      } else {
        // Fallback: insert before the last </div> before </body>
        int lastDivIndex = htmlContent.lastIndexOf("    </div>");
        if (lastDivIndex > 0) {
          htmlContent = htmlContent.substring(0, lastDivIndex) + budgetHtml + "\n" 
              + htmlContent.substring(lastDivIndex);
        }
      }
            
      sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
    } catch (IOException e) {
      sendErrorResponse(exchange, 500, "Error loading page: " + e.getMessage());
    }
  }
    
  /**
     * Serves the year selection page with error message.
     * @param exchange HTTP exchange
     * @param frontendPath Path to frontend directory
     * @param filename HTML file name
     * @param username Username for employee page (null for minister)
     * @param errorMessage Error message to display
     */
  private static void serveYearPageWithError(final HttpExchange exchange,
                                              final String frontendPath,
                                              final String filename,
                                              final String username,
                                              final String errorMessage) throws IOException {
    try {
      Path filePath = Paths.get(frontendPath, filename);
      if (!Files.exists(filePath)) {
        sendErrorResponse(exchange, 404, "File not found: " + filename);
        return;
      }
            
      String htmlContent = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            
      // Replace username placeholders if employee page (always replace, even if null)
      String safeUsername = (username == null || username.isBlank()) ? "Υπάλληλε" : username;
      String encodedUsername = URLEncoder.encode(safeUsername, StandardCharsets.UTF_8);
      htmlContent = htmlContent.replace("{{username}}", safeUsername);
      htmlContent = htmlContent.replace("{{usernameEncoded}}", encodedUsername);
            
      // Add subtle error message
      String errorHtml = "<div style='margin-top: 24px; padding: 16px 20px; background-color: #fff3e0; border-radius: 8px; border: 1px solid #ffb74d; box-shadow: 0 2px 8px rgba(255, 183, 77, 0.15);'>";
      errorHtml += "<p style='color: #e65100; font-weight: 500; margin: 0; font-size: 15px; text-align: center; line-height: 1.6;'>";
      errorHtml += "<span style='margin-right: 8px;'>⚠</span>" + errorMessage;
      errorHtml += "</p></div>";
            
      // Insert error message before the closing card div
      if (htmlContent.contains("</form>")) {
        htmlContent = htmlContent.replace("</form>", "</form>" + errorHtml);
      } else if (htmlContent.contains("        </div>\n    </div>")) {
        htmlContent = htmlContent.replace("        </div>\n    </div>", errorHtml  
            +"\n        </div>\n    </div>");
      } else if (htmlContent.contains("    </div>\n\n    <div class=\"container\"")) {
        htmlContent = htmlContent.replace("    </div>\n\n    <div class=\"container\"", errorHtml 
            + "\n    </div>\n\n    <div class=\"container\"");
      } else {
        // Fallback: insert before the last </div> before </body>
        int lastDivIndex = htmlContent.lastIndexOf("    </div>");
        if (lastDivIndex > 0) {
          htmlContent = htmlContent.substring(0, lastDivIndex) + errorHtml + "\n" + htmlContent.substring(lastDivIndex);
        }
      }
            
      sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
    } catch (IOException e) {
      sendErrorResponse(exchange, 500, "Error loading page: " + e.getMessage());
    }
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
