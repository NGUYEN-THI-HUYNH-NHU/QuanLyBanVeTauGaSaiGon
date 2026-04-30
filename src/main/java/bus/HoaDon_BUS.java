package bus;

/*
 * @(#) HoaDon_BUS.java  1.0  [1:06:29 PM] Nov 2, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 2, 2025
 * @version: 1.0
 */

import dao.impl.HoaDonChiTietDAO;
import dao.impl.HoaDonDAO;
import dao.impl.PhieuDungPhongVIPDAO;
import dto.DonDatChoDTO;
import dto.KhachHangDTO;
import dto.VeDTO;
import entity.*;
import entity.type.LoaiDichVuEnums;
import entity.type.LoaiDoiTuongEnums;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import gui.application.form.doiVe.VeDoiRow;
import gui.application.form.hoanVe.VeHoanRow;
import mapper.KhachHangMapper;
import mapper.VeMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDon_BUS {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final HoaDonChiTietDAO hoaDonChiTietDAO = new HoaDonChiTietDAO();
    private final PhieuDungPhongVIPDAO phieuDungPhongVIPDAO = new PhieuDungPhongVIPDAO();

    /**
     * @param bookingSession
     * @return
     */
    public HoaDon taoHoaDon(BookingSession bookingSession) {
        String hdID = "HD-" + bookingSession.getDonDatCho().getDonDatChoID().substring(4);
        LocalDateTime now = LocalDateTime.now();
        KhachHang khachHang = KhachHangMapper.INSTANCE.toEntity(bookingSession.getKhachHang());
        HoaDon hoaDon = new HoaDon(hdID, khachHang, bookingSession.getNhanVien(), now,
                bookingSession.getGiaoDichThanhToan().getTongTien(), bookingSession.getGiaoDichThanhToan().getMaGD(),
                bookingSession.getGiaoDichThanhToan().getTienNhan(),
                bookingSession.getGiaoDichThanhToan().getTienHoan(),
                bookingSession.getGiaoDichThanhToan().isThanhToanTienMat());

        return hoaDon;
    }

    /**
     * @param exchangeSession
     * @return
     */
    public HoaDon taoHoaDonDoiVe(ExchangeSession exchangeSession) {
        String hdID = "HDDV-" + exchangeSession.getDonDatChoMoi().getId().substring(4);
        LocalDateTime now = LocalDateTime.now();
        KhachHang khachHang = KhachHangMapper.INSTANCE.toEntity(exchangeSession.getKhachHang());
        HoaDon hoaDon = new HoaDon(hdID, khachHang, exchangeSession.getNhanVien(), now,
                exchangeSession.getGiaoDichThanhToan().getTongTien(), exchangeSession.getGiaoDichThanhToan().getMaGD(),
                exchangeSession.getGiaoDichThanhToan().getTienNhan(),
                exchangeSession.getGiaoDichThanhToan().getTienHoan(),
                exchangeSession.getGiaoDichThanhToan().isThanhToanTienMat());

        return hoaDon;
    }

    /**
     * @param donDatCho
     * @param khachHang
     * @param nhanVien
     * @param tongTienHoan
     * @return
     */
    public HoaDon taoHoaDonHoanVe(DonDatChoDTO donDatCho, KhachHangDTO khachHang, NhanVien nhanVien, double tongTienHoan) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setHoaDonID("HDHV-" + donDatCho.getId().substring(4));
        hoaDon.setKhachHang(KhachHangMapper.INSTANCE.toEntity(khachHang));
        hoaDon.setNhanVien(nhanVien);
        hoaDon.setThoiDiemTao(LocalDateTime.now());
        hoaDon.setTongTien(-tongTienHoan);
        hoaDon.setMaGD(null);
        hoaDon.setTienNhan(0);
        hoaDon.setTienHoan(tongTienHoan);
        hoaDon.setThanhToanTienMat(true);

        return hoaDon;
    }

    /**
     * @param hoaDon
     * @param listVeMoi
     * @return
     */
    public List<HoaDonChiTiet> taoCacHoaDonChiTietBanVe(HoaDon hoaDon, List<VeSession> listVeMoi) {
        List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
        int stt = 0;
        for (VeSession ve : listVeMoi) {
            String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
            VeDTO veDTO = ve.getVe();
            HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, hoaDon, new Ve(veDTO.getVeID()), "Vé HK: " + ve,
                    LoaiDichVuEnums.VE_BAN, "Vé", 1, veDTO.getGia(), veDTO.getGia());
            dsHoaDonChiTiet.add(hdctVe);

            if (ve.getPhieuDungPhongVIP() != null) {
                String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, ve.getPhieuDungPhongVIP(),
                        "Phiếu dùng phòng chờ VIP Ga Sài Gòn", LoaiDichVuEnums.PHONG_VIP, "Phiếu", 1,
                        ve.getPhiPhieuDungPhongChoVIP(), ve.getPhiPhieuDungPhongChoVIP());
                dsHoaDonChiTiet.add(hdctPhieu);
            }

            if (veDTO.getKhachHangDTO().getLoaiDoiTuongID().equals(LoaiDoiTuongEnums.TRE_EM.toString())) {
                String hdctGiamDTID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctGiamDT = new HoaDonChiTiet(hdctGiamDTID, hoaDon, new Ve(veDTO.getVeID()),
                        "Giảm giá đối tượng trẻ em", LoaiDichVuEnums.KHUYEN_MAI, 1, -ve.getGiamDoiTuong(),
                        -ve.getGiamDoiTuong());
                dsHoaDonChiTiet.add(hdctGiamDT);

            }

            if (ve.getKhuyenMaiApDung() != null && ve.getKhuyenMaiApDung().getKhuyenMaiID() != null) {
                String hdctKMID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctKM = new HoaDonChiTiet(hdctKMID, hoaDon, new Ve(veDTO.getVeID()),
                        ve.getKhuyenMaiApDung().getMaKhuyenMai() + ": " + ve.getKhuyenMaiApDung().getMoTa(),
                        LoaiDichVuEnums.KHUYEN_MAI, 1, -ve.getGiamKM(), -ve.getGiamKM());
                ve.getSuDungKhuyenMai().setHoaDonChiTiet(hdctKM);
                dsHoaDonChiTiet.add(hdctKM);

            }
        }
        return dsHoaDonChiTiet;
    }

    public List<HoaDonChiTiet> taoCacHoaDonChiTietHoanVe(HoaDon hoaDon,
                                                         List<VeHoanRow> listVeHoanRow) {
        List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
        int stt = 0;

        for (VeHoanRow row : listVeHoanRow) {
            String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
            Ve ve = VeMapper.INSTANCE.toEntity(row.getVe());
            HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, hoaDon, ve, "Điều chỉnh giảm theo BB trả vé số: 2177975",
                    LoaiDichVuEnums.VE_HOAN, "Vé", 1, -ve.getGia(), -ve.getGia());
            dsHoaDonChiTiet.add(hdctVe);

            PhieuDungPhongVIP phieu = phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(ve.getVeID());
            if (phieu != null) {
                String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, phieu, "Hủy phiếu dùng phòng chờ VIP theo vé hoàn",
                        LoaiDichVuEnums.PHIEU_HUY, "Phiếu", 1, 0, 0);
                dsHoaDonChiTiet.add(hdctPhieu);
            }

            String hdctLePhiID = hoaDon.getHoaDonID() + "-" + (++stt);
            HoaDonChiTiet hdctLePhi = new HoaDonChiTiet(hdctLePhiID, hoaDon, ve, "Lệ phí hoàn vé",
                    LoaiDichVuEnums.PHI_HOAN, 1, row.getLePhiHoanVe(), row.getLePhiHoanVe());
            dsHoaDonChiTiet.add(hdctLePhi);
        }

        return dsHoaDonChiTiet;
    }

    public List<HoaDonChiTiet> taoCacHoaDonChiTietDoiVe(HoaDon hoaDon,
                                                        ExchangeSession exchangeSession) {
        List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
        List<VeDoiRow> listVeDoi = exchangeSession.getListVeCuCanDoi();
        List<VeSession> listVeMoi = exchangeSession.getListVeMoiDangChon();
        int soLuongVe = listVeDoi.size();
        int stt = 0;
        for (int i = 0; i < soLuongVe; i++) {
            // Dòng vé đổi
            String hdctVeDoiID = hoaDon.getHoaDonID() + "-" + (++stt);
            Ve ve = VeMapper.INSTANCE.toEntity(listVeDoi.get(i).getVe());
            HoaDonChiTiet hdctVeDoi = new HoaDonChiTiet(hdctVeDoiID, hoaDon, ve,
                    "Điều chỉnh giảm theo BB trả vé số: 2177975", LoaiDichVuEnums.VE_DOI, "Vé", 1,
                    -ve.getGia(), -ve.getGia());
            dsHoaDonChiTiet.add(hdctVeDoi);

            // Dòng phiếu đổi (hủy)
            PhieuDungPhongVIP phieuDoi = listVeDoi.get(i).getPhieuDungPhongVIP();
            if (phieuDoi != null) {
                String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, phieuDoi,
                        "Hủy phiếu dùng phòng chờ VIP theo vé đổi", LoaiDichVuEnums.PHONG_VIP, "Phiếu", 1, 0, 0);
                dsHoaDonChiTiet.add(hdctPhieu);
            }

            // Dòng vé mới
            String hdctVeMoiID = hoaDon.getHoaDonID() + "-" + (++stt);
            VeDTO veMoiDTO = listVeMoi.get(i).getVe();
            HoaDonChiTiet hdctVeMoi = new HoaDonChiTiet(hdctVeMoiID, hoaDon, new Ve(veMoiDTO.getVeID()),
                    "Vé HK: " + listVeMoi.get(i).toString(), LoaiDichVuEnums.VE_BAN, "Vé", 1,
                    listVeMoi.get(i).getVe().getGia(), listVeMoi.get(i).getVe().getGia());
            dsHoaDonChiTiet.add(hdctVeMoi);

            // Dòng phiếu mới (nếu có)
            PhieuDungPhongVIP phieuMoi = listVeMoi.get(i).getPhieuDungPhongVIP();
            if (phieuMoi != null) {
                if (phieuDungPhongVIPDAO.getPhieuDungPhongVIPByID(phieuMoi.getPhieuDungPhongVIPID()) == null) {
                    String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                    HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, phieuMoi,
                            "Phiếu dùng phòng chờ VIP Ga Sài Gòn", LoaiDichVuEnums.PHONG_VIP, "Phiếu", 1, 20000, 20000);
                    dsHoaDonChiTiet.add(hdctPhieu);
                }
            }

            // Dòng giảm giá đối tượng trẻ em
            if (veMoiDTO.getKhachHangDTO().getLoaiDoiTuongID().equals(LoaiDoiTuongEnums.TRE_EM.name())) {
                String hdctGiamDTID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctGiamDT = new HoaDonChiTiet(hdctGiamDTID, hoaDon, new Ve(veMoiDTO.getVeID()),
                        "Giảm giá đối tượng trẻ em", LoaiDichVuEnums.KHUYEN_MAI, 1, listVeMoi.get(i).getGiamDoiTuong(),
                        listVeMoi.get(i).getGiamDoiTuong());
                dsHoaDonChiTiet.add(hdctGiamDT);

            }

            if (listVeMoi.get(i).getKhuyenMaiApDung() != null
                    && listVeMoi.get(i).getKhuyenMaiApDung().getKhuyenMaiID() != null) {
                String hdctKMID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctKM = new HoaDonChiTiet(hdctKMID, hoaDon, new Ve(veMoiDTO.getVeID()),
                        listVeMoi.get(i).getKhuyenMaiApDung().getMaKhuyenMai() + ": "
                                + listVeMoi.get(i).getKhuyenMaiApDung().getMoTa(),
                        LoaiDichVuEnums.KHUYEN_MAI, 1, -listVeMoi.get(i).getGiamKM(), -listVeMoi.get(i).getGiamKM());
                listVeMoi.get(i).getSuDungKhuyenMai().setHoaDonChiTiet(hdctKM);
                dsHoaDonChiTiet.add(hdctKM);

            }

            // Dòng phí đổi vé
            String hdctLePhiID = hoaDon.getHoaDonID() + "-" + (++stt);
            HoaDonChiTiet hdctLePhi = new HoaDonChiTiet(hdctLePhiID, hoaDon, new Ve(veMoiDTO.getVeID()), "Lệ phí đổi vé",
                    LoaiDichVuEnums.PHI_DOI, 1, listVeDoi.get(i).getLePhiDoiVe(), listVeDoi.get(i).getLePhiDoiVe());
            dsHoaDonChiTiet.add(hdctLePhi);
        }
        return dsHoaDonChiTiet;
    }

    public boolean themHoaDon(HoaDon hoaDon) throws Exception {
        return hoaDonDAO.insertHoaDon(hoaDon);
    }

    /**
     * @param dsHoaDonChiTiet
     */
    public void themCacHoaDonChiTiet(List<HoaDonChiTiet> dsHoaDonChiTiet) throws Exception {
        for (HoaDonChiTiet hdct : dsHoaDonChiTiet) {
            hoaDonChiTietDAO.insertHoaDonChiTiet(hdct);
        }
    }

    /**
     * @param loaiHD
     * @param khachHang
     * @param tuNgay
     * @param denNgay
     * @param hinhThucTT
     * @return
     */
    public List<HoaDon> locHoaDonTheoCacTieuChi(String loaiHD, String khachHang, String khachHangID, Date tuNgay,
                                                Date denNgay, String hinhThucTT) {
        return hoaDonDAO.searchHoaDonByFilter(loaiHD, khachHang, khachHangID, tuNgay, denNgay, hinhThucTT);
    }

    /**
     * @param keyword
     * @param type
     * @return
     */
    public List<HoaDon> layHoaDonTheoKeyWord(String keyword, String type) {
        return hoaDonDAO.searchHoaDonByKeyword(keyword, type);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10HoaDonID(String keyword) {
        return hoaDonDAO.getTop10HoaDonID(keyword);
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10KhachHangID(String keyword) {
        return hoaDonDAO.getTop10KhachHangID(keyword);

    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10MaGD(String keyword) {
        return hoaDonDAO.getTop10MaGD(keyword);
    }

    /**
     * @param hoaDonID
     * @return
     */
    public List<HoaDonChiTiet> layCacHoaDonChiTietTheoHoaDonID(String hoaDonID) {
        return hoaDonChiTietDAO.getHoaDonChiTietByHoaDonID(hoaDonID);
    }

    /**
     * @return
     */
    public List<HoaDon> layTatCaHoaDon() {
        return hoaDonDAO.getAllHoaDon();
    }
}