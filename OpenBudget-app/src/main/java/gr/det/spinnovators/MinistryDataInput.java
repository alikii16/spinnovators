package gr.det.spinnovators;

public final class MinistryDataInput {

  /** The maximum number of ministries.
  * the system can store data for.*/
  
  private static final int MAX_MINISTRIES = 30;

  /**Names of ministries for year 2025.*/
  
  private String[] names25;
  
  /**Budget amounts for year 2025.*/
  
  private double[] budgetAmount25;
  
  /**Actual number of records used for 2025.*/
  
  private int size25;

  /**Names of ministries for year 2024.*/
  
  private String[] names24;
  
  /**Budget amounts for year 2024.*/
  
  private double[] budgetAmount24;
  
  /**Actual number of records used for 2024.*/

  private int size24;

  /**Names of ministries for year 2023.*/

  private String[] names23;

  /**Budget amounts for year 2023.*/

  private double[] budgetAmount23;

  /**Actual number of records used for 2023.*/

  private int size23;

  // constructors

  /** Names of ministries for year 2025.*/
  
  private static final String[] NAMES_2025 = {
    "Προεδρία της Δημοκρατίας",
    "Βουλή των Ελλήνων",
    "Προεδρία της Κυβέρνησης",
    "Υπουργείο Εσωτερικών",
    "Υπουργείο Εξωτερικών",
    "Υπουργείο Εθνικής Άμηνας",
    "Υπουργείο Υγείας",
    "Υπουργείο Δικαιοσύνης",
    "Υπουργείο Παιδείας Θρησκευμάτων και Αθλητισμού",
    "Υπουργείο Πολιτισμού",
    "Υπουργείο Εθνικής Οικονομίας και Οικονομικών",
    "Υπουργείο Αγροτικής Ανάπτυξης και Τροφίμων",
    "Υπουργείο Περιβάλλοντος και Ενέργειας",
    "Υπουργείο Εργασίας και Κοινωνικής Ασφάλισης",
    "Υπουργείο Κοινωνικής Συνοχής και Οικογένειας",
    "Υπουργείο Ανάπτυξης",
    "Υπουργείο Υποδομών και Μεταφορών",
    "Υπουργείο Ναυτιλίας και Νησιωτικής Πολιτικής",
    "Υπουργείο Τουρισμού",
    "Υπουργείο Ψηφιακής Διακυβέρνησης",
    "Υπουργείο Μετανάστευσης και Ασύλου",
    "Υπουργείο Προστασίας του Πολίτη",
    "Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας",
    "Αποκεντρωμένη Διοίκηση Αττικής",
    "Αποκεντρωμένη Διοίκηση Θεσσαλίας - Στερεάς Ελλάδας",
    "Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας",
    "Αποκεντρωμένη Διοίκηση Πελοποννήσου Δυτικής Ελλάδας και Ιονίου",
    "Αποκεντρωμένη Διοίκηση Αιγαίου",
    "Αποκεντρωμένη Διοίκηση Κρήτης",
    "Αποκεντρωμένη Διοίκηση Μακεδονίας - Θράκης"
  };

  /** Budget amounts for year 2025.*/
  
  private static final double[] AMOUNTS_2025 = {
    4_638_000.00,
    171_950_000.00,
    41_689_000.00,
    3_830_276_000.00,
    420_237_000.00,
    6_130_000_000.00,
    7_177_424_000.00,
    650_803_000.00,
    6_606_000_000.00,
    575_419_000.00,
    1_246_518_464_000.00,
    1_281_403_000.00,
    2_341_227_000.00,
    18_678_084_000.00,
    3_989_553_000.00,
    818_045_000.00,
    2_694_810_000.00,
    651_864_000.00,
    189_293_000.00,
    1_073_928_000.00,
    475_871_000.00,
    2_285_820_000.00,
    1_221_116_000.00,
    13_091_000.00,
    10_579_000.00,
    9_943_000.00,
    14_918_000.00,
    6_188_000.00,
    6_497_000.00,
    18_376_000.00
  };

  /** Names of ministries for year 2024.*/
  
