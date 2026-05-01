package bus;

import dao.impl.TaiKhoan_DAO;
import entity.NhanVien;
import entity.NhatKyAudit;
import entity.TaiKhoan;
import gui.application.AuthService;
import mapper.NhanVienMapper;

import java.util.List;

public class TaiKhoan_BUS {
    private final TaiKhoan_DAO taiKhoan_dao;
    private final NhanVien nhanVienHienTai;

    public TaiKhoan_BUS() {
        taiKhoan_dao = new TaiKhoan_DAO();
        this.nhanVienHienTai = NhanVienMapper.INSTANCE.toEntity(AuthService.getInstance().getCurrentUser());
    }

    // lay danh sach tai khoan
    public List<TaiKhoan> layDanhSachTaiKhoan() {
        return taiKhoan_dao.getDanhSachTaiKhoan();
    }

    // lay tai khoan theo ten dang nhap
    public TaiKhoan layTKThenDangNhap(String tenDangNhap) {
        return taiKhoan_dao.getTaiKhoanByTenDangNhap(tenDangNhap);
    }

    // lay tai khoan theo ma nhan vien
    public TaiKhoan layTKTheoMaNV(String maNV) {
        return taiKhoan_dao.getTaiKhoanVoiNhanVienID(maNV);
    }

    // kiem tra ten dang nhap da ton tai
    public boolean kiemTraTenDangNhapTonTai(String tenDN) {
        return taiKhoan_dao.kiemTraTenDangNhap(tenDN);
    }

    // doi mat khau
    public boolean doiMatKhau(String tenDN, String matKhauMoi) {
        return taiKhoan_dao.capNhatMatKhau(tenDN, matKhauMoi);
    }

    // tao ma tai khoan
    public String taoMaTaiKhoan() {
        return taiKhoan_dao.taoMaTaiKhoanMoi();
    }

    // regex
    // kiem tra ten dang nhap
    public boolean kiemTraTenDangNhap(String tenDN) {
        String regex = "^[a-zA-Z0-9._-]{5,20}$";
        return tenDN.matches(regex);
    }

    // kiem tra xac nhan mat khau
    public boolean kiemTraXacNhanMatKhau(String matKhau, String xacNhanMatKhau) {
        return matKhau.equals(xacNhanMatKhau);
    }

    // tim kiem tong hop
    public List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
        return taiKhoan_dao.timKiemTongHop(maNV, tenDN, vaiTro, trangThai);
    }

    public boolean isKhopMatKhau(String nhanVienID, String matKhau) {
        return taiKhoan_dao.getTaiKhoanVoiNhanVienID(nhanVienID).getMatKhauHash().equals(matKhau);
    }

    // ghi log
    public void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
        if (nguoiThucHienID == null || nguoiThucHienID.isBlank()) {
            nguoiThucHienID = nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : "SYSTEM";
        }

        NhatKyAudit_BUS nhatKyAudit_bus = new NhatKyAudit_BUS();
        NhatKyAudit audit = new NhatKyAudit(nhatKyAudit_bus.taoMaNhatKyAuditMoi(), doiTuongID, nguoiThucHienID,
                java.time.LocalDateTime.now(), loai, chiTiet, "TAI_KHOAN");
        nhatKyAudit_bus.ghiNhatKyAudit(audit);
    }

    // them tai khoan
    public boolean themTaiKhoan(TaiKhoan tk) {
        boolean ok = taiKhoan_dao.taoTaiKhoan(tk);

        if (ok) {
            ghiLog(tk.getTaiKhoanID(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
                    entity.type.NhatKyAudit.THEM, "Thêm tài khoản: " + "Mã tài khoản " + tk.getTaiKhoanID() + "\n"
                            + "Tên đăng nhập: " + tk.getTenDangNhap());
        }
        return ok;
    }

    // tìm các thành phần đã bị thay đổi
    public String thanhPhanDaBiThayDoi(TaiKhoan cu, TaiKhoan moi) {
        StringBuilder thayDoi = new StringBuilder();

        if (!cu.getTenDangNhap().equals(moi.getTenDangNhap())) {
            thayDoi.append(String.format("Cập nhật tên đăng nhập: ('%s' -> '%s')" + "\n", cu.getTenDangNhap(),
                    moi.getTenDangNhap()));
        }
        if (cu.isTrangThai() != moi.isTrangThai()) {
            thayDoi.append(String.format("Cập nhật trạng thái: ('%s' -> '%s')" + "\n",
                    cu.isTrangThai() ? "Hoạt động" : "Khóa", moi.isTrangThai() ? "Hoạt động" : "Khóa"));
        }
        if (!cu.getVaiTroTaiKhoan().equals(moi.getVaiTroTaiKhoan())) {
            thayDoi.append(String.format("Cập nhật vai trò: ('%s' -> '%s')" + "\n", cu.getVaiTroTaiKhoan(),
                    moi.getVaiTroTaiKhoan()));
        }
        if (!cu.getMatKhauHash().equals(moi.getMatKhauHash())) {
            thayDoi.append("Cập nhật mật khẩu" + "\n");
        }
        return thayDoi.toString();
    }

    // sua tai khoan
    public boolean suaTaiKhoan(TaiKhoan tkMoi) {

        // 1. Lay tai khoan cu
        TaiKhoan tkCu = taiKhoan_dao.timTaiKhoanTheoID(tkMoi.getTaiKhoanID());
        if (tkCu == null) {
            return false;
        }

        // 2. Tim cac thanh phan bi thay doi
        String thayDoi = thanhPhanDaBiThayDoi(tkCu, tkMoi);
        if (thayDoi.isEmpty()) {
            return true;
        }

        // 3. Cap nhat tai khoan
        boolean ok = taiKhoan_dao.capNhatTaiKhoan(tkMoi);
        if (ok) {
            ghiLog(tkMoi.getTaiKhoanID(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
                    entity.type.NhatKyAudit.SUA, "Sửa tài khoản: " + "\n" + thayDoi);
        }
        return ok;
    }

    /**
     * @param nhanVienID
     * @param cccd
     * @param email
     * @return
     */
    public boolean kiemTraThongTinQuenMatKhau(String nhanVienID, String cccd, String email) {
        return taiKhoan_dao.checkForgotPasswordInfo(nhanVienID, cccd, email);
    }

    /**
     * @param nhanVienID
     * @param newPass
     * @return
     */
    public boolean kiemTraDatLaiMatKhauTrung(String nhanVienID, String newPass) {
        return taiKhoan_dao.checkDuplicatingPasswords(nhanVienID, newPass);
    }

    /**
     * @param nhanVienID
     * @param newPass
     * @return
     */
    public boolean capNhatMatKhau(String nhanVienID, String newPass) {
        return taiKhoan_dao.capNhatMatKhau(nhanVienID, newPass);
    }
}