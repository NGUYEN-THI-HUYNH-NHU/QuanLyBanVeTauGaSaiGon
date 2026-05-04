package bus;

import dao.ITaiKhoanDAO;
import dao.impl.TaiKhoanDAO;
import entity.NhanVien;
import entity.NhatKyAudit;
import entity.TaiKhoan;
import entity.VaiTroTaiKhoan;
import gui.application.AuthService;
import mapper.NhanVienMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TaiKhoan_BUS {
    private final ITaiKhoanDAO taiKhoanDAO;
    private final NhanVien nhanVienHienTai;

    public TaiKhoan_BUS() {
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.nhanVienHienTai = NhanVienMapper.INSTANCE.toEntity(AuthService.getInstance().getCurrentUser());
    }

    // ================= LẤY DỮ LIỆU =================

    public List<TaiKhoan> layDanhSachTaiKhoan() {
        // SỬA TẠI ĐÂY: Gọi đúng hàm của DAO đã có LEFT JOIN FETCH
        return taiKhoanDAO.getDanhSachTaiKhoan();
    }

    public TaiKhoan layTKThenDangNhap(String tenDangNhap) {
        return taiKhoanDAO.getTaiKhoanVoiTenDangNhap(tenDangNhap);
    }

    public TaiKhoan layTKTheoMaNV(String maNV) {
        return taiKhoanDAO.getTaiKhoanVoiNhanVienID(maNV);
    }

    public boolean kiemTraTenDangNhapTonTai(String tenDN) {
        return taiKhoanDAO.kiemTraTenDangNhap(tenDN);
    }

    public String taoMaTaiKhoan() {
        return taiKhoanDAO.taoMaTaiKhoanMoi();
    }

    // SỬA TẠI ĐÂY: Thêm hàm lấy tài khoản theo ID
    public TaiKhoan layTKTheoID(String id) {
        return taiKhoanDAO.findById(id);
    }

    // SỬA TẠI ĐÂY: Thêm hàm lấy vai trò đầy đủ dữ liệu từ DB
    public VaiTroTaiKhoan layVaiTroTheoID(String vaiTroID) {
        dao.impl.AbstractGenericDAO<VaiTroTaiKhoan, String> vaiTroDAO =
                new dao.impl.AbstractGenericDAO<>(VaiTroTaiKhoan.class) {};
        return vaiTroDAO.findById(vaiTroID);
    }

    // ================= THAO TÁC CẬP NHẬT / THÊM MỚI =================

    public boolean doiMatKhau(String nhanVienID, String matKhauMoi) {
        return taiKhoanDAO.capNhatMatKhau(nhanVienID, matKhauMoi);
    }

    public boolean themTaiKhoan(TaiKhoan tk) {
        if (tk == null) return false;
        try {
            taiKhoanDAO.create(tk);
            ghiLog(tk.getTaiKhoanID(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
                    entity.type.NhatKyAudit.THEM, "Thêm tài khoản: " + "Mã tài khoản " + tk.getTaiKhoanID() + "\n"
                            + "Tên đăng nhập: " + tk.getTenDangNhap());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean suaTaiKhoan(TaiKhoan tkMoi) {
        if (tkMoi == null) return false;

        TaiKhoan tkCu = taiKhoanDAO.findById(tkMoi.getTaiKhoanID());
        if (tkCu == null) {
            return false;
        }

        String thayDoi = thanhPhanDaBiThayDoi(tkCu, tkMoi);
        if (thayDoi.isEmpty()) {
            return true;
        }

        try {
            taiKhoanDAO.update(tkMoi);
            ghiLog(tkMoi.getTaiKhoanID(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
                    entity.type.NhatKyAudit.SUA, "Sửa tài khoản: " + "\n" + thayDoi);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= VALIDATION LOGIC =================

    public boolean kiemTraTenDangNhap(String tenDN) {
        String regex = "^[a-zA-Z0-9._-]{5,20}$";
        return tenDN != null && tenDN.matches(regex);
    }

    public boolean kiemTraXacNhanMatKhau(String matKhau, String xacNhanMatKhau) {
        if (matKhau == null || xacNhanMatKhau == null) return false;
        return matKhau.equals(xacNhanMatKhau);
    }

    public boolean isKhopMatKhau(String nhanVienID, String matKhau) {
        if (nhanVienID == null || matKhau == null) return false;
        TaiKhoan tk = taiKhoanDAO.getTaiKhoanVoiNhanVienID(nhanVienID);
        return tk != null && Objects.equals(tk.getMatKhauHash(), matKhau);
    }

    public List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
        return taiKhoanDAO.timKiemTongHop(maNV, tenDN, vaiTro, trangThai);
    }

    public boolean kiemTraThongTinQuenMatKhau(String nhanVienID, String soDienThoai, String email) {
        return taiKhoanDAO.checkForgotPasswordInfo(nhanVienID, soDienThoai, email);
    }

    public boolean kiemTraDatLaiMatKhauTrung(String nhanVienID, String newPass) {
        return taiKhoanDAO.checkDuplicatingPasswords(nhanVienID, newPass);
    }

    public boolean capNhatMatKhau(String nhanVienID, String newPass) {
        return taiKhoanDAO.capNhatMatKhau(nhanVienID, newPass);
    }

    // ================= LOGGING & AUDIT =================

    public void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
        String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ?
                (nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : "SYSTEM") : nguoiThucHienID;

        NhatKyAudit_BUS nhatKyAudit_bus = new NhatKyAudit_BUS();
        NhatKyAudit audit = new NhatKyAudit(
                nhatKyAudit_bus.taoMaNhatKyAuditMoi(),
                doiTuongID,
                nguoi,
                LocalDateTime.now(),
                loai,
                chiTiet,
                "TAI_KHOAN"
        );
        nhatKyAudit_bus.ghiNhatKyAudit(audit);
    }

    // ================= LẤY THÀNH PHẦN BỊ THAY ĐỔI =================

    public String thanhPhanDaBiThayDoi(TaiKhoan cu, TaiKhoan moi) {
        StringBuilder thayDoi = new StringBuilder();

        if (cu == null || moi == null) return thayDoi.toString();

        if (!Objects.equals(cu.getTenDangNhap(), moi.getTenDangNhap())) {
            thayDoi.append(String.format("Cập nhật tên đăng nhập: ('%s' -> '%s')\n", cu.getTenDangNhap(), moi.getTenDangNhap()));
        }
        if (cu.isTrangThai() != moi.isTrangThai()) {
            thayDoi.append(String.format("Cập nhật trạng thái: ('%s' -> '%s')\n",
                    cu.isTrangThai() ? "Hoạt động" : "Khóa", moi.isTrangThai() ? "Hoạt động" : "Khóa"));
        }

        // SỬA TẠI ĐÂY: Trích xuất ID vai trò ra để so sánh, tránh lỗi Proxy/Lazy Loading
        String vtCuID = cu.getVaiTroTaiKhoan() != null ? cu.getVaiTroTaiKhoan().getVaiTroTaiKhoanID() : "Trống";
        String vtMoiID = moi.getVaiTroTaiKhoan() != null ? moi.getVaiTroTaiKhoan().getVaiTroTaiKhoanID() : "Trống";

        if (!Objects.equals(vtCuID, vtMoiID)) {
            String vtCuDesc = cu.getVaiTroTaiKhoan() != null ? cu.getVaiTroTaiKhoan().getDescription() : "Trống";
            String vtMoiDesc = moi.getVaiTroTaiKhoan() != null ? moi.getVaiTroTaiKhoan().getDescription() : "Trống";
            thayDoi.append(String.format("Cập nhật vai trò: ('%s' -> '%s')\n", vtCuDesc, vtMoiDesc));
        }

        if (moi.getMatKhauHash() != null && !Objects.equals(cu.getMatKhauHash(), moi.getMatKhauHash())) {
            thayDoi.append("Cập nhật mật khẩu\n");
        }
        return thayDoi.toString();
    }
}