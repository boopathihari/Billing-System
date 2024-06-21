package Client;

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
}
