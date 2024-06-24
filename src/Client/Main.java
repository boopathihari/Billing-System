package Client;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        Product product = new Product();

        while (true) {
            System.out.println("\n===============Billing System Application==============\n");
            System.out.println("1.Generate Bill");
            System.out.println("2.Generate Report");
            System.out.println("3.Exit");

            System.out.print("\nEnter your choice:");
            int option = sc.nextInt();

            switch (option) {
                case 1:
                System.out.println("============Genereate Bill====================\n");
                System.out.println("=====================Product List=======================");
                System.out.print("Product ID\t Product Name\n");
                ArrayList<String> list = product.getProducts();
                    for ( String l : list) {
                        System.out.println(l);
                    }
                System.out.println("==========================================================");
                    BillGenerator.generateBill();
                    break;
                case 2: 
                System.out.println("============Generate Report====================\n");
                    Report.generateReport();    
                    break;
                case 3:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Please Enter valid option");
                    break;
            }
        }

    }
}
