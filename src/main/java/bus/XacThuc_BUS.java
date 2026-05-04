/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/1/2026
 */

package bus;

import dao.impl.NhanVienDAO;
import dao.impl.TaiKhoanDAO;
import dto.NhanVienDTO;
import entity.TaiKhoan;
import mapper.NhanVienMapper;
import org.mindrot.jbcrypt.BCrypt;

public class XacThuc_BUS {
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public XacThuc_BUS() {
    }

    private boolean kiemTraXacThuc(String tenDangNhap, String matKhau) {
        TaiKhoan taiKhoan = taiKhoanDAO.getTaiKhoanVoiTenDangNhap(tenDangNhap);
        return taiKhoan != null && BCrypt.checkpw(matKhau, taiKhoan.getMatKhauHash());
    }

    public NhanVienDTO getNhanVienByTenDangNhap(String tenDangNhap, String matKhau) {
        boolean isXacThuc = kiemTraXacThuc(tenDangNhap, matKhau);
        if (!isXacThuc) return null;
        return NhanVienMapper.INSTANCE.toDTO(nhanVienDAO.getNhanVienByTenDangNhap(tenDangNhap));
    }
}
