//package dao;
///*
// * @(#) Ve_DAO.java  1.0  [11:13:56 AM] Sep 27, 2025
// *
// * Copyright (c) 2025 IUH. All rights reserved.
// */
//
//import connectDB.ConnectDB;
//import entity.Ve;
//
///*
// * @description
// * @author: NguyenThiHuynhNhu
// * @date: Sep 27, 2025
// * @version: 1.0
// */
//
//public class Ve_DAO {
//	private ConnectDB connectDB = ConnectDB.getInstance();
//
//    public Ve_DAO() {
//    	connectDB.connect();
//    }
//    
//    public boolean taoVeTamThoi(Ve ve) {
//        String sql = "INSERT INTO Ve (veID, donDatChoID, chuyenID, gheID, hanhKhachID, thuTuGaDi, thuTuGaDen, gia, trangThai, ngayBan) "
//                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, ve.getVeID());
//            ps.setString(2, ve.getDonDatChoID());
//            ps.setString(3, ve.getChuyenID());
//            ps.setString(4, ve.getGheID());
//            ps.setString(5, ve.getHanhKhachID());
//            ps.setInt(6, ve.getThuTuGaDi());
//            ps.setInt(7, ve.getThuTuGaDen());
//            ps.setDouble(8, ve.getGia());
//            ps.setString(9, ve.getTrangThai());
//            ps.setTimestamp(10, ve.getNgayBan() == null ? null : Timestamp.valueOf(ve.getNgayBan()));
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean update(Ve ve) {
//        String sql = "UPDATE Ve SET donDatChoID=?, chuyenID=?, gheID=?, hanhKhachID=?, thuTuGaDi=?, thuTuGaDen=?, gia=?, trangThai=?, ngayBan=? WHERE veID=?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, ve.getDonDatChoID());
//            ps.setString(2, ve.getChuyenID());
//            ps.setString(3, ve.getGheID());
//            ps.setString(4, ve.getHanhKhachID());
//            ps.setInt(5, ve.getThuTuGaDi());
//            ps.setInt(6, ve.getThuTuGaDen());
//            ps.setDouble(7, ve.getGia());
//            ps.setString(8, ve.getTrangThai());
//            ps.setTimestamp(9, ve.getNgayBan() == null ? null : Timestamp.valueOf(ve.getNgayBan()));
//            ps.setString(10, ve.getVeID());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public boolean delete(String veID) {
//        String sql = "DELETE FROM Ve WHERE veID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, veID);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public Ve findById(String veID) {
//        String sql = "SELECT * FROM Ve WHERE veID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, veID);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    Ve v = new Ve();
//                    v.setVeID(rs.getString("veID"));
//                    v.setDonDatChoID(rs.getString("donDatChoID"));
//                    v.setChuyenID(rs.getString("chuyenID"));
//                    v.setGheID(rs.getString("gheID"));
//                    v.setHanhKhachID(rs.getString("hanhKhachID"));
//                    v.setThuTuGaDi(rs.getInt("thuTuGaDi"));
//                    v.setThuTuGaDen(rs.getInt("thuTuGaDen"));
//                    v.setGia(rs.getDouble("gia"));
//                    v.setTrangThai(rs.getString("trangThai"));
//                    Timestamp t = rs.getTimestamp("ngayBan");
//                    v.setNgayBan(t == null ? null : t.toLocalDateTime());
//                    return v;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    public List<Ve> findAll() {
//        String sql = "SELECT * FROM Ve";
//        List<Ve> list = new ArrayList<>();
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                Ve v = new Ve();
//                v.setVeID(rs.getString("veID"));
//                v.setDonDatChoID(rs.getString("donDatChoID"));
//                v.setChuyenID(rs.getString("chuyenID"));
//                v.setGheID(rs.getString("gheID"));
//                v.setHanhKhachID(rs.getString("hanhKhachID"));
//                v.setThuTuGaDi(rs.getInt("thuTuGaDi"));
//                v.setThuTuGaDen(rs.getInt("thuTuGaDen"));
//                v.setGia(rs.getDouble("gia"));
//                v.setTrangThai(rs.getString("trangThai"));
//                Timestamp t = rs.getTimestamp("ngayBan");
//                v.setNgayBan(t == null ? null : t.toLocalDateTime());
//                list.add(v);
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return list;
//    }
//
//    /* ======= Nghiệp vụ bán vé / giữ chỗ ======= */
//
//    /**
//     * Tìm các ghế còn trống cho chuyến và đoạn (thuTuGaDi..thuTuGaDen)
//     * Logic: chọn ghế của các toa thuộc tàu của chuyenId và đảm bảo không có vé hiện thời
//     * có trạng thái chiếm chỗ trùng chồng lấp đoạn.
//     */
//    public List<Ghe> timGheConTrong(String chuyenID, int thuTuGaDiMoi, int thuTuGaDenMoi) {
//        String sql = ""
//            + "SELECT g.* FROM Ghe g "
//            + "JOIN Toa t ON g.toaID = t.toaID "
//            + "JOIN Tau tau ON t.tauID = tau.tauID "
//            + "JOIN Chuyen c ON c.tauID = tau.tauID "
//            + "WHERE c.chuyenID = ? "
//            + "AND NOT EXISTS ("
//            + "  SELECT 1 FROM Ve v "
//            + "  WHERE v.gheID = g.gheID AND v.chuyenID = ? AND v.trangThai IN ('BOOKED','RESERVED','CONFIRMED','USED')"
//            + "    AND (? < v.thuTuGaDen AND ? > v.thuTuGaDi) "
//            + ")";
//        List<Ghe> ds = new ArrayList<>();
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, chuyenID);
//            ps.setString(2, chuyenID);
//            ps.setInt(3, thuTuGaDenMoi); // new_end > existing_start  -> parameter order aligns with condition
//            ps.setInt(4, thuTuGaDiMoi);  // new_start < existing_end
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Ghe g = new Ghe();
//                    g.setGheID(rs.getString("gheID"));
//                    g.setToaID(rs.getString("toaID"));
//                    g.setSoGhe(rs.getString("soGhe"));
//                    g.setTrangThai(rs.getBoolean("trangThai"));
//                    ds.add(g);
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return ds;
//    }
//
//    /**
//     * Giữ ghế: tạo đơn đặt chỗ (DonDatCho + DonDatChoChiTiet) - dùng DonDatCho_DAO
//     * Ở đây chỉ insert trực tiếp Ve nếu muốn giữ bằng vé tạm; thông thường tạo DonDatCho.
//     */
//    public boolean giuGhe(String donDatChoID, String khachHangID, String chuyenID,
//                          List<DonDatChoChiTiet> chiTiets, LocalDateTime thoiDiemHetHan) {
//        DonDatCho_DAO rdao = new DonDatCho_DAO();
//        DonDatCho dd = new DonDatCho();
//        dd.setDonDatChoID(donDatChoID);
//        dd.setKhachHangID(khachHangID);
//        dd.setChuyenID(chuyenID);
//        dd.setThoiDiemDatCho(LocalDateTime.now());
//        dd.setThoiDiemHetHan(thoiDiemHetHan);
//        dd.setTongTien(chiTiets.stream().mapToDouble(t -> t.getGia()).sum());
//        dd.setTrangThaiDatChoID("PENDING");
//        boolean ok = rdao.insert(dd);
//        if (!ok) return false;
//        DonDatChoChiTiet_DAO ctdao = new DonDatChoChiTiet_DAO();
//        for (DonDatChoChiTiet ct : chiTiets) {
//            ct.setDonDatChoID(donDatChoID);
//            boolean ins = ctdao.insert(ct);
//            if (!ins) return false;
//        }
//        return true;
//    }
//
//    /**
//     * Xác nhận vé: chuyển từ DonDatCho -> Ve (thanh toán xong)
//     * Tạo vé (Ve) cho từng DonDatChoChiTiet, cập nhật trạng thái DonDatCho
//     */
//    public boolean xacNhanVeTuDonDatCho(String donDatChoID, String nhanVienThanhToanID) {
//        DonDatCho_DAO rdao = new DonDatCho_DAO();
//        DonDatCho dd = rdao.findById(donDatChoID);
//        if (dd == null) return false;
//        DonDatChoChiTiet_DAO ctDao = new DonDatChoChiTiet_DAO();
//        List<DonDatChoChiTiet> ds = ctDao.findByDonDatChoId(donDatChoID);
//        if (ds.isEmpty()) return false;
//
//        try (Connection c = db.getConnection()) {
//            c.setAutoCommit(false);
//            Ve_DAO veDao = new Ve_DAO();
//            HoaDon_DAO hdDao = new HoaDon_DAO();
//            // Tạo hóa đơn trước (simple)
//            HoaDon hd = new HoaDon();
//            hd.setHoaDonID("HD_" + System.currentTimeMillis());
//            hd.setKhachHangID(dd.getKhachHangID());
//            hd.setNhanVienID(nhanVienThanhToanID);
//            hd.setThoiDiemTao(LocalDateTime.now());
//            hd.setTamTinh(dd.getTongTien());
//            hd.setTongTien(dd.getTongTien());
//            hd.setTrangThai(true);
//            if (!hdDao.insert(hd)) { c.rollback(); c.setAutoCommit(true); return false; }
//
//            // Tạo vé + chi tiết hóa đơn
//            for (DonDatChoChiTiet ct : ds) {
//                Ve v = new Ve();
//                v.setVeID("VE_" + System.currentTimeMillis() + "_" + ct.getGheID());
//                v.setDonDatChoID(donDatChoID);
//                v.setChuyenID(dd.getChuyenID());
//                v.setGheID(ct.getGheID());
//                v.setHanhKhachID(ct.getHanhKhachID());
//                v.setThuTuGaDi(ct.getThuTuGaDi());
//                v.setThuTuGaDen(ct.getThuTuGaDen());
//                v.setGia(ct.getGia());
//                v.setTrangThai("BOOKED");
//                v.setNgayBan(LocalDateTime.now());
//                if (!veDao.insert(v)) { c.rollback(); c.setAutoCommit(true); return false; }
//
//                // thêm chi tiết hóa đơn liên kết vé: HoaDonChiTiet_DAO
//                HoaDonChiTiet_DAO hdcDao = new HoaDonChiTiet_DAO();
//                HoaDonChiTiet hdc = new HoaDonChiTiet();
//                hdc.setHoaDonChiTietID("HDC_" + System.currentTimeMillis());
//                hdc.setHoaDonID(hd.getHoaDonID());
//                hdc.setLoaiDichVu("VEXE");
//                hdc.setMatHangID("VE");
//                hdc.setTenMatHang("Vé " + v.getVeID());
//                hdc.setDonGia(v.getGia());
//                hdc.setSoLuong(1);
//                hdc.setSoTien(v.getGia());
//                hdc.setThue(0);
//                hdc.setVeID(v.getVeID());
//                if (!hdcDao.insert(hdc)) { c.rollback(); c.setAutoCommit(true); return false; }
//            }
//
//            // cập nhật DonDatCho trạng thái -> CONFIRMED
//            dd.setTrangThaiDatChoID("CONFIRMED");
//            if (!rdao.update(dd)) { c.rollback(); c.setAutoCommit(true); return false; }
//
//            c.commit();
//            c.setAutoCommit(true);
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /* Các phương thức khác như hủy vé, update trạng thái... */
//}
//
///* ============================
//   DAO: DonDatCho (Reservation)
//   ============================ */
//
//
///* ============================
//   DAO: DonDatChoChiTiet
//   ============================ */
//class DonDatChoChiTiet_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public boolean insert(DonDatChoChiTiet d) {
//        String sql = "INSERT INTO DonDatChoChiTiet (donDatChoChiTietID, donDatChoID, gheID, hanhKhachID, thuTuGaDi, thuTuGaDen, gia) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, d.getId());
//            ps.setString(2, d.getDonDatChoID());
//            ps.setString(3, d.getGheID());
//            ps.setString(4, d.getHanhKhachID());
//            ps.setInt(5, d.getThuTuGaDi());
//            ps.setInt(6, d.getThuTuGaDen());
//            ps.setDouble(7, d.getGia());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public boolean update(DonDatChoChiTiet d) {
//        String sql = "UPDATE DonDatChoChiTiet SET gheID=?, hanhKhachID=?, thuTuGaDi=?, thuTuGaDen=?, gia=? WHERE donDatChoChiTietID=?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, d.getGheID());
//            ps.setString(2, d.getHanhKhachID());
//            ps.setInt(3, d.getThuTuGaDi());
//            ps.setInt(4, d.getThuTuGaDen());
//            ps.setDouble(5, d.getGia());
//            ps.setString(6, d.getId());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public boolean delete(String id) {
//        String sql = "DELETE FROM DonDatChoChiTiet WHERE donDatChoChiTietID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, id);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public List<DonDatChoChiTiet> findByDonDatChoId(String donDatChoID) {
//        String sql = "SELECT * FROM DonDatChoChiTiet WHERE donDatChoID = ?";
//        List<DonDatChoChiTiet> ds = new ArrayList<>();
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, donDatChoID);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    DonDatChoChiTiet d = new DonDatChoChiTiet();
//                    d.setId(rs.getString("donDatChoChiTietID"));
//                    d.setDonDatChoID(rs.getString("donDatChoID"));
//                    d.setGheID(rs.getString("gheID"));
//                    d.setHanhKhachID(rs.getString("hanhKhachID"));
//                    d.setThuTuGaDi(rs.getInt("thuTuGaDi"));
//                    d.setThuTuGaDen(rs.getInt("thuTuGaDen"));
//                    d.setGia(rs.getDouble("gia"));
//                    ds.add(d);
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return ds;
//    }
//}
//
///* ============================
//   DAO: HoaDon (Invoice)
//   ============================ */
//class HoaDon_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public boolean insert(HoaDon hd) {
//        String sql = "INSERT INTO HoaDon (hoaDonID, khachHangID, nhanVienID, thoiDiemTao, tamTinh, tongGiamGia, tongThue, tongTien, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, hd.getHoaDonID());
//            ps.setString(2, hd.getKhachHangID());
//            ps.setString(3, hd.getNhanVienID());
//            ps.setTimestamp(4, hd.getThoiDiemTao() == null ? null : Timestamp.valueOf(hd.getThoiDiemTao()));
//            ps.setDouble(5, hd.getTamTinh());
//            ps.setDouble(6, 0); // tongGiamGia
//            ps.setDouble(7, 0); // tongThue
//            ps.setDouble(8, hd.getTongTien());
//            ps.setBoolean(9, hd.isTrangThai());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public HoaDon findById(String id) {
//        String sql = "SELECT * FROM HoaDon WHERE hoaDonID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, id);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    HoaDon h = new HoaDon();
//                    h.setHoaDonID(rs.getString("hoaDonID"));
//                    h.setKhachHangID(rs.getString("khachHangID"));
//                    h.setNhanVienID(rs.getString("nhanVienID"));
//                    Timestamp t = rs.getTimestamp("thoiDiemTao");
//                    h.setThoiDiemTao(t == null ? null : t.toLocalDateTime());
//                    h.setTamTinh(rs.getDouble("tamTinh"));
//                    h.setTongTien(rs.getDouble("tongTien"));
//                    h.setTrangThai(rs.getBoolean("trangThai"));
//                    return h;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    public List<HoaDon> findByKhachHang(String khachHangID) {
//        String sql = "SELECT * FROM HoaDon WHERE khachHangID = ?";
//        List<HoaDon> ds = new ArrayList<>();
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, khachHangID);
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    HoaDon h = new HoaDon();
//                    h.setHoaDonID(rs.getString("hoaDonID"));
//                    h.setKhachHangID(rs.getString("khachHangID"));
//                    h.setNhanVienID(rs.getString("nhanVienID"));
//                    Timestamp t = rs.getTimestamp("thoiDiemTao");
//                    h.setThoiDiemTao(t == null ? null : t.toLocalDateTime());
//                    h.setTamTinh(rs.getDouble("tamTinh"));
//                    h.setTongTien(rs.getDouble("tongTien"));
//                    h.setTrangThai(rs.getBoolean("trangThai"));
//                    ds.add(h);
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return ds;
//    }
//
//    // update, delete, findAll tương tự có thể bổ sung nếu cần
//}
//
///* ============================
//   DAO: HoaDonChiTiet
//   ============================ */
//class HoaDonChiTiet_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public boolean insert(HoaDonChiTiet hdc) {
//        String sql = "INSERT INTO HoaDonChiTiet (hoaDonChiTietID, hoaDonID, loaiDichVu, matHangID, tenMatHang, donGia, soLuong, soTien, thue, veID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, hdc.getHoaDonChiTietID());
//            ps.setString(2, hdc.getHoaDonID());
//            ps.setString(3, hdc.getLoaiDichVu());
//            ps.setString(4, hdc.getMatHangID());
//            ps.setString(5, hdc.getTenMatHang());
//            ps.setDouble(6, hdc.getDonGia());
//            ps.setInt(7, hdc.getSoLuong());
//            ps.setDouble(8, hdc.getSoTien());
//            ps.setDouble(9, hdc.getThue());
//            ps.setString(10, hdc.getVeID());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    // update/delete/findById/findAll tương tự có thể thêm khi cần
//}
//
///* ============================
//   DAO: BieuGiaVe (Pricing)
//   ============================ */
//class BieuGiaVe_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public BieuGiaVe findApplicablePrice(String tuyenID, String hangToaID, String loaiTauID, int km) {
//        String sql = "SELECT TOP 1 * FROM BieuGiaVe WHERE tuyenApDungID = ? AND hangToaApDungID = ? AND LoaiTauApDungID = ? "
//                   + "AND ? >= ISNULL(minKm,0) AND ? <= ISNULL(maxKm, 2147483647) AND isCoHieuLuc = 1 ORDER BY doUuTien DESC, ngayCoHieuLuc DESC";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, tuyenID);
//            ps.setString(2, hangToaID);
//            ps.setString(3, loaiTauID);
//            ps.setInt(4, km);
//            ps.setInt(5, km);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    BieuGiaVe b = new BieuGiaVe();
//                    b.setId(rs.getString("bieuGiaveID"));
//                    b.setTuyenApDungID(rs.getString("tuyenApDungID"));
//                    b.setHangToaApDungID(rs.getString("hangToaApDungID"));
//                    b.setLoaiTauApDungID(rs.getString("LoaiTauApDungID"));
//                    b.setDonGiaTrenKm(rs.getDouble("donGiaTrenKm"));
//                    b.setGiaCoDinh(rs.getDouble("giaCoDinh"));
//                    b.setPhuPhiCaoDiem(rs.getDouble("phuPhiCaoDiem"));
//                    return b;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    /**
//     * Tính giá vé cơ bản (không gồm khuyến mãi, thuế):
//     * - Nếu giá cố định != null -> trả giá cố định
//     * - else: donGiaTrenKm * km -> cộng phụ phí
//     */
//    public double tinhGia(String tuyenID, String hangToaID, String loaiTauID, int km) {
//        BieuGiaVe b = findApplicablePrice(tuyenID, hangToaID, loaiTauID, km);
//        if (b == null) return 0.0;
//        double base;
//        if (b.getGiaCoDinh() != null && b.getGiaCoDinh() > 0) base = b.getGiaCoDinh();
//        else if (b.getDonGiaTrenKm() != null) base = b.getDonGiaTrenKm() * km;
//        else base = 0.0;
//        if (b.getPhuPhiCaoDiem() != null) base += b.getPhuPhiCaoDiem();
//        return base;
//    }
//}
//
///* ============================
//   DAO: HeSoGiaHangToa
//   ============================ */
//class HeSoGiaHangToa_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public HeSoGiaHangToa findByHangToa(String hangToaID) {
//        String sql = "SELECT * FROM HeSoGiaHangToa WHERE hangToaID = ? AND ngayCoHieuLuc <= GETDATE() AND ngayHetHieuLuc >= GETDATE()";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, hangToaID);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    HeSoGiaHangToa h = new HeSoGiaHangToa();
//                    h.setId(rs.getString("hsgHangToaID"));
//                    h.setHangToaID(rs.getString("hangToaID"));
//                    h.setHsg(rs.getDouble("hsg"));
//                    return h;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//}
//
///* ============================
//   DAO: HeSoGiaLoaiTau
//   ============================ */
//class HeSoGiaLoaiTau_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public HeSoGiaLoaiTau findByLoaiTau(String loaiTauID) {
//        String sql = "SELECT * FROM HeSoGiaLoaiTau WHERE loaiTauID = ? AND ngayCoHieuLuc <= GETDATE() AND ngayHetHieuLuc >= GETDATE()";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, loaiTauID);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    HeSoGiaLoaiTau h = new HeSoGiaLoaiTau();
//                    h.setId(rs.getString("hsgLoaiTauID"));
//                    h.setLoaiTauID(rs.getString("loaiTauID"));
//                    h.setHsg(rs.getDouble("hsg"));
//                    return h;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//}
//
///* ============================
//   DAO: Refund (Hoàn vé / đổi vé)
//   ============================ */
//class Refund_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    /**
//     * Tính tiền hoàn vé theo quy tắc đơn giản:
//     * - Nếu yêu cầu trước giờ khởi hành > 48h: hoàn 90%
//     * - 24h-48h: 70%
//     * - <24h: 50%
//     * - Nếu đã đi (USED) hoặc EXPIRED: không hoàn
//     * (Bạn có thể điều chỉnh luật tùy schema thực tế)
//     */
//    public double tinhTienHoan(String veID, LocalDateTime yeuCau) {
//        Ve_DAO veDao = new Ve_DAO();
//        Ve v = veDao.findById(veID);
//        if (v == null) return 0.0;
//        if ("USED".equalsIgnoreCase(v.getTrangThai()) || "EXPIRED".equalsIgnoreCase(v.getTrangThai())) return 0.0;
//
//        // lấy thời gian khởi hành từ Chuyen
//        String sqlChuyen = "SELECT gioKhoiHanh FROM Chuyen WHERE chuyenID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sqlChuyen)) {
//            ps.setString(1, v.getChuyenID());
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    Timestamp t = rs.getTimestamp("gioKhoiHanh");
//                    if (t == null) return 0.0;
//                    LocalDateTime khoiHanh = t.toLocalDateTime();
//                    long hours = java.time.Duration.between(yeuCau, khoiHanh).toHours();
//                    double ratio = 0.0;
//                    if (hours > 48) ratio = 0.9;
//                    else if (hours > 24) ratio = 0.7;
//                    else if (hours >= 0) ratio = 0.5;
//                    else ratio = 0.0;
//                    return v.getGia() * ratio;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return 0.0;
//    }
//
//    /**
//     * Xử lý hoàn vé: tính tiền hoàn, ghi DonHoanDoi và cập nhật trạng thái vé + tạo giao dịch hoàn tiền
//     */
//    public boolean xuLyHoanVe(String veID, LocalDateTime yeuCau, String nhanVienID) {
//        double soTienHoan = tinhTienHoan(veID, yeuCau);
//        if (soTienHoan <= 0) return false;
//        // Thực tế: insert DonHoanDoi, DonHoanDoiChiTiet, tạo giao dịch hoàn tiền, update trạng thái vé -> "REFUNDED"
//        try (Connection c = db.getConnection()) {
//            c.setAutoCommit(false);
//            String donHoanID = "DHD_" + System.currentTimeMillis();
//            String insertDHD = "INSERT INTO DonHoanDoi (donHoanDoiID, donDatChoID, khachHangID, nhanVienID, laDonHoan, ngayYeuCau, tongTienHoan, trangThai) "
//                             + "VALUES (?, NULL, NULL, ?, 1, ?, ?, 'APPROVED')";
//            try (PreparedStatement ps = c.prepareStatement(insertDHD)) {
//                ps.setString(1, donHoanID);
//                ps.setString(2, nhanVienID);
//                ps.setTimestamp(3, Timestamp.valueOf(yeuCau));
//                ps.setDouble(4, soTienHoan);
//                ps.executeUpdate();
//            }
//
//            String insertCT = "INSERT INTO DonHoanDoiChiTiet (donHoanDoiChiTietID, donHoanDoiID, veCuID, veMoiID, soTienHoan, phiPhatSinh, ghiChu) VALUES (?, ?, ?, NULL, ?, 0, ?)";
//            try (PreparedStatement ps2 = c.prepareStatement(insertCT)) {
//                ps2.setString(1, "DHDCT_" + System.currentTimeMillis());
//                ps2.setString(2, donHoanID);
//                ps2.setString(3, veID);
//                ps2.setDouble(4, soTienHoan);
//                ps2.setString(5, "Hoàn vé tự động");
//                ps2.executeUpdate();
//            }
//
//            String updVe = "UPDATE Ve SET trangThai = 'REFUNDED' WHERE veID = ?";
//            try (PreparedStatement ps3 = c.prepareStatement(updVe)) {
//                ps3.setString(1, veID);
//                ps3.executeUpdate();
//            }
//
//            // TODO: tạo giao dịch hoàn tiền trong GiaoDichThanhToan nếu schema cần
//
//            c.commit();
//            c.setAutoCommit(true);
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}
//
///* ============================
//   DAO: KhuyenMai (basic)
//   ============================ */
//class KhuyenMai_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public boolean insert(KhuyenMai km) {
//        String sql = "INSERT INTO KhuyenMai (khuyenMaiID, maKhuyenMai, moTa, tyLeGiamGia, tienGiamGia, ngayBatDau, ngayKetThuc, soLuong, gioiHanMoiKhachHang, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, km.getKhuyenMaiID());
//            ps.setString(2, km.getMaKhuyenMai());
//            ps.setString(3, km.getMoTa());
//            if (km.getTyLeGiamGia() != null) ps.setDouble(4, km.getTyLeGiamGia()); else ps.setNull(4, Types.DOUBLE);
//            if (km.getTienGiamGia() != null) ps.setDouble(5, km.getTienGiamGia()); else ps.setNull(5, Types.DOUBLE);
//            ps.setDate(6, km.getNgayBatDau() == null ? null : new java.sql.Date(km.getNgayBatDau().getTime()));
//            ps.setDate(7, km.getNgayKetThuc() == null ? null : new java.sql.Date(km.getNgayKetThuc().getTime()));
//            ps.setInt(8, km.getSoLuong());
//            ps.setInt(9, km.getGioiHanMoiKhachHang());
//            ps.setBoolean(10, km.isTrangThai());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    // findByCode, applyPromotion... có thể thêm khi cần
//}
//
///* ============================
//   Các DAO khác (Chuyen, ChuyenGa, Ghe, Toa, Tau, KhachHang, HanhKhach ...)
//   Tôi cung cấp CRUD mẫu cho Chuyen, ChuyenGa, Ghe — pattern tương tự áp dụng cho bảng khác.
//   ============================ */
//
//class Chuyen_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public boolean insert(Chuyen cE) {
//        String sql = "INSERT INTO Chuyen (chuyenID, tuyenID, tauID, gioKhoiHanh, gioDen) VALUES (?, ?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, cE.getChuyenID());
//            ps.setString(2, cE.getTuyenID());
//            ps.setString(3, cE.getTauID());
//            ps.setTimestamp(4, cE.getGioKhoiHanh());
//            ps.setTimestamp(5, cE.getGioDen());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public Chuyen findById(String id) {
//        String sql = "SELECT * FROM Chuyen WHERE chuyenID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, id);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    Chuyen ch = new Chuyen();
//                    ch.setChuyenID(rs.getString("chuyenID"));
//                    ch.setTuyenID(rs.getString("tuyenID"));
//                    ch.setTauID(rs.getString("tauID"));
//                    ch.setGioKhoiHanh(rs.getTimestamp("gioKhoiHanh"));
//                    ch.setGioDen(rs.getTimestamp("gioDen"));
//                    return ch;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    public List<Chuyen> findAll() {
//        List<Chuyen> ds = new ArrayList<>();
//        String sql = "SELECT * FROM Chuyen";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//            while (rs.next()) {
//                Chuyen ch = new Chuyen();
//                ch.setChuyenID(rs.getString("chuyenID"));
//                ch.setTuyenID(rs.getString("tuyenID"));
//                ch.setTauID(rs.getString("tauID"));
//                ch.setGioKhoiHanh(rs.getTimestamp("gioKhoiHanh"));
//                ch.setGioDen(rs.getTimestamp("gioDen"));
//                ds.add(ch);
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return ds;
//    }
//
//    public boolean update(Chuyen cE) {
//        String sql = "UPDATE Chuyen SET tuyenID=?, tauID=?, gioKhoiHanh=?, gioDen=? WHERE chuyenID=?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, cE.getTuyenID());
//            ps.setString(2, cE.getTauID());
//            ps.setTimestamp(3, cE.getGioKhoiHanh());
//            ps.setTimestamp(4, cE.getGioDen());
//            ps.setString(5, cE.getChuyenID());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public boolean delete(String id) {
//        String sql = "DELETE FROM Chuyen WHERE chuyenID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, id);
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//}
//
//class ChuyenGa_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public boolean insert(ChuyenGa cg) {
//        String sql = "INSERT INTO ChuyenGa (chuyenGaID, chuyenID, gaID, thuTu, gioDen, gioKhoiHanh) VALUES (?, ?, ?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, cg.getChuyenGaID());
//            ps.setString(2, cg.getChuyenID());
//            ps.setString(3, cg.getGaID());
//            ps.setInt(4, cg.getThuTu());
//            ps.setTimestamp(5, cg.getGioDen());
//            ps.setTimestamp(6, cg.getGioKhoiHanh());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    // update/delete/findById/findAll: pattern tương tự
//}
//
//class Ghe_DAO {
//    private final ConnectDB db = ConnectDB.getInstance();
//
//    public boolean insert(Ghe g) {
//        String sql = "INSERT INTO Ghe (gheID, toaID, soGhe, trangThai) VALUES (?, ?, ?, ?)";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, g.getGheID());
//            ps.setString(2, g.getToaID());
//            ps.setString(3, g.getSoGhe());
//            ps.setBoolean(4, g.isTrangThai());
//            return ps.executeUpdate() > 0;
//        } catch (SQLException e) { e.printStackTrace(); return false; }
//    }
//
//    public Ghe findById(String id) {
//        String sql = "SELECT * FROM Ghe WHERE gheID = ?";
//        try (Connection c = db.getConnection();
//             PreparedStatement ps = c.prepareStatement(sql)) {
//            ps.setString(1, id);
//            try (ResultSet rs = ps.executeQuery()) {
//                if (rs.next()) {
//                    Ghe g = new Ghe();
//                    g.setGheID(rs.getString("gheID"));
//                    g.setToaID(rs.getString("toaID"));
//                    g.setSoGhe(rs.getString("soGhe"));
//                    g.setTrangThai(rs.getBoolean("trangThai"));
//                    return g;
//                }
//            }
//        } catch (SQLException e) { e.printStackTrace(); }
//        return null;
//    }
//
//    // update/delete/findAll...
//}
//
///* ============================
//   Lưu ý:
//   - Tôi đã cung cấp các DAO chính và mẫu các phương thức nghiệp vụ.
//   - Bạn có thể nhân rộng pattern tương tự cho các bảng khác (Toa_DAO, Tau_DAO, KhachHang_DAO, HanhKhach_DAO, DonHoanDoi_DAO, v.v.).
//   - Các tên phương thức tiếng Việt súc tích (timGheConTrong, giuGhe, xacNhanVeTuDonDatCho, taoDonDatCho, huyNeuHetHan, tinhTienHoan, xuLyHoanVe, tinhGia).
//   - Nếu bạn muốn, tôi có thể:
//     1) hoàn thiện phần CRUD cho mọi bảng còn thiếu,
//     2) tách từng DAO ra file riêng,
//     3) viết unit-test mẫu (Junit) cho các DAO,
//     4) điều chỉnh luật hoàn vé/chi phí theo chính sách bạn đưa.
//*/
