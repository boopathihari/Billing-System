package Client;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        BillGenerator billGeneretor = new BillGenerator();
        Report report = new Report();
        Product product = new Product();

        while (true) {
            System.out.println("\n===============Billing System Application==============\n");
            System.out.println("1.Generate Bill");
            System.out.println("2.Generate Report");
            System.out.println("3.Add Products");
            System.out.println("4.Exit");

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
                    billGeneretor.generateBill();
                    
                    break;
                case 2: 
                System.out.println("============Generate Report====================\n");
                    // report.generateReport();    
                    break;
                case 3: 
                    System.out.println("============Add Product====================\n");
                    // product.addProduct();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    System.exit(0);
                default:
                    System.out.println("Please Enter valid option");
                    break;
            }
        }

    }
}
