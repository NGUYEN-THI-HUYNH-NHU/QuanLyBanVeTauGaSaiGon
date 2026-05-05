package bus;

import dao.IKhachHangDAO;
import dao.impl.KhachHangDAO;
import dto.KhachHangDTO;
import entity.KhachHang;
import entity.NhanVien;
import entity.NhatKyAudit;
import gui.application.AuthService;
import mapper.KhachHangMapper;
import mapper.NhanVienMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class KhachHang_BUS {
    private final IKhachHangDAO khachHangDAO;
    private final NhanVien nhanVienHienTai;
    private final NhatKyAudit_BUS nhatKyAudit_bus;

    public KhachHang_BUS() {
        this.khachHangDAO = new KhachHangDAO();
        this.nhanVienHienTai = NhanVienMapper.INSTANCE.toEntity(AuthService.getInstance().getCurrentUser());
        this.nhatKyAudit_bus = new NhatKyAudit_BUS();
    }

    // ================= LẤY DỮ LIỆU =================

    public List<KhachHang> getAllKhachHang() {
        // SỬA TẠI ĐÂY: Gọi đúng hàm đã được Override có LEFT JOIN FETCH ở DAO
        return khachHangDAO.getAllKhachHang();
    }

    public KhachHang timKiemKhachHangTheoSDT(String sdt) {
        return khachHangDAO.timKhachHangTheoSDT(sdt);
    }

    public KhachHangDTO timKiemKhachHangTheoSoGiayTo(String soGiayTo) {
        KhachHang kh = khachHangDAO.timKhachHangTheoSoGiayTo(soGiayTo);
        return kh != null ? KhachHangMapper.INSTANCE.toDTO(kh) : null;
    }

    public List<KhachHangDTO> layGoiYKhachHang(String keyword) {
        return khachHangDAO.getTop10KhachHangSuggest(keyword).stream()
                .map(KhachHangMapper.INSTANCE::toDTO)
                .toList();
    }

    // ================= THÊM KHÁCH HÀNG =================

    public boolean themKhachHang(KhachHangDTO kh) {
        if (kh == null) return false;
        KhachHang khachHang = KhachHangMapper.INSTANCE.toEntity(kh);
        try {
            khachHangDAO.create(khachHang);
            ghiLog(kh.getId(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
                    entity.type.NhatKyAudit.THEM, "Thêm khách hàng: " + kh.getHoTen() + " - " + kh.getSoDienThoai());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean themHoacCapNhatKhachHang(KhachHangDTO khDTO) {
        KhachHang kh = khachHangDAO.findById(khDTO.getId());
        if (kh == null) return false;
        return khachHangDAO.update(KhachHangMapper.INSTANCE.toEntity(khDTO)) != null;
    }

    // ================= CẬP NHẬT KHÁCH HÀNG =================

    public boolean capNhatKhachHang(KhachHangDTO kh) {
        if (kh == null) return false;
        KhachHang khachHang = KhachHangMapper.INSTANCE.toEntity(kh);

        KhachHang khachHangCu = khachHangDAO.findById(kh.getId());
        if (khachHangCu == null) {
            return false;
        }

        // 1. Build chi tiết thay đổi TRƯỚC khi cập nhật
        String thanhPhan = thanhPhanDaBiSua(khachHangCu, khachHang);

        // 2. Cập nhật khách hàng
        try {
            khachHangDAO.update(khachHang);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // 3. Ghi log nếu có thay đổi
        if (thanhPhan != null && !thanhPhan.isBlank()) {
            ghiLog(kh.getId(), nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : null,
                    entity.type.NhatKyAudit.SUA, "Cập nhật khách hàng: " + thanhPhan);
        }

        return true;
    }

    // ================= LOGGING LOGIC =================

    public void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
        if (nhatKyAudit_bus == null) return;

        String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ? "SYSTEM" : nguoiThucHienID;

        NhatKyAudit audit = new NhatKyAudit(
                nhatKyAudit_bus.taoMaNhatKyAuditMoi(),
                doiTuongID,
                nguoi,
                LocalDateTime.now(),
                loai,
                chiTiet,
                "KHACH_HANG"
        );

        nhatKyAudit_bus.ghiNhatKyAudit(audit);
    }

    // ================= VALIDATION LOGIC =================

    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    public boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^(0[35789][0-9]{8})$";
        return phoneNumber != null && phoneNumber.matches(phoneRegex);
    }

    public boolean isValidTen(String ten) {
        String tenRegex = "^([A-ZÀ-ỸĐ][a-zà-ỹđ]*)(\\s[A-ZÀ-ỸĐ][a-zà-ỹđ]*)*$";
        return ten != null && ten.matches(tenRegex);
    }

    public boolean isValidDiaChi(String diaChi) {
        String diaChiRegex = "^[\\p{L}0-9\\s,./-]+$";
        return diaChi != null && diaChi.matches(diaChiRegex);
    }

    // ================= KIỂM TRA TRÙNG DỮ LIỆU TỐI ƯU =================

    public boolean kiemTraTrungSDT(String sdt) {
        if (sdt == null || sdt.isBlank()) return false;
        return khachHangDAO.timKhachHangTheoSDT(sdt.trim()) != null;
    }

    public boolean kiemTraTrungSoGiayTo(String soGiayTo) {
        if (soGiayTo == null || soGiayTo.isBlank()) return false;
        return khachHangDAO.timKhachHangTheoSoGiayTo(soGiayTo.trim()) != null;
    }

    // ================= TẠO MÃ TỰ ĐỘNG TỐI ƯU =================

    public String taoMaKhachHangTuDong() {
        return khachHangDAO.taoMaKhachHangTuDong();
    }

    // ================= LẤY THÀNH PHẦN BỊ THAY ĐỔI =================

    public String thanhPhanDaBiSua(KhachHang cu, KhachHang moi) {
        StringBuilder thayDoi = new StringBuilder();

        if (!Objects.equals(cu.getHoTen(), moi.getHoTen())) {
            thayDoi.append("Cập nhật tên khách hàng: (").append(cu.getHoTen()).append(") -> (").append(moi.getHoTen()).append(")\n");
        }
        if (!Objects.equals(cu.getSoDienThoai(), moi.getSoDienThoai())) {
            thayDoi.append("Cập nhật số điện thoại: (").append(cu.getSoDienThoai()).append(") -> (").append(moi.getSoDienThoai()).append(")\n");
        }
        if (!Objects.equals(cu.getEmail(), moi.getEmail())) {
            thayDoi.append("Cập nhật email: (").append(cu.getEmail()).append(") -> (").append(moi.getEmail()).append(")\n");
        }
        if (!Objects.equals(cu.getDiaChi(), moi.getDiaChi())) {
            thayDoi.append("Cập nhật địa chỉ: (").append(cu.getDiaChi()).append(") -> (").append(moi.getDiaChi()).append(")\n");
        }
        if (!Objects.equals(cu.getSoGiayTo(), moi.getSoGiayTo())) {
            thayDoi.append("Cập nhật số giấy tờ: (").append(cu.getSoGiayTo()).append(") -> (").append(moi.getSoGiayTo()).append(")\n");
        }

        String maLKHCu = cu.getLoaiKhachHang() != null ? cu.getLoaiKhachHang().getLoaiKhachHangID() : null;
        String maLKHMoi = moi.getLoaiKhachHang() != null ? moi.getLoaiKhachHang().getLoaiKhachHangID() : null;
        if (!Objects.equals(maLKHCu, maLKHMoi)) {
            String descCu = cu.getLoaiKhachHang() != null ? cu.getLoaiKhachHang().getDescription() : "Không có";
            String descMoi = moi.getLoaiKhachHang() != null ? moi.getLoaiKhachHang().getDescription() : "Không có";
            thayDoi.append("Cập nhật loại khách hàng: (").append(descCu).append(") -> (").append(descMoi).append(")\n");
        }

        String maLDTCu = cu.getLoaiDoiTuong() != null ? cu.getLoaiDoiTuong().getLoaiDoiTuongID() : null;
        String maLDTMoi = moi.getLoaiDoiTuong() != null ? moi.getLoaiDoiTuong().getLoaiDoiTuongID() : null;
        if (!Objects.equals(maLDTCu, maLDTMoi)) {
            String descCu = cu.getLoaiDoiTuong() != null ? cu.getLoaiDoiTuong().getDescription() : "Không có";
            String descMoi = moi.getLoaiDoiTuong() != null ? moi.getLoaiDoiTuong().getDescription() : "Không có";
            thayDoi.append("Cập nhật loại đối tượng: (").append(descCu).append(") -> (").append(descMoi).append(")\n");
        }

        return thayDoi.toString();
    }

    public List<String> layTop10SoGiayTo(String soGiayTo) {
        return khachHangDAO.getTop10SoGiayTo(soGiayTo);
    }

    public List<String> layTop10SoDienThoai(String soDienThoai) {
        return khachHangDAO.getTop10SoDienThoai(soDienThoai);
    }

    public List<String> layTop10HoTen(String hoTen) {
        return khachHangDAO.getTop10HoTen(hoTen);
    }

    // PHÂN TRANG
    public List<KhachHang> getKhachHangPhanTrang(int page, int pageSize) {
        return khachHangDAO.getKhachHangPhanTrang(page, pageSize);
    }

    public long getTotalKhachHang() {
        return khachHangDAO.getTotalKhachHang();
    }
}