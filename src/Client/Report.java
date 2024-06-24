package Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class Report {
    public static void generateReport() {
        Scanner sc = new Scanner(System.in);

        System.out.println("1.Bill Report");
        System.out.println("2.Stock Report");    
        System.out.println("3.Sales Report");    
        System.out.println("4.Customer Specific Report");    
        System.out.print("\nEnter your choice:");

        int option = sc.nextInt();
        
        switch (option) {
            case 1:
                BillReport();
                break;
            case 2:
                StockReport();
                break;
            case 3:
                SalesReport();
                break;
            case 4:
                CustomerReport();
                break;
            default:
                break;
        }
    }


    public static void BillReport(){
      
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "select b.Bill_id , b.bill_date, b.total_amount , c.customer_id,c.name,c.phone,p.payment_id,p.payment_date,p.payment_method,  p.payment_status from bills b join customers c on b.customer_id = c.customer_id join payments p on p.Bill_id = b.Bill_id";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();


            int cnt=1;
            while(rs.next()){
                int BillId = rs.getInt(1);
                Date BillDate = rs.getDate(2);
                double totalAmount = rs.getDouble(3);

                int customerID = rs.getInt(4);
                String customerName = rs.getString(5);
                String ContactNumber = rs.getString(6);

                int PaymentID = rs.getInt(7);
                Date PaymentDate = rs.getDate(8);
                String paymentMethod = rs.getString(9);
                String paymentStatus = rs.getString(10);
                



                System.out.println("\n\n-------------------------------------------------------------------");
                System.out.println("===============Bill Report "+cnt+" ===============");
                System.out.println("\nBill Info\n");
                System.out.println("Bill ID \t: "+BillId);
                System.out.println("Bill Date\t: "+BillDate);
                System.out.println("Total Amount\t: "+totalAmount);

                System.out.println("\n\n");
                System.out.println("Customer Info\n");
                System.out.println("Customer ID\t: "+customerID);
                System.out.println("Customer Name\t: "+customerName);
                System.out.println("Contact Number\t: "+ContactNumber);

                
                System.out.println("\n\n");
                System.out.println("Payment Info\n");
                System.out.println("Payment ID\t: "+PaymentID);
                System.out.println("Payment Date\t: "+PaymentDate);
                System.out.println("Payment Method\t: "+paymentMethod);
                System.out.println("Payment Status\t: "+paymentStatus);

                System.out.println("-------------------------------------------------------------------");
                cnt++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void StockReport(){
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "select * from products";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();


            int cnt=1;
            while(rs.next()){
                int productID = rs.getInt(1);
                String Name = rs.getString(2);
                double unitPrice = rs.getDouble(3);
                int stockQuantity = rs.getInt(4);



                System.out.println("\n\n-------------------------------------------------------------------");
                System.out.println("===============Stock Report "+cnt+" ===============");
                System.out.println("\nStock Info\n");
                System.out.println("Product ID \t: "+productID);
                System.out.println("Product Name\t: "+Name);
                System.out.println("Unit Price\t: "+unitPrice);
                System.out.println("Stock Quantity\t: "+stockQuantity);

                System.out.println("-------------------------------------------------------------------");
                cnt++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SalesReport(){
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "select b.Bill_id, b.bill_date,c.name, b.total_amount from bills b join customers c on b.customer_id = c.customer_id";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();


            int cnt=1;
            while(rs.next()){
                int BillID = rs.getInt(1);
                Date Date = rs.getDate(2);
                String custName = rs.getString(3);
                double total_amount = rs.getDouble(4);



                System.out.println("\n\n-------------------------------------------------------------------");
                System.out.println("===============Sales Report "+cnt+" ===============");
                System.out.println("\n Sales Info\n");
                System.out.println("Bill ID \t: "+BillID);
                System.out.println("Date \t\t: "+Date);
                System.out.println("Customer Name\t: "+custName);
                System.out.println("Total Amount\t: $"+total_amount);
                System.out.println("\nItem Sold: \n");
                Product.getSaleProduct(BillID);
                System.out.println("-------------------------------------------------------------------");
                cnt++;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void CustomerReport(){
        Scanner sc = new Scanner(System.in);

        System.out.print("\nEnter the Customer ID:\t");
        int Cusid = sc.nextInt();

        int BillID = BillGenerator.getBillID(Cusid);
        ArrayList<String> list = Customer.getCustomerDetails(BillID);


        System.out.println("\nCustomer Info\n");
        System.out.println("Customer ID:\t"+list.get(0));
        System.out.println("Customer Name:\t"+list.get(1));
        System.out.println("Contact Number:\t"+list.get(2));
        System.out.println("Date:\t"+list.get(3));


        if(BillID == -1){
            System.out.println("User Not Found");
        }else{
            System.out.println("\n\n===============Purchase History================");
            Product.getSaleProduct(BillID);
        }

    }

}
