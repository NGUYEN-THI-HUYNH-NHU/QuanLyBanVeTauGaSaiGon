/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/1/2026
 */

package bus;

import dao.impl.TaiKhoan_DAO;
import dto.NhanVienDTO;
import entity.TaiKhoan;
import mapper.NhanVienMapper;

public class XacThuc_BUS {
    private final TaiKhoan_DAO taiKhoanDAO = new TaiKhoan_DAO();

    public XacThuc_BUS() {
    }

    private boolean kiemTraXacThuc(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = taiKhoanDAO.getTaiKhoanByTenDangNhap(tenDangNhap);
        if (taiKhoan == null || !taiKhoan.getMatKhauHash().equals(matKhau)) {
            return false;
        }
//		if (taiKhoan == null || !BCrypt.checkpw(matKhau, taiKhoan.getMatKhauHash())) {
//			return false;
//		}
        return true;
    }

    public NhanVienDTO getNhanVienByTenDangNhap(String tenDangNhap, String matKhau) {
        boolean isXacThuc = kiemTraXacThuc(tenDangNhap, matKhau);
        if (!isXacThuc) return null;
        return NhanVienMapper.INSTANCE.toDTO(taiKhoanDAO.getNhanVienByTenDangNhap(tenDangNhap, isXacThuc));
    }
}
