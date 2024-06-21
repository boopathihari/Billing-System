package Client;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.sql.Date;

public class Coupon {
    public static void ApplyCoupon(int BillID) {
        Scanner sc = new Scanner(System.in);

        Product product = new Product();

        System.out.print("\nEnter the Coupon Code:");
        String code = sc.next();
        LocalDate todayDate = LocalDate.now();

        Date date = Date.valueOf(todayDate);
        double discountPercent = validCoupon(date, code);

        if (discountPercent != -1) {
            updateCoupon(code);
            System.out.println("Coupon Applied Successfully");

            double totalAmount = product.getTotalAmount(BillID);
            double reducedAmount = totalAmount - totalAmount * (discountPercent / 100);

            System.out.println("========================================================");
            System.out.println("Total Amount after coupon applied: " + reducedAmount);
            System.out.println("========================================================");

            product.updateTotalAmount(reducedAmount, BillID);

        } else {
            System.out.println("Coupon Not Valid");
        }

    }

    public static double validCoupon(Date todayDate, String code) {
        double discountPercent = -1;

        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT discount_percentage from coupons where valid_from <= ? and ? <= valid_to and code=? and times_used < usage_limit";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setDate(1, todayDate);
            preparedStatement.setDate(2, todayDate);
            preparedStatement.setString(3, code);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                discountPercent = rs.getDouble("discount_percentage");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return discountPercent;
    }

    public static void updateCoupon(String code) {
        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "UPDATE coupons set times_used=times_used+1 where code=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, code);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
