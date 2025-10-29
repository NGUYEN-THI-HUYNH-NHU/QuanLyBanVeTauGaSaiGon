package bus;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;

import java.time.LocalDate;
import java.util.List;

public class NhanVien_BUS {
    private final NhanVien_DAO nhanVien_dao;


    public NhanVien_BUS() {
        nhanVien_dao = new NhanVien_DAO();
    }

    //lấy danh sách nhân viên
    public List<NhanVien> layDanhSachNhanVien() {
         return nhanVien_dao.getAllNhanVien();
    }

    //thêm nhân viên
    public boolean themNhanVien(NhanVien nv){
        return nhanVien_dao.themNhanVien(nv);
    }
    //sửa nhân viên
    public boolean suaNhanvVien(NhanVien nv){
        return nhanVien_dao.capNhatNhanVien(nv);
    }
    //regex
    //==1. Ho ten
    public boolean validHoTen(String hoTen){
        String regex = "^([A-ZÀ-ỸĐ][a-zà-ỹđ]*)(\\s[A-ZÀ-ỸĐ][a-zà-ỹđ]*)*$";
        return hoTen.matches(regex);
    }

    //==2. So dien thoai
    public boolean validSDT(String sdt){
        String regex = "^(0[35789][0-9]{8})$";
        return sdt.matches(regex);
    }

    //3. Email
    public boolean validEmail(String email){
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

    //4. Dia chi
    public boolean validDiaChi(String diaChi){
        String regex = "^[\\wÀ-ỹ0-9\\s,.-]{1,100}$";
        return diaChi.matches(regex);
    }

    //5. Ngay thang nam
    public boolean ngaySinh(LocalDate ns){
        return ns.isBefore(LocalDate.now());
    }

    //6. Ngay tham gia
    public boolean ngayThamGia(LocalDate ntg){
        return !ntg.isAfter(LocalDate.now());
    }

    //7. Ca lam
    public boolean validCaLam(String caLam){
        String regex = "^(Sáng|Chiều|Tối)$";
        return caLam.matches(regex);
    }

    //8. Giới tính
    public boolean validGioiTinh(boolean isNu){
        return isNu == true || isNu == false;
    }


    //Tao ma nhan vien tu dong
    public String taoMaNhanVienTuDong(){
        return nhanVien_dao.taoMaNhanVienTuDong();
    }

    //tim kiem nhan vien
    public List<NhanVien> timKiemNhanVien(String ten, String sdt, VaiTroNhanVien vaiTro, Boolean isHoatDong) {
        return nhanVien_dao.timKiemNhanVien(ten, sdt, vaiTro, isHoatDong);
    }


}
