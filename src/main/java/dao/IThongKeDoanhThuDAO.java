package dao;

import dao.impl.ThongKeDoanhThuDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: IThongKeDoanhThuDAO
 * Author: hongdung
 * Created: 4/5/26 4:23 PM
 */
public interface IThongKeDoanhThuDAO {
    // ======================================================================
    //  1. THỐNG KÊ CHI TIẾT (BẢNG & BIỂU ĐỒ)
    // ======================================================================
    Map<String, ThongKeDoanhThuDAO.ThongKeChiTietItem> getThongKeDoanhThuChiTiet(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, Integer isTienMat) throws SQLException;

    int getTongHoaDonBan(LocalDate tuNgay, LocalDate denNgay,
                         String loaiTuyen, String tenGaDi, String tenGaDen,
                         String nhanVienID, Integer isTienMat) throws SQLException;

    int getTongHoaDonHoanDoi(LocalDate tuNgay, LocalDate denNgay,
                             String loaiTuyen, String tenGaDi, String tenGaDen,
                             String nhanVienID, Integer isTienMat) throws SQLException;

    double getTongThuDichVu(LocalDate tuNgay, LocalDate denNgay,
                            String loaiTuyen, String tenGaDi, String tenGaDen,
                            String nhanVienID, Integer isTienMat) throws SQLException;

    Map<String, Double> getTongDoanhThuChiLoiNhuan(LocalDate tuNgay, LocalDate denNgay,
                                                   String loaiTuyen, String tenGaDi, String tenGaDen,
                                                   String nhanVienID, Integer isTienMat) throws SQLException;
}
