package dao.impl;

import connectDB.ConnectDB;
import entity.DieuKienKhuyenMai;
import entity.KhuyenMai;
import entity.Tuyen;
import entity.Ve;
import entity.type.HangToa;
import entity.type.LoaiDoiTuong;
import entity.type.LoaiTau;
import gui.application.form.banVe.VeSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KhuyenMai_DAO {
    private final ConnectDB connectDB;

    public KhuyenMai_DAO() {
        connectDB = ConnectDB.getInstance();
        connectDB.connect();
    }

    // thêm khuyến mãi và điều kiện
    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        String sqlKM = "INSERT INTO KhuyenMai VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDK = "INSERT INTO DieuKienKhuyenMai VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = connectDB.getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement psKM = con.prepareStatement(sqlKM);
                 PreparedStatement psDK = con.prepareStatement(sqlDK)) {

                // --- Insert KhuyenMai ---
                psKM.setString(1, km.getId());
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

                // DieuKienKhuyenMai
                psDK.setString(1, dkkm.getId());
                psDK.setString(2, km.getId());
                psDK.setString(3, dkkm.getTuyen() != null ? dkkm.getTuyen().getId() : null);
                psDK.setString(4, dkkm.getLoaiTau() != null ? dkkm.getLoaiTau().getMoTa() : null);
                psDK.setString(5, dkkm.getHangToa() != null ? dkkm.getHangToa().getMoTa() : null);
                psDK.setString(6, dkkm.getLoaiDoiTuong() != null ? dkkm.getLoaiDoiTuong().getMoTa() : null);
                if (dkkm.getNgayTrongTuan() >= 1 && dkkm.getNgayTrongTuan() <= 7) {
                    psDK.setInt(7, dkkm.getNgayTrongTuan());
                } else {
                    psDK.setNull(7, java.sql.Types.INTEGER);
                }

                psDK.setBoolean(8, dkkm.getNgayLe());

                if (dkkm.getMinGiaTriDonHang() > 0) {
                    psDK.setDouble(9, dkkm.getMinGiaTriDonHang());
                } else {
                    psDK.setNull(9, java.sql.Types.FLOAT);
                }

                psDK.executeUpdate();

                con.commit();
                return true;

            } catch (SQLException e) {
                con.rollback(); //
                System.err.println(e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // sửa khuyến mãi
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
                psKM.setString(10, km.getId());
                psKM.executeUpdate();


            }

            // --- Cập nhật Điều kiện khuyến mãi ---
            try (PreparedStatement psDK = con.prepareStatement(sqlUpdateDK)) {
                psDK.setString(1, dk.getTuyen() != null ? dk.getTuyen().getId() : null);
                psDK.setString(2, dk.getLoaiTau() != null ? dk.getLoaiTau().getMoTa() : null);
                psDK.setString(3, dk.getHangToa() != null ? dk.getHangToa().getMoTa() : null);
                psDK.setString(4, dk.getLoaiDoiTuong() != null ? dk.getLoaiDoiTuong().getMoTa() : null);

                if (dk.getNgayTrongTuan() >= 1 && dk.getNgayTrongTuan() <= 7) psDK.setInt(5, dk.getNgayTrongTuan());
                else psDK.setNull(5, java.sql.Types.INTEGER);

                psDK.setBoolean(6, dk.getNgayLe());

                if (dk.getMinGiaTriDonHang() > 0) psDK.setDouble(7, dk.getMinGiaTriDonHang());
                else psDK.setNull(7, java.sql.Types.FLOAT);

                String kmID = dk.getKhuyenMai() != null ? dk.getKhuyenMai().getId() : km.getId();
                psDK.setString(8, kmID);

            }


            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (con != null) {
                    con.rollback();
                }
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

    // tìm khuyến mãi theo mã khuyến mãi
    public List<KhuyenMai> timKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai,
                                        LocalDate ngayBD, LocalDate ngayKT,
                                        LoaiTau loaiTau, HangToa hangToa, LoaiDoiTuong loaiDoiTuong) {

        List<KhuyenMai> ds = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT km.khuyenMaiID, km.maKhuyenMai, km.moTa, km.tyLeGiamGia, km.tienGiamGia, ")
                .append("       km.ngayBatDau, km.ngayKetThuc, km.soLuong, km.gioiHanMoiKhachHang, km.trangThai ")
                .append("FROM KhuyenMai km ")
                .append("LEFT JOIN DieuKienKhuyenMai dk ON dk.khuyenMaiID = km.khuyenMaiID ")
                .append("WHERE 1=1 ");


        List<Object> params = new ArrayList<>();

        if (tuKhoa != null && !tuKhoa.isBlank()) {
            sql.append(" AND (km.maKhuyenMai LIKE ? OR km.moTa LIKE ?) ");
            String like = "%" + tuKhoa.trim() + "%";
            params.add(like);
            params.add(like);
        }

        // Tuyến
        if (maTuyen != null && !maTuyen.isBlank()) {
            sql.append(" AND dk.tuyenID = ? ");
            params.add(maTuyen.trim());
        }

        // Trạng thái
        if (trangThai != null) {
            sql.append(" AND km.trangThai = ? ");
            params.add(trangThai);
        }

        // Ngày bắt đầu/kết thúc (bạn đang muốn filter theo khoảng)
        if (ngayBD != null) {
            sql.append(" AND km.ngayBatDau >= ? ");
            params.add(java.sql.Date.valueOf(ngayBD));
        }
        if (ngayKT != null) {
            sql.append(" AND km.ngayKetThuc <= ? ");
            params.add(java.sql.Date.valueOf(ngayKT));
        }

        // Loại tàu
        if (loaiTau != null) {
            sql.append(" AND dk.loaiTauID = ? ");
            params.add(loaiTau.name());
        }

        // Hạng toa
        if (hangToa != null) {
            sql.append(" AND dk.hangToaID = ? ");
            params.add(hangToa.name());
        }

        // Loại đối tượng
        if (loaiDoiTuong != null) {
            sql.append(" AND dk.loaiDoiTuongID = ? ");
            params.add(loaiDoiTuong.name());
        }

        sql.append(" ORDER BY km.ngayBatDau DESC ");


        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhuyenMai km = new KhuyenMai();

                    km.setId(rs.getString("khuyenMaiID"));
                    km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
                    km.setMoTa(rs.getString("moTa"));
                    km.setTyLeGiamGia(rs.getDouble("tyLeGiamGia"));
                    km.setTienGiamGia(rs.getDouble("tienGiamGia"));
                    km.setSoLuong(rs.getInt("soLuong"));
                    km.setGioiHanMoiKhachHang(rs.getInt("gioiHanMoiKhachHang"));
                    km.setTrangThai(rs.getBoolean("trangThai"));
                    Date sqlNbd = rs.getDate("ngayBatDau");
                    Date sqlNkt = rs.getDate("ngayKetThuc");


                    LocalDate nbd = (sqlNbd != null) ? sqlNbd.toLocalDate() : null;
                    LocalDate nkt = (sqlNkt != null) ? sqlNkt.toLocalDate() : null;

                    // set ngày bắt đầu và kết thúc
                    km.setNgayKetThuc(nkt);
                    km.setNgayBatDau(nbd);

                    ds.add(km);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi timKhuyenMai: " + e.getMessage(), e);
        }

        return ds;
    }


    private KhuyenMai mapKhuyenMai(ResultSet rs) throws SQLException {
        String khuyenMaiID = rs.getString("khuyenMaiID");
        String maKhuyenMai = rs.getString("maKhuyenMai"); // code
        String moTa = rs.getString("moTa");

        double tyLeGiamGia = rs.getDouble("tyLeGiamGia");
        double tienGiamGia = rs.getDouble("tienGiamGia");

        LocalDate ngayBatDau = rs.getDate("ngayBatDau").toLocalDate();
        LocalDate ngayKetThuc = rs.getDate("ngayKetThuc").toLocalDate();

        int soLuong = rs.getInt("soLuong");
        int gioiHanMoiKhachHang = rs.getInt("gioiHanMoiKhachHang");
        boolean trangThai = rs.getBoolean("trangThai");

        return new KhuyenMai(
                khuyenMaiID,
                maKhuyenMai,
                moTa,
                tyLeGiamGia,
                tienGiamGia,
                ngayBatDau,
                ngayKetThuc,
                soLuong,
                gioiHanMoiKhachHang,
                trangThai
        );
    }

    // lấy ma điều kiện khuyến mãi theo mã khuyến mãi
    public String layDieuKienKhuyenMai(String khuyenMaiID) {
        String sql = "SELECT dieuKienID FROM DieuKienKhuyenMai WHERE khuyenMaiID = ?";
        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, khuyenMaiID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("dieuKienID");
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    // lấy tất cả khuyến mãi
    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai ORDER BY ngayBatDau DESC";

        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(rs.getString("khuyenMaiID"), rs.getString("maKhuyenMai"),
                        rs.getString("moTa"), rs.getDouble("tyLeGiamGia"), rs.getDouble("tienGiamGia"),
                        rs.getDate("ngayBatDau").toLocalDate(), rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getInt("soLuong"), rs.getInt("gioiHanMoiKhachHang"), rs.getBoolean("trangThai"));
                ds.add(km);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ds;
    }

    // tạo mã khuyến mãi tự động
    public String taoMaKhuyenMaiTuDong() {
        String sql = "SELECT COUNT(*) AS soLuong FROM KhuyenMai";
        String maKhuyenMai = "KM";

        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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

    // tạo mã điều kiện khuyến mãi tự động
    public String taoMaDieuKienTuDong() {
        String sql = "SELECT MAX(dieuKienID) FROM DieuKienKhuyenMai";
        try (Connection con = connectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getString(1) != null) {
                int so = Integer.parseInt(rs.getString(1).substring(2)) + 1;
                return String.format("DK%03d", so);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "DK001";
    }


    // lay dieu kien khuyen mai theo ma khuyen mai
    public DieuKienKhuyenMai layDieuKienKhuyenMaiTheoKhuyenMai(String khuyenMaiID) {
        String sql = "SELECT * FROM DieuKienKhuyenMai WHERE khuyenMaiID = ?";
        DieuKienKhuyenMai dk = null;
        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, khuyenMaiID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                dk = new DieuKienKhuyenMai();
                dk.setId(rs.getString("dieuKienID"));

                KhuyenMai km = new KhuyenMai();
                km.setId(rs.getString("khuyenMaiID"));
                dk.setKhuyenMai(km);

                String tuyenID = rs.getString("tuyenID");
                if (tuyenID != null) {
                    Tuyen tuyen = new Tuyen();
                    tuyen.setId(tuyenID);
                    dk.setTuyen(tuyen);
                }

                String loaiTauStr = rs.getString("loaiTauID");
                if (loaiTauStr != null) {
                    try {
                        dk.setLoaiTau(entity.LoaiTau.builder().id(loaiTauStr).build());
                    } catch (IllegalArgumentException e) {
                        System.err.println(loaiTauStr);
                    }
                }

                // HangToa
                String hangToaStr = rs.getString("hangToaID");
                if (hangToaStr != null) {
                    try {
                        dk.setHangToa(entity.HangToa.builder().id(hangToaStr).build());
                    } catch (IllegalArgumentException e) {
                        System.err.println(hangToaStr);
                    }
                }

                // LoaiDoiTuong
                String loaiDTStr = rs.getString("loaiDoiTuongID");
                if (loaiDTStr != null) {
                    try {
                        dk.setLoaiDoiTuong(entity.LoaiDoiTuong.builder().id(loaiDTStr).build());
                    } catch (IllegalArgumentException e) {
                        System.err.println(loaiDTStr);
                    }
                }

                dk.setNgayTrongTuan(rs.getInt("ngayTrongTuan"));
                dk.setNgayLe(rs.getBoolean("ngayLe"));
                dk.setMinGiaTriDonHang(rs.getDouble("minGiaTriDonHang"));
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return dk;
    }

    // lấy danh sách tuyến
    public List<Tuyen> layDanhSachTuyen() {
        List<Tuyen> ds = new ArrayList<>();
        String sql = "SELECT * FROM Tuyen";

        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tuyen tuyen = new Tuyen();
                tuyen.setId(rs.getString("tuyenID"));
                tuyen.setMoTa(rs.getString("moTa"));
                ds.add(tuyen);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return ds;
    }

    // tu dong cap nhat trang thai
    public boolean tuDongCapNhatTrangThai() {
        String sql = "UPDATE KhuyenMai SET trangThai = 0 WHERE ngayKetThuc < ?";
        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            int row = ps.executeUpdate();
            return row > 0;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách khuyến mãi khả dụng dựa trên thông tin vé đang chọn. Logic:
     * Join bảng KhuyenMai và DieuKienKhuyenMai. So sánh các trường: Tuyen, LoaiTau,
     * HangToa, LoaiDoiTuong, MinGia, NgayTrongTuan.
     */
    public List<KhuyenMai> getDanhSachKhuyenMaiPhuHop(VeSession veSession) {
        List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
        Connection con = connectDB.getConnection();

        // 1. Trích xuất thông tin
        String tuyenID = veSession.getVe().getChuyen().getTuyen().getId();
        String loaiTauID = veSession.getVe().getChuyen().getTau().getLoaiTau().toString();
        String hangToaID = veSession.getVe().getGhe().getToa().getHangToa().toString();
        String loaiDoiTuongID = veSession.getVe().getKhachHang().getLoaiDoiTuong().toString();

        // Thêm ID Khách hàng để kiểm tra giới hạn
        String khachHangID = veSession.getVe().getKhachHang().getId();

        double giaVe = veSession.getVe().getGia();
        int ngayTrongTuan = veSession.getVe().getNgayGioDi().getDayOfWeek().getValue();

        // Join thêm bảng SuDungKhuyenMai (thông qua HoaDonChiTiet -> HoaDon) để đếm số
        // lần đã dùng.
        String sql = "SELECT km.*, " + "(SELECT COUNT(*) FROM SuDungKhuyenMai sd "
                + " JOIN HoaDonChiTiet hdct ON sd.hoaDonChiTietID = hdct.hoaDonChiTietID "
                + " JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID " + " WHERE sd.khuyenMaiID = km.khuyenMaiID "
                + " AND hd.khachHangID = ? " + " AND sd.trangThai = 'DA_AP_DUNG') AS soLanDaDung "
                + "FROM KhuyenMai km " + "JOIN DieuKienKhuyenMai dk ON km.khuyenMaiID = dk.khuyenMaiID "
                + "WHERE km.trangThai = 1 " + "AND km.soLuong > 0 "
                + "AND CAST(GETDATE() AS DATE) BETWEEN km.ngayBatDau AND km.ngayKetThuc "
                // Điều kiện phù hợp vé
                + "AND (dk.tuyenID IS NULL OR dk.tuyenID = ?) " + "AND (dk.loaiTauID IS NULL OR dk.loaiTauID = ?) "
                + "AND (dk.hangToaID IS NULL OR dk.hangToaID = ?) "
                + "AND (dk.loaiDoiTuongID IS NULL OR dk.loaiDoiTuongID = ?) "
                + "AND (dk.minGiaTriDonHang IS NULL OR ? >= dk.minGiaTriDonHang) "
                + "AND (dk.ngayTrongTuan IS NULL OR dk.ngayTrongTuan = ?) "
                // Điều kiện giới hạn: (gioiHan = 0 là không giới hạn) HOẶC (số lần đã dùng <
                // giới hạn)
                + "AND (km.gioiHanMoiKhachHang = 0 OR " + "      (SELECT COUNT(*) FROM SuDungKhuyenMai sd "
                + "       JOIN HoaDonChiTiet hdct ON sd.hoaDonChiTietID = hdct.hoaDonChiTietID "
                + "       JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID "
                + "       WHERE sd.khuyenMaiID = km.khuyenMaiID "
                + "       AND hd.khachHangID = ?) < km.gioiHanMoiKhachHang)";

        try {
            PreparedStatement pstm = con.prepareStatement(sql);
            int i = 1;
            pstm.setString(i++, khachHangID);
            pstm.setString(i++, tuyenID);
            pstm.setString(i++, loaiTauID);
            pstm.setString(i++, hangToaID);
            pstm.setString(i++, loaiDoiTuongID);
            pstm.setDouble(i++, giaVe);
            pstm.setInt(i++, ngayTrongTuan);
            pstm.setString(i++, khachHangID);

            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                String khuyenMaiID = rs.getString("khuyenMaiID");
                String maKhuyenMai = rs.getString("maKhuyenMai");
                String moTa = rs.getString("moTa");

                double tyLeGiamGia = rs.getDouble("tyLeGiamGia");
                if (rs.wasNull()) {
                    tyLeGiamGia = 0;
                }

                double tienGiamGia = rs.getDouble("tienGiamGia");
                if (rs.wasNull()) {
                    tienGiamGia = 0;
                }

                LocalDate ngayBatDau = rs.getDate("ngayBatDau").toLocalDate();
                LocalDate ngayKetThuc = rs.getDate("ngayKetThuc").toLocalDate();

                int soLuong = rs.getInt("soLuong");
                int gioiHanMoiKhachHang = rs.getInt("gioiHanMoiKhachHang");
                boolean trangThai = rs.getBoolean("trangThai");

                KhuyenMai km = new KhuyenMai(khuyenMaiID, maKhuyenMai, moTa, tyLeGiamGia, tienGiamGia, ngayBatDau,
                        ngayKetThuc, soLuong, gioiHanMoiKhachHang, trangThai);
                dsKhuyenMai.add(km);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKhuyenMai;
    }

    public boolean giamSoLuongKhuyenMai(Connection conn, String khuyenMaiID) throws Exception {
        // Lưu ý: Kiểm tra soLuong > 0 ngay trong câu lệnh UPDATE để tránh Race
        // Condition (nhiều người mua cùng lúc)
        String sql = "UPDATE KhuyenMai SET soLuong = soLuong - 1 WHERE khuyenMaiID = ? AND soLuong > 0";

        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setString(1, khuyenMaiID);
            return pstm.executeUpdate() > 0;
        }
    }

    /**
     * Kiểm tra khách hàng đã dùng mã này bao nhiêu lần
     */
    public int demSoLanSuDungCuaKhachHang(String khuyenMaiID, String khachHangID) {
        Connection con = connectDB.getConnection();
        // Join SuDungKM -> HoaDonChiTiet -> HoaDon -> KhachHang
        String sql = "SELECT COUNT(*) FROM SuDungKhuyenMai sd "
                + "JOIN HoaDonChiTiet hdct ON sd.hoaDonChiTietID = hdct.hoaDonChiTietID "
                + "JOIN HoaDon hd ON hdct.hoaDonID = hd.hoaDonID "
                + "WHERE sd.khuyenMaiID = ? AND hd.khachHangID = ? AND sd.trangThai = 'DA_AP_DUNG'";
        try {
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, khuyenMaiID);
            pstm.setString(2, khachHangID);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy danh sách mã khuyến mãi và số lượng cần hoàn lại dựa trên danh sách vé.
     *
     * @param conn
     * @param listVe
     * @return Map<String, Integer>: Key là khuyenMaiID, Value là số lượng cần cộng
     * lại
     */
    public Map<String, Integer> getDanhSachKhuyenMaiCanHoan(Connection conn, List<Ve> listVe) throws Exception {
        Map<String, Integer> resultMap = new HashMap<>();

        if (listVe == null || listVe.isEmpty()) {
            return resultMap;
        }

        // Xây dựng câu query động với IN (?,?,...)
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT sdk.khuyenMaiID, COUNT(*) as soLuongCanHoan ");
        sqlBuilder.append("FROM SuDungKhuyenMai sdk ");
        sqlBuilder.append("JOIN HoaDonChiTiet hdct ON sdk.hoaDonChiTietID = hdct.hoaDonChiTietID ");
        sqlBuilder.append("WHERE sdk.trangThai = 'DA_AP_DUNG' ");
        sqlBuilder.append("AND hdct.veID IN (");

        // Tạo chuỗi placeholder (?,?,?) tương ứng số lượng vé
        String placeholders = listVe.stream().map(v -> "?").collect(Collectors.joining(","));
        sqlBuilder.append(placeholders);
        sqlBuilder.append(") ");

        // Gom nhóm để đếm số lượng cho từng mã khuyến mãi
        sqlBuilder.append("GROUP BY sdk.khuyenMaiID");

        try (PreparedStatement pstm = conn.prepareStatement(sqlBuilder.toString())) {
            for (int i = 0; i < listVe.size(); i++) {
                pstm.setString(i + 1, listVe.get(i).getId());
            }

            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                String kmID = rs.getString("khuyenMaiID");
                int count = rs.getInt("soLuongCanHoan");
                resultMap.put(kmID, count);
            }
        }

        return resultMap;
    }

    /**
     * @param conn
     * @param khuyenMaiID
     * @param soLuongCanCong
     * @return
     */
    public boolean updateSoLuongKhuyenMai(Connection conn, String khuyenMaiID, int soLuongCanCong) throws Exception {
        String sql = "UPDATE KhuyenMai SET soLuong = soLuong + ? WHERE khuyenMaiID = ?";
        try (PreparedStatement pstm = conn.prepareStatement(sql)) {
            pstm.setInt(1, soLuongCanCong);
            pstm.setString(2, khuyenMaiID);
            return pstm.executeUpdate() > 0;

        }
    }

    //tìm khuyến mãi bằng ID
    public KhuyenMai timKiemKhuyenMaiByID(String khuyenMaiID) {
        KhuyenMai km = null;
        String sql = "SELECT * FROM KhuyenMai WHERE khuyenMaiID = ?";

        try (Connection con = connectDB.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, khuyenMaiID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                km = new KhuyenMai(
                        rs.getString("khuyenMaiID"),
                        rs.getString("maKhuyenMai"),
                        rs.getString("moTa"),
                        rs.getDouble("tyLeGiamGia"),
                        rs.getDouble("tienGiamGia"),
                        rs.getDate("ngayBatDau").toLocalDate(),
                        rs.getDate("ngayKetThuc").toLocalDate(),
                        rs.getInt("soLuong"),
                        rs.getInt("gioiHanMoiKhachHang"),
                        rs.getBoolean("trangThai"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return km;
    }
}