  private static final String[] NAMES_2024 = {
    "Προεδρία της Δημοκρατίας",
    "Βουλή των Ελλήνων",
    "Προεδρία της Κυβέρνησης",
    "Υπουργείο Εσωτερικών",
    "Υπουργείο Εξωτερικών",
    "Υπουργείο Εθνικής Άμηνας",
    "Υπουργείο Υγείας",
    "Υπουργείο Δικαιοσύνης",
    "Υπουργείο Παιδείας Θρησκευμάτων και Αθλητισμού",
    "Υπουργείο Πολιτισμού",
    "Υπουργείο Εθνικής Οικονομίας και Οικονομικών",
    "Υπουργείο Αγροτικής Ανάπτυξης και Τροφίμων",
    "Υπουργείο Περιβάλλοντος και Ενέργειας",
    "Υπουργείο Εργασίας και Κοινωνικής Ασφάλισης",
    "Υπουργείο Κοινωνικής Συνοχής και Οικογένειας",
    "Υπουργείο Ανάπτυξης",
    "Υπουργείο Υποδομών και Μεταφορών",
    "Υπουργείο Ναυτιλίας και Νησιωτικής Πολιτικής",
    "Υπουργείο Τουρισμού",
    "Υπουργείο Ψηφιακής Διακυβέρνησης",
    "Υπουργείο Μετανάστευσης και Ασύλου",
    "Υπουργείο Προστασίας του Πολίτη",
    "Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας",
    "Αποκεντρωμένη Διοίκηση Αττικής",
    "Αποκεντρωμένη Διοίκηση Θεσσαλίας - Στερεάς Ελλάδας",
    "Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας",
    "Αποκεντρωμένη Διοίκηση Πελοποννήσου Δυτικής Ελλάδας και Ιονίου",
    "Αποκεντρωμένη Διοίκηση Αιγαίου",
    "Αποκεντρωμένη Διοίκηση Κρήτης",
    "Αποκεντρωμένη Διοίκηση Μακεδονίας - Θράκης"
  };

  /** Budget amounts for year 2024.*/
  
  private static final double[] AMOUNTS_2024 = {
    4_636_000.00,
    160_400_000.00,
    43_259_000.00,
    3_705_487_000.00,
    407_982_000.00,
    6_123_388_000.00,
    6_027_031_000.00,
    624_464_000.00,
    6_547_630_000.00,
    400_113_000.00,
    1_049_897_798_000.00,
    1_184_694_000.00,
    1_823_158_000.00,
    18_629_492_000.00,
    3_992_039_000.00,
    924_661_000.00,
    2_350_117_000.00,
    576_280_000.00,
    167_787_000.00,
    843_765_000.00,
    473_304_000.00,
    2_262_973_000.00,
    936_610_000.00,
    11_629_000.00,
    10_659_000.00,
    9_796_000.00,
    15_415_000.00,
    6_211_000.00,
    6_719_000.00,
    19_863_000.00
  };

  /** Names of ministries for year 2023.*/
  
  private static final String[] NAMES_2023 = {
    "Προεδρία της Δημοκρατίας",
    "Βουλή των Ελλήνων",
    "Προεδρία της Κυβέρνησης",
    "Υπουργείο Εσωτερικών",
    "Υπουργείο Εξωτερικών",
    "Υπουργείο Εθνικής Άμηνας",
    "Υπουργείο Υγείας",
    "Υπουργείο Δικαιοσύνης",
    "Υπουργείο Παιδείας και Θρησκευμάτων",
    "Υπουργείο Πολιτισμού και Αθλητισμού",
    "Υπουργείο Οικονομικών",
    "Υπουργείο Αγροτικής Ανάπτυξης και Τροφίμων",
    "Υπουργείο Περιβάλλοντος και Ενέργειας",
    "Υπουργείο Εργασίας και Κοινωνικών Υποθέσεων",
    "Υπουργείο Ανάπτυξης και Επενδύσεων",
    "Υπουργείο Υποδομών και Μεταφορών",
    "Υπουργείο Ναυτιλίας και Νησιωτικής Πολιτικής",
    "Υπουργείο Τουρισμού",
    "Υπουργείο Ψηφιακής Διακυβέρνησης",
    "Υπουργείο Μετανάστευσης και Ασύλου",
    "Υπουργείο Προστασίας του Πολίτη",
    "Υπουργείο Κλιματικής Κρίσης και Πολιτικής Προστασίας",
    "Αποκεντρωμένη Διοίκηση Αττικής",
    "Αποκεντρωμένη Διοίκηση Θεσσαλίας - Στερεάς Ελλάδας",
    "Αποκεντρωμένη Διοίκηση Ηπείρου - Δυτικής Μακεδονίας",
    "Αποκεντρωμένη Διοίκηση Πελοποννήσου Δυτικής Ελλάδας και Ιονίου",
    "Αποκεντρωμένη Διοίκηση Αιγαίου",
    "Αποκεντρωμένη Διοίκηση Κρήτης",
    "Αποκεντρωμένη Διοίκηση Μακεδονίας - Θράκης"
  };

