package dao;

import dao.impl.ThongKeKhachHangDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Project: QuanLyBanVeTauGaSaiGon
 * File: IThongKeKhachHangDAO
 * Author: hongdung
 * Created: 4/5/26 4:50 PM
 */
public interface IThongKeKhachHangDAO {
    List<String> getDanhSachLoaiDoiTuong() throws SQLException;

    Map<String, ThongKeKhachHangDAO.KhachHangRFM> getThongKeKhachHang(
            LocalDate tuNgay, LocalDate denNgay,
            String loaiDoiTuong, String phanLoai) throws SQLException;
}
