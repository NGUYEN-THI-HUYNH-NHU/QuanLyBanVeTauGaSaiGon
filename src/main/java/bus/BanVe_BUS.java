package bus;
/*
 * @(#) ThanhToan_BUS.java  1.0  [5:12:09 PM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 1, 2025
 * @version: 1.0
 */

import dto.KhachHangDTO;
import entity.*;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.AuthService;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import mapper.DonDatChoMapper;
import mapper.HoaDonMapper;
import mapper.NhanVienMapper;
import mapper.PhieuGiuChoMapper;

import java.time.LocalDateTime;
import java.util.List;

public class BanVe_BUS {
    private final DatCho_BUS datChoBUS = new DatCho_BUS();
    private final Ve_BUS veBUS = new Ve_BUS();
    private final PhieuDungPhongVIP_BUS phieuDungPhongChoVIPBUS = new PhieuDungPhongVIP_BUS();
    private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
    private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();
    private final SuDungKhuyenMai_BUS suDungKhuyenMaiBUS = new SuDungKhuyenMai_BUS();
    private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();
    private final NhatKyAudit_BUS nhatKyAuditBUS = new NhatKyAudit_BUS();
    private final NhanVien nhanVien = NhanVienMapper.INSTANCE.toEntity(AuthService.getInstance().getCurrentUser());

    /**
     * Gói toàn bộ nghiệp vụ thanh toán vào một Transaction CSDL duy nhất.
     *
     * @param session Chứa toàn bộ thông tin (vé, khách hàng, người mua, PGC...)
     * @return true nếu tất cả các bước thành công
     */
    public boolean thucHienBanVe(BookingSession session) throws Exception {
        try {
            KhachHangDTO khachHang = session.getKhachHang();

            // --- BẮT ĐẦU CHUỖI GIAO DỊCH ---
            // 2. Lưu/Cập nhật Khách Hàng (Người mua + các Hành khách)
            khachHangBUS.themHoacCapNhatKhachHang(khachHang);
            for (VeSession v : session.getAllSelectedTickets()) {
                khachHangBUS.themHoacCapNhatKhachHang(khachHang);
            }

            // 3. Tạo và Lưu Đơn Đặt Chỗ
            DonDatCho donDatCho = datChoBUS.taoDonDatCho(khachHang);
            datChoBUS.themDonDatCho(donDatCho);
            session.setDonDatCho(DonDatChoMapper.INSTANCE.toDTO(donDatCho));

            // 4. Tạo và Lưu Hóa đơn
            HoaDon hoaDon = hoaDonBUS.taoHoaDon(session);
            hoaDonBUS.themHoaDon(hoaDon);
            session.setHoaDon(HoaDonMapper.INSTANCE.toDTO(hoaDon));

            // 6. Tạo và Lưu Vé (Batch Insert)
            List<Ve> dsVe = veBUS.taoCacVeVaThemVaoBookingSession(session);
            veBUS.themCacVe(dsVe);

            // 7. Tạo và Lưu Phiếu VIP (Batch Insert)
            List<PhieuDungPhongVIP> dsPhieu = phieuDungPhongChoVIPBUS
                    .taoCacPhieuDungPhongChoVIP(session.getAllSelectedTickets());
            phieuDungPhongChoVIPBUS.themCacPhieuDungPhongChoVIP(dsPhieu);

            // 8. Gán các sử dụng khuyến mãi cho các vé áp dụng khuyến mãi
            khuyenMaiBUS.ganDanhSachSuDungKhuyenMai(session.getAllSelectedTickets());

            // 9. Tạo và Lưu Hóa Đơn Chi Tiết (khi tạo các hóa đơn chi tiết đã bao gồm gán
            // nó cho sử dụng khuyến mãi tương ứng)
            List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonBUS.taoCacHoaDonChiTietBanVe(HoaDonMapper.INSTANCE.toEntity(session.getHoaDon()),
                    session.getAllSelectedTickets());
            hoaDonBUS.themCacHoaDonChiTiet(listHoaDonChiTiet);

            // 10. Lưu các sử dụng khuyến mãi (đã kèm giảm số lượng khuyến mãi tương ứng)
            khuyenMaiBUS.themDanhSachSuDungKhuyenMai(session.getAllSelectedTickets());

            // 11. Cập nhật Phiếu Giữ Chỗ (sau khi mọi thứ thành công)
            PhieuGiuCho phieuGiuCho = PhieuGiuChoMapper.INSTANCE.toEntity(session.getPhieuGiuCho());
            datChoBUS.capNhatPhieuGiuCho(phieuGiuCho, TrangThaiPhieuGiuCho.XAC_NHAN);
            datChoBUS.capNhatCacPhieuGiuChoChiTiet(phieuGiuCho, TrangThaiPhieuGiuCho.XAC_NHAN);

            // Ghi log
            for (VeSession v : session.getAllSelectedTickets()) {
                ghiLog(v.getVe().getVeID(), nhanVien.getNhanVienID(), entity.type.NhatKyAudit.BAN_VE,
                        "Bán vé - " + session.getDonDatCho().getId() + ": " + v.getVe().getVeID());
            }
            return true;

        } catch (Exception e) {
            throw new Exception("Lỗi khi xử lý bán vé: " + e.getMessage());
        }
    }

    // ghi log
    private void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
        String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ? "SYSTEM" : nguoiThucHienID;

        NhatKyAudit audit = new NhatKyAudit(nhatKyAuditBUS.taoMaNhatKyAuditMoi(), doiTuongID, nguoi,
                LocalDateTime.now(), loai, chiTiet, "VE");

        nhatKyAuditBUS.ghiNhatKyAudit(audit);
    }
}
