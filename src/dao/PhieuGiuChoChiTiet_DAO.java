package dao;
/*
 * @(#) PhieuGiuChoChiTiet_DAO.java  1.0  [2:54:06 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.PhieuGiuCho;
import entity.PhieuGiuChoChiTiet;
// Giả sử bạn có các entity này:
import entity.Chuyen;
import entity.Ghe;
import entity.Ga;

public class PhieuGiuChoChiTiet_DAO {

    private ConnectDB connectDB = ConnectDB.getInstance();
    // Giả sử thời gian giữ chỗ mặc định là 10 phút
    private static final int HOLD_DURATION_MINUTES = 10; 

    public PhieuGiuChoChiTiet_DAO() {
        connectDB.connect();
    }

    /**
     * Phương thức kiểm tra xung đột đã được cập nhật.
     * Nhận đầu vào là thông tin từ "vé session".
     *
     * @param chuyenID ID của chuyến (đã có)
     * @param tenGaDi  Tên ga đi (ví dụ: 'Sài Gòn')
     * @param tenGaDen Tên ga đến (ví dụ: 'Đà Nẵng')
     * @param toaID    ID của toa (đã có)
     * @param soGhe    Số thứ tự của ghế (ví dụ: 5)
     * @return true nếu CÓ XUNG ĐỘT (bị chiếm), false nếu GHẾ TRỐNG.
     */
    public boolean checkConflict(String chuyenID, String tenGaDi, String tenGaDen, String toaID, int soGhe) {
        Connection conn = connectDB.getConnection();

        // --- BẮT ĐẦU SỬA SQL ---
        String sqlCheck = 
                  "-- 1. Khai báo tham số từ session\n"
                + "DECLARE @chuyenID VARCHAR(50) = ?;\n"
                + "DECLARE @tenGaDi NVARCHAR(255) = ?;\n"
                + "DECLARE @tenGaDen NVARCHAR(255) = ?;\n"
                + "DECLARE @toaID VARCHAR(50) = ?;\n"
                + "DECLARE @soGhe INT = ?;\n"
                + "DECLARE @holdMinutes INT = ?;\n" // Tham số thời gian giữ chỗ
                + "\n"
                + "-- 2. Truy vấn các ID cần thiết từ thông tin session\n"
                + "DECLARE @gaDiID VARCHAR(50), @gaDenID VARCHAR(50), @gheID VARCHAR(50);\n"
                + "DECLARE @thuTuGaDi_Moi INT, @thuTuGaDen_Moi INT;\n"
                + "\n"
                + "SELECT @gaDiID = gaID FROM Ga WHERE tenGa = @tenGaDi;\n"
                + "SELECT @gaDenID = gaID FROM Ga WHERE tenGa = @tenGaDen;\n"
                + "SELECT @gheID = gheID FROM Ghe WHERE toaID = @toaID AND soGhe = @soGhe;\n"
                + "\n"
                + "IF @gheID IS NULL OR @gaDiID IS NULL OR @gaDenID IS NULL\n"
                + "BEGIN\n"
                + "    RAISERROR('Không tìm thấy thông tin Ga ID hoặc Ghế ID từ dữ liệu đầu vào.', 16, 1);\n"
                + "    RETURN;\n"
                + "END\n"
                + "\n"
                + "-- 3. Lấy thứ tự của ga đi/ga đến MỚI (Logic này giữ nguyên)\n"
                + "SELECT @thuTuGaDi_Moi = thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDiID;\n"
                + "SELECT @thuTuGaDen_Moi = thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDenID;\n"
                + "\n"
                + "IF @thuTuGaDi_Moi IS NULL OR @thuTuGaDen_Moi IS NULL\n"
                + "BEGIN\n"
                + "    RAISERROR('Không tìm thấy thông tin ga đi/ga đến trong lịch trình của chuyến.', 16, 1);\n"
                + "    RETURN;\n"
                + "END\n"
                + "\n"
                + "-- 4. Phần logic kiểm tra xung đột (Giữ nguyên)\n"
                + "-- 4.1. Kiểm tra Vé đã bán (Ve)\n"
                + "SELECT v.veID\n"
                + "FROM Ve v\n"
                + "JOIN ChuyenGa cgDi ON v.chuyenID = cgDi.chuyenID AND v.gaDiID = cgDi.gaID\n"
                + "JOIN ChuyenGa cgDen ON v.chuyenID = cgDen.chuyenID AND v.gaDenID = cgDen.gaID\n"
                + "WHERE v.chuyenID = @chuyenID\n"
                + "  AND v.gheID = @gheID\n" // Dùng @gheID đã truy vấn được
                + "  AND v.trangThai IN ('DA_BAN', 'DA_DUNG')\n"
                + "  AND cgDi.thuTu < @thuTuGaDen_Moi\n"
                + "  AND cgDen.thuTu > @thuTuGaDi_Moi\n"
                + "\n"
                + "UNION ALL\n"
                + "\n"
                + "-- 4.2. Kiểm tra Phiếu giữ chỗ (PhieuGiuChoChiTiet) đang giữ và còn hạn\n"
                + "SELECT pgcct.phieuGiuChoChiTietID\n"
                + "FROM PhieuGiuChoChiTiet pgcct\n"
                + "JOIN PhieuGiuCho pgc ON pgcct.phieuGiuChoID = pgc.phieuGiuChoID\n"
                + "JOIN ChuyenGa cgDi ON pgcct.chuyenID = cgDi.chuyenID AND pgcct.gaDiID = cgDi.gaID\n"
                + "JOIN ChuyenGa cgDen ON pgcct.chuyenID = cgDen.chuyenID AND pgcct.gaDenID = cgDen.gaID\n"
                + "WHERE pgcct.chuyenID = @chuyenID\n"
                + "  AND pgcct.gheID = @gheID\n" // Dùng @gheID đã truy vấn được
                + "  AND pgcct.trangThai = 'DANG_GIU'\n"
                + "  AND pgc.trangThai = 'DANG_GIU'\n"
                + "  AND pgc.thoiDiemTao > DATEADD(minute, -@holdMinutes, SYSUTCDATETIME())\n"
                + "  AND cgDi.thuTu < @thuTuGaDen_Moi\n"
                + "  AND cgDen.thuTu > @thuTuGaDi_Moi;";
        // --- KẾT THÚC SỬA SQL ---

        try (PreparedStatement ps = conn.prepareStatement(sqlCheck)) {
            // Set các tham số mới theo đúng thứ tự
            ps.setString(1, chuyenID);
            ps.setString(2, tenGaDi);   // Thay vì gaDiID
            ps.setString(3, tenGaDen); // Thay vì gaDenID
            ps.setString(4, toaID);     // Thêm toaID
            ps.setInt(5, soGhe);        // Thêm soGhe
            ps.setInt(6, HOLD_DURATION_MINUTES); // Thời gian giữ chỗ
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Nếu rs.next() là true, nghĩa là tìm thấy ít nhất 1 xung đột
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true; // An toàn là trên hết, nếu lỗi CSDL thì xem như có xung đột
        }
    }

    /**
     * Thêm một chi tiết giữ chỗ vào CSDL (Khi nhân viên chọn ghế).
     * Hàm này chỉ INSERT, không kiểm tra logic.
     * Tầng BUS phải gọi checkConflict() trước khi gọi hàm này.
     *
     * @param ct Đối tượng PhieuGiuChoChiTiet đã có đầy đủ thông tin
     * @return true nếu INSERT thành công
     */
    public boolean createPhieuGiuChoChiTiet(PhieuGiuChoChiTiet ct) {
        Connection conn = connectDB.getConnection();
        String sql = "INSERT INTO PhieuGiuChoChiTiet (phieuGiuChoChiTietID, phieuGiuChoID, chuyenID, gheID, gaDiID, gaDenID, thoiDiemGiuCho, trangThai) "
                   + "VALUES (?, ?, ?, ?, ?, ?, SYSUTCDATETIME(), 'DANG_GIU')";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getPhieuGiuChoChiTietID());
            ps.setString(2, ct.getPhieuGiuCho().getPhieuGiuChoID());
            ps.setString(3, ct.getChuyen().getChuyenID());
            ps.setString(4, ct.getGhe().getGheID());
            ps.setString(5, ct.getGaDi().getGaID());
            ps.setString(6, ct.getGaDen().getGaID());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Lỗi có thể do trùng PK hoặc FK (phieuGiuChoID) không tồn tại
            return false;
        }
    }

    /**
     * Xóa một chi tiết giữ chỗ (Khi nhân viên bỏ chọn ghế).
     *
     * @param phieuGiuChoChiTietID ID của chi tiết cần xóa
     * @return true nếu XÓA thành công
     */
    public boolean deletePhieuGiuChoChiTiet(String phieuGiuChoChiTietID) {
        Connection conn = connectDB.getConnection();
        String sql = "DELETE FROM PhieuGiuChoChiTiet WHERE phieuGiuChoChiTietID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phieuGiuChoChiTietID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tất cả các chi tiết thuộc về một phiếu giữ chỗ cha.
     *
     * @param phieuGiuChoID ID của phiếu cha
     * @return Danh sách các chi tiết
     */
    public List<PhieuGiuChoChiTiet> getChiTietByPhieuGiuChoID(String phieuGiuChoID) {
        List<PhieuGiuChoChiTiet> chiTietList = new ArrayList<>();
        Connection conn = connectDB.getConnection();
        String sql = "SELECT * FROM PhieuGiuChoChiTiet WHERE phieuGiuChoID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phieuGiuChoID);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhieuGiuChoChiTiet ct = new PhieuGiuChoChiTiet();
                    ct.setPhieuGiuChoChiTietID(rs.getString("phieuGiuChoChiTietID"));
                    
                    // Set các đối tượng cha (chỉ set ID, tầng BUS sẽ tải đầy đủ nếu cần)
                    ct.setPhieuGiuCho(new PhieuGiuCho(rs.getString("phieuGiuChoID")));
                    ct.setChuyen(new Chuyen(rs.getString("chuyenID")));
                    ct.setGhe(new Ghe(rs.getString("gheID")));
                    ct.setGaDi(new Ga(rs.getString("gaDiID")));
                    ct.setGaDen(new Ga(rs.getString("gaDenID")));
                    
                    ct.setThoiDiemGiuCho(rs.getTimestamp("thoiDiemGiuCho").toLocalDateTime());
                    ct.setTrangThai(rs.getString("trangThai"));
                    
                    chiTietList.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chiTietList;
    }

    /**
     * Cập nhật trạng thái của TẤT CẢ các chi tiết thuộc về một phiếu cha.
     * Dùng khi phiếu cha được xác nhận (XAC_NHAN) hoặc hết hạn (HET_HAN).
     *
     * @param phieuGiuChoID ID của phiếu cha
     * @param newTrangThai Trạng thái mới ('XAC_NHAN', 'HET_GIU')
     * @return true nếu cập nhật thành công
     */
    public boolean updateTrangThaiByPhieuGiuChoID(String phieuGiuChoID, String newTrangThai) {
        Connection conn = connectDB.getConnection();
        String sql = "UPDATE PhieuGiuChoChiTiet SET trangThai = ? WHERE phieuGiuChoID = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newTrangThai);
            ps.setString(2, phieuGiuChoID);
            
            // Sẽ trả về true nếu có ít nhất 1 dòng bị ảnh hưởng
            return ps.executeUpdate() > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Chạy định kỳ để dọn dẹp các chi tiết giữ chỗ đã hết hạn.
     * Cập nhật trạng thái từ 'DANG_GIU' -> 'HET_GIU'.
     *
     * @param expiryMinutes Số phút quy định hết hạn
     * @return Số lượng chi tiết đã được cập nhật
     */
    public int cleanUpExpiredPhieuGiuChoChiTiet(int expiryMinutes) {
        Connection conn = connectDB.getConnection();
        String sqlPGCT = "UPDATE pgcct\n"
                + "SET pgcct.trangThai = 'HET_GIU'\n"
                + "FROM PhieuGiuChoChiTiet pgcct\n"
                + "JOIN PhieuGiuCho pgc ON pgcct.phieuGiuChoID = pgc.phieuGiuChoID\n"
                + "WHERE pgcct.trangThai = 'DANG_GIU'\n"
                + "  AND (pgc.trangThai = 'DANG_GIU' OR pgc.trangThai = 'HET_HAN')\n"
                + "  AND pgc.thoiDiemTao < DATEADD(minute, -?, SYSUTCDATETIME());";
        
        try (PreparedStatement ps = conn.prepareStatement(sqlPGCT)) {
            ps.setInt(1, expiryMinutes);
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}