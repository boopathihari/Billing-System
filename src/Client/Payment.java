package Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Payment {
    static Scanner sc = new Scanner(System.in);

    public static boolean PaymentGateway(int BillID, ArrayList<Integer> PurchaseProductID,
            ArrayList<Integer> PurchaseQuantity, ArrayList<Integer> oldQuantity) {
        boolean isSuccess = false;
        System.out.println("Enter the Payment Mode");
        System.out.println("1.Cash");
        System.out.println("2.UPI");
        System.out.println("3.Card");

        int option = sc.nextInt();

        switch (option) {
            case 1:
                isSuccess = Payment("Cash", BillID, PurchaseProductID, PurchaseQuantity, oldQuantity, isSuccess);
                break;

            case 2:
                isSuccess = Payment("UPI", BillID, PurchaseProductID, PurchaseQuantity, oldQuantity, isSuccess);
                break;

            case 3:
                isSuccess = Payment("Card", BillID, PurchaseProductID, PurchaseQuantity, oldQuantity, isSuccess);
                break;

            default:
                System.out.println("Please enter the valid payment mode");
                break;
        }

        return isSuccess;
    }

    public static boolean Payment(String Mode, int BillID, ArrayList<Integer> PurchaseProductID,
            ArrayList<Integer> PurchaseQuantity, ArrayList<Integer> oldQuantity, boolean isSuccess) {
        Product product = new Product();

        double totalAmount = product.getTotalAmount(BillID);

        double totalAmountwithoutGST = totalAmount - totalAmount * (5.00 / 100);
        double roundedAmountWithoutGST = Math.round(totalAmountwithoutGST);
        double addedGST = totalAmountwithoutGST * (5.00 / 100);
        double roundedGST = Math.round(addedGST * 10.0) / 10.0;

        System.out.println("==================================================================");
        System.out.println("\nTotal Amount : \t$" + roundedAmountWithoutGST);
        System.out.println("CGST 2.5% + SGST 2.5% : $" + roundedGST);
        System.out.println("SUBTOTAL:\t $" + totalAmount);
        System.out.println("==================================================================");

        System.out.print("\nEnter the Payment Status (Done/NotDone) Y/N: ");
        String status = sc.next();

        if (status.equals("Y")) {
            System.out.println("Payment through the " + Mode + " is sucessfully Done");
            for (int i = 0; i < PurchaseProductID.size(); i++) {
                BillGenerator.updateProductQuantity(PurchaseProductID.get(i), PurchaseQuantity.get(i),
                        oldQuantity.get(i));
            }
            InsertPaymentDetails("Success", BillID, totalAmount, Mode);
            isSuccess = true;
        } else {
            System.out.println("Payment through the " + Mode + " is failed");
            InsertPaymentDetails("Failed", BillID, totalAmount, Mode);
        }

        return isSuccess;

    }

    public static void InsertPaymentDetails(String status, int BillID, double totalAmount, String Mode) {
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
