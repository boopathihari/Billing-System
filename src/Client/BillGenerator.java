package Client;

import java.sql.*;
import java.util.*;

public class BillGenerator {

    public static void generateBill() {
        Product product = new Product();
        Payment payment = new Payment();
        Coupon coupon = new Coupon();

        Scanner sc = new Scanner(System.in);
        Customer customer = new Customer();
        int CustomerID = customer.addCustomer();
        int BillID = addBill(CustomerID);
        double TotalAmount = 0;

        System.out.println("=========Add Products==========");
        while (true) {
            System.out.println("Enter the Product ID:");
            int ProductID = sc.nextInt();

            ArrayList<String> list = product.getProduct(ProductID);

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
                System.out.println("Do you want add more Product(Y/N):");
                String s = sc.next();

            if (s.charAt(0) == 'N') {
                break;
            }
                continue;
            }


            addProductToBill(BillID, ProductID, quantity, Double.parseDouble(productPrice));
            updateProductQuantity(ProductID,quantity,Integer.parseInt(proQuantity));
            TotalAmount += quantity * Double.parseDouble(productPrice);
            System.out.println("Do you want add more Product(Y/N):");
            String s = sc.next();

            if (s.charAt(0) == 'N') {
                break;
            }

        }

        updateBill(BillID,TotalAmount);

        System.out.print("\nEnter Y/N for coupon:");
        String option = sc.next();
        
        if(option.equals("Y")){
            coupon.ApplyCoupon(BillID);
        }

        System.out.println("===============Payment==================");
        payment.PaymentGateway(BillID);

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
}
