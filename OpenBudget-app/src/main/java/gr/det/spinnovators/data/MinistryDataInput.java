package gr.det.spinnovators.data;

import java.util.ArrayList;
import java.util.List;

/** 
 * Handles the storage and retrieval of national budget data for various ministries.
 * This class serves as a data repository for budget allocations spanning 
 * from the year 2023 to 2026.
 */
public class MinistryDataInput {

  /** 
   * Represents a single ministry budget record.
   * Associates a ministry's name with its corresponding budget amount.
   */
  public static class MinistryEntry {
    public String name;
    public double amount;

    /**
     * Constructs a new MinistryEntry.
     *
     * @param name The name of the ministry.
     * 
     * @param amount The budget amount assigned to the ministry.
     */
    public MinistryEntry(String name, double amount) {
      this.name = name;
      this.amount = amount;
    }
  }

  private List<MinistryEntry> data26 = new ArrayList<>();
  private List<MinistryEntry> data25 = new ArrayList<>();
  private List<MinistryEntry> data24 = new ArrayList<>();
  private List<MinistryEntry> data23 = new ArrayList<>();

  /**
   * Initializes the MinistryDataInput object.
   * Automatically populates the data lists for the years 2023-2026 
   * by calling the internal initialization method.
   */
  public MinistryDataInput() {
    initializeData();
  }

  /**
   * Populates the internal data structures with hardcoded budget values.
   * This method organizes data for each year using parallel arrays for names 
   * and amounts before converting them into MinistryEntry objects.
   */
  private void initializeData() {
    String[] namesCommon = {
        "Προεδρία της Δημοκρατίας", "Βουλή των Ελλήνων", "Προεδρία της Κυβέρνησης",
        "Υπουργείο Εσωτερικών", "Υπουργείο Εξωτερικών", "Υπουργείο Εθνικής Άμηνας",
        "Υπουργείο Υγείας", "Υπουργείο Δικαιοσύνης",
        "Υπουργείο Παιδείας Θρησκευμάτων και Αθλητισμού", "Υπουργείο Πολιτισμού",
        "Υπουργείο Εθνικής Οικονομίας και Οικονομικών",
        "Υπουργείο Αγροτικής Ανάπτυξης και Τροφίμων", "Υπουργείο Περιβάλλοντος και Ενέργειας",
        "Υπουργείο Εργασίας και Κοινωνικής Ασφάλισης",
        "Υπουργείο Κοινωνικής Συνοχής και Οικογένειας", "Υπουργείο Ανάπτυξης",
        "Υπουργείο Υποδομών και Μεταφορών", "Υπουργείο Ναυτιλίας και Νησιωτικής Πολιτικής",
        "Υπουργείο Τουρισμού", "Υπουργείο Ψηφιακής Διακυβέρνησης",
        "Υπουργείο Μετανάστευσης και Ασύλου", "Υπουργείο Προστασίας του Πολίτη",
        "Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας", "Αποκεντρωμένη Διοίκηση Αττικής",
        "Αποκεντρωμένη Διοίκηση Θεσσαλίας - Στερεάς Ελλάδας",
        "Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας",
        "Αποκεντρωμένη Διοίκηση Πελοποννήσου Δυτικής Ελλάδας και Ιονίου",
        "Αποκεντρωμένη Διοίκηση Αιγαίου", "Αποκεντρωμένη Διοίκηση Κρήτης",
        "Αποκεντρωμένη Διοίκηση Μακεδονίας - Θράκης"
    };

    double[] amounts26 = {
        4951000.00, 186900000.00, 45905000.00, 4163668000.00, 483237000.00, 7063272000.00,
        7841945000.00, 679577000.00, 6763933000.00, 653109000.00, 19377392000.00, 1503877000.00,
        3133452000.00, 19101078000.00, 3803316000.00, 798476000.00, 3391756000.00, 756593000.00,
        237992000.00, 1391403000.00, 536133000.00, 2603285000.00, 1438115000.00, 14380000.00,
        11142000.00, 10981000.00, 15556000.00, 7149000.00, 7311000.00, 19640000.00
    };
    
    double[] amounts25 = {
        4638000.00, 171950000.00, 41689000.00, 3830276000.00, 420237000.00, 6130000000.00,
        7177424000.00, 650803000.00, 6606000000.00, 575419000.00, 1246518464000.00, 1281403000.00,
        2341227000.00, 18678084000.00, 3989553000.00, 818045000.00, 2694810000.00, 651864000.00,
        189293000.00, 1073928000.00, 475871000.00, 2285820000.00, 1221116000.00, 13091000.00,
        10579000.00, 9943000.00, 14918000.00, 6188000.00, 6497000.00, 18376000.00
    };

    double[] amounts24 = {
        4636000.00, 160400000.00, 43259000.00, 3705487000.00, 407982000.00, 6123388000.00,
        6027031000.00, 624464000.00, 6547630000.00, 400113000.00, 1049897798000.00, 1184694000.00,
        1823158000.00, 18629492000.00, 3992039000.00, 924661000.00, 2350117000.00, 576280000.00,
        167787000.00, 843765000.00, 473304000.00, 2262973000.00, 936610000.00, 11629000.00,
        10659000.00, 9796000.00, 15415000.00, 6211000.00, 6719000.00, 19863000.00
    };

    String[] names23 = {
        "Προεδρία της Δημοκρατίας", "Βουλή των Ελλήνων", "Προεδρία της Κυβέρνησης",
        "Υπουργείο Εσωτερικών", "Υπουργείο Εξωτερικών", "Υπουργείο Εθνικής Άμυνας",
        "Υπουργείο Υγείας", "Υπουργείο Δικαιοσύνης", "Υπουργείο Παιδείας και Θρησκευμάτων",
        "Υπουργείο Πολιτισμού και Αθλητισμού", "Υπουργείο Οικονομικών",
        "Υπουργείο Αγροτικής Ανάπτυξης και Τροφίμων", "Υπουργείο Περιβάλλοντος και Ενέργειας",
        "Υπουργείο Εργασίας και Κοινωνικών Υποθέσεων", "Υπουργείο Ανάπτυξης και Επενδύσεων",
        "Υπουργείο Υποδομών και Μεταφορών", "Υπουργείο Ναυτιλίας και Νησιωτικής Πολιτικής",
        "Υπουργείο Τουρισμού", "Υπουργείο Ψηφιακής Διακυβέρνησης",
        "Υπουργείο Μετανάστευσης και Ασύλου", "Υπουργείο Προστασίας του Πολίτη",
        "Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας", "Αποκεντρωμένη Διοίκηση Αττικής",
        "Αποκεντρωμένη Διοίκηση Θεσσαλίας - Στερεάς Ελλάδας",
        "Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας",
        "Αποκεντρωμένη Διοίκηση Πελοποννήσου Δυτικής Ελλάδας και Ιονίου",
        "Αποκεντρωμένη Διοίκηση Αιγαίου", "Αποκεντρωμένη Διοίκηση Κρήτης",
        "Αποκεντρωμένη Διοίκηση Μακεδονίας - Θράκης"
    };
    
    double[] amounts23 = {
        4263000.00, 149900000.00, 40679000.00, 3548748000.00, 282175000.00, 5707800000.00,
        5202388000.00, 566374000.00, 6080504000.00, 458563000.00, 748592323000.00, 1547980000.00,
        1707333000.00, 22492686000.00, 3295349000.00, 2346259000.00, 498453000.00, 239360000.00,
        972937000.00, 418728000.00, 2012833000.00, 643473000.00, 11610000.00, 7723000.00,
        8468000.00, 12467000.00, 5631000.00, 6068000.00, 17118000.00
    };

    for (int i = 0; i < namesCommon.length; i++) {
      data26.add(new MinistryEntry(namesCommon[i], amounts26[i]));
      data25.add(new MinistryEntry(namesCommon[i], amounts25[i]));
      data24.add(new MinistryEntry(namesCommon[i], amounts24[i]));
    } 

    for (int i = 0; i < names23.length; i++) {
      data23.add(new MinistryEntry(names23[i], amounts23[i]));
    }
  }

