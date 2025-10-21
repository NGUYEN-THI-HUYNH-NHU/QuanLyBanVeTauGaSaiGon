package dao;

import connectDB.ConnectDB;
import entity.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class KhachHang_DAO {
    private ConnectDB connectDB;

    public KhachHang_DAO(){
        connectDB  = ConnectDB.getInstance();
        connectDB.connect();

    }

    public boolean themKhachHang(KhachHang kh){
        String sql = "INSERT INTO KhachHang (khachHangID, hoTen, soDienThoai, email, diaChi) VALUES (?, ?, ?, ?, ?)";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;

        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, kh.getKhachHangID());
            pstmt.setString(2, kh.getHoTen());
            pstmt.setString(3, kh.getSoDienThoai());
            pstmt.setString(4, kh.getEmail());
            pstmt.setString(5, kh.getDiaChi());
            int rs = pstmt.executeUpdate();
            if(rs > 0){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean capNhatKhachHang(KhachHang newKH){
        String upSQL = "UPDATE KhachHang SET hoTen = ?, soDienThoai = ?, email = ?, diaChi = ? WHERE khachHangID = ?";
        Connection con = connectDB.getConnection();
        PreparedStatement pstmt = null;
        try{
            pstmt = con.prepareStatement(upSQL);
            pstmt.setString(1, newKH.getHoTen());
            pstmt.setString(2,newKH.getSoDienThoai());
            pstmt.setString(3, newKH.getEmail());
            pstmt.setString(4, newKH.getDiaChi());
            pstmt.setString(5, newKH.getKhachHangID());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;


        }catch(SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public KhachHang timKhachHangTheoSDT(String sdt){
        String findSQL = "SELECT *FROM KhachHang WHERE soDienThoai = ?";
        Connection con = connectDB.getConnection();
        try{
            PreparedStatement pstmt = con.prepareStatement(findSQL);
            pstmt.setString(1, sdt);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                String id = rs.getString(1);
                String hoTen = rs.getString(2);
                String soDienThoai = rs.getString(3);
                String email = rs.getString(4);
                String diaChi = rs.getString(5);

                return new KhachHang(id, hoTen, soDienThoai, email, diaChi);
            }
            return null;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public List<KhachHang> getAllKhachHang(){
        List<KhachHang> listKH = new ArrayList<>();
        String getSQL = "SELECT *FROM KhachHang";
        Connection con = connectDB.getConnection();
        try{
            PreparedStatement pstmt = con.prepareStatement(getSQL);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String id = rs.getString(1);
                String hoTen = rs.getString(2);
                String soDienThoai = rs.getString(3);
                String email = rs.getString(4);
                String diaChi = rs.getString(5);

                listKH.add(new KhachHang(id, hoTen, soDienThoai, email, diaChi));
            }
            return listKH;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

}
