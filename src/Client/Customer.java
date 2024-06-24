package Client;

import java.util.ArrayList;
import java.util.Scanner;


import java.sql.*;

public class Customer {
    public static int addCustomer(){

        Scanner sc = new Scanner(System.in);

        String mobileNumber;
        String name;

        System.out.print("\nEnter the Customer Name: ");
        name = sc.nextLine();
        
        while (true) {
            System.out.print("\nEnter the Customer Mobile Number:");
            mobileNumber = sc.nextLine();
            
            if(mobileNumber.length() != 10){
                System.out.println("!!!!!!!invalid Mobile number please enter valid Number!!!!!!");
            }else{
                break;
            }
        }

        try(Connection conn = Server.DBConnection.getConnection()) {
            String sql = "INSERT INTO customers (name,phone) values (?,?)";
            PreparedStatement statement = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setString(2, mobileNumber);
            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            
            if(rs.next()){
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }


    public static ArrayList<String> getCustomerDetails(int BillID){
        ArrayList<String> list = new ArrayList<>();
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT c.customer_id,c.name,c.phone,b.bill_date from customers c join bills b on c.customer_id = b.customer_id where b.Bill_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, BillID);
            preparedStatement.executeQuery();
            ResultSet rs = preparedStatement.getResultSet();

            if(rs.next()){
                int id = rs.getInt(1);
                String name = rs.getString(2);
                String phone = rs.getString(3);
                Date date = rs.getDate(4);


                list.add(String.valueOf(id));
                list.add(String.valueOf(name));
                list.add(String.valueOf(phone));
                list.add(String.valueOf(date));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
