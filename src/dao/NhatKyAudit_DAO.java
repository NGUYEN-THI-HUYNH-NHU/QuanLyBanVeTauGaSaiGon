package dao;

import connectDB.ConnectDB;
import entity.NhatKyAudit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhatKyAudit_DAO {
    private final ConnectDB connectDB;

    public NhatKyAudit_DAO() {
        connectDB = ConnectDB.getInstance();
    }

//    /**
//     * Ghi nhật ký audit vào cơ sở dữ liệu.
//     * @param nhatKy Loại nhật ký audit
//     * @param chiTiet Mô tả chi tiết về hành động
//     * @param nhanVienID Người thực hiện hành động
//     * @param thoiGian Thời gian thực hiện hành động
//     * @param doiTuongThaoTac Đối tượng bị thao tác (ví dụ: "VE", "TUYEN")
//     * @param doiTuongID ID của đối tượng bị thao tác
//     */

    //ghi vào nhât ký
    public void ghiNhatKyAudit(NhatKyAudit nhatKy) {
        String sql = "INSERT INTO NhatKyAudit (nhatKyID, doiTuongID, nhanVienID, thoiDiemThaoTac, chiTiet, doiTuongThaoTac)" +
                " VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nhatKy.getNhatKyAuditID());
            ps.setString(2, nhatKy.getDoiTuongID());
            ps.setString(3, nhatKy.getNhanVienID());
            ps.setString(4, nhatKy.getThoiDiemThaoTac().toString());
            ps.setString(5, nhatKy.getChiTiet());
            ps.setString(6, nhatKy.getDoiTuongThaoTac());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Lấy danh sách nhật ký
    public List<NhatKyAudit> layDanhSachNhatKy() {
        String sql = "SELECT * FROM NhatKyAudit";
        List<NhatKyAudit> danhSachNhatKy = new ArrayList<>();

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            while (rs.next()) {
                NhatKyAudit nhatKy = new NhatKyAudit(
                        rs.getString("nhatKyID"),
                        rs.getString("doiTuongID"),
                        rs.getString("nhanVienID"),
                        rs.getObject("thoiDiemThaoTac", java.time.LocalDateTime.class),
                        rs.getString("chiTiet"),
                        rs.getString("doiTuongThaoTac")
                );
                danhSachNhatKy.add(nhatKy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return danhSachNhatKy;
    }

    //get danh sach nhat ky audit theo nhan vien ID
    public List<NhatKyAudit> layDanhSachNhatKyTheoNhanVien(String NhanVienID){
        String sql = "SELECT * FROM NhatKyAudit WHERE nhanVienID = ?";
        List<NhatKyAudit> danhSachNhatKy = new ArrayList<>();
        try(Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, NhanVienID);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            while(rs.next()){
                NhatKyAudit nhatKy = new NhatKyAudit(
                        rs.getString("nhatKyID"),
                        rs.getString("doiTuongID"),
                        rs.getString("nhanVienID"),
                        rs.getObject("thoiDiemThaoTac", java.time.LocalDateTime.class),
                        rs.getString("chiTiet"),
                        rs.getString("doiTuongThaoTac")
                );
                danhSachNhatKy.add(nhatKy);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return danhSachNhatKy;
    }

    //get danh sach nhat ky audit theo doi tuong thao tac
    public List<NhatKyAudit> layDanhSachNhatKyTheoDoiTuong(String doiTuongThaoTac){
        String sql = "SELECT * FROM NhatKyAudit WHERE doiTuongThaoTac = ?";
        List<NhatKyAudit> danhSachNhatKy = new ArrayList<>();
        try(Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.setString(1, doiTuongThaoTac);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            while(rs.next()){
                NhatKyAudit nhatKy = new NhatKyAudit(
                        rs.getString("nhatKyID"),
                        rs.getString("doiTuongID"),
                        rs.getString("nhanVienID"),
                        rs.getObject("thoiDiemThaoTac", java.time.LocalDateTime.class),
                        rs.getString("chiTiet"),
                        rs.getString("doiTuongThaoTac")
                );
                danhSachNhatKy.add(nhatKy);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return danhSachNhatKy;
    }

    //get danh sach nhat ky audit theo khoang thoi gian
    public List<NhatKyAudit> layDanhSachNhatKyTheoKhoangThoiGian(LocalDate tu, LocalDate den){
        String sql = "SELECT * FROM NhatKyAudit WHERE thoiDiemThaoTac BETWEEN ? AND ?";
        List<NhatKyAudit> danhSachNhatKy = new ArrayList<>();
        try(Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){

            ps.setObject(1, tu.atStartOfDay());
            ps.setObject(2, den.atTime(23, 59, 59));
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            while(rs.next()){
                NhatKyAudit nhatKy = new NhatKyAudit(
                        rs.getString("nhatKyID"),
                        rs.getString("doiTuongID"),
                        rs.getString("nhanVienID"),
                        rs.getObject("thoiDiemThaoTac", java.time.LocalDateTime.class),
                        rs.getString("chiTiet"),
                        rs.getString("doiTuongThaoTac")
                );
                danhSachNhatKy.add(nhatKy);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return danhSachNhatKy;
    }

    //tạo mã nhật ký audit mới
    public String maNhatKyMoi(){
        String sql = "SELECT COUNT(*) AS soLuong FROM NhatKyAudit";
        try(Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            if(rs.next()){
                int soLuong = rs.getInt("soLuong") + 1;
                return "NK" + String.format("%05d", soLuong);
            }
    }catch(Exception e){
        e.printStackTrace();
    }
        return null;
    }

}