  /** Budget amounts for year 2023.*/
  
  private static final double[] AMOUNTS_2023 = {
    4_263_000.00,
    149_900_000.00,
    40_679_000.00,
    40_679_000.00,
    282_175_000.00,
    5_707_800_000.00,
    5_202_388_000.00,
    566_374_000.00,
    6_080_504_000.00,
    458_563_000.00,
    748_592_323_000.00,
    1_547_980_000.00,
    1_707_333_000.00,
    22_492_686_000.00,
    3_295_349_000.00,
    2_346_259_000.00,
    498_453_000.00,
    239_360_000.00,
    972_937_000.00,
    418_728_000.00,
    2_012_833_000.00,
    643_473_000.00,
    11_610_000.00,
    7_723_000.00,
    8_468_000.00,
    12_467_000.00,
    5_631_000.00,
    6_068_000.00,
    17_118_000.00
  };

  /** Initializes the MinistryDataInput with 2025,2024,2023 data.*/
  
  public MinistryDataInput() {

    // YEAR 2025

    this.names25 = new String[MAX_MINISTRIES];
    this.budgetAmount25 = new double[MAX_MINISTRIES];
    this.size25 = Math.min(NAMES_2025.length, MAX_MINISTRIES);

    for (int i = 0; i < this.size25; i++) {
      this.names25[i] = NAMES_2025[i];
      this.budgetAmount25[i] = AMOUNTS_2025[i]; 
    } 

    // YEAR 2024

    this.names24 = new String[MAX_MINISTRIES];
    this.budgetAmount24 = new double[MAX_MINISTRIES];
    this.size24 = Math.min(NAMES_2024.length, MAX_MINISTRIES);

    for (int i = 0; i < this.size24; i++) {
      this.names24[i] = NAMES_2024[i];
      this.budgetAmount24[i] = AMOUNTS_2024[i];
    }

    // YEAR 2023

    this.names23 = new String[MAX_MINISTRIES];
    this.budgetAmount23 = new double[MAX_MINISTRIES];
    this.size23 = Math.min(NAMES_2023.length, MAX_MINISTRIES);

    for (int i = 0; i < this.size23; i++) {
      this.names23[i] = NAMES_2023[i];
      this.budgetAmount23[i] = AMOUNTS_2023[i];
    }
  }


  /**Returns the array of ministry names for 2025.
   * 
   * @return The String array containg names.
   */
  
  public String[] getNames25() {
    return names25;
  }
  
  /**Returns the array of budget amounts for 2025.
   * 
   * @return The double array containing budget amounts.
   */
  
  public double[] getBudgetAmount25() {
    return budgetAmount25;
  }
  
  /** Returns the size of the 2025 budget data set.
   * 
   * @return the integer size of the data set.
   */
  
  public int getSize25() {
    return size25;
  }

  /**Returns the array of ministry names for 2024.
   * 
   * @return The String array containg names.
   */
  
  public String[] getNames24() {
    return names24;
  }
  
  /**Returns the array of budget amounts for 2024.
   * 
   * @return The double array containing budget amounts.
   */
  
  public double[] getBudgetAmount24() {
    return budgetAmount24;
  }
  
  /** Returns the size of the 2024 budget data set.
   * 
   * @return the integer size of the data set.
   */
  
  public int getSize24() {
    return size24;
  }

  /**Returns the array of ministry names for 2023.
   * 
   *  @return The String array containg names.
   */

  public String[] getNames23() {
    return names23;
  }

  /**Returns the array of budget amounts for 2023.
   * 
   * @return The double array containing budget amounts.
   */

  public double[] getBudgetAmount23() {
    return budgetAmount23;
  }

  /** Returns the size of the 2023 budget data set.
   * 
   * @return the integer size of the data set.
   */

  public int getSize23() {
    return size23;
  }
}
