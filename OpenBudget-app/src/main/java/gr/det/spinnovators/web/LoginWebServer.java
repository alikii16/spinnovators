package gr.det.spinnovators.web;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import gr.det.spinnovators.data.MinistryDataInput;
import gr.det.spinnovators.envdatamodel.EnvBudgetData;
import gr.det.spinnovators.envdatamodel.EnvEntry;
import gr.det.spinnovators.envdatamodel.EnvSector;
import gr.det.spinnovators.envdatamodel.EnvUnit;
import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.printer.EnvBudgetPrinter;
import gr.det.spinnovators.printer.FullBudgetPrinter;
import gr.det.spinnovators.service.EnvBudgetLoader;
import gr.det.spinnovators.service.EnvBudgetTranslator;

/**
 * Web server class to handle login functionality via HTTP.
 * Uses singleton pattern with static methods. Thread-local storage for session management.
 * Handles: authentication, budget queries/display, budget modification workflow.
 */
public final class LoginWebServer {
  // CONFIGURATION CONSTANTS
  private static final int PORT = 8080;
  // Note: Hardcoded credentials for demo/educational use only. For production, use secure authentication.
  private static final String MINISTER = "Minister";
  private static final String PASSWORD_MINISTER = "m1n1st3r";
  private static final String PASSWORD_EMPLOYEE = "3mpl0y33";
  private static final String[] VALID_BUDGET_YEARS = {"2023", "2024", "2025", "2026"};
  private static final String[] VALID_CHANGE_YEARS = {"2025", "2026"};
  private static final double BUDGET_2025 = 2341227000.00;
  private static final double BUDGET_2026 = 3133452000.00;

  private static double getBudgetAmountForYear(String year) {
    if (VALID_CHANGE_YEARS[0].equals(year)) return BUDGET_2025;
    if (VALID_CHANGE_YEARS[1].equals(year)) return BUDGET_2026;
    return 0.0;
  }

  // STATIC STATE (Singleton Pattern - Shared Across All Requests)
  private static EnvBudgetData envBudgetData = null;
  private static EnvBudgetTranslator translator = null;
  private static EnvBudgetPrinter envPrinter = null;

  // CHANGE-BUDGET SESSION + STATE (ThreadLocal for thread safety)
  private enum ChangeState {
      START,
      SELECT_YEAR,
      SELECT_SECTOR,
      SELECT_UNIT,
      SELECT_ENTRY,
      EDIT_VALUE,
      CONFIRM_EXTREME,
      FINISH
  }

  private static class ChangeSession {
      public ChangeState state = ChangeState.START;

      public EnvYear envYear;

      public EnvSector selectedSector;
      public EnvUnit selectedUnit;
      public EnvEntry selectedEntry;

      public double totalBudget = 0.0;
      public double currentBalance = 0.0;
      public double oldValue;
      public Double pendingValue;
  }

  private static final ThreadLocal<ChangeSession> changeSessionThreadLocal =
      ThreadLocal.withInitial(ChangeSession::new);

  private static ChangeSession getChangeSession() {
    return changeSessionThreadLocal.get();
  }

  private static void resetChangeSession() {
    changeSessionThreadLocal.set(new ChangeSession());
  }

  private LoginWebServer() {
  }

  private static void initializeBudgetData() {
    if (envBudgetData == null) {
      EnvBudgetLoader envLoader = new EnvBudgetLoader();
      envBudgetData = envLoader.loadBudget();
      translator = new EnvBudgetTranslator();
      envPrinter = new EnvBudgetPrinter(envBudgetData, translator);
    }
  }

  public static void startServer(final String frontendPath) throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

    server.createContext("/", exchange -> {
        if ("GET".equals(exchange.getRequestMethod())) {
            serveLoginPage(exchange, frontendPath, null);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handleLoginPost(exchange, frontendPath);
        }
    });

    server.createContext("/login.html", exchange -> {
        if ("GET".equals(exchange.getRequestMethod())) {
            serveLoginPage(exchange, frontendPath, null);
        } else if ("POST".equals(exchange.getRequestMethod())) {
            handleLoginPost(exchange, frontendPath);
        }
    });

    server.createContext("/login", exchange -> {
        if ("POST".equals(exchange.getRequestMethod())) {
            handleLoginPost(exchange, frontendPath);
        } else {
            redirect(exchange, "/login.html");
        }
    });

    server.createContext("/minister_statebudget.html", exchange -> {
        if ("POST".equals(exchange.getRequestMethod())) {
            handleYearSubmission(exchange, frontendPath, "minister_statebudget.html", null);
        } else {
            serveStaticFile(exchange, frontendPath, "minister_statebudget.html");
        }
    });

    server.createContext("/employee_statebudget.html", exchange -> {
        String username = getQueryParam(exchange, "user");

        if ("POST".equals(exchange.getRequestMethod())) {
            handleYearSubmission(exchange, frontendPath, "employee_statebudget.html", username);
        } else {
            serveHtmlWithUsername(exchange, frontendPath, "employee_statebudget.html", username);
        }
    });

    server.createContext("/minister_budget.html", exchange -> {
        if ("POST".equals(exchange.getRequestMethod())) {
            handleYearSubmission(exchange, frontendPath, "minister_budget.html", null);
        } else {

            serveStaticFile(exchange, frontendPath, "minister_budget.html");
        }
    });

    server.createContext("/employee_budget.html", exchange -> {
        String username = getQueryParam(exchange, "user");

        if ("POST".equals(exchange.getRequestMethod())) {
            handleYearSubmission(exchange, frontendPath, "employee_budget.html", username);
        } else {
            serveHtmlWithUsername(exchange, frontendPath, "employee_budget.html", username);
        }
    });
server.createContext("/change-budget", exchange -> {
    if ("GET".equals(exchange.getRequestMethod())) {
        serveChangeBudgetPage(exchange, frontendPath);
    } else if ("POST".equals(exchange.getRequestMethod())) {
        handleChangeBudgetPost(exchange, frontendPath);
    }
});

