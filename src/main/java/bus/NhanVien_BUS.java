package bus;

import dao.impl.NhanVien_DAO;
import entity.CaLam;
import entity.NhanVien;
import entity.NhatKyAudit;
import entity.type.VaiTroNhanVien;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NhanVien_BUS {

    private final NhanVien_DAO nhanVien_dao;
    private final NhatKyAudit_BUS audit_bus;

    public NhanVien_BUS(NhatKyAudit_BUS auditBus) {
        this.audit_bus = auditBus;
        this.nhanVien_dao = new NhanVien_DAO();
    }

    // ================= LẤY DỮ LIỆU =================

    public List<NhanVien> layDanhSachNhanVien() {
        return nhanVien_dao.findAll();
    }

    public NhanVien layNhanVienBangMaNV(String maNV) {
        return nhanVien_dao.findById(maNV);
    }

    // ================= THÊM NHÂN VIÊN =================

    public boolean themNhanVien(NhanVien nv, String nguoiThucHienID) {
        NhanVien res = nhanVien_dao.create(nv);

        if (res != null) {
            ghiAudit(
                    nv.getId(),
                    nguoiThucHienID,
                    entity.type.NhatKyAudit.THEM,
                    "Thêm nhân viên: " + nv.getHoTen() + "-" + nv.getSoDienThoai()
            );
        }
        return res != null;
    }

    // ================= CẬP NHẬT AVATAR =================

    public boolean capNhatAvatar(String nhanVienID, byte[] imgBytes, String nguoiThucHienID) {
        boolean ok = nhanVien_dao.capNhatAvatar(nhanVienID, imgBytes);

        if (ok) {
            ghiAudit(
                    nhanVienID,
                    nguoiThucHienID,
                    entity.type.NhatKyAudit.SUA,
                    "Cập nhật ảnh đại diện nhân viên"
            );
        }
        return ok;
    }

    // giữ hàm cũ để không vỡ code
    public boolean capNhatAvatar(String nhanVienID, byte[] imgBytes) {
        return capNhatAvatar(nhanVienID, imgBytes, nhanVienID);
    }


    //
    private void ghiAudit(
            String doiTuongID,
            String nguoiThucHienID,
            entity.type.NhatKyAudit loai,
            String chiTiet
    ) {
        if (audit_bus == null) return;

        String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ? "SYSTEM" : nguoiThucHienID;

        NhatKyAudit audit = new NhatKyAudit(
                audit_bus.taoMaNhatKyAuditMoi(),
                doiTuongID,
                nguoi,
                LocalDateTime.now(),
                loai,
                chiTiet,
                "NHAN_VIEN"
        );

        audit_bus.ghiNhatKyAudit(audit);
    }

    public boolean validHoTen(String hoTen) {
        String regex = "^([A-ZÀ-ỸĐ][a-zà-ỹđ]*)(\\s[A-ZÀ-ỸĐ][a-zà-ỹđ]*)*$";
        return hoTen.matches(regex);
    }

    public boolean validSDT(String sdt) {
        String regex = "^(0[35789][0-9]{8})$";
        return sdt.matches(regex);
    }

    public boolean validEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

    public boolean validDiaChi(String diaChi) {
        String regex = "^[\\wÀ-ỹ0-9\\s,.-]{1,100}$";
        return diaChi.matches(regex);
    }

    public boolean ngaySinh(LocalDate ns) {
        return ns.isBefore(LocalDate.now());
    }

    public boolean ngayThamGia(LocalDate ntg) {
        return !ntg.isAfter(LocalDate.now());
    }

    public boolean validCaLam(String caLam) {
        String regex = "^(Sáng|Chiều|Tối)$";
        return caLam.matches(regex);
    }

    public boolean validGioiTinh(boolean isNu) {
        return true;
    }


    public String taoMaNhanVienTuDong() {
        return nhanVien_dao.taoMaNhanVienTuDong();
    }

    public List<NhanVien> timKiemNhanVien(String ten, String sdt, String vaiTroID, Boolean isHoatDong) {
        return nhanVien_dao.timKiemNhanVien(ten, sdt, vaiTroID, isHoatDong);
    }

    public VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV) {
        NhanVien nv = nhanVien_dao.findById(maNV);
        return (nv != null) ? VaiTroNhanVien.valueOf(nv.getVaiTroNhanVien().getId()) : null;
    }

    public List<String> layDanhSachMaNhanVien() {
        return nhanVien_dao.layDanhSachMaNhanVien();
    }


    //======================== TÌM RA THÀNH PHẦN ĐÃ BỊ CHỈNH SỬA ===========================
    private String thanhPhanBiChinhSua(NhanVien cu, NhanVien moi) {
        StringBuilder sb = new StringBuilder();

        if (!Objects.equals(cu.getVaiTroNhanVien(), moi.getVaiTroNhanVien()))
            sb.append("Cập nhật vai trò nhân viên: (" + cu.getVaiTroNhanVien() + " -> " + moi.getVaiTroNhanVien() + "), ");
        if (!Objects.equals(cu.getHoTen(), moi.getHoTen()))
            sb.append("Cập nhật họ tên: (" + cu.getHoTen() + " -> " + moi.getHoTen() + "), ");
        if (cu.isNu() != moi.isNu())
            sb.append("Giới tính: (" + (cu.isNu() ? "Nữ" : "Nam") + " -> " + (moi.isNu() ? "Nữ" : "Nam") + "),");
        if (!Objects.equals(cu.getNgaySinh(), moi.getNgaySinh()))
            sb.append("Ngày sinh: [" + cu.getNgaySinh() + " -> " + moi.getNgaySinh() + "], ");
        if (!Objects.equals(cu.getSoDienThoai(), moi.getSoDienThoai()))
            sb.append("Số điện thoại: [" + cu.getSoDienThoai() + " -> " + moi.getSoDienThoai() + "], ");
        if (!Objects.equals(cu.getEmail(), moi.getEmail()))
            sb.append("Email: (" + cu.getEmail() + " -> " + moi.getEmail() + "), ");
        if (!Objects.equals(cu.getDiaChi(), moi.getDiaChi()))
            sb.append("Địa chỉ: (" + cu.getDiaChi() + " -> " + moi.getDiaChi() + "), ");
        if (!Objects.equals(cu.getNgayThamGia(), moi.getNgayThamGia()))
            sb.append("Ngày tham gia: (" + cu.getNgayThamGia() + " -> " + moi.getNgayThamGia() + "), ");
        if (cu.isHoatDong() != moi.isHoatDong())
            sb.append("Trạng thái hoạt động: " + (cu.isHoatDong() ? "Hoạt động" : "Không hoạt động") + " -> " + (moi.isHoatDong() ? "Hoạt động" : "Không hoạt động") + ", ");
        if (!Objects.equals(cu.getCaLam(), moi.getCaLam()))
            sb.append("Ca làm: [" + cu.getCaLam() + " -> " + moi.getCaLam() + "]" + ", ");
        if (!Arrays.equals(cu.getAvatar(), moi.getAvatar())) sb.append("Avatar: [Đã thay đổi]" + ", ");

        if (sb.length() > 0) sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    // ================= SỬA NHÂN VIÊN =================

    public boolean suaNhanvVien(NhanVien nv, String nguoiThucHienID) {

        //1. Lấy thông tin nhân viên cũ
        NhanVien nvCu = nhanVien_dao.findById(nv.getId());
        if (nv == null) return false;

        //2. Cập nhật nhân viên
        NhanVien ok = nhanVien_dao.update(nv);
        if (ok == null) return false;

        // 3) build chi tiết thay đổi
        String thanhPhan = thanhPhanBiChinhSua(nvCu, nv);
        if (thanhPhan == null || thanhPhan.isBlank()) return true;

        //4. Ghi nhật ký audit
        ghiAudit(
                nv.getId(),
                nguoiThucHienID,
                entity.type.NhatKyAudit.SUA,
                "Cập nhật nhân viên. " + thanhPhan
        );
        return true;
    }

    // lấy tất cả ca làm
    public List<CaLam> layTatCaCaLam() {
        return nhanVien_dao.getAllCaLam();
    }

    //lấy ca làm
    public CaLam layCaLamTheoTen(String tenCa) {
        return nhanVien_dao.getCaLamById(tenCa);
    }

}