  // --- 2026 getters ---

  /**
   * Retrieves the names of all ministries for the 2026 budget.
   *
   * @return An array of strings containing ministry names.
   */
  public String[] getNames26() {
    return data26.stream().map(e -> e.name).toArray(String[]::new);
  }

  /**
   * Retrieves the budget amounts for all ministries in 2026.
   *
   * @return An array of doubles representing the budget allocations.
   */
  public double[] getBudgetAmount26() {
    return data26.stream().mapToDouble(e -> e.amount).toArray();
  }

  /**
   * Returns the total number of ministry records stored for 2026.
   *
   * @return The size of the 2026 data list.
   */
  public int getSize26() {
    return data26.size();
  }

  // --- 2025 getters ---

  /**
   * Retrieves the names of all ministries for the 2025 budget.
   *
   * @return An array of strings containing ministry names.
   */
  public String[] getNames25() {
    return data25.stream().map(e -> e.name).toArray(String[]::new);
  }

  /**
   * Retrieves the budget amounts for all ministries in 2025.
   *
   * @return An array of doubles representing the budget allocations.
   */
  public double[] getBudgetAmount25() {
    return data25.stream().mapToDouble(e -> e.amount).toArray();
  }

  /**
   * Returns the total number of ministry records stored for 2025.
   *
   * @return The size of the 2025 data list.
   */
  public int getSize25() {
    return data25.size();
  }

  // --- 2024 getters ---

  /**
   * Retrieves the names of all ministries for the 2024 budget.
   *
   * @return An array of strings containing ministry names.
   */
  public String[] getNames24() {
    return data24.stream().map(e -> e.name).toArray(String[]::new);
  }

  /**
   * Retrieves the budget amounts for all ministries in 2024.
   *
   * @return An array of doubles representing the budget allocations.
   */
  public double[] getBudgetAmount24() {
    return data24.stream().mapToDouble(e -> e.amount).toArray();
  }

  /**
   * Returns the total number of ministry records stored for 2024.
   *
   * @return The size of the 2024 data list.
   */
  public int getSize24() {
    return data24.size();
  }

  // --- 2023 getters ---

  /**
   * Retrieves the names of all ministries for the 2023 budget.
   *
   * @return An array of strings containing ministry names.
   */
  public String[] getNames23() {
    return data23.stream().map(e -> e.name).toArray(String[]::new);
  }

  /**
   * Retrieves the budget amounts for all ministries in 2023.
   *
   * @return An array of doubles representing the budget allocations.
   */
  public double[] getBudgetAmount23() {
    return data23.stream().mapToDouble(e -> e.amount).toArray();
  }

  /**
   * Returns the total number of ministry records stored for 2023.
   *
   * @return The size of the 2023 data list.
   */
  public int getSize23() {
    return data23.size();
  }
}
