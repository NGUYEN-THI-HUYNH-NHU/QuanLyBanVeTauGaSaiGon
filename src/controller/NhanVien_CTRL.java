package controller;

import bus.NhanVien_BUS;
import bus.NhatKyAudit_BUS;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;

import java.time.LocalDate;
import java.util.List;

public class NhanVien_CTRL {
    private final NhanVien_BUS nhanVien_bus;
    private final NhanVien nhanVienHienTai;

    public NhanVien_CTRL(NhanVien nhanVienHienTai) {
        this.nhanVienHienTai = nhanVienHienTai;

        NhatKyAudit_BUS auditBus = new NhatKyAudit_BUS();
        this.nhanVien_bus = new NhanVien_BUS(auditBus);
    }

//    public NhanVien_CTRL() {
//        this(null);
//    }

    public List<NhanVien> layDanhSachNhanVien() {
        return nhanVien_bus.layDanhSachNhanVien();
    }

    public NhanVien layNhanVienBangMaNV(String maNV) {
        return nhanVien_bus.layNhanVienBangMaNV(maNV);
    }

    public boolean themNhanVien(NhanVien nv) {
        String nguoiThucHienID =
                (nhanVienHienTai != null && nhanVienHienTai.getNhanVienID() != null) ? nhanVienHienTai.getNhanVienID() : null;
        return nhanVien_bus.themNhanVien(nv, nguoiThucHienID);
    }

    public boolean suaNhanVien(NhanVien nv) {
        return nhanVien_bus.suaNhanvVien(nv);
    }

    public boolean validHoTen(String hoTen) { return nhanVien_bus.validHoTen(hoTen); }
    public boolean validSDT(String sdt) { return nhanVien_bus.validSDT(sdt); }
    public boolean validEmail(String email) { return nhanVien_bus.validEmail(email); }
    public boolean validDiaChi(String diaChi) { return nhanVien_bus.validDiaChi(diaChi); }
    public boolean validCaLam(String caLam) { return nhanVien_bus.validCaLam(caLam); }
    public boolean ngaySinh(LocalDate ngaySinh) { return nhanVien_bus.ngaySinh(ngaySinh); }
    public boolean ngayThamGia(LocalDate ngayThamGia) { return nhanVien_bus.ngayThamGia(ngayThamGia); }
    public boolean validGioiTinh(boolean isNu) { return nhanVien_bus.validGioiTinh(isNu); }

    public String taoMaNhanVien() {
        return nhanVien_bus.taoMaNhanVienTuDong();
    }

    public List<NhanVien> timKiemNhanVien(String ten, String sdt, VaiTroNhanVien vaiTro, Boolean isHoatDong) {
        return nhanVien_bus.timKiemNhanVien(ten, sdt, vaiTro, isHoatDong);
    }

    public VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV) {
        return nhanVien_bus.layVaiTroNhanVienTheoMaNV(maNV);
    }

//    public List<String> layDanhSachMaNhanVien() {
//        return nhanVien_bus.layDanhSachMaNhanVien();
//    }
}
