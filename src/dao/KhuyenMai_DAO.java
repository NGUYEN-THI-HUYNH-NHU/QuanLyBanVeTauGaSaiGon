package dao;

import connectDB.ConnectDB;
import entity.DieuKienKhuyenMai;
import entity.KhuyenMai;
import entity.Tuyen;
import entity.type.HangToa;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiTau;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {
    private final ConnectDB connectDB;

    public KhuyenMai_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    //thêm khuyến mãi và điều kiện
    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        String sqlKM = "INSERT INTO KhuyenMai VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDK = "INSERT INTO DieuKienKhuyenMai VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement psKM = con.prepareStatement(sqlKM);
                 PreparedStatement psDK = con.prepareStatement(sqlDK)) {

                // --- Insert KhuyenMai ---
                psKM.setString(1, km.getKhuyenMaiID());
                psKM.setString(2, km.getMaKhuyenMai());
                psKM.setString(3, km.getMoTa());
                psKM.setDouble(4, km.getTyLeGiamGia());
                psKM.setDouble(5, km.getTienGiamGia());
                psKM.setDate(6, java.sql.Date.valueOf(km.getNgayBatDau()));
                psKM.setDate(7, java.sql.Date.valueOf(km.getNgayKetThuc()));
                psKM.setDouble(8, km.getSoLuong());
                psKM.setInt(9, km.getGioiHanMoiKhachHang());
                psKM.setBoolean(10, km.isTrangThai());
                psKM.executeUpdate();

                //DieuKienKhuyenMai
                psDK.setString(1, dkkm.getDieuKienID());
                psDK.setString(2, km.getKhuyenMaiID());
                psDK.setString(3, dkkm.getTuyen() != null ? dkkm.getTuyen().getTuyenID() : null);
                psDK.setString(4, dkkm.getLoaiTau() != null ? dkkm.getLoaiTau().name() : null);
                psDK.setString(5, dkkm.getHangToa() != null ? dkkm.getHangToa().name() : null);
                psDK.setString(6, dkkm.getLoaiDoiTuong() != null ? dkkm.getLoaiDoiTuong().name() : null);
                if (dkkm.getNgayTrongTuan() >= 1 && dkkm.getNgayTrongTuan() <= 7)
                    psDK.setInt(7, dkkm.getNgayTrongTuan());
                else
                    psDK.setNull(7, java.sql.Types.INTEGER);

                psDK.setBoolean(8, dkkm.isNgayLe());

                if (dkkm.getMinGiaTriDonHang() > 0)
                    psDK.setDouble(9, dkkm.getMinGiaTriDonHang());
                else
                    psDK.setNull(9, java.sql.Types.FLOAT);

                psDK.executeUpdate();

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback(); //
                System.err.println( e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    //sửa khuyến mãi
    public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dk) {
        String sqlUpdateKM = "UPDATE KhuyenMai SET maKhuyenMai=?, moTa=?, tyLeGiamGia=?, tienGiamGia=?, ngayBatDau=?, ngayKetThuc=?, soLuong=?, gioiHanMoiKhachHang=?, trangThai=? WHERE khuyenMaiID=?";

        String sqlUpdateDK = "UPDATE DieuKienKhuyenMai SET tuyenID=?, loaiTauID=?, hangToaID=?, loaiDoiTuongID=?,ngayTrongTuan=?, ngayLe=?, minGiaTriDonHang=? WHERE khuyenMaiID=?";

        Connection con = null;

        try {
            con = connectDB.getConnection();
            con.setAutoCommit(false);

            // --- Cập nhật Khuyến mãi ---
            try (PreparedStatement psKM = con.prepareStatement(sqlUpdateKM)) {
                psKM.setString(1, km.getMaKhuyenMai());
                psKM.setString(2, km.getMoTa());
                psKM.setDouble(3, km.getTyLeGiamGia());
                psKM.setDouble(4, km.getTienGiamGia());
                psKM.setDate(5, java.sql.Date.valueOf(km.getNgayBatDau()));
                psKM.setDate(6, java.sql.Date.valueOf(km.getNgayKetThuc()));
                psKM.setDouble(7, km.getSoLuong());
                psKM.setInt(8, km.getGioiHanMoiKhachHang());
                psKM.setBoolean(9, km.isTrangThai());
                psKM.setString(10, km.getKhuyenMaiID());
                psKM.executeUpdate();
            }

            // --- Cập nhật Điều kiện khuyến mãi ---
            try (PreparedStatement psDK = con.prepareStatement(sqlUpdateDK)) {
                psDK.setString(1, dk.getTuyen() != null ? dk.getTuyen().getTuyenID() : null);
                psDK.setString(2, dk.getLoaiTau() != null ? dk.getLoaiTau().name() : null);
                psDK.setString(3, dk.getHangToa() != null ? dk.getHangToa().name() : null);
                psDK.setString(4, dk.getLoaiDoiTuong() != null ? dk.getLoaiDoiTuong().name() : null);

                if (dk.getNgayTrongTuan() >= 1 && dk.getNgayTrongTuan() <= 7) {
                    psDK.setInt(5, dk.getNgayTrongTuan());
                }else{
                    psDK.setNull(5, java.sql.Types.INTEGER);
                }

                psDK.setBoolean(6, dk.isNgayLe());

                if (dk.getMinGiaTriDonHang() > 0){
                    psDK.setDouble(7, dk.getMinGiaTriDonHang());
                }else{
                    psDK.setNull(7, java.sql.Types.DECIMAL);
                }
                String kmID = dk.getKhuyenMai() != null ? dk.getKhuyenMai().getKhuyenMaiID() : km.getKhuyenMaiID();
                psDK.setString(8, kmID);

                int row = psDK.executeUpdate();
                if (row == 0)
                    throw new SQLException("Không tìm thấy điều kiện khuyến mãi tương ứng để cập nhật!");
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    //tìm khuyến mãi theo mã  khuyến mãi
    public List<KhuyenMai> timKiemKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai, LocalDate tuNgay, LocalDate denNgay) {
        List<KhuyenMai> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT DISTINCT km.* " +
                        "FROM KhuyenMai km " +
                        "LEFT JOIN DieuKienKhuyenMai dkkm ON km.khuyenMaiID = dkkm.khuyenMaiID " +
                        "WHERE 1=1 "
        );

        //Tìm theo từ khóa: có thể là mã hoặc mô tả
        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append("AND (km.maKhuyenMai LIKE ? OR km.moTa LIKE ?) ");
        }

        //Tìm theo tuyến (nếu có chọn tuyến)
        if (maTuyen != null && !maTuyen.trim().isEmpty()) {
            sql.append("AND dkkm.tuyenID = ? ");
        }

        //Trạng thái
        if (trangThai != null) {
            sql.append("AND km.trangThai = ? ");
        }

        //Ngày bắt đầu / kết thúc
        if (tuNgay != null) {
            sql.append("AND km.ngayBatDau >= ? ");
        }
        if (denNgay != null) {
            sql.append("AND km.ngayKetThuc <= ? ");
        }

        sql.append("ORDER BY km.ngayBatDau DESC");

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                ps.setString(index++, "%" + tuKhoa + "%");
                ps.setString(index++, "%" + tuKhoa + "%");
            }


            if (maTuyen != null && !maTuyen.trim().isEmpty()) {
                ps.setString(index++, maTuyen);
            }

            if (trangThai != null) {
                ps.setBoolean(index++, trangThai);
            }

            if (tuNgay != null) {
                ps.setDate(index++, java.sql.Date.valueOf(tuNgay));
            }

            if (denNgay != null) {
                ps.setDate(index++, java.sql.Date.valueOf(denNgay));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                        rs.getString("khuyenMaiID"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("moTa"),
                        rs.getDouble("tyLeGiamGia"),
                        rs.getDouble("tienGiamGia"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getInt("soLuong"),
                        rs.getInt("gioiHanMoiKhachHang"),
                        rs.getBoolean("trangThai")
                );
                ds.add(km);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }



    //lấy danh sách khuyến mãi
    public DieuKienKhuyenMai layDieuKienKhuyenMai(String khuyenMaiID) {
        String sql = "SELECT * FROM DieuKienKhuyenMai WHERE khuyenMaiID = ?";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, khuyenMaiID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                //Liên kết khuyến mãi
                KhuyenMai km = new KhuyenMai();
                km.setKhuyenMaiID(rs.getString("khuyenMaiID"));

                //Tạo điều kiện khuyến mãi
                DieuKienKhuyenMai dk = new DieuKienKhuyenMai();
                dk.setDieuKienID(rs.getString("dieuKienID"));
                dk.setKhuyenMai(km);
                dk.setNgayTrongTuan(rs.getInt("ngayTrongTuan"));
                dk.setNgayLe(rs.getBoolean("ngayLe"));
                dk.setMinGiaTriDonHang(rs.getDouble("minGiaTriDonHang"));

                // Các trường enum
                String tuyenID = rs.getString("tuyenID");
                if (tuyenID != null) {
                    Tuyen tuyen = new Tuyen();
                    tuyen.setTuyenID(tuyenID);
                    dk.setTuyen(tuyen);
                }

                // LoaiTau
                String loaiTauStr = rs.getString("loaiTauID");
                if (loaiTauStr != null) {
                    try {
                        dk.setLoaiTau(LoaiTau.valueOf(loaiTauStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println( loaiTauStr);
                    }
                }

                // HangToa
                String hangToaStr = rs.getString("hangToaID");
                if (hangToaStr != null) {
                    try {
                        dk.setHangToa(HangToa.valueOf(hangToaStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println(hangToaStr);
                    }
                }

                // LoaiDoiTuong
                String loaiDTStr = rs.getString("loaiDoiTuongID");
                if (loaiDTStr != null) {
                    try {
                        dk.setLoaiDoiTuong(LoaiDoiTuong.valueOf(loaiDTStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println(loaiDTStr);
                    }
                }

                return dk;
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }


    //lấy tất cả khuyến mãi
    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai ORDER BY ngayBatDau DESC";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                        rs.getString("khuyenMaiID"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("moTa"),
                        rs.getDouble("tyLeGiamGia"),
                        rs.getDouble("tienGiamGia"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getInt("soLuong"),
                        rs.getInt("gioiHanMoiKhachHang"),
                        rs.getBoolean("trangThai")
                );
                ds.add(km);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ds;
    }
    //tạo mã khuyến mãi tự động
    public String taoMaKhuyenMaiTuDong() {
        String sql = "SELECT COUNT(*) AS soLuong FROM KhuyenMai";
        String maKhuyenMai = "KM";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int soLuong = rs.getInt("soLuong") + 1;
                maKhuyenMai += String.format("%03d", soLuong);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return maKhuyenMai;
    }
    //tạo mã điều kiện khuyến mãi tự động
    public String taoMaDieuKienTuDong(){
        String sql = "SELECT COUNT(*) AS soLuong FROM DieuKienKhuyenMai";
        String maDieuKien = "DKKM";

        try(Connection con = connectDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int soLuong = rs.getInt("soLuong") + 1;
                maDieuKien += String.format("%03d", soLuong);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return maDieuKien;
    }
    //lay danh sach dieu kien khuyen mai
    public List<DieuKienKhuyenMai> getAllDKKM() {
        String sql = "SELECT * FROM DieuKienKhuyenMai";
        List<DieuKienKhuyenMai> ds = new ArrayList<>();
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DieuKienKhuyenMai dk = new DieuKienKhuyenMai();
                dk.setDieuKienID(rs.getString("dieuKienID"));

                // --- Liên kết khuyến mãi ---
                KhuyenMai km = new KhuyenMai();
                km.setKhuyenMaiID(rs.getString("khuyenMaiID"));
                dk.setKhuyenMai(km);

                // --- Các trường enum ---
                String tuyenID = rs.getString("tuyenID");
                if (tuyenID != null) {
                    Tuyen tuyen = new Tuyen();
                    tuyen.setTuyenID(tuyenID);
                    dk.setTuyen(tuyen);
                }

                // LoaiTau
                String loaiTauStr = rs.getString("loaiTauID");
                if (loaiTauStr != null) {
                    try {
                        dk.setLoaiTau(LoaiTau.valueOf(loaiTauStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println(loaiTauStr);
                    }
                }

                // HangToa
                String hangToaStr = rs.getString("hangToaID");
                if (hangToaStr != null) {
                    try {
                        dk.setHangToa(HangToa.valueOf(hangToaStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println(hangToaStr);
                    }
                }

                // LoaiDoiTuong
                String loaiDTStr = rs.getString("loaiDoiTuongID");
                if (loaiDTStr != null) {
                    try {
                        dk.setLoaiDoiTuong(LoaiDoiTuong.valueOf(loaiDTStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println(loaiDTStr);
                    }
                }

                dk.setNgayTrongTuan(rs.getInt("ngayTrongTuan"));
                dk.setNgayLe(rs.getBoolean("ngayLe"));
                dk.setMinGiaTriDonHang(rs.getDouble("minGiaTriDonHang"));

                ds.add(dk);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ds;
    }

    //lấy danh sách tuyến
    public List<Tuyen> layDanhSachTuyen() {
        List<Tuyen> ds = new ArrayList<>();
        String sql = "SELECT * FROM Tuyen";

        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tuyen tuyen = new Tuyen();
                tuyen.setTuyenID(rs.getString("tuyenID"));
                tuyen.setMoTa(rs.getString("moTa"));
                ds.add(tuyen);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());

        }
        return ds;
    }

}
