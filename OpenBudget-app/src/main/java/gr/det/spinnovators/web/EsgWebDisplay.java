package gr.det.spinnovators.web;

import gr.det.spinnovators.envdatamodel.EnvYear;
import gr.det.spinnovators.envdatamodel.EsgReport;
import gr.det.spinnovators.service.EsgScoreCalculator;
import java.util.Locale;

/**
 * Handles the generation of ESG report components for the web interface.
 *
 * <p>This class calculates ESG metrics and formats them into HTML fragments
 * to be displayed in the web application's comparison dashboard.
 */
public class EsgWebDisplay {

  private static final Locale HELLENIC_LOCALE = Locale.forLanguageTag("el-GR");
  private final EsgScoreCalculator calculator;

  public EsgWebDisplay() {
    this.calculator = new EsgScoreCalculator();
  }

  /**
   * Generates only the content HTML for ESG comparison (without full page structure).
   *
   * @param originalYear Original budget year (before changes)
   * @param modifiedYear Modified budget year (after changes)
   * @param totalBudget Total ministry budget
   * @return Content HTML for ESG comparison
   */
  public String generateEsgComparisonContent(EnvYear originalYear, 
                                             EnvYear modifiedYear,
                                             double totalBudget) {
    EsgReport originalReport = calculator.calculateReport(originalYear, totalBudget);
    EsgReport modifiedReport = calculator.calculateReport(modifiedYear, totalBudget);

    return buildComparisonContent(originalReport, modifiedReport, originalYear.getYear());
  }

  private String buildComparisonContent(EsgReport original, 
                                       EsgReport modified,
                                       String year) {
    double scoreDiff = modified.getOverallScore() - original.getOverallScore();
    String message = scoreDiff > 0 ? "ΒΕΛΤΙΩΣΗ" :
        (scoreDiff < 0 ? "ΕΠΙΔΕΙΝΩΣΗ" : "ΚΑΜΙΑ ΑΛΛΑΓΗ");
    
    double envDiff = modified.getEnvironmentalScore() - original.getEnvironmentalScore();
    double socDiff = modified.getSocialScore() - original.getSocialScore();
    double govDiff = modified.getGovernanceScore() - original.getGovernanceScore();
    
    String envClass = envDiff > 0 ? "positive" : (envDiff < 0 ? "negative" : "neutral");
    String socClass = socDiff > 0 ? "positive" : (socDiff < 0 ? "negative" : "neutral");
    String govClass = govDiff > 0 ? "positive" : (govDiff < 0 ? "negative" : "neutral");
    
    String feedbackMsg;
    if (scoreDiff > 2.0) {
      feedbackMsg = "Εξαιρετικά! Η αλλαγή βελτιώνει σημαντικά τη βιωσιμότητα!";
    } else if (scoreDiff > 0) {
      feedbackMsg = "Καλή αλλαγή! Μικρή βελτίωση στη βιωσιμότητα.";
    } else if (scoreDiff < -2.0) {
      feedbackMsg = "ΠΡΟΣΟΧΗ: Η αλλαγή επιδεινώνει σημαντικά τη βιωσιμότητα!";
    } else if (scoreDiff < 0) {
      feedbackMsg = "Η αλλαγή μειώνει ελαφρώς τη βιωσιμότητα.";
    } else {
      feedbackMsg = "Η αλλαγή δεν επηρεάζει το ESG score.";
    }
    
    return String.format(HELLENIC_LOCALE, """
            <h2 class='section-title'>ESG Αξιολόγηση - Έτος %s</h2>
            <p class='description'>Σύγκριση Πριν & Μετά τις Αλλαγές</p>
            <div class='esg-scores'>
                <div class='score-box'>
                    <div class='score-label'>ESG Score Πριν</div>
                    <div class='score-value'>%.2f / 100</div>
                </div>
                <div class='score-box'>
                    <div class='score-label'>ESG Score Μετά</div>
                    <div class='score-value'>%.2f / 100</div>
                </div>
                <div class='score-box difference'>
                    <div class='score-label'>Διαφορά</div>
                    <div class='score-value'>%.2f points</div>
                    <div class='score-message'>%s</div>
                </div>
            </div>
            <div class='category-section'>
                <h3 class='category-title'>Λεπτομέρειες Αλλαγών</h3>
                <div class='category-row'>
                    <div class='category-label'>Environmental (E)</div>
                    <div class='category-values'>
                        <span>%.1f%%</span>
                        <span class='arrow'>→</span>
                        <span>%.1f%%</span>
                        <span class='diff %s'>%.1f%%</span>
                    </div>
                </div>
                <div class='category-row'>
                    <div class='category-label'>Social (S)</div>
                    <div class='category-values'>
                        <span>%.1f%%</span>
                        <span class='arrow'>→</span>
                        <span>%.1f%%</span>
                        <span class='diff %s'>%.1f%%</span>
                    </div>
                </div>
                <div class='category-row'>
                    <div class='category-label'>Governance (G)</div>
                    <div class='category-values'>
                        <span>%.1f%%</span>
                        <span class='arrow'>→</span>
                        <span>%.1f%%</span>
                        <span class='diff %s'>%.1f%%</span>
                    </div>
                </div>
            </div>
            <div class='feedback-box'>
                <p>%s</p>
            </div>
            """,
        year,
        original.getOverallScore(),
        modified.getOverallScore(),
        Math.abs(scoreDiff),
        message,
        original.getEnvironmentalScore(),
        modified.getEnvironmentalScore(),
        envClass,
        Math.abs(envDiff),
        original.getSocialScore(),
        modified.getSocialScore(),
        socClass,
        Math.abs(socDiff),
        original.getGovernanceScore(),
        modified.getGovernanceScore(),
        govClass,
        Math.abs(govDiff),
        feedbackMsg
    );
  }
}