    server.setExecutor(null);
    server.start();

    System.out.println("Server started: http://localhost:" + PORT);
  }

  private static void serveLoginPage(final HttpExchange exchange,
                                     final String frontendPath,
                                     final String errorMessage) throws IOException {
    try {
      Path htmlPath = Paths.get(frontendPath, "login.html");
      if (!Files.exists(htmlPath)) {
        sendErrorResponse(exchange, 404, "login.html not found at: " + htmlPath);
        return;
      }

      String htmlContent = new String(Files.readAllBytes(htmlPath), StandardCharsets.UTF_8);
      if (errorMessage != null && !errorMessage.isEmpty()) {
        htmlContent = htmlContent.replace(
            "class=\"message error-message error-message-hidden\"",
            "class=\"message error-message\"");
        htmlContent = htmlContent.replace("Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά.", errorMessage);
      } else if (!htmlContent.contains("error-message-hidden")) {
        htmlContent = htmlContent.replace(
            "class=\"message error-message\"",
            "class=\"message error-message error-message-hidden\"");
      }
      sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
    } catch (IOException e) {
      sendErrorResponse(exchange, 500,
          "Unable to load login page. Please check if login.html exists in the frontend directory.", e);
    }
  }

  private static void handleLoginPost(final HttpExchange exchange,
                                      final String frontendPath) throws IOException {
    try {
      java.util.Map<String, String> formData = parseFormData(exchange);
      String username = formData.getOrDefault("username", null);
      String password = formData.getOrDefault("password", null);
      if (username != null && password != null) {
        if (username.equals(MINISTER) && password.equals(PASSWORD_MINISTER)) {
          redirect(exchange, "/minister_statebudget.html");
          return;
        } else if (!username.equals(MINISTER) && password.equals(PASSWORD_EMPLOYEE)) {
          String encodedUser = URLEncoder.encode(username, StandardCharsets.UTF_8);
          redirect(exchange, "/employee_statebudget.html?user=" + encodedUser);
          return;
        }
      }
      String errorMessage = "Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά.";
      serveLoginPage(exchange, frontendPath, errorMessage);
    } catch (Exception e) {
      sendErrorResponse(exchange, 500, "Error processing login request", e);
    }
  }

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
      sendErrorResponse(exchange, 500, "Error loading file: " + filename, e);
    }
  }

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
      htmlContent = replaceUsernamePlaceholdersSafe(htmlContent, username, filename);
      sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
    } catch (IOException e) {
      sendErrorResponse(exchange, 500,
          "Unable to read or process file '" + filename + "'. Please check file permissions and try again.", e);
    }
  }

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

  private static void redirect(final HttpExchange exchange,
      final String location) throws IOException {
    exchange.getResponseHeaders().set("Location", location);
    exchange.sendResponseHeaders(302, -1);
    exchange.close();
  }

  private static String buildStyledChangePage(String innerCardHtml, String extraBelowCard) {
    String styles = """
        <style>
            * { margin:0; padding:0; box-sizing:border-box; }
            body {
                background: linear-gradient(135deg, #1b5e20 0%, #0d4f1c 25%, #0a3d15 50%, #0d4f1c 75%, #1b5e20 100%);
                background-color: #0a3d15;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                min-height: 100vh;
                display: flex;
                flex-direction: column;
                align-items: center;
                padding: 20px;
                position: relative;
            }
            body::before {
                content: '';
                position: absolute;
                inset: 0;
                background:
                    radial-gradient(circle at 15% 40%, rgba(13, 79, 28, 0.18) 0%, transparent 45%),
                    radial-gradient(circle at 80% 70%, rgba(5, 26, 10, 0.25) 0%, transparent 55%);
                pointer-events: none;
                z-index: 0;
            }
            .ministry-header {
                text-align: center;
                margin-top: 30px;
                margin-bottom: 40px;
                width: 100%;
                position: relative;
                z-index: 1;
            }
            .ministry-header h1 {
                color: #e8f5e9;
                font-size: 34px;
                font-weight: 600;
                letter-spacing: 2px;
                text-shadow: 0 2px 4px rgba(0, 0, 0, 0.35);
                text-transform: uppercase;
                line-height: 1.2;
            }
            .container {
                width: 100%;
                max-width: 640px;
                display: flex;
                justify-content: center;
                align-items: center;
                position: relative;
                z-index: 1;
            }
            .card {
                width: 100%;
                background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%);
                border-radius: 20px;
                padding: 40px 36px;
                box-shadow:
                    0 12px 48px rgba(0, 0, 0, 0.35),
                    inset 0 1px 0 rgba(255, 255, 255, 0.9),
                    0 0 0 2px rgba(13, 79, 28, 0.2);
                border: 3px solid #0d4f1c;
            }
            .section-title {
                text-align: center;
                font-size: 24px;
                font-weight: 700;
                color: #0d4f1c;
                margin-bottom: 16px;
            }
            .description {
                text-align: center;
                font-size: 16px;
                color: #2e7d32;
                margin-bottom: 18px;
            }
            .input-box, input[type=number], select {
                width: 100%;
                padding: 14px;
                border: 2px solid #a5d6a7;
                border-radius: 8px;
                font-size: 16px;
                background: #fafafa;
                transition: border-color 0.25s, box-shadow 0.25s;
                margin-top: 10px;
            }
            .input-box:focus, input[type=number]:focus, select:focus {
                outline: none;
                border-color: #1b5e20;
                box-shadow: 0 0 0 3px rgba(27, 94, 32, 0.15);
                background: #fff;
            }
            .primary-btn, .secondary-btn, .wide-btn {
                display: inline-block;
                width: 100%;
                padding: 15px;
                background: linear-gradient(135deg, #0d4f1c 0%, #1b5e20 100%);
                color: #ffffff;
                text-align: center;
                text-decoration: none;
                font-size: 17px;
                font-weight: 600;
                border-radius: 10px;
                border: none;
                cursor: pointer;
                box-shadow: 0 6px 18px rgba(13, 79, 28, 0.4);
                transition: transform 0.2s ease, box-shadow 0.2s ease;
                margin-top: 14px;
            }
            .primary-btn:hover, .secondary-btn:hover, .wide-btn:hover {
                transform: translateY(-2px);
                box-shadow: 0 10px 24px rgba(13, 79, 28, 0.45);
            }
            .secondary-btn {
                background: linear-gradient(135deg, #558b2f 0%, #33691e 100%);
                box-shadow: 0 6px 18px rgba(85, 139, 47, 0.4);
            }
            .choice-group {
                display: flex;
                gap: 12px;
                flex-wrap: wrap;
                margin-top: 10px;
            }
            .error-box {
                margin-top: 16px;
                padding: 14px 16px;
                background: #fff3e0;
                border: 1px solid #ffb74d;
                border-radius: 10px;
                color: #e65100;
                font-weight: 600;
                text-align: center;
            }
        </style>
        """;

    String header = """
        <div class="ministry-header">
            <h1>ΥΠΟΥΡΓΕΙΟ ΠΕΡΙΒΑΛΛΟΝΤΟΣ ΚΑΙ ΕΝΕΡΓΕΙΑΣ</h1>
        </div>
        """;

    String extra = (extraBelowCard == null || extraBelowCard.isBlank()) ? "" : extraBelowCard;

    return "<!DOCTYPE html><html lang='el'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "<title>Αλλαγή Προϋπολογισμού</title>" + styles + "</head><body>"
            + header
            + "<div class='container'><div class='card'>"
            + innerCardHtml
            + "</div></div>"
            + extra
            + "</body></html>";
  }

  private static void sendResponse(final HttpExchange exchange,
                                     final String response,
                                     final int statusCode,
                                     final String contentType) throws IOException {
    exchange.getResponseHeaders().set("Content-Type", contentType);
    byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
    exchange.sendResponseHeaders(statusCode, responseBytes.length);
    try (OutputStream os = exchange.getResponseBody()) {
      os.write(responseBytes);
    }
  }

  private static void handleYearSubmission(final HttpExchange exchange,
                                            final String frontendPath,
                                            final String filename,
                                            final String username) throws IOException {
    try {
      java.util.Map<String, String> formDataMap = parseFormData(exchange);
      String year = formDataMap.get("year");
      String formDataUsername = formDataMap.get("user");
      String formUsername = determineUsername(username, formDataUsername);
      if (year == null || year.isEmpty()) {
        serveYearPageWithError(exchange, frontendPath, filename, formUsername, "Παρακαλώ εισάγετε ένα έτος.");
        return;
      }
      boolean isBudgetPage = filename.contains("budget.html") && !filename.contains("statebudget");
      if (!isValidYear(year, VALID_BUDGET_YEARS)) {
        serveYearPageWithError(exchange, frontendPath, filename, formUsername,
            "Δεν υπάρχουν δεδομένα για το έτος " + year + ". Παρακαλώ επιλέξτε 2023, 2024, 2025 ή 2026.");
        return;
      }
      String budgetOutput = isBudgetPage ? captureEnvBudgetOutput(year) : captureFullBudgetOutput(year);
      serveYearPageWithBudget(exchange, frontendPath, filename, formUsername, year, budgetOutput);
    } catch (IOException e) {
      sendErrorResponse(exchange, 500, "An error occurred while processing your year submission. Please try again.", e);
    } catch (Exception e) {
      sendErrorResponse(exchange, 500, "An unexpected error occurred while processing your request. Please contact support if the problem persists.", e);
    }
  }

  private static String determineUsername(String queryUsername, String formDataUsername) {
    if (formDataUsername != null && !formDataUsername.equals("{{usernameEncoded}}") && !formDataUsername.isEmpty()) {
      logDebug("Using username from form data (decoded): " + formDataUsername);
      return formDataUsername;
    } else if (queryUsername != null && !queryUsername.isEmpty()) {
      logDebug("Using username from query string: " + queryUsername);
      return queryUsername;
    } else {
      String defaultUsername = "Υπάλληλε";
      logDebug("WARNING: No username found, using default: " + defaultUsername);
      return defaultUsername;
    }
  }

  // NOTE: System.out redirection is not thread-safe. Ideally, printers should return String directly.
  private static String captureEnvBudgetOutput(String year) {
    initializeBudgetData();
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    java.io.PrintStream capturedOut = new java.io.PrintStream(baos, true, StandardCharsets.UTF_8);
    System.setOut(capturedOut);
    try {
      envPrinter.printYearlyBudget(year);
    } finally {
      System.setOut(originalOut);
    }
    return baos.toString(StandardCharsets.UTF_8);
  }

  // NOTE: System.out redirection is not thread-safe. Ideally, printers should return String directly.
  private static String captureFullBudgetOutput(String year) {
    MinistryDataInput allData = new MinistryDataInput();
    FullBudgetPrinter printer = new FullBudgetPrinter(allData);
    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
    java.io.PrintStream originalOut = System.out;
    java.io.PrintStream capturedOut = new java.io.PrintStream(baos, true, StandardCharsets.UTF_8);
    System.setOut(capturedOut);
    try {
      printer.showBudget(year);
    } finally {
      System.setOut(originalOut);
    }
    return baos.toString(StandardCharsets.UTF_8);
  }

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
      htmlContent = replaceUsernamePlaceholders(htmlContent, username, filename);
      String budgetHtml = parseBudgetOutputToHtml(budgetOutput, year);
      htmlContent = insertBudgetHtmlIntoContent(htmlContent, budgetHtml);
      sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
    } catch (IOException e) {
      sendErrorResponse(exchange, 500, "Error loading page", e);
    }
  }


  private static String replaceUsernamePlaceholders(String htmlContent, String username, String filename) {
    return replaceUsernamePlaceholdersSafe(htmlContent, username, filename);
  }

  private static String parseBudgetOutputToHtml(String budgetOutput, String year) {
    String budgetHtml = buildBudgetHtmlHeader(year);
    boolean isEnvFormat = budgetOutput.contains("ΤΟΜΕΑΣ:") || budgetOutput.contains("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ:");
    if (isEnvFormat) {
      budgetHtml += parseEnvBudgetFormatToHtml(budgetOutput);
    } else {
      budgetHtml += parseFullBudgetFormatToHtml(budgetOutput);
    }
    budgetHtml += "</div></div>";
    return budgetHtml;
  }

  private static String buildBudgetHtmlHeader(String year) {
      String budgetHtml = "<div style='margin-top: 32px; padding: 0; background: linear-gradient(135deg, #ffffff 0%, #f9f9f9 100%); border-radius: 12px; border: 2px solid #0d4f1c; overflow: hidden; box-shadow: 0 4px 12px rgba(13, 79, 28, 0.2);'>";
      budgetHtml += "<div style='background: linear-gradient(135deg, #0d4f1c 0%, #1b5e20 100%); padding: 20px; text-align: center;'>";
      budgetHtml += "<h3 style='color: #ffffff; margin: 0; font-size: 22px; font-weight: 600; letter-spacing: 1px;'>ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "</h3>";
      budgetHtml += "</div>";
      budgetHtml += "<div style='padding: 24px;'>";
    return budgetHtml;
  }

  private static String parseEnvBudgetFormatToHtml(String budgetOutput) {
    String[] lines = budgetOutput.split("\n");
    StringBuilder budgetHtml = new StringBuilder();
    budgetHtml.append("<div style='font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif;'>");
    String currentSector = null;
    String currentUnit = null;
    boolean inUnit = false;
    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty() || line.startsWith("---") || line.startsWith("==") || line.contains("ΑΝΑΛΥΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ")) {
        continue;
      }
      if (line.startsWith("ΤΟΜΕΑΣ:")) {
            if (currentSector != null) {
          budgetHtml.append("</div></div>"); // Close previous sector
            }
            currentSector = line.replace("ΤΟΜΕΑΣ:", "").trim();
        budgetHtml.append("<div style='margin-bottom: 24px; border: 1px solid #c8e6c9; border-radius: 8px; overflow: hidden;'>");
        budgetHtml.append("<div style='background: linear-gradient(135deg, #1b5e20 0%, #0d4f1c 100%); padding: 16px; color: #ffffff; font-weight: 600; font-size: 18px;'>");
        budgetHtml.append(currentSector);
        budgetHtml.append("</div><div style='padding: 16px;'>");
        inUnit = false;
      } else if (line.startsWith("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ:")) {
            if (currentUnit != null && inUnit) {
          budgetHtml.append("</table></div>"); // Close previous unit table
            }
            currentUnit = line.replace("ΕΚΤΕΛΕΣΤΙΚΗ ΜΟΝΑΔΑ:", "").trim();
        budgetHtml.append("<div style='margin-top: 16px; margin-bottom: 12px;'>");
        budgetHtml.append("<h4 style='color: #0d4f1c; font-size: 16px; font-weight: 600; margin-bottom: 8px;'>").append(currentUnit).append("</h4>");
        budgetHtml.append("<table style='width: 100%; border-collapse: collapse;'>");
        inUnit = true;
      } else if (line.startsWith("-") && line.contains(":") && inUnit) {
            String[] parts = line.substring(1).split(":", 2);
            if (parts.length == 2) {
              String entryName = parts[0].trim();
              String amount = parts[1].trim();
          budgetHtml.append("<tr style='border-bottom: 1px solid #e8e8e8;'>");
          budgetHtml.append("<td style='padding: 10px 12px; color: #2e7d32; font-size: 14px;'>").append(entryName).append("</td>");
          budgetHtml.append("<td style='padding: 10px 12px; text-align: right; color: #1b5e20; font-weight: 600; font-size: 14px;'>").append(amount).append("</td>");
          budgetHtml.append("</tr>");
        }
      } else if (line.startsWith("ΣΥΝΟΛΟ ΜΟΝΑΔΑΣ:") && inUnit) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
              String amount = parts[1].trim();
          budgetHtml.append("<tr style='background-color: #f1f8e9; border-top: 2px solid #0d4f1c;'>");
          budgetHtml.append("<td style='padding: 12px; font-weight: 700; color: #0d4f1c; font-size: 15px;'>Σύνολο Μονάδας</td>");
          budgetHtml.append("<td style='padding: 12px; text-align: right; font-weight: 700; color: #1b5e20; font-size: 15px;'>").append(amount).append("</td>");
          budgetHtml.append("</tr></table></div>");
          inUnit = false;
        }
      } else if (line.startsWith("ΣΥΝΟΛΙΚΟ ΠΟΣΟ ΤΟΜΕΑ")) {
    String amount = line.split(":")[1].trim();
        budgetHtml.append("<div style='margin-top: 16px; padding: 12px 16px; background-color: #f1f8e9; border: 2px solid #0d4f1c; border-radius: 8px; font-weight: 700; color: #0d4f1c; font-size: 15px;'>");
        budgetHtml.append("Συνολικό ποσό τομέα: <span style='float:right; color:#1b5e20;'>").append(amount).append("</span>");
        budgetHtml.append("</div>");
      }
    }
    if (currentSector != null) {
      budgetHtml.append("</div></div>");
    }
    budgetHtml.append("</div>");
    return budgetHtml.toString();
  }

  private static String parseFullBudgetFormatToHtml(String budgetOutput) {
    String[] lines = budgetOutput.split("\n");
    StringBuilder budgetHtml = new StringBuilder();
    budgetHtml.append("<table style='width: 100%; border-collapse: collapse; font-family: \"Segoe UI\", Tahoma, Geneva, Verdana, sans-serif;'>");
    for (String line : lines) {
      line = line.trim();
      if (line.isEmpty() || line.startsWith("---") || line.startsWith("==") || line.startsWith("--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ")) {
        continue;
      }
      if (line.contains("ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ")) {
        budgetHtml.append("<tr style='background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%); border-top: 3px solid #0d4f1c;'>");
            String[] parts = line.split(":", 2);
            if (parts.length >= 2) {
              String label = parts[0].trim().replace("*", "").trim();
              String amount = parts[1].trim();
          budgetHtml.append("<td style='padding: 18px 20px; font-weight: 700; font-size: 17px; color: #0d4f1c;'>").append(label).append("</td>");
          budgetHtml.append("<td style='padding: 18px 20px; text-align: right; font-weight: 700; font-size: 17px; color: #1b5e20;'>").append(amount).append("</td>");
            } else {
          budgetHtml.append("<td colspan='2' style='padding: 18px 20px; font-weight: 700; font-size: 17px; color: #0d4f1c; text-align: center;'>").append(line.replace("*", "").trim()).append("</td>");
            }
        budgetHtml.append("</tr>");
      } else if (line.contains(":") && !line.startsWith("---")) {
        String[] parts = line.split(":", 2);
        if (parts.length == 2) {
          String ministryName = parts[0].trim().replace("*", "").trim();
          String amount = parts[1].trim();
          budgetHtml.append("<tr style='border-bottom: 1px solid #e8e8e8; transition: background-color 0.2s;'>");
          budgetHtml.append("<td style='padding: 14px 20px; color: #2e7d32; font-size: 15px; line-height: 1.5;'>").append(ministryName).append("</td>");
          budgetHtml.append("<td style='padding: 14px 20px; text-align: right; color: #1b5e20; font-weight: 600; font-size: 15px;'>").append(amount).append("</td>");
          budgetHtml.append("</tr>");
        }
      }
    }
    budgetHtml.append("</table>");
    return budgetHtml.toString();
  }

  private static String insertBudgetHtmlIntoContent(String htmlContent, String budgetHtml) {
    if (htmlContent.contains("</form>")) {
      return htmlContent.replace("</form>", "</form>" + budgetHtml);
    } else if (htmlContent.contains("        </div>\n    </div>")) {
      return htmlContent.replace("        </div>\n    </div>", budgetHtml + "\n        </div>\n    </div>");
    } else if (htmlContent.contains("    </div>\n\n    <div class=\"container\"")) {
      return htmlContent.replace("    </div>\n\n    <div class=\"container\"", budgetHtml + "\n    </div>\n\n    <div class=\"container\"");
    } else {
      int lastDivIndex = htmlContent.lastIndexOf("    </div>");
      if (lastDivIndex > 0) {
        return htmlContent.substring(0, lastDivIndex) + budgetHtml + "\n" + htmlContent.substring(lastDivIndex);
      }
    }
    return htmlContent;
  }

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
      htmlContent = replaceUsernamePlaceholdersSafe(htmlContent, username, filename);
      String errorHtml = "<div style='margin-top: 24px; padding: 16px 20px; background-color: #fff3e0; border-radius: 8px; border: 1px solid #ffb74d; box-shadow: 0 2px 8px rgba(255, 183, 77, 0.15);'>";
      errorHtml += "<p style='color: #e65100; font-weight: 500; margin: 0; font-size: 15px; text-align: center; line-height: 1.6;'>";
      errorHtml += "<span style='margin-right: 8px;'>⚠</span>" + errorMessage;
      errorHtml += "</p></div>";
      if (htmlContent.contains("</form>")) {
        htmlContent = htmlContent.replace("</form>", "</form>" + errorHtml);
      } else if (htmlContent.contains("        </div>\n    </div>")) {
        htmlContent = htmlContent.replace("        </div>\n    </div>", errorHtml
            +"\n        </div>\n    </div>");
      } else if (htmlContent.contains("    </div>\n\n    <div class=\"container\"")) {
        htmlContent = htmlContent.replace("    </div>\n\n    <div class=\"container\"", errorHtml + "\n    </div>\n\n    <div class=\"container\"");
      } else {
        int lastDivIndex = htmlContent.lastIndexOf("    </div>");
        if (lastDivIndex > 0) {
          htmlContent = htmlContent.substring(0, lastDivIndex) + errorHtml + "\n" + htmlContent.substring(lastDivIndex);
        }
      }
      sendResponse(exchange, htmlContent, 200, "text/html; charset=UTF-8");
    } catch (IOException e) {
      sendErrorResponse(exchange, 500, "Error loading page", e);
    }
  }

  private static void sendErrorResponse(final HttpExchange exchange,
                                         final int statusCode,
                                         final String message) throws IOException {
    logError("HTTP " + statusCode + ": " + message);
    String errorHtml = "<html><head><meta charset='UTF-8'><title>Error " + statusCode + "</title></head>"
        + "<body style='font-family: Arial; padding: 40px; text-align: center;'>"
        + "<h1>Error " + statusCode + "</h1><p>" + escapeHtml(message) + "</p></body></html>";
    sendResponse(exchange, errorHtml, statusCode, "text/html; charset=UTF-8");
  }

  private static void sendErrorResponse(final HttpExchange exchange,
                                         final int statusCode,
                                         final String userMessage,
                                         final Exception exception) throws IOException {
    logError("HTTP " + statusCode + ": " + userMessage, exception);
    sendErrorResponse(exchange, statusCode, userMessage);
  }

  private static void logError(String message) {
    System.err.println("[ERROR] " + message);
  }

  private static void logError(String message, Exception exception) {
    System.err.println("[ERROR] " + message);
    if (exception != null) exception.printStackTrace(System.err);
  }

  private static void logDebug(String message) {
    System.out.println("[DEBUG] " + message);
  }

  private static void serveChangeBudgetPage(HttpExchange exchange, String frontendPath) throws IOException {
    initializeBudgetData();

    String inner = """
        <h2 class='section-title'>Θέλετε να κάνετε αλλαγή στον προϋπολογισμό;</h2>
        <form method='POST'>
            <div class='choice-group'>
                <button class='primary-btn' name='answer' value='yes' style='flex:1;'>ΝΑΙ</button>
                <button class='secondary-btn' name='answer' value='no' style='flex:1;'>ΟΧΙ</button>
            </div>
        </form>
        """;

    String html = buildStyledChangePage(inner, "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
}


  private static void handleChangeBudgetPost(HttpExchange exchange, String frontendPath) throws IOException {
    java.util.Map<String, String> formDataMap = parseFormData(exchange);
    String answer = formDataMap.get("answer");

    if ("no".equals(answer)) {
        redirect(exchange, "/minister_statebudget.html");
        return;
    }

    if ("yes".equals(answer)) {
        resetChangeSession();
        ChangeSession changeSession = getChangeSession();
        changeSession.state = ChangeState.SELECT_YEAR;
        serveYearQuestion(exchange);
        return;
    }
    String formDataString = convertFormDataMapToString(formDataMap);

    ChangeSession changeSession = getChangeSession();
    switch (changeSession.state) {
        case SELECT_YEAR -> handleYearInput(exchange, formDataString);
        case SELECT_SECTOR -> handleSectorInput(exchange, formDataString);
        case SELECT_UNIT -> handleUnitInput(exchange, formDataString);
        case SELECT_ENTRY -> handleEntryInput(exchange, formDataString);
        case EDIT_VALUE -> handleValueChange(exchange, formDataString);
        case CONFIRM_EXTREME -> handleExtremeConfirmation(exchange, formDataString);
        default -> serveChangeBudgetPage(exchange, frontendPath);
    }
  }

  private static void serveYearQuestion(HttpExchange exchange) throws IOException {

    String inner = """
        <h2 class='section-title'>Εισάγετε το έτος</h2>
        <p class='description'>(Επιτρέπονται: 2025 ή 2026)</p>
        <form method='POST'>
            <input class='input-box' type='number' name='year' placeholder='π.χ. 2025' required>
            <button class='primary-btn' type='submit'>Συνέχεια</button>
        </form>
        """;

    String html = buildStyledChangePage(inner, "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
  }

  private static void handleYearInput(HttpExchange exchange, String formData) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String year = extractFormValue(formData, "year");

    if (!isValidYear(year, VALID_CHANGE_YEARS)) {
        serveErrorPage(exchange, "Το έτος πρέπει να είναι 2025 ή 2026.");
        return;
    }

    changeSession.envYear = envBudgetData.getBudgetForYear(year);
    changeSession.totalBudget = getBudgetAmountForYear(year);
    changeSession.currentBalance = 0.0;

    changeSession.state = ChangeState.SELECT_SECTOR;
    serveSectorQuestion(exchange, null);
  }

  private static void serveSectorQuestion(HttpExchange exchange, String infoMessage) throws IOException {
    ChangeSession changeSession = getChangeSession();

    StringBuilder buttons = new StringBuilder();
    buttons.append("<h2 class='section-title'>Επιλέξτε Τομέα</h2>");
    if (infoMessage != null && !infoMessage.isBlank()) {
        buttons.append("<div class='error-box' style='background:#e8f5e9; border:1px solid #a5d6a7; color:#1b5e20; margin-bottom:12px;'>")
               .append(infoMessage)
               .append("</div>");
    }
    buttons.append("<form method='POST' style='display:flex; flex-direction:column; gap:10px;'>");
    for (EnvSector s : changeSession.envYear.getSectors()) {
        String name = translator.translateCategory(s.getJsonKey());
        buttons.append("<button class='primary-btn' name='sector' value='")
               .append(name).append("'>").append(name).append("</button>");
    }
    buttons.append("<button class='secondary-btn' name='sector' value='__end__'>ΤΕΛΟΣ / ΕΛΕΓΧΟΣ ΙΣΟΣΚΕΛΙΣΜΟΥ</button>");
    buttons.append("</form>");

    String html = buildStyledChangePage(buttons.toString(), "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
}


private static void handleSectorInput(HttpExchange exchange, String formData) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String chosen = extractFormValue(formData, "sector");

    if ("__end__".equals(chosen)) {
        handleEndAttempt(exchange);
        return;
    }

    for (EnvSector s : changeSession.envYear.getSectors()) {
        if (translator.translateCategory(s.getJsonKey()).equals(chosen)) {
            changeSession.selectedSector = s;
            changeSession.state = ChangeState.SELECT_UNIT;
            serveUnitQuestion(exchange);
            return;
        }
    }

    serveErrorPage(exchange, "Ο τομέας δεν βρέθηκε.");
  }

  private static void handleEndAttempt(HttpExchange exchange) throws IOException {
    ChangeSession changeSession = getChangeSession();

    if (Math.abs(changeSession.currentBalance) < 0.01) {
        String msg = "Ο προϋπολογισμός είναι ισοσκελισμένος! Τερματισμός Λειτουργίας.";
        serveEndMessage(exchange, msg, true);
    } else {
        String msg = "!!! ΠΡΟΣΟΧΗ !!! Δεν επιτρέπεται τερματισμός. Ο προϋπολογισμός δεν ισοσκελίστηκε. Πρέπει να καλύψετε διαφορά: "
                + String.format("%,.2f €", changeSession.currentBalance);
        changeSession.state = ChangeState.SELECT_SECTOR;
        serveSectorQuestion(exchange, msg);
    }
  }

  private static void serveEndMessage(HttpExchange exchange, String message, boolean success) throws IOException {
    String color = success ? "#1b5e20" : "#b71c1c";
    String buttonHtml;
    if (success) {
        buttonHtml = "<a class='primary-btn' href='/minister_statebudget.html' style='text-decoration:none; margin-top:18px;'>Επιστροφή</a>";
    } else {
        buttonHtml = """
            <form method='POST' style='margin-top:18px;'>
                <button class='primary-btn' type='submit' name='continue' value='yes'>Συνέχεια Αλλαγών</button>
            </form>
            """;
    }
    String inner = """
        <h2 class='section-title'>Έλεγχος Ισοσκελισμού</h2>
        <div class='description' style='color:%s; font-weight:700;'>%s</div>
        %s
        """.formatted(color, message, buttonHtml);
    String html = buildStyledChangePage(inner, "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
  }

  private static void serveUnitQuestion(HttpExchange exchange) throws IOException {
    ChangeSession changeSession = getChangeSession();

    StringBuilder buttons = new StringBuilder();
    buttons.append("<h2 class='section-title'>Επιλέξτε Μονάδα</h2>");
    buttons.append("<form method='POST'>");
    for (EnvUnit u : changeSession.selectedSector.getUnits()) {
        String name = translator.translateCategory(u.getJsonKey());
        buttons.append("<button class='primary-btn' name='unit' value='")
               .append(name).append("'>").append(name).append("</button>");
    }
    buttons.append("</form>");

    String html = buildStyledChangePage(buttons.toString(), "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
}


private static void handleUnitInput(HttpExchange exchange, String formData) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String chosen = extractFormValue(formData, "unit");

    for (EnvUnit u : changeSession.selectedSector.getUnits()) {
        if (translator.translateCategory(u.getJsonKey()).equals(chosen)) {
            changeSession.selectedUnit = u;
            changeSession.state = ChangeState.SELECT_ENTRY;
            serveEntryQuestion(exchange);
            return;
        }
    }

    serveErrorPage(exchange, "Η μονάδα δεν βρέθηκε.");
  }

  private static void serveEntryQuestion(HttpExchange exchange) throws IOException {
    ChangeSession changeSession = getChangeSession();

    StringBuilder buttons = new StringBuilder();
    buttons.append("<h2 class='section-title'>Επιλέξτε Κατηγορία</h2>");
    buttons.append("<form method='POST'>");
    for (EnvEntry e : changeSession.selectedUnit.getEntries()) {
        String name = translator.translateCategory(e.getJsonKey());
        buttons.append("<button class='primary-btn' name='entry' value='")
               .append(name).append("'>").append(name).append("</button>");
    }
    buttons.append("</form>");

    String html = buildStyledChangePage(buttons.toString(), "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
}


private static void handleEntryInput(HttpExchange exchange, String formData) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String chosen = extractFormValue(formData, "entry");

    for (EnvEntry e : changeSession.selectedUnit.getEntries()) {
        if (translator.translateCategory(e.getJsonKey()).equals(chosen)) {
            changeSession.selectedEntry = e;
            changeSession.oldValue = e.getAmount();
            changeSession.pendingValue = null;

            changeSession.state = ChangeState.EDIT_VALUE;
            serveValueEditor(exchange);
            return;
        }
    }

    serveErrorPage(exchange, "Η κατηγορία δεν βρέθηκε.");
  }

  private static void serveValueEditor(HttpExchange exchange) throws IOException {
    serveValueEditor(exchange, null);
}

private static void serveValueEditor(HttpExchange exchange, String validationMessage) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String entryName = translator.translateCategory(changeSession.selectedEntry.getJsonKey());

    String inner = """
        <h2 class='section-title'>Αλλαγή ποσού</h2>
        <div class='description'>Κατηγορία: <b>""" + entryName + "</b></div>" +
        "<div class='description' style='color:#0d4f1c; font-weight:700;'>Τρέχον ποσό: " + String.format("%,.2f €", changeSession.oldValue) + "</div>" +
        "<form method='POST' style='margin-top:18px; display:flex; flex-direction:column; gap:12px;'>"
        + "<input class='input-box' type='text' name='newValue' placeholder='π.χ. 2.000.000 ή 2000000,50' required>"
        + "<button class='primary-btn' type='submit'>Αποθήκευση</button>"
        + "</form>";

    if (validationMessage != null && !validationMessage.isBlank()) {
        inner += "<div class='error-box' style='margin-top:14px;'>" + validationMessage + "</div>";
    }

    String html = buildStyledChangePage(inner, "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
}


private static void handleValueChange(HttpExchange exchange, String formData) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String raw = extractFormValue(formData, "newValue");

    if (raw == null || raw.isBlank()) {
        serveValueEditor(exchange, "Δεν δόθηκε τιμή. Καμία αλλαγή.");
        return;
    }

    String cleaned = raw.replace(" ", "")
                        .replace(".", "")
                        .replace(",", ".")
                        .trim();

    double newValue;
    try {
        newValue = Double.parseDouble(cleaned);
    } catch (NumberFormatException e) {
        serveValueEditor(exchange, "Λάθος: Παρακαλώ δώστε έγκυρο αριθμό.");
        return;
    }

    if (newValue < 0) {
        serveValueEditor(exchange, "ΣΦΑΛΜΑ: Η τιμή δεν μπορεί να είναι αρνητική.");
        return;
    }

    if (newValue > changeSession.totalBudget) {
        String msg = String.format("ΣΦΑΛΜΑ: Η τιμή (%,.2f €) υπερβαίνει τον συνολικό προϋπολογισμό του Υπουργείου (%,.2f €).", newValue, changeSession.totalBudget);
        serveValueEditor(exchange, msg);
        return;
    }

    double deviation = Math.abs((newValue - changeSession.oldValue) / changeSession.oldValue) * 100.0;
    if (deviation > 30.0) {
        changeSession.pendingValue = newValue;
        changeSession.state = ChangeState.CONFIRM_EXTREME;
        serveExtremeWarning(exchange, deviation);
        return;
    }

    applyNewValueAndFinish(exchange, newValue);
}

private static void applyNewValueAndFinish(HttpExchange exchange, double newValue) throws IOException {
    ChangeSession changeSession = getChangeSession();

    changeSession.selectedEntry.setAmount(newValue);
    changeSession.pendingValue = null;

    double offset = changeSession.oldValue - newValue;
    changeSession.currentBalance += offset;
    StringBuilder banner = new StringBuilder();
    banner.append("[OK] Η τιμή άλλαξε επιτυχώς. Δημιουργήθηκε διαφορά: ")
          .append(String.format("%,.2f €", offset));

    String balanceInfo;
    if (Math.abs(changeSession.currentBalance) < 0.01) {
        balanceInfo = "Ο προϋπολογισμός είναι ισοσκελισμένος!";
    } else {
        balanceInfo = String.format("ΥΠΟΛΟΙΠΟ ΓΙΑ ΙΣΟΣΚΕΛΙΣΜΟ: %,.2f €", changeSession.currentBalance);
    }
    banner.append(" | ").append(balanceInfo);

    changeSession.state = ChangeState.SELECT_SECTOR;
    serveSectorQuestion(exchange, banner.toString());
  }

  private static void serveErrorPage(HttpExchange exchange, String message) throws IOException {

    String inner = """
        <h2 class='section-title'>Σφάλμα</h2>
        <div class='error-box'>""" + message + "</div>" +
        "<a class='secondary-btn' href='/change-budget' style='text-decoration:none; margin-top:18px;'>Ξανά προσπάθεια</a>";

    String html = buildStyledChangePage(inner, "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
  }

  // TEMPLATE REPLACEMENT UTILITIES
  private static String replaceTemplatePlaceholder(String template, String placeholderKey, String value) {
    String placeholder = "{{" + placeholderKey + "}}";
    return template.replace(placeholder, escapeHtml(value));
  }

  private static String escapeHtml(String text) {
    if (text == null) {
      return "";
    }
    return text.replace("&", "&amp;")
               .replace("<", "&lt;")
               .replace(">", "&gt;")
               .replace("\"", "&quot;")
               .replace("'", "&#39;");
  }

  private static String replaceUsernamePlaceholdersSafe(String htmlContent, String username, String filename) {
    String safeUsername = (username == null || username.isBlank()) ? "Υπάλληλε" : username;
    String encodedUsername = URLEncoder.encode(safeUsername, StandardCharsets.UTF_8);
    htmlContent = replaceTemplatePlaceholder(htmlContent, "username", safeUsername);
    htmlContent = htmlContent.replace("{{usernameEncoded}}", encodedUsername);
    return htmlContent;
  }

  // FORM DATA PARSING UTILITIES
  private static java.util.Map<String, String> parseFormData(final HttpExchange exchange) throws IOException {
    java.util.Map<String, String> formDataMap = new java.util.HashMap<>();
    try (java.io.InputStream requestBody = exchange.getRequestBody()) {
      String formDataString = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
      if (formDataString != null && !formDataString.isEmpty()) {
        try {
          String[] pairs = formDataString.split("&");
          for (String pair : pairs) {
            if (pair.isEmpty()) continue;
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
              String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
              String value = java.net.URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
              formDataMap.put(key, value);
            } else if (keyValue.length == 1) {
              String key = java.net.URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
              formDataMap.put(key, "");
            }
          }
        } catch (IllegalArgumentException e) {
          logError("Failed to decode form data: " + e.getMessage());
          return formDataMap;
        }
      }
    }
    return formDataMap;
  }

  private static String convertFormDataMapToString(java.util.Map<String, String> formDataMap) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (java.util.Map.Entry<String, String> entry : formDataMap.entrySet()) {
      if (!first) {
        sb.append("&");
      }
      sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
        .append("=")
        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
      first = false;
    }
    return sb.toString();
  }

  private static String extractFormValue(String formData, String key) {
    for (String pair : formData.split("&")) {
        String[] kv = pair.split("=", 2);
        if (kv.length == 2 && kv[0].equals(key)) {
            return java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
        }
    }
    return "";
  }

  private static boolean isValidYear(String year, String[] allowedYears) {
    if (year == null || year.isEmpty()) {
      return false;
    }
    for (String allowedYear : allowedYears) {
      if (allowedYear.equals(year)) {
        return true;
      }
    }
    return false;
  }

  private static void serveExtremeWarning(HttpExchange exchange, double deviation) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String formatted = String.format("%6.2f", deviation);

    double prospectiveBalance = changeSession.currentBalance;
    if (changeSession.pendingValue != null) {
        double offset = changeSession.oldValue - changeSession.pendingValue;
        prospectiveBalance += offset;
    }

    String balanceInfo;
    if (Math.abs(prospectiveBalance) < 0.01) {
        balanceInfo = "Ο προϋπολογισμός θα είναι ισοσκελισμένος.";
    } else {
        balanceInfo = String.format("ΥΠΟΛΟΙΠΟ ΓΙΑ ΙΣΟΣΚΕΛΙΣΜΟ (αν εφαρμοστεί): %,.2f €", prospectiveBalance);
    }

    String inner = """
        <h2 class='section-title'>ΠΡΟΕΙΔΟΠΟΙΗΣΗ: ΑΚΡΑΙΑ ΑΛΛΑΓΗ ΠΡΟΫΠΟΛΟΓΙΣΜΟΥ</h2>

        <p class='description' style='color:#b71c1c; font-weight:700;'>
        Ποσοστιαία μεταβολή:
        """ + formatted + """
        %
        </p>

        <p class='description'>Η μεταβολή υπερβαίνει το όριο του 30%!</p>

        <p class='description' style='color:#0d4f1c; font-weight:700;'>
        """ + balanceInfo + """
        </p>

        <div class='choice-group' style='margin-top:18px;'>
            <form method='POST' style='flex:1;'>
                <button class='primary-btn' name='confirmExtreme' value='yes' style='width:100%;'>ΝΑΙ</button>
            </form>
            <form method='POST' style='flex:1;'>
                <button class='secondary-btn' name='confirmExtreme' value='no' style='width:100%;'>ΟΧΙ</button>
            </form>
        </div>
        """;
    String html = buildStyledChangePage(inner, "");
    sendResponse(exchange, html, 200, "text/html; charset=UTF-8");
  }

  private static void handleExtremeConfirmation(HttpExchange exchange, String formData) throws IOException {
    ChangeSession changeSession = getChangeSession();

    String choice = extractFormValue(formData, "confirmExtreme");

    if ("yes".equals(choice) && changeSession.pendingValue != null) {
        applyNewValueAndFinish(exchange, changeSession.pendingValue);
        return;
    }

    // "no" or anything else: ask again for new value
    serveValueEditor(exchange, "Ακύρωση: Παρακαλώ εισάγετε νέα τιμή.");
    changeSession.state = ChangeState.EDIT_VALUE;
}
}
