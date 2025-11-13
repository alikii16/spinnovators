package gr.det.spinnovators;

public class FullBudgetPrinter {

    //δημιουργω μεταβλητη data που θελω να δεχεται τα δεδομενα τησ κλασησ MinistryDataInput
    private MinistryDataInput data;

    public FullBudgetPrinter(MinistryDataInput dataToUse) {
        this.data = dataToUse;
    }

    public void ShowBudget(String year) {
        // χρηση τησ συναρτησης, διοτι το year ειναι string (ελεγχει αν ειναι οι ιδιοι χαρακτηρεσ, οχι η ιδια διευθυνση)
        
        double totalBudget;

        if ("2025".equals(year)) {

            totalBudget = 0;

            System.out.println("\n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "---");
            int size = this.data.getSize25();
            String[] names = this.data.getNames25();
            double[] amounts = this.data.getBudgetAmount25();

            for (int i = 0; i < size; i++) {
                // μορφοποιησεις για εμφάνιση ανω κατω τελείας, συμβόλου ευρώ, σωστής απεικόνισης ποσών και αλλαγή γραμμής
                System.out.printf("%s: %,.2f €\n", names[i], amounts[i]);
                totalBudget = totalBudget + amounts[i];
            }

            System.out.println("-------------------------------------------------------------------------");
            System.out.printf("%s: %,.2f €\n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget);
        
        } else if ("2024".equals(year)) {

            totalBudget = 0;

            System.out.println("\n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "---");
            int size = this.data.getSize24();
            String[] names = this.data.getNames24();
            double[] amounts = this.data.getBudgetAmount24();

            for (int i = 0; i < size; i++) {
                System.out.printf(" * %-55s: %,.2f €\n", names[i], amounts[i]);
                totalBudget = totalBudget + amounts[i];
            }

            System.out.println("-------------------------------------------------------------------------");
            System.out.printf(" * %-55s: %,.2f €\n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget);

        } else if ("2023".equals(year)) {

            totalBudget = 0;

            System.out.println("\n--- ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ ΕΤΟΥΣ " + year + "---");
            int size = this.data.getSize23();
            String[] names = this.data.getNames23();
            double[] amounts = this.data.getBudgetAmount23();

            for (int i = 0; i < size; i++) {
                System.out.printf(" * %-55s: %,.2f €\n", names[i], amounts[i]);
                totalBudget = totalBudget + amounts[i];
            }

            System.out.println("-------------------------------------------------------------------------");
            System.out.printf(" * %-55s: %,.2f €\n", "ΣΥΝΟΛΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ", totalBudget);

        } else {

            System.out.println("Δεν υπάρχουν δεδομένα για το έτος " + year);

        }
            
    }

}
