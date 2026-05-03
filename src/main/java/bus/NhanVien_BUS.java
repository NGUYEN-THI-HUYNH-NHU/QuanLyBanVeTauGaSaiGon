package bus;

import dao.INhanVienDAO;
import dao.impl.NhanVienDAO;
import entity.CaLam;
import entity.NhanVien;
import entity.NhatKyAudit;
import entity.VaiTroNhanVien;
import entity.type.VaiTroNhanVienEnums;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NhanVien_BUS {

    private final INhanVienDAO nhanVienDAO;
    private final NhatKyAudit_BUS audit_bus;

    public NhanVien_BUS(NhatKyAudit_BUS auditBus) {
        this.audit_bus = auditBus;
        this.nhanVienDAO = new NhanVienDAO();
    }

    // ================= LẤY DỮ LIỆU =================

    public List<NhanVien> layDanhSachNhanVien() {
        return nhanVienDAO.findAll();
    }

    public NhanVien layNhanVienBangMaNV(String maNV) {
        return nhanVienDAO.findById(maNV);
    }

    // ================= THÊM NHÂN VIÊN =================

    public boolean themNhanVien(NhanVien nv, String nguoiThucHienID) {
        if (nv == null) return false;
        try {
            nhanVienDAO.create(nv);
            ghiAudit(
                    nv.getNhanVienID(),
                    nguoiThucHienID,
                    entity.type.NhatKyAudit.THEM,
                    "Thêm nhân viên: " + nv.getHoTen() + "-" + nv.getSoDienThoai()
            );
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= CẬP NHẬT AVATAR =================

    public boolean capNhatAvatar(String nhanVienID, byte[] imgBytes, String nguoiThucHienID) {
        boolean ok = nhanVienDAO.capNhatAvatar(nhanVienID, imgBytes);

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


    // ================= GHI NHẬT KÝ AUDIT =================
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

    // ================= VALIDATION LOGIC =================

    public boolean validHoTen(String hoTen) {
        String regex = "^([A-ZÀ-ỸĐ][a-zà-ỹđ]*)(\\s[A-ZÀ-ỸĐ][a-zà-ỹđ]*)*$";
        return hoTen != null && hoTen.matches(regex);
    }

    public boolean validSDT(String sdt) {
        String regex = "^(0[35789][0-9]{8})$";
        return sdt != null && sdt.matches(regex);
    }

    public boolean validEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(regex);
    }

    public boolean validDiaChi(String diaChi) {
        String regex = "^[\\wÀ-ỹ0-9\\s,.-]{1,100}$";
        return diaChi != null && diaChi.matches(regex);
    }

    public boolean ngaySinh(LocalDate ns) {
        return ns != null && ns.isBefore(LocalDate.now());
    }

    public boolean ngayThamGia(LocalDate ntg) {
        return ntg != null && !ntg.isAfter(LocalDate.now());
    }

    public boolean validCaLam(String caLam) {
        String regex = "^(Sáng|Chiều|Tối)$";
        return caLam != null && caLam.matches(regex);
    }

    public boolean validGioiTinh(boolean isNu) {
        return true;
    }


    public String taoMaNhanVienTuDong() {
        return nhanVienDAO.taoMaNhanVienTuDong();
    }

    public List<NhanVien> timKiemNhanVien(String ten, String sdt, VaiTroNhanVienEnums vaiTro, Boolean isHoatDong) {
        // Nếu người dùng nhập tên thì ưu tiên lấy tên, ngược lại lấy SĐT
        String tuKhoa = (ten != null && !ten.trim().isEmpty()) ? ten : sdt;

        String vaiTroID = (vaiTro != null) ? vaiTro.name() : null;
        return nhanVienDAO.timKiemNhanVien(tuKhoa, vaiTroID, isHoatDong);
    }

    public VaiTroNhanVienEnums layVaiTroNhanVienTheoMaNV(String maNV) {
        VaiTroNhanVien vt = nhanVienDAO.layVaiTroNhanVienTheoMaNV(maNV);
        return (vt != null) ? VaiTroNhanVienEnums.valueOf(vt.getVaiTroNhanVienID()) : null;
    }

    public List<String> layDanhSachMaNhanVien() {
        return nhanVienDAO.layDanhSachMaNhanVien();
    }


    //======================== TÌM RA THÀNH PHẦF ĐÃ BỊ CHỈNH SỬA ===========================
    private String thanhPhanBiChinhSua(NhanVien cu, NhanVien moi) {
        StringBuilder sb = new StringBuilder();

        String maVaiTroCu = cu.getVaiTroNhanVien() != null ? cu.getVaiTroNhanVien().getVaiTroNhanVienID() : null;
        String maVaiTroMoi = moi.getVaiTroNhanVien() != null ? moi.getVaiTroNhanVien().getVaiTroNhanVienID() : null;

        if (!Objects.equals(maVaiTroCu, maVaiTroMoi)) {
            String tenCu = cu.getVaiTroNhanVien() != null ? cu.getVaiTroNhanVien().getDescription() : "Không có";
            String tenMoi = moi.getVaiTroNhanVien() != null ? moi.getVaiTroNhanVien().getDescription() : "Không có";
            sb.append("Cập nhật vai trò nhân viên: (").append(tenCu).append(" -> ").append(tenMoi).append("), ");
        }

        if (!Objects.equals(cu.getHoTen(), moi.getHoTen()))
            sb.append("Cập nhật họ tên: (").append(cu.getHoTen()).append(" -> ").append(moi.getHoTen()).append("), ");
        if (cu.isNu() != moi.isNu())
            sb.append("Giới tính: (").append(cu.isNu() ? "Nữ" : "Nam").append(" -> ").append(moi.isNu() ? "Nữ" : "Nam").append("), ");
        if (!Objects.equals(cu.getNgaySinh(), moi.getNgaySinh()))
            sb.append("Ngày sinh: [").append(cu.getNgaySinh()).append(" -> ").append(moi.getNgaySinh()).append("], ");
        if (!Objects.equals(cu.getSoDienThoai(), moi.getSoDienThoai()))
            sb.append("Số điện thoại: [").append(cu.getSoDienThoai()).append(" -> ").append(moi.getSoDienThoai()).append("], ");
        if (!Objects.equals(cu.getEmail(), moi.getEmail()))
            sb.append("Email: (").append(cu.getEmail()).append(" -> ").append(moi.getEmail()).append("), ");
        if (!Objects.equals(cu.getDiaChi(), moi.getDiaChi()))
            sb.append("Địa chỉ: (").append(cu.getDiaChi()).append(" -> ").append(moi.getDiaChi()).append("), ");
        if (!Objects.equals(cu.getNgayThamGia(), moi.getNgayThamGia()))
            sb.append("Ngày tham gia: (").append(cu.getNgayThamGia()).append(" -> ").append(moi.getNgayThamGia()).append("), ");
        if (cu.isHoatDong() != moi.isHoatDong())
            sb.append("Trạng thái hoạt động: ").append(cu.isHoatDong() ? "Hoạt động" : "Không hoạt động").append(" -> ").append(moi.isHoatDong() ? "Hoạt động" : "Không hoạt động").append(", ");

        String maCaCu = cu.getCaLam() != null ? cu.getCaLam().getCaLamID() : null;
        String maCaMoi = moi.getCaLam() != null ? moi.getCaLam().getCaLamID() : null;

        if (!Objects.equals(maCaCu, maCaMoi)) {
            String tenCaCu = maCaCu != null ? maCaCu : "Không có";
            String tenCaMoi = maCaMoi != null ? maCaMoi : "Không có";
            sb.append("Ca làm: [").append(tenCaCu).append(" -> ").append(tenCaMoi).append("], ");
        }

        if (!java.util.Arrays.equals(cu.getAvatar(), moi.getAvatar()))
            sb.append("Avatar: [Đã thay đổi], ");

        if (sb.length() > 0) sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    // ================= SỬA NHÂN VIÊN =================

    public boolean suaNhanVien(NhanVien nv, String nguoiThucHienID) {
        if (nv == null) return false;

        // 1. Lấy thông tin nhân viên cũ để làm căn cứ so sánh
        NhanVien nvCu = nhanVienDAO.findById(nv.getNhanVienID());
        if (nvCu == null) return false;

        // 2. Build chi tiết thay đổi TRƯỚC KHI cập nhật vào Database
        String thanhPhan = thanhPhanBiChinhSua(nvCu, nv);

        // 3. Cập nhật nhân viên mới vào Database
        try {
            nhanVienDAO.update(nv);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 4. Ghi nhật ký audit nếu có sự thay đổi
        if (thanhPhan != null && !thanhPhan.isBlank()) {
            ghiAudit(
                    nv.getNhanVienID(),
                    nguoiThucHienID,
                    entity.type.NhatKyAudit.SUA,
                    "Cập nhật nhân viên. " + thanhPhan
            );
        }

        return true;
    }

    // Lấy tất cả ca làm
    public List<CaLam> layTatCaCaLam() {
        return nhanVienDAO.getAllCaLam();
    }

    // Lấy ca làm theo ID
    public CaLam layCaLamTheoTen(String tenCa) {
        return nhanVienDAO.getCaLamById(tenCa);
    }
}