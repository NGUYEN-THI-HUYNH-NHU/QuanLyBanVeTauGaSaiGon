package bus;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: ThongKeKhachHang_BUS
 * Author: hongdung
 * Created: 5/5/26 9:55 AM
 */


import dao.impl.ThongKeKhachHangDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * BUS layer cho thống kê khách hàng.
 * Chịu trách nhiệm validate input và điều phối gọi DAO.
 */
public class ThongKeKhachHang_BUS {

    private final ThongKeKhachHangDAO dao = new ThongKeKhachHangDAO();

    /**
     * Lấy danh sách loại đối tượng khách hàng cho ComboBox.
     */
    public List<String> getDanhSachLoaiDoiTuong() {
        try {
            return dao.getDanhSachLoaiDoiTuong();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách loại đối tượng", e);
        }
    }

    /**
     * Lấy thống kê RFM khách hàng theo bộ lọc.
     *
     * @param tuNgay       Ngày bắt đầu
     * @param denNgay      Ngày kết thúc
     * @param loaiDoiTuong Loại đối tượng ("Tất cả" hoặc giá trị cụ thể)
     * @param phanLoai     Phân loại khách hàng ("Tất cả", "VIP", "Thân thiết", "Khách quay lại", "Ngủ đông", "Khách mới")
     */
    public Map<String, ThongKeKhachHangDAO.KhachHangRFM> getThongKeKhachHang(
            LocalDate tuNgay, LocalDate denNgay,
            String loaiDoiTuong, String phanLoai) {

        validateNgay(tuNgay, denNgay);

        try {
            return dao.getThongKeKhachHang(tuNgay, denNgay, loaiDoiTuong, phanLoai);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy thống kê khách hàng", e);
        }
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
