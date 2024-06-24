package Client;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class BillGenerator {

    public static void generateBill() {

        Scanner sc = new Scanner(System.in);
        int CustomerID = Customer.addCustomer();
        int BillID = addBill(CustomerID);
        double TotalAmount = 0;
        ArrayList<Integer> PurchaseProductID = new ArrayList<>();
        ArrayList<Integer> PurchaseQuantity = new ArrayList<>(); 
        ArrayList<Integer> oldQuantity = new ArrayList<>(); 

        System.out.println("=========Add Products==========");
        while (true) {
            System.out.print("\nEnter the Product ID:\t");
            int ProductID = sc.nextInt();

            ArrayList<String> list = Product.getProduct(ProductID);

            if (list.isEmpty()) {
                System.out.println("!!!!Product not found please enter the valid Product ID!!!!");
                continue;
            }

            String proID = list.get(0);
            String productName = list.get(1);
            String productPrice = list.get(2);
            String proQuantity = list.get(3);

            System.out.println("==========Product detail for the entered ID=========");
            System.out.println("Product Name: " + productName);
            System.out.println("Price: " + productPrice);
            System.out.println("Total quantity available: " + proQuantity);

            System.out.print("\nEnter the Quantity: ");
            int quantity = sc.nextInt();

            if (quantity > Integer.parseInt(proQuantity) || proQuantity.equals("0")) {
                System.out.println("!!!!!Entered quantity is out of stack");
                System.out.print("\nDo you want add more Product(Y/N):");
                String s = sc.next();

            if (s.charAt(0) == 'N') {
                break;
            }
                continue;
            }


            addProductToBill(BillID, ProductID, quantity, Double.parseDouble(productPrice));
            PurchaseProductID.add(ProductID);
            PurchaseQuantity.add(quantity);
            oldQuantity.add(Integer.parseInt(proQuantity));

            // updateProductQuantity(ProductID,quantity,Integer.parseInt(proQuantity));
            TotalAmount += quantity * Double.parseDouble(productPrice);
            System.out.println("Do you want add more Product(Y/N):");
            String s = sc.next();

            if (s.charAt(0) == 'N') {
                break;
            }

        }

        updateBill(BillID,TotalAmount);

        System.out.print("\nEnter Y/N for coupon:");
        String CouponOption = sc.next();
        
        if(CouponOption.equals("Y")){
            Coupon.ApplyCoupon(BillID);
        }

        System.out.println("\n\n===============Payment==================");

        boolean isSuccess = Payment.PaymentGateway(BillID,PurchaseProductID,PurchaseQuantity,oldQuantity);


        if (isSuccess == true && CouponOption.equals("Y")) {
            System.out.println("\n=================Bill Details================");
            DisplayBill(BillID,TotalAmount);
        }else if(isSuccess == true && CouponOption.equals("N")){
            System.out.println("\n=================Bill Details================");
            DisplayBillNoCoupon(BillID,TotalAmount);
        }
    }

    

    public static int addBill(int customerID) {
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "INSERT INTO bills (customer_id) values(?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, customerID);
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void addProductToBill(int BillID, int ProductID, int quantity, Double productPrice) {
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "INSERT INTO bill_items (Bill_id,product_id,quantity,price) values(?,?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, BillID);
            preparedStatement.setInt(2, ProductID);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setDouble(4, productPrice);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateProductQuantity(int ProductID, int quantity,int oldQuantity){
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "UPDATE products SET stock_quantity=? where product_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, oldQuantity-quantity);
            preparedStatement.setInt(2, ProductID);
            preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBill(int BillID, double TotalAmount){
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "UPDATE bills SET total_amount=? where Bill_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setDouble(1, TotalAmount);
            preparedStatement.setDouble(2, BillID);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addCoupon(int BillID,int couponID){
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "UPDATE bills SET couponID=? where Bill_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setDouble(1, couponID);
            preparedStatement.setDouble(2, BillID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void DisplayBill(int BillID, double TotalAmount){
        System.out.println("-------------------------");
        System.out.println("Customer Information:");
        System.out.println("-------------------------");

        ArrayList<String> cusDetails= Customer.getCustomerDetails(BillID);

        System.out.println("Customer ID: "+cusDetails.get(0));
        System.out.println("Name: "+cusDetails.get(1));
        System.out.println("Contact Number: "+cusDetails.get(2));

        System.out.println("\n-------------------------");
        System.out.println("Bill Information:");
        System.out.println("-------------------------");

        ArrayList<String> billDetails = getBillDetails(BillID);

        
        double discountPercent = Double.parseDouble(billDetails.get(4));
        double couponDiscount = TotalAmount*discountPercent/100;

        System.out.println("Bill ID: "+billDetails.get(0));
        System.out.println("Bill Date: "+billDetails.get(1));
        System.out.println("Total Amount (Before Discount and Coupon): $"+TotalAmount);
        System.out.println("Coupon Code: "+billDetails.get(3));
        System.out.println("Coupon Discount Percentage: "+billDetails.get(4)+"%");
        System.out.println("Coupon Discount Amount: $"+couponDiscount);
        System.out.println("Additional Discount: 0");

       

        ArrayList<String> productsDetails = Product.getBillProduct(BillID);

        System.out.println("\n\n=========Invoice=============");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("| Product ID | Product Name | Quantity | Price per Quantity | Total Price |" );
        for (String details : productsDetails) {
            System.out.println(details);
        }
        System.out.println("-------------------------------------------------------------------------------------");

        System.out.println("\n========================================================================================");
        System.out.println("\t\t\tTotal Amount Payed (After Discount and Coupon): "+billDetails.get(2));
        System.out.println("=========================================================================================\n\n");
        System.out.println("\nThank you for your Purchase!");
    }


    public static void DisplayBillNoCoupon(int BillID, double TotalAmount){
        System.out.println("-------------------------");
        System.out.println("Customer Information:");
        System.out.println("-------------------------");

        ArrayList<String> cusDetails= Customer.getCustomerDetails(BillID);

        System.out.println("Customer ID: "+cusDetails.get(0));
        System.out.println("Name: "+cusDetails.get(1));
        System.out.println("Contact Number: "+cusDetails.get(2));

        System.out.println("\n-------------------------");
        System.out.println("Bill Information:");
        System.out.println("-------------------------");

        ArrayList<String> billDetails = getBillDetailsNoCoupon(BillID);
       

        System.out.println("Bill ID: "+billDetails.get(0));
        System.out.println("Bill Date: "+billDetails.get(1));
        System.out.println("Additional Discount: 0");


        ArrayList<String> productsDetails = Product.getBillProduct(BillID);

        System.out.println("\n\n=========Invoice=============");
        System.out.println("-------------------------------------------------------------------------------------");
        System.out.println("| Product ID | Product Name | Quantity | Price per Quantity | Total Price |" );
        for (String details : productsDetails) {
            System.out.println(details);
        }
        System.out.println("-------------------------------------------------------------------------------------");

        System.out.println("\n========================================================================================");
        System.out.println("\t\t\tTotal Amount Payed : "+billDetails.get(2));
        System.out.println("=========================================================================================\n\n");
        System.out.println("\nThank you for your Purchase!");
    }

    
    public static ArrayList<String> getBillDetailsNoCoupon(int BillID){
        ArrayList<String> list = new ArrayList<>();
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT b.Bill_id,b.bill_date,b.total_amount from bills b where b.Bill_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, BillID);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();


            if(rs.next()){
                int BillId = rs.getInt(1);
                Date BillDate = rs.getDate(2);
                Double Amount = rs.getDouble(3);

                list.add(String.valueOf(BillId));
                list.add(String.valueOf(BillDate));
                list.add(String.valueOf(Amount));

            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
        
    }
    
   

    public static ArrayList<String> getBillDetails(int BillID){
        ArrayList<String> list = new ArrayList<>();
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT b.Bill_id,b.bill_date,b.total_amount,c.code,c.discount_percentage from bills b join coupons c on c.coupon_id = b.couponID where b.Bill_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, BillID);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();


            if(rs.next()){
                int BillId = rs.getInt(1);
                Date BillDate = rs.getDate(2);
                double totalAmount = rs.getDouble(3);
                String code = rs.getString(4);
                String discountPercent = rs.getString(5);


                list.add(String.valueOf(BillId));
                list.add(String.valueOf(BillDate));
                list.add(String.valueOf(totalAmount));
                list.add(String.valueOf(code));
                list.add(String.valueOf(discountPercent));

            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
        
    }



    public static int getBillID(int customerID) {
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "select Bill_id from bills where customer_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, customerID);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
