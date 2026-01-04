package gr.det.spinnovators.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.io.TempDir;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginWebServerTest {

  private static final String BASE_URL = "http://localhost:8080";
  private static final HttpClient client = HttpClient.newHttpClient();

  // Greek Strings encoded as Unicode to prevent Windows/Maven encoding issues
  // "Λάθος όνομα ή κωδικός. Προσπαθήστε ξανά."
  private static final String ERROR_MSG_GR = "\u039b\u03ac\u03b8\u03bf\u03c2 \u03cc\u03bd\u03bf\u03bc\u03b1 \u03ae \u03ba\u03c9\u03b4\u03b9\u03ba\u03cc\u03c2. \u03a0\u03c1\u03bf\u03c3\u03c0\u03b1\u03b8\u03ae\u03c3\u03c4\u03b5 \u03be\u03b1\u03bd\u03ac.";
  // "Λάθος όνομα" (partial for check)
  private static final String PARTIAL_ERROR_GR = "\u039b\u03ac\u03b8\u03bf\u03c2 \u03cc\u03bd\u03bf\u03bc\u03b1";
  
  // "ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ"
  private static final String BUDGET_HEADER_GR = "\u03a0\u03a1\u039f\u03ab\u03a0\u039f\u039b\u039f\u0393\u0399\u03a3\u039c\u039f\u03a3 \u0395\u03a4\u039f\u03a5\u03a3";
  
  // "ΤΟΜΕΑΣ"
  private static final String SECTOR_GR = "\u03a4\u039f\u039c\u0395\u0391\u03a3";
  
  // "Δεν υπάρχουν δεδομένα για το έτος"
  private static final String NO_DATA_GR = "\u0394\u03b5\u03bd \u03c5\u03c0\u03ac\u03c1\u03c7\u03bf\u03c5\u03bd \u03b4\u03b5\u03b4\u03bf\u03bc\u03ad\u03bd\u03b1 \u03b3\u03b9\u03b1 \u03c4\u03bf \u03ad\u03c4\u03bf\u03c2";

  @TempDir
  static Path tempFrontendDir;

  @BeforeAll
  static void setup() throws IOException {
    // 1. login.html
    createDummyFile("login.html", 
        "<html><body>" +
        "<div class=\"message error-message error-message-hidden\">" + ERROR_MSG_GR + "</div>" +
        "<form method='POST'>Login</form>" +
        "</body></html>");

    // 2. minister_statebudget.html
    createDummyFile("minister_statebudget.html", 
        "<html><body><h1>State Budget</h1>{{username}}<form></form></body></html>");

    // 3. minister_budget.html
    createDummyFile("minister_budget.html", 
        "<html><body><h1>Ministry Budget</h1>{{username}}<form></form>" +
        "    <div class=\"container\" style=\"margin-top: 30px;\"></div>" + 
        "</body></html>");

    // 4. esg.html
    createDummyFile("esg.html", 
        "<html><body></body></html>");

    // Start server in background thread to avoid blocking if needed, 
    // but here we just call main logic.
    new Thread(() -> {
        try {
            LoginWebServer.startServer(tempFrontendDir.toAbsolutePath().toString());
        } catch (IOException e) {
            // Server might already be running, ignore
        }
    }).start();
    
    // Give it time to start
    try { Thread.sleep(1000); } catch (InterruptedException e) {}
  }

  private static void createDummyFile(String filename, String content) throws IOException {
    Files.writeString(tempFrontendDir.resolve(filename), content, StandardCharsets.UTF_8);
  }

  @Test
  @Order(1)
  void testLoginSuccessMinister() throws IOException, InterruptedException {
    Map<String, String> formData = new HashMap<>();
    formData.put("username", "Minister");
    formData.put("password", "m1n1st3r");

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/login"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(buildFormDataFromMap(formData))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEquals(302, response.statusCode());
    String location = response.headers().firstValue("Location").orElse("");
    assertEquals("/minister_statebudget.html", location);
  }

  @Test
  @Order(2)
  void testLoginSuccessEmployee() throws IOException, InterruptedException {
    Map<String, String> formData = new HashMap<>();
    formData.put("username", "TestUser");
    formData.put("password", "3mpl0y33");

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/login"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(buildFormDataFromMap(formData))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEquals(302, response.statusCode());
    String location = response.headers().firstValue("Location").orElse("");
    assertTrue(location.contains("/employee_statebudget.html"));
  }

  @Test
  @Order(3)
  void testLoginFailure() throws IOException, InterruptedException {
    Map<String, String> formData = new HashMap<>();
    formData.put("username", "Hacker");
    formData.put("password", "wrongpass");

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/login"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(buildFormDataFromMap(formData))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEquals(200, response.statusCode());
    // Use Unicode variable to check content
    assertTrue(response.body().contains("class=\"message error-message\""), "Error class missing");
    assertTrue(response.body().contains(PARTIAL_ERROR_GR), "Greek error text missing (Check encoding)");
  }

  @Test
  @Order(4)
  void testViewStateBudget2025() throws IOException, InterruptedException {
    Map<String, String> formData = new HashMap<>();
    formData.put("year", "2025");

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/minister_statebudget.html"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(buildFormDataFromMap(formData))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

    assertEquals(200, response.statusCode());
    String body = response.body();

    // Use Unicode variable
    assertTrue(body.contains(BUDGET_HEADER_GR + " 2025"), "Budget Header missing");
    assertTrue(body.contains("<table style='width: 100%;"), "Table start missing");
  }

  @Test
  @Order(5)
  void testViewMinistryBudgetWithEsg() throws IOException, InterruptedException {
    Map<String, String> formData = new HashMap<>();
    formData.put("year", "2026");

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/minister_budget.html"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(buildFormDataFromMap(formData))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    String body = response.body();

    // Use Unicode variable for Sector check
    assertTrue(body.contains(SECTOR_GR) || body.contains("Environmental"), 
        "Env Printer Sector missing (Could be data loading issue or encoding)");
    
    // ESG check (English text is safe)
    assertTrue(body.contains("ESG SCORE"), "ESG Section missing");
  }

  @Test
  @Order(6)
  void testInvalidYearSubmission() throws IOException, InterruptedException {
    Map<String, String> formData = new HashMap<>();
    formData.put("year", "2099");

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(BASE_URL + "/minister_statebudget.html"))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(buildFormDataFromMap(formData))
        .build();

    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    
    assertTrue(response.body().contains(NO_DATA_GR), 
        "Error message for invalid year missing");
  }

  private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<String, String> data) {
    String builder = data.entrySet().stream()
        .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" 
            + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
        .collect(Collectors.joining("&"));
    return HttpRequest.BodyPublishers.ofString(builder);
  }
}
