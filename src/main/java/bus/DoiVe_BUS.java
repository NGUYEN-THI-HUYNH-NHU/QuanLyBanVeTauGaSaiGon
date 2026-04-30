package bus;
/*
 * @(#) DoiVe_BUS.java  1.0  [8:17:38 PM] Nov 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 21, 2025
 * @version: 1.0
 */

import connectDB.ConnectDB;
import dto.KhachHangDTO;
import dto.VeDTO;
import entity.*;
import entity.type.TrangThaiPDPVIP;
import entity.type.TrangThaiPhieuGiuCho;
import entity.type.TrangThaiVe;
import gui.application.AuthService;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import gui.application.form.doiVe.VeDoiRow;
import mapper.DonDatChoMapper;
import mapper.PhieuGiuChoMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DoiVe_BUS {
    private final DatCho_BUS datChoBUS = new DatCho_BUS();
    private final Ve_BUS veBUS = new Ve_BUS();
    private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
    private final PhieuDungPhongVIP_BUS phieuDungPhongVIPBUS = new PhieuDungPhongVIP_BUS();
    private final GiaoDichHoanDoi_BUS giaoDichHoanDoiBUS = new GiaoDichHoanDoi_BUS();
    private final PhieuGiuCho_BUS phieuGiuChoBUS = new PhieuGiuCho_BUS();
    private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();
    private final NhatKyAudit_BUS nhatKyAuditBUS = new NhatKyAudit_BUS();

    /**
     * @param exchangeSession
     * @return
     */
    public boolean thucHienDoiVe(ExchangeSession exchangeSession) throws Exception {
        Connection conn = null;
        try {
            // 1. Lấy kết nối và BẮT ĐẦU TRANSACTION
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false);

            // --- BẮT ĐẦU CHUỖI GIAO DỊCH ---
            List<VeDTO> listVeDoi = new ArrayList<>();
            for (VeDoiRow r : exchangeSession.getListVeCuCanDoi()) {
                listVeDoi.add(r.getVe());
            }
            KhachHangDTO khachHang = exchangeSession.getKhachHang();
            NhanVien nhanVien = exchangeSession.getNhanVien();

            // 1. Tạo và Lưu Đơn Đặt Chỗ cho các vé mới
            DonDatCho donDatChoMoi = datChoBUS.taoDonDatCho(nhanVien, khachHang);
            datChoBUS.themDonDatCho(donDatChoMoi);
            exchangeSession.setDonDatChoMoi(DonDatChoMapper.INSTANCE.toDTO(donDatChoMoi));

            // 2. Tạo và Lưu Hóa đơn đổi vé
            HoaDon hoaDon = hoaDonBUS.taoHoaDonDoiVe(exchangeSession);
            hoaDonBUS.themHoaDon(hoaDon);
            exchangeSession.setHoaDon(hoaDon);

            // 4. Tạo và Lưu Vé mới (Batch Insert)
            List<Ve> dsVe = veBUS.taoCacVeVaThemVaoExchangeSession(exchangeSession);
            veBUS.themCacVe(dsVe);

            // 5. Tạo và Lưu Phiếu VIP (Batch Insert)
            List<PhieuDungPhongVIP> dsPhieu = phieuDungPhongVIPBUS
                    .taoCacPhieuDungPhongChoVIP(exchangeSession.getListVeMoiDangChon());
            phieuDungPhongVIPBUS.themCacPhieuDungPhongChoVIP(dsPhieu);

            // 6. Gán các sử dụng khuyến mãi cho các vé áp dụng khuyến mãi
            khuyenMaiBUS.ganDanhSachSuDungKhuyenMai(exchangeSession.getListVeMoiDangChon());

            // 7. Tạo và Lưu Hóa Đơn Chi Tiết (Batch Insert)
            List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonBUS.taoCacHoaDonChiTietDoiVe(hoaDon, exchangeSession);
            hoaDonBUS.themCacHoaDonChiTiet(listHoaDonChiTiet);

            // 8. Lưu các sử dụng khuyến mãi cho vé mới (đã kèm giảm số lượng khuyến mãi
            // tương ứng)
            khuyenMaiBUS.themDanhSachSuDungKhuyenMai(conn, exchangeSession.getListVeMoiDangChon());

            // 9. Cập nhật Phiếu Giữ Chỗ cho vé mới (sau khi mọi thứ thành công)
            PhieuGiuCho phieuGiuCho = PhieuGiuChoMapper.INSTANCE.toEntity(exchangeSession.getPhieuGiuCho());
//            PhieuGiuCho phieuGiuCho = Mapper.map(exchangeSession.getPhieuGiuCho());
            datChoBUS.capNhatPhieuGiuCho(phieuGiuCho, TrangThaiPhieuGiuCho.XAC_NHAN);
            datChoBUS.capNhatCacPhieuGiuChoChiTiet(phieuGiuCho, TrangThaiPhieuGiuCho.XAC_NHAN);

            // 10. Cập nhật trạng thái các vé cũ (và phiếu dùng phòng chờ VIP nếu có)
            veBUS.capNhatTrangThaiVe(listVeDoi, TrangThaiVe.DA_DOI);
            phieuDungPhongVIPBUS.capNhatPhieuDungPhongChoVIP(listVeDoi, TrangThaiPDPVIP.DA_HUY);

            // 11. Tạo và lưu các Giao dịch đổi vé (giao dịch hoàn đổi)
            List<GiaoDichHoanDoi> dsGdhd = giaoDichHoanDoiBUS.taoCacGiaoDichDoiVe(exchangeSession);
            giaoDichHoanDoiBUS.themCacGiaoDichHoanDoi(dsGdhd);

            // 12. Set trạng thái các phiếu giữ chỗ thành HET_GIU
            phieuGiuChoBUS.huyCacPhieuGiuChoChiTiet(listVeDoi, TrangThaiPhieuGiuCho.HET_GIU);

            // 13. Cập nhật các khuyến mãi đã sử dụng ở các vé cũ
            Map<String, Integer> mapKhuyenMaiHoan = khuyenMaiBUS.layDanhSachKhuyenMaiCanHoan(conn, listVeDoi);
            khuyenMaiBUS.capNhatTrangThaiSDKMCuaVe(conn, listVeDoi);
            for (Map.Entry<String, Integer> entry : mapKhuyenMaiHoan.entrySet()) {
                String kmID = entry.getKey();
                int soLuongCanCong = entry.getValue();
                khuyenMaiBUS.congSoLuongKhuyenMai(conn, kmID, soLuongCanCong);
            }

            // Ghi log
            String nvID = AuthService.getInstance().getCurrentUser().getNhanVienID();
            for (VeDoiRow v : exchangeSession.getListVeCuCanDoi()) {
                ghiLog(v.getVe().getVeID(), nvID, entity.type.NhatKyAudit.DOI_VE, "Hủy vé: " + v.getVe().getVeID());
            }

            for (VeSession v : exchangeSession.getListVeMoiDangChon()) {
                ghiLog(v.getVe().getVeID(), nvID, entity.type.NhatKyAudit.DOI_VE, "Vé đổi: " + v.getVe().getVeID());
            }

            // --- KẾT THÚC GIAO DỊCH ---
            // Hoàn tất giao dịch
            conn.commit();
            return true;

        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào, hoàn tác tất cả thay đổi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new Exception("Lỗi khi xử lý đổi vé: " + e.getMessage());
        } finally {
            // Trả lại trạng thái AutoCommit cho kết nối
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    // ghi log
    private void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
        if (nhatKyAuditBUS == null) {
            return;
        }

        String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ? "SYSTEM" : nguoiThucHienID;

        NhatKyAudit audit = new NhatKyAudit(nhatKyAuditBUS.taoMaNhatKyAuditMoi(), doiTuongID, nguoi,
                LocalDateTime.now(), loai, chiTiet, "VE");

        nhatKyAuditBUS.ghiNhatKyAudit(audit);
    }
}
