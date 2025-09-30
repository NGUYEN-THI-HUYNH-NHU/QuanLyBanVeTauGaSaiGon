package dao;

import connectDB.ConnectDB;
import entity.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KhachHang_DAO {
    private ConnectDB connectDB;

    public KhachHang_DAO(){
        connectDB  = ConnectDB.getInstance();
        connectDB.connect();

    }

    public boolean insertCustomer(KhachHang kh){
        String sql = "INSERT INTO KhachHang (khachHangID, hoTen, soDienThoai, email, diaChi) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, kh.getKhachHangID());
            pstmt.setString(2, kh.getHoTen());
            pstmt.setString(3, kh.getSoDienThoai());
            pstmt.setString(4, kh.getEmail());
            pstmt.setString(5, kh.getDiaChi());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }


}
