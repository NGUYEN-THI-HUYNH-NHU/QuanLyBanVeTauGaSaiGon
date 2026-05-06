package bus;
/*
 * @(#) HoanVe_BUS.java  1.0  [11:03:30 AM] Nov 16, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 16, 2025
 * @version: 1.0
 */

import dto.DonDatChoDTO;
import dto.KhachHangDTO;
import dto.VeDTO;
import entity.*;
import entity.type.TrangThaiPDPVIP;
import entity.type.TrangThaiPhieuGiuCho;
import entity.type.TrangThaiVe;
import gui.application.AuthService;
import gui.application.form.hoanVe.VeHoanRow;
import mapper.NhanVienMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoanVe_BUS {
    private final Ve_BUS veBUS = new Ve_BUS();
    private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
    private final PhieuDungPhongVIP_BUS phieuDungPhongVIPBUS = new PhieuDungPhongVIP_BUS();
    private final GiaoDichHoanDoi_BUS giaoDichHoanDoiBUS = new GiaoDichHoanDoi_BUS();
    private final PhieuGiuCho_BUS phieuGiuChoChiTietBUS = new PhieuGiuCho_BUS();
    private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();
    private final NhatKyAudit_BUS nhatKyAuditBUS = new NhatKyAudit_BUS();
    private final NhanVien nhanVien = NhanVienMapper.INSTANCE.toEntity(AuthService.getInstance().getCurrentUser());

    /**
     * @param donDatCho
     * @param khachHang
     * @param listVeHoanRow
     * @param tongTienHoan
     * @return
     */
    public boolean thucHienHoanVe(DonDatChoDTO donDatCho, KhachHangDTO khachHang, List<VeHoanRow> listVeHoanRow,
                                  double tongTienHoan) throws Exception {
        try {

            // --- BẮT ĐẦU CHUỖI GIAO DỊCH ---
            List<VeDTO> listVe = new ArrayList<>();
            for (VeHoanRow r : listVeHoanRow) {
                listVe.add(r.getVe());
            }

            // 1. Cập nhật trạng thái vé (và phiếu dùng phòng chờ VIP nếu có)
            veBUS.capNhatTrangThaiVe(listVe, TrangThaiVe.DA_HOAN);
            phieuDungPhongVIPBUS.capNhatPhieuDungPhongChoVIP(listVe, TrangThaiPDPVIP.DA_HUY);

            // 2. Tạo hóa đơn
            HoaDon hoaDon = hoaDonBUS.taoHoaDonHoanVe(donDatCho, khachHang, nhanVien, tongTienHoan);
            hoaDonBUS.themHoaDon(hoaDon);

            // 3. Tạo và Lưu Hóa Đơn Chi Tiết
            List<HoaDonChiTiet> dsHDCT = hoaDonBUS.taoCacHoaDonChiTietHoanVe(hoaDon, listVeHoanRow);
            hoaDonBUS.themCacHoaDonChiTiet(dsHDCT);

            // 4. Tạo và lưu Giao dịch hoàn đổi
            List<GiaoDichHoanDoi> dsGdhd = giaoDichHoanDoiBUS.taoCacGiaoDichHoanVe(hoaDon, nhanVien, listVeHoanRow);
            giaoDichHoanDoiBUS.themCacGiaoDichHoanDoi(dsGdhd);

            // 5. Set trạng thái các phiếu giữ chỗ thành HET_GIU
            phieuGiuChoChiTietBUS.huyCacPhieuGiuChoChiTiet(listVe, TrangThaiPhieuGiuCho.HET_GIU);

//            =============================================================================== MO RA
            // 6. Cập nhật các khuyến mãi đã sử dụng
//            Map<String, Integer> mapKhuyenMaiHoan = khuyenMaiBUS.layDanhSachKhuyenMaiCanHoan(conn, listVe);
//            khuyenMaiBUS.capNhatTrangThaiSDKMCuaVe(conn, listVe);
//            for (Map.Entry<String, Integer> entry : mapKhuyenMaiHoan.entrySet()) {
//                String kmID = entry.getKey();
//                int soLuongCanCong = entry.getValue();
//                khuyenMaiBUS.congSoLuongKhuyenMai(conn, kmID, soLuongCanCong);
//            }

            // Ghi log
            for (VeDTO v : listVe) {
                ghiLog(v.getVeID(), nhanVien.getNhanVienID(), entity.type.NhatKyAudit.HOAN_VE,
                        "Hoàn vé - " + v.getDonDatChoID() + ": " + v.getVeID());
            }

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return true;
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
