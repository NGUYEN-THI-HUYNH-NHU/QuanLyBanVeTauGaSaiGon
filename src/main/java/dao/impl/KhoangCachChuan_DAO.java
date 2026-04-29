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
import java.util.HashMap;
import java.util.Map;

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

    /**
     * Tải toàn bộ bảng khoảng cách chuẩn vào bộ nhớ dưới dạng Đồ thị (Graph).
     * @return Map<String, Map<String, Integer>> (Đồ thị: GaID_Nguồn -> ( GaID_Đích -> Khoảng cách ))
     */
    public Map<String, Map<String, Integer>> getAllKhoangCachMap(){
        Map<String, Map<String, Integer>> doThi = new HashMap<>();
        String sql = "SELECT gaID_Dau, gaID_Cuoi, khoangCachKm FROM KhoangCachChuan";
        try(Connection connection = connectDB.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()){
                String gaID_Dau = resultSet.getString("gaID_Dau").trim();
                String gaID_Cuoi = resultSet.getString("gaID_Cuoi").trim();
                int khoangCachKm = resultSet.getInt("khoangCachKm");

                doThi.putIfAbsent(gaID_Dau, new HashMap<>());
                doThi.get(gaID_Dau).put(gaID_Cuoi, khoangCachKm);

                doThi.putIfAbsent(gaID_Cuoi, new HashMap<>());
                doThi.get(gaID_Cuoi).put(gaID_Dau, khoangCachKm);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return doThi;
    }
}
