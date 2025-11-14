package gr.det.spinnovators;

import java.util.Scanner;  

public class App {
    public static void main( String[] args ) {
        FirstLogin.login();
    
        MinistryDataInput allData = new MinistryDataInput(); 
        FullBudgetPrinter printer = new FullBudgetPrinter(allData); 

        Scanner scanner = new Scanner(System.in); 
        System.out.println("================================="); 
        System.out.print("Ποιού έτους τον προύπολογισμό; (2023, 2024 ή 2025): "); 
        String chosenYear = scanner.nextLine(); 
        printer.ShowBudget(chosenYear);  
        scanner.close(); 
    } 
}
