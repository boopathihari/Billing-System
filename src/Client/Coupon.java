package Client;

import java.util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.sql.Date;

public class Coupon {
    public static boolean ApplyCoupon(int BillID) {
        boolean isValidCoupon=true;

        Scanner sc = new Scanner(System.in);

        System.out.print("\nEnter the Coupon Code:");
        String code = sc.next();
        LocalDate todayDate = LocalDate.now();

        Date date = Date.valueOf(todayDate);
        double discountPercent = validCoupon(date, code);

        if (discountPercent != -1) {
            updateCoupon(code);
            System.out.println("\nCoupon Applied Successfully!!!!!");

            double totalAmount = Product.getTotalAmount(BillID);
            double reducedAmount = totalAmount - totalAmount * (discountPercent / 100);

            System.out.println("========================================================");
            System.out.println("Total Amount after coupon applied: " + reducedAmount);
            System.out.println("========================================================");

            

            Product.updateTotalAmount(reducedAmount, BillID);
            
            int couponID = getCouponID(code);
            BillGenerator.addCoupon(BillID,couponID);


        } else {
            System.out.println("Coupon Not Valid");
            isValidCoupon=false;
        }

        return isValidCoupon;
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

    public static int getCouponID(String code){
        int couponID=-1;

        try (Connection conn = Server.DBConnection.getConnection()) {
            String sql = "SELECT coupon_id from coupons where code=?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, code);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                couponID = rs.getInt("coupon_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return couponID;
    }
}
