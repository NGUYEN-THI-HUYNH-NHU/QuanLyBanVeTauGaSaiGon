package bus;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: ThongKeDoanhThu_BUS
 * Author: hongdung
 * Created: 5/5/26 9:49 AM
 */

import dao.impl.ThongKeDoanhThuDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * BUS layer cho thống kê doanh thu.
 * Chịu trách nhiệm validate input và điều phối gọi DAO.
 */
public class ThongKeDoanhThu_BUS {

    private final ThongKeDoanhThuDAO dao = new ThongKeDoanhThuDAO();

    // ======================================================================
    //  THỐNG KÊ CHI TIẾT (cho bảng & biểu đồ)
    // ======================================================================
    public Map<String, ThongKeDoanhThuDAO.ThongKeChiTietItem> getThongKeChiTiet(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, Integer isTienMat) {

        validateNgay(tuNgay, denNgay);

        try {
            return dao.getThongKeDoanhThuChiTiet(
                    loaiThoiGian, tuNgay, denNgay,
                    loaiTuyen, tenGaDi, tenGaDen,
                    nhanVienID, isTienMat);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy thống kê chi tiết", e);
        }
    }

    // ======================================================================
    //  CÁC CHỈ SỐ TỔNG QUAN (cho cards)
    // ======================================================================
    public int getTongHoaDonBan(LocalDate tuNgay, LocalDate denNgay,
                                String loaiTuyen, String tenGaDi, String tenGaDen,
                                String nhanVienID, Integer isTienMat) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongHoaDonBan(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tổng hóa đơn bán", e);
        }
    }

    public int getTongHoaDonHoanDoi(LocalDate tuNgay, LocalDate denNgay,
                                    String loaiTuyen, String tenGaDi, String tenGaDen,
                                    String nhanVienID, Integer isTienMat) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongHoaDonHoanDoi(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tổng hóa đơn hoàn/đổi", e);
        }
    }

    public Map<String, Double> getTongDoanhThuChiLoiNhuan(LocalDate tuNgay, LocalDate denNgay,
                                                          String loaiTuyen, String tenGaDi, String tenGaDen,
                                                          String nhanVienID, Integer isTienMat) {
        validateNgay(tuNgay, denNgay);
        try {
            return dao.getTongDoanhThuChiLoiNhuan(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, isTienMat);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tổng doanh thu/chi/lợi nhuận", e);
        }
    }

    /**
     * Tính tổng tiền mặt và chuyển khoản.
     * Nếu isTienMat đã được lọc sẵn thì tính trực tiếp,
     * ngược lại gọi DAO 2 lần để lấy cả 2.
     */
    public Map<String, Double> getTongTienMatVaChuyenKhoan(LocalDate tuNgay, LocalDate denNgay,
                                                           String loaiTuyen, String tenGaDi, String tenGaDen,
                                                           String nhanVienID, Integer isTienMat,
                                                           double tongDoanhThu) {
        Map<String, Double> result = new HashMap<>();
        try {
            if (isTienMat != null) {
                if (isTienMat == 1) {
                    result.put("tongTienMat", tongDoanhThu);
                    result.put("tongChuyenKhoan", 0.0);
                } else {
                    result.put("tongTienMat", 0.0);
                    result.put("tongChuyenKhoan", tongDoanhThu);
                }
            } else {
                Map<String, Double> mTM = dao.getTongDoanhThuChiLoiNhuan(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, 1);
                Map<String, Double> mCK = dao.getTongDoanhThuChiLoiNhuan(tuNgay, denNgay, loaiTuyen, tenGaDi, tenGaDen, nhanVienID, 0);
                result.put("tongTienMat", mTM.get("doanhThu"));
                result.put("tongChuyenKhoan", mCK.get("doanhThu"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy tổng tiền mặt/chuyển khoản", e);
        }
        return result;
    }

    // ======================================================================
    //  VALIDATE
    // ======================================================================
    private void validateNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        if (tuNgay.isAfter(denNgay)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước hoặc bằng ngày kết thúc");
        }
    }
}