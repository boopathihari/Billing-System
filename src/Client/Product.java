package Client;

import java.sql.*;
import java.util.*;

public class Product {
    public static ArrayList<String> getProduct(int productID){
        ArrayList<String> productDetails = new ArrayList<>();
        
        try(Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT * from products where product_id=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, productID);
            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                int id = rs.getInt(1);
                String productName = rs.getString(2);
                double price = rs.getDouble(3);
                int quantity = rs.getInt(4);
                
                productDetails.add(String.valueOf(id));
                productDetails.add(productName);
                productDetails.add(String.valueOf(price));
                productDetails.add(String.valueOf(quantity));

            }else{
                return productDetails;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productDetails;
    }

    public static ArrayList<String> getProducts(){
        ArrayList<String> list = new ArrayList<>();
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT * from products";
            PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();

            while(rs.next()){
                int id = rs.getInt("product_id");
                String productName = rs.getString("name");
                list.add(id+"\t\t "+productName);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return list;
    }


    public static double getTotalAmount(int BillID){
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT total_amount from bills where Bill_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, BillID);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();

            if(rs.next()){
                return rs.getDouble("total_amount");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }


    public static void updateTotalAmount(double reducedAmount,int billID){
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "UPDATE bills SET total_amount=? where Bill_id=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDouble(1, reducedAmount);
            preparedStatement.setInt(2, billID);
            preparedStatement.executeUpdate();

           
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
