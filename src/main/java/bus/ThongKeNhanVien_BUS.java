package bus;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: ThongKeNhanVien_BUS
 * Author: hongdung
 * Created: 5/5/26 11:38 AM
 */

import dao.impl.ThongKeNhanVienDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * BUS layer cho thống kê nhân viên.
 * Chịu trách nhiệm validate input và điều phối gọi DAO.
 */
public class ThongKeNhanVien_BUS {

    private final ThongKeNhanVienDAO dao = new ThongKeNhanVienDAO();

    // =========================================================================
    // VALIDATE
    // =========================================================================
    private void validateInput(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        if (maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }
        if (ngay == null) {
            throw new IllegalArgumentException("Ngày không được để trống");
        }
        if (gioBD == null || gioKT == null) {
            throw new IllegalArgumentException("Giờ bắt đầu và giờ kết thúc không được để trống");
        }
        if (gioBD.isAfter(gioKT)) {
            throw new IllegalArgumentException("Giờ bắt đầu phải trước giờ kết thúc");
        }
    }

    // =========================================================================
    // 1. TỔNG SỐ HÓA ĐƠN BÁN ĐƯỢC
    // =========================================================================
    public int getTongSoHoaDonBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        validateInput(maNV, ngay, gioBD, gioKT);
        return dao.getTongSoHoaDonBanDuoc(maNV, ngay, gioBD, gioKT);
    }

    // =========================================================================
    // 2. TỔNG SỐ HÓA ĐƠN ĐỔI / TRẢ
    // =========================================================================
    public int getTongSoHoaDonDoiTra(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        validateInput(maNV, ngay, gioBD, gioKT);
        return dao.getTongSoHoaDonDoiTra(maNV, ngay, gioBD, gioKT);
    }

    // =========================================================================
    // 3. TỔNG SỐ VÉ BÁN ĐƯỢC
    // =========================================================================
    public int getTongSoVeBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        validateInput(maNV, ngay, gioBD, gioKT);
        return dao.getTongSoVeBanDuoc(maNV, ngay, gioBD, gioKT);
    }

    // =========================================================================
    // 4. TỔNG TIỀN CHUYỂN KHOẢN
    // =========================================================================
    public double getTongTienChuyenKhoan(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        validateInput(maNV, ngay, gioBD, gioKT);
        return dao.getTongTienChuyenKhoan(maNV, ngay, gioBD, gioKT);
    }

    // =========================================================================
    // 5. TỔNG TIỀN MẶT
    // =========================================================================
    public double getTongTienMat(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        validateInput(maNV, ngay, gioBD, gioKT);
        return dao.getTongTienMat(maNV, ngay, gioBD, gioKT);
    }

    // =========================================================================
    // 6. DANH SÁCH HÓA ĐƠN TRONG CA
    // =========================================================================
    public List<Object[]> getListHoaDonTrongCa(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT) {
        validateInput(maNV, ngay, gioBD, gioKT);
        return dao.getListHoaDonTrongCa(maNV, ngay, gioBD, gioKT);
    }
}