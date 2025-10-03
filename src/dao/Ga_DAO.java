package dao;

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
    
    public List<Ga> searchGaByPrefix(String prefix, int limit) {
        Connection con = connectDB.getConnection();
        String sql = "SELECT TOP (?) gaID, tenGa FROM Ga WHERE tenGa LIKE ? ORDER BY tenGa";
        List<Ga> gaList = null;
        
        try {
	        PreparedStatement ps = con.prepareStatement(sql);
	        ps.setInt(1, limit);
	        ps.setString(2, prefix + "%");
	        ResultSet rs = ps.executeQuery();
	        gaList = new ArrayList<>();
	        while (rs.next())
	            gaList.add(new Ga(rs.getString("gaID"), rs.getString("tenGa")));
	    } catch (SQLException e){
            e.printStackTrace();
        }
        
        return gaList;
    }

    public Ga getGaByTenGa(String tenGa) {
        Connection conn = connectDB.getConnection();
        String sql = "SELECT gaID, tenGa FROM Ga WHERE tenGa = ?";
        Ga ga = null;

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, tenGa);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ga = new Ga(rs.getString("gaID"), rs.getString("tenGa"));
        } catch (SQLException e){
            e.printStackTrace();
        }
        return ga;
    }
    
    public List<Ga> getGaByTenGaList(String tenGaTim) {
        List<Ga> dsGa = new ArrayList<>();
        Connection connection = connectDB.getConnection();
        String sql = "SELECT * FROM Ga WHERE LOWER(tenGa) LIKE ?";
        try{
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + tenGaTim + "%");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
               String gaID = resultSet.getString("gaID");
               String tenGa = resultSet.getString("tenGa");
               boolean isGaLon = resultSet.getBoolean("isGaLon");
                String tinhThanh = resultSet.getString("tinhThanh");
                dsGa.add(new Ga(gaID, isGaLon,tenGa, tinhThanh));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return dsGa;
    }
    
    public List<Ga> searchGaDenKhaThiByGaDi(String gaDiID, String prefixGaDen, int limit) {
	    Connection conn = connectDB.getConnection();
	    String sql = "SELECT DISTINCT TOP (?)"
	    			 + " cg2.gaID, g2.tenGa "
	                 + " FROM ChuyenGa cg1 "
	                 + " JOIN Chuyen c ON c.chuyenID = cg1.chuyenID "
	                 + " JOIN ChuyenGa cg2 ON cg2.chuyenID = cg1.chuyenID AND cg2.thuTu > cg1.thuTu "
	                 + " JOIN Ga g2 ON g2.gaID = cg2.gaID "
	                 + " WHERE cg1.gaID = ? "
	                 + " AND g2.tenGa LIKE ? "
	                 + " AND cg2.gaID != ?"
	                 + " ORDER BY g2.tenGa";
	    List<Ga> gaList = null;
		try {
	    	PreparedStatement ps = conn.prepareStatement(sql);
		    ps.setInt(1, limit);
		    ps.setString(2, gaDiID);
		    ps.setString(3, prefixGaDen + "%");
		    ps.setString(4, gaDiID);
		    ResultSet rs = ps.executeQuery();
		    gaList = new ArrayList<>();
		    while (rs.next())
		        gaList.add(new Ga(rs.getString("gaID"), rs.getString("tenGa")));
		}catch (SQLException e) {
            e.printStackTrace();
		}
	    return gaList;
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
                boolean isGaLon = resultSet.getBoolean("isGaLon");
                String tinhThanh = resultSet.getString("tinhThanh");
                gaDS.add(new Ga(gaID,isGaLon, tenGa, tinhThanh));
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
                boolean isGaLon = resultSet.getBoolean("isGaLon");
                String tinhThanh = resultSet.getString("tinhThanh");
                return new Ga(gaID,isGaLon, tenGa, tinhThanh);
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
