package dao;/*
 * @ (#) KhoangCachChuan_DAO.java   1.0     28/10/2025
package dao;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 28/10/2025
 */

import connectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KhoangCachChuan_DAO {
    private final ConnectDB connectDB;

    public KhoangCachChuan_DAO() {
        connectDB = ConnectDB.getInstance();
    }

    /**
     * Lấy khoảng cách chuẩn giữa hai ga liền kề
     *@param gaID_Dau ID Ga Xuất Phát của đoạn (ví dụ: 'HNI').
     * @param gaID_Cuoi ID Ga Đích của đoạn (ví dụ: 'VIN').
     * @return Khoảng cách thực tế giữa hai ga, hoặc -1 nếu không tìm thấy.
     */
    public int getKhoangCachDoan(String gaID_Dau,String gaID_Cuoi){
        String sql = "SELECT khoangCachKm " +
                "FROM KhoangCachChuan " +
                "WHERE (GaID_Dau = ? AND GaID_Cuoi = ?)" +
                "OR (GaID_Cuoi = ? AND GaID_Dau = ?)";
        try(Connection con = connectDB.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)){
            pstmt.setString(1, gaID_Dau);
            pstmt.setString(2, gaID_Cuoi);
            pstmt.setString(3, gaID_Cuoi);
            pstmt.setString(4, gaID_Dau);

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return rs.getInt("khoangCachKm");
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

}
