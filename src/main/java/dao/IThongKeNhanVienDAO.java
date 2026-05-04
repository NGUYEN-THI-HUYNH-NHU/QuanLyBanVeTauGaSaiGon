package dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: IThongKeNhanVienDAO
 * Author: hongdung
 * Created: 4/5/26 4:51 PM
 */
public interface IThongKeNhanVienDAO {
    // ====================== TỔNG SỐ HÓA ĐƠN HOÀN THÀNH ======================
    int getTongSoHoaDonBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT);

    // ====================== TỔNG SỐ HÓA ĐƠN ĐỔI / TRẢ ======================
    int getTongSoHoaDonDoiTra(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT);

    // ====================== TỔNG SỐ VÉ BÁN ======================
    int getTongSoVeBanDuoc(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT);

    // ====================== TỔNG TIỀN CHUYỂN KHOẢN ======================
    double getTongTienChuyenKhoan(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT);

    // ====================== TỔNG TIỀN MẶT ======================
    double getTongTienMat(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT);

    // ====================== LẤY DANH SÁCH HÓA ĐƠN (ĐÃ SỬA) ======================
    List<Object[]> getListHoaDonTrongCa(String maNV, LocalDate ngay, LocalTime gioBD, LocalTime gioKT);
}
