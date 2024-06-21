package Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Payment {
    static Scanner sc= new Scanner(System.in);

    public static void PaymentGateway(int BillID){
        System.out.println("Enter the Payment Mode");
        System.out.println("1.Cash");
        System.out.println("2.UPI");
        System.out.println("3.Credit Card");
        
        int option =  sc.nextInt();

        switch (option) {
            case 1:
                Payment("Cash",BillID);
                break;
        
            case 2:
                Payment("UPI",BillID);
                break;
        
            case 3:
                Payment("Credit Card",BillID);
                break;
        
            default:
                System.out.println("Please enter the valid payment mode");
                break;
        }

    }

    public static void Payment(String Mode,int BillID) {
        Product product = new Product();

        double totalAmount = product.getTotalAmount(BillID);

        System.out.println("\nTotal Amount to be Pay: $"+totalAmount);

            System.out.print("\nEnter the Payment Status (Done/NotDone) Y/N: ");
            String status = sc.next();
            
            if(status.equals("Y")){
                System.out.println("Payment through the "+Mode+" is sucessfully Done");
                InsertPaymentDetails("Success",BillID,totalAmount,Mode);
            }else{
                System.out.println("Payment through the "+Mode+" is failed");
                InsertPaymentDetails("Failed",BillID,totalAmount,Mode);
            }

    }


    public static void InsertPaymentDetails(String status,int BillID, double totalAmount,String Mode){
         try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "INSERT INTO payments (Bill_id,amount,payment_method,payment_status) values(?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, BillID);
            preparedStatement.setDouble(2, totalAmount);
            preparedStatement.setString(3, Mode);
            preparedStatement.setString(4, status);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
