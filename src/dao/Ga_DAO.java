package dao;/*
 * @ (#) Ga_DAO.java   1.0     26/09/2025
package dao;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 26/09/2025
 */

import connectDB.ConnectDB;
import entity.Ga;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Ga_DAO {
    private ConnectDB connectDB;

    public Ga_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    public List<Ga> getAllGa(){
        Connection connection = connectDB.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Ga> gaDS = null;

        try{
            statement = connection.prepareStatement("SELECT * FROM Ga");
            resultSet = statement.executeQuery();
            gaDS = new ArrayList<Ga>();
            while(resultSet.next()){
                String gaID = resultSet.getString("gaID");
                String tenGa = resultSet.getString("tenGa");
                String tinhThanh = resultSet.getString("tinhThanh");
                gaDS.add(new Ga(gaID, tenGa, tinhThanh));
            }
        }catch (SQLException e){
            e.printStackTrace();
        } finally {
            connectDB.close(statement, resultSet);
        }
        return gaDS;
    }

    public Ga getGaByIDTim(String gaIDTim) {
        Connection connection = connectDB.getConnection();
        try {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM Ga WHERE gaID = ?");
            statement.setString(1, gaIDTim);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                String gaID = resultSet.getString("gaID");
                String tenGa = resultSet.getString("tenGa");
                String tinhThanh = resultSet.getString("tinhThanh");
                return new Ga(gaID, tenGa, tinhThanh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean themGa(Ga gaMoi) {
        Connection connection = connectDB.getConnection();
        String insertSQL = "INSERT INTO Ga (gaID,tenGa, tinhThanh) VALUES (?,?,?)";
        try{
            PreparedStatement statement = connection.prepareStatement(insertSQL);
            statement.setString(1, gaMoi.getGaID());
            statement.setString(2, gaMoi.getTenGa());
            statement.setString(3, gaMoi.getTinhThanh());
            int hangAnhuong = statement.executeUpdate();
            System.out.println(hangAnhuong + " hàng đã được thêm vào thành công!");
            return hangAnhuong > 0;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public int capNhatGa(String gaIDSua, Ga gaCapNhat){
        Connection connection = connectDB.getConnection();
        String updateSQL = "UPDATE Ga SET tenGa = ?, tinhThanh = ? WHERE gaID = ?";
        try{
            PreparedStatement statement = connection.prepareStatement(updateSQL);
            statement.setString(1, gaCapNhat.getTenGa());
            statement.setString(2, gaCapNhat.getTinhThanh());
            statement.setString(3, gaIDSua);
            int hangAnhHuong = statement.executeUpdate();
            System.out.println(hangAnhHuong + " hàng đã được cập nhật thành công!");
            return hangAnhHuong;
    }catch(SQLException e){
            e.printStackTrace();
            System.out.println("Dữ liệu bị trùng, vui lòng kiểm tra lại!");
            return 0;
        }
    }
}
