package dao;/*
 * @ (#) Tuyen_DAO.java   1.0     26/09/2025
package dao;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 26/09/2025
 */

import connectDB.ConnectDB;
import entity.Tuyen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Tuyen_DAO {
    private ConnectDB connectDB;

    public Tuyen_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public List<Tuyen> getAllTuyen(){
        ArrayList<Tuyen> tuyenDS = new ArrayList<Tuyen>();
        Connection connection = connectDB.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try{
            statement = connection.prepareStatement("SELECT * FROM Tuyen");
            resultSet = statement.executeQuery();

            while(resultSet.next()){
                String tuyenID = resultSet.getString("tuyenID");
                String gaDiID = resultSet.getString("gaDiID");
                String gaDenID = resultSet.getString("gaDenID");
                int khoangCachKm = resultSet.getInt("khoangCachKm");
                int thoiGianDuKienPhut = resultSet.getInt("thoiGianDuKienPhut");
                tuyenDS.add(new Tuyen(tuyenID, new Ga_DAO().getGaByIDTim(gaDiID), new Ga_DAO().getGaByIDTim(gaDenID), khoangCachKm, thoiGianDuKienPhut));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally{
            connectDB.close(statement, resultSet);
        }

        return tuyenDS;
    }

    public Tuyen getTuyenByID(String tuyenIDTim){
        Connection connection = connectDB.getConnection();
        String selectByIDSQL = "SELECT * FROM Tuyen WHERE tuyenID = ?";
        try{
            PreparedStatement statement = connection.prepareStatement(selectByIDSQL);
            statement.setString(1, tuyenIDTim);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String tuyenID = resultSet.getString("tuyenID");
                String gaDiID = resultSet.getString("gaDiID");
                String gaDenID = resultSet.getString("gaDenID");
                int khoangCachKm = resultSet.getInt("khoangCachKm");
                int thoiGianDuKienPhut = resultSet.getInt("thoiGianDuKienPhut");
                return new Tuyen(tuyenID, new Ga_DAO().getGaByIDTim(gaDiID), new Ga_DAO().getGaByIDTim(gaDenID), khoangCachKm, thoiGianDuKienPhut);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean themTuyenMoi(Tuyen tuyenMoi){
        Connection connection = connectDB.getConnection();
        String insertSQl = "INSERT INTO Tuyen(TuyenID, gaDiID, gaDenID, khoangCachKm, thoiGianDuKienPhut) VALUES(?, ?, ?, ?, ?)";
        try{
            PreparedStatement statement = connection.prepareStatement(insertSQl);
            statement.setString(1, tuyenMoi.getTuyenID());
            statement.setString(2, tuyenMoi.getGaDi().getGaID());
            statement.setString(3, tuyenMoi.getGaDen().getGaID());
            statement.setInt(4, tuyenMoi.getKhoangCachKm());
            statement.setInt(5, tuyenMoi.getThoiGianDuKienPhut());
            int hangAnhHuong = statement.executeUpdate();
            System.out.println(hangAnhHuong + " hàng đã được thêm thành công!");
            return hangAnhHuong > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public int capNhatTuyenByID(String tuyenIDSua, Tuyen capNhatTuyen) {
        Connection connection = connectDB.getConnection();
        String updateSQL = "UPDATE Tuyen SET gaDiID = ?, gaDenID = ?, khoangCachKm = ?, thoiGianDuKienPhut = ? WHERE tuyenID = ?";
        try{
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setString(1, capNhatTuyen.getGaDi().getGaID());
            statement.setString(2, capNhatTuyen.getGaDen().getGaID());
            statement.setInt(3, capNhatTuyen.getKhoangCachKm());
            statement.setInt(4, capNhatTuyen.getThoiGianDuKienPhut());
            statement.setString(5, tuyenIDSua);
            int hangAnhHuong = statement.executeUpdate();
            System.out.println(hangAnhHuong + " hàng đã được cập nhật thành công!");
            return hangAnhHuong;
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Dữ liệu không thay đổi, vui lòng kiểm tra lại!");
            return 0;
        }
    }


}
