package bus;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: ThongKeVe_BUS
 * Author: hongdung
 * Created: 5/5/26 11:49 AM
 */

import dao.impl.ThongKeVeDAO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * BUS layer cho thống kê vé.
 * Chịu trách nhiệm validate input và điều phối gọi DAO.
 */
public class ThongKeVe_BUS {

    private final ThongKeVeDAO dao = new ThongKeVeDAO();

    // =========================================================================
    // VALIDATE
    // =========================================================================
    private void validateNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        }
    }

    // =========================================================================
    // 1. COMBOBOX DATA
    // =========================================================================

    public List<String> getDanhSachTenGa() {
        try {
            return dao.getDanhSachTenGa();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách ga", e);
        }
    }

    public Map<String, String> getDanhSachLoaiVe() {
        try {
            return dao.getDanhSachLoaiVe();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách loại vé", e);
        }
    }

    public Map<String, String> getDanhSachNhanVien() {
        try {
            return dao.getDanhSachNhanVien();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách nhân viên", e);
        }
    }

    // =========================================================================
    // 2. THỐNG KÊ CHI TIẾT (BIỂU ĐỒ/BẢNG)
    // =========================================================================

    public Map<String, ThongKeVeDAO.ThongKeVeChiTietItem> getThongKeVeChiTietTheoThoiGian(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, String hangToaID, String trangThai) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getThongKeVeChiTietTheoThoiGian(loaiThoiGian, tuNgay, denNgay,
                    loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thống kê vé chi tiết", e);
        }
    }

    // =========================================================================
    // 3. CARD TỔNG QUAN
    // =========================================================================

    public int getTongSoVeBanTrongKhoang(LocalDate tuNgay, LocalDate denNgay,
                                         String loaiTuyen, String tenGaDi, String tenGaDen,
                                         String nhanVienID, String hangToaID, String trangThai) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongSoVeBanTrongKhoang(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng số vé bán", e);
        }
    }

    public int getTongVeConHieuLucTrongKhoang(LocalDate tuNgay, LocalDate denNgay,
                                              String loaiTuyen, String tenGaDi, String tenGaDen,
                                              String nhanVienID, String hangToaID, String trangThai) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongVeConHieuLucTrongKhoang(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng vé còn hiệu lực", e);
        }
    }

    public int getTongVeDaDungTrongKhoang(LocalDate tuNgay, LocalDate denNgay,
                                          String loaiTuyen, String tenGaDi, String tenGaDen,
                                          String nhanVienID, String hangToaID, String trangThai) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongVeDaDungTrongKhoang(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng vé đã dùng", e);
        }
    }

    public int getTongVeDaDoiTrongKhoang(LocalDate tuNgay, LocalDate denNgay,
                                         String loaiTuyen, String tenGaDi, String tenGaDen,
                                         String nhanVienID, String hangToaID, String trangThai) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongVeDaDoiTrongKhoang(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng vé đã đổi", e);
        }
    }

    public int getTongVeHoanTrongKhoang(LocalDate tuNgay, LocalDate denNgay,
                                        String loaiTuyen, String tenGaDi, String tenGaDen,
                                        String nhanVienID, String hangToaID, String trangThai) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongVeHoanTrongKhoang(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng vé hoàn", e);
        }
    }

    public double getTongTienVeTrongKhoang(LocalDate tuNgay, LocalDate denNgay,
                                           String loaiTuyen, String tenGaDi, String tenGaDen,
                                           String nhanVienID, String hangToaID, String trangThai) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongTienVeTrongKhoang(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, hangToaID, trangThai);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy tổng tiền vé", e);
        }
    }
}