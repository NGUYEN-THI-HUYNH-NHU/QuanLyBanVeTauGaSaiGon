package controller;

import bus.NhanVien_BUS;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;

import java.time.LocalDate;
import java.util.List;

public class NhanVien_CTRL {
    private final NhanVien_BUS nhanVien_bus;

    public NhanVien_CTRL() {
        nhanVien_bus = new NhanVien_BUS();
    }

    //lấy danh sách nhân viên
    public List<NhanVien> layDanhSachNhanVien() {
        return nhanVien_bus.layDanhSachNhanVien();
    }

    //them nhân viên
    public boolean themNhanVien(entity.NhanVien nv){
        return nhanVien_bus.themNhanVien(nv);
    }

    //sửa nhân viên
    public boolean suaNhanVien(entity.NhanVien nv){
        return nhanVien_bus.suaNhanvVien(nv);
    }
    //regex
    //==1. Ho ten
    public boolean validHoTen(String hoTen){
        return nhanVien_bus.validHoTen(hoTen);
    }
    //==2. So dien thoai
    public boolean validSDT(String sdt){
        return nhanVien_bus.validSDT(sdt);
    }
    //==3. Email
    public boolean validEmail(String email){
        return nhanVien_bus.validEmail(email);
    }
    //==4. Dia chi
    public boolean validDiaChi(String diaChi){
        return nhanVien_bus.validDiaChi(diaChi);
    }
    //==5. Ca lam
    public boolean validCaLam(String caLam){
        return nhanVien_bus.validCaLam(caLam);
    }
    //==6. Ngay sinh
    public boolean ngaySinh(LocalDate ngaySinh){
        return nhanVien_bus.ngaySinh(ngaySinh);
    }
    //==7. Ngay tham gia
    public boolean ngayThamGia(LocalDate ngayThamGia){
        return nhanVien_bus.ngayThamGia(ngayThamGia);
    }
    //==8. Gioi tinh
    public boolean validGioiTinh(boolean isNu){
        return nhanVien_bus.validGioiTinh(isNu);
    }

    //tao ma nhan vien
    public String taoMaNhanVien(){
        return nhanVien_bus.taoMaNhanVienTuDong();
    }

    //tim nhan vien
    public List<NhanVien> timKiemNhanVien(String ten, String sdt, VaiTroNhanVien vaiTro, Boolean isHoatDong) {
        return nhanVien_bus.timKiemNhanVien(ten, sdt, vaiTro, isHoatDong);
    }


}
