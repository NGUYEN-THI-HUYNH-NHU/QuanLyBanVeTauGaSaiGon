package dao;

import dao.impl.ThongKeVeDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: IThongKeVeDAO
 * Author: hongdung
 * Created: 4/5/26 8:56 PM
 */
public interface IThongKeVeDAO {
    List<String> getDanhSachTenGa() throws SQLException;

    Map<String, String> getDanhSachLoaiVe() throws SQLException;

    Map<String, String> getDanhSachNhanVien() throws SQLException;

    Map<String, ThongKeVeDAO.ThongKeVeChiTietItem> getThongKeVeChiTietTheoThoiGian(
            String loaiThoiGian, LocalDate tuNgay, LocalDate denNgay,
            String loaiTuyen, String tenGaDi, String tenGaDen,
            String nhanVienID, String hangToaID, String trangThai) throws SQLException;

    int getTongSoVeBanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException;

    int getTongVeConHieuLucTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException;

    int getTongVeDaDungTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException;

    int getTongVeDaDoiTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException;

    int getTongVeHoanTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException;

    double getTongTienVeTrongKhoang(LocalDate tuNgay, LocalDate denNgay, String loaiTuyen, String tenGaDi, String tenGaDen, String nhanVienID, String hangToaID, String trangThai) throws SQLException;
}
