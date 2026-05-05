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
import dao.impl.VeDAO;
import dto.*;
import entity.*;
import entity.type.LoaiDichVuEnums;
import entity.type.LoaiDoiTuongEnums;
import gui.application.AuthService;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import gui.application.form.doiVe.VeDoiRow;
import gui.application.form.hoanVe.VeHoanRow;
import mapper.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDon_BUS {
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final HoaDonChiTietDAO hoaDonChiTietDAO = new HoaDonChiTietDAO();
    private final VeDAO veDAO = new VeDAO();
    private final PhieuDungPhongVIPDAO phieuDungPhongVIPDAO = new PhieuDungPhongVIPDAO();
    private final NhanVien nhanVien = NhanVienMapper.INSTANCE.toEntity(AuthService.getInstance().getCurrentUser());

    /**
     * @param bookingSession
     * @return
     */
    public HoaDon taoHoaDon(BookingSession bookingSession) {
        String hdID = "HD-" + bookingSession.getDonDatCho().getId().substring(4);
        LocalDateTime now = LocalDateTime.now();
        KhachHang khachHang = KhachHangMapper.INSTANCE.toEntity(bookingSession.getKhachHang());
        HoaDon hoaDon = new HoaDon(hdID, khachHang, nhanVien, now, bookingSession.getGiaoDichThanhToan().getTongTien(),
                bookingSession.getGiaoDichThanhToan().getMaGD(), bookingSession.getGiaoDichThanhToan().getTienNhan(),
                bookingSession.getGiaoDichThanhToan().getTienHoan(), bookingSession.getGiaoDichThanhToan().isThanhToanTienMat());

        return hoaDon;
    }

    /**
     * @param exchangeSession
     * @return
     */
    public HoaDon taoHoaDonDoiVe(ExchangeSession exchangeSession) {
        String hdID = "HDDV-" + exchangeSession.getDonDatChoMoi().getId().substring(4);
        KhachHang khachHang = KhachHangMapper.INSTANCE.toEntity(exchangeSession.getKhachHang());
        HoaDon hoaDon = new HoaDon(hdID, khachHang, nhanVien, LocalDateTime.now(), exchangeSession.getGiaoDichThanhToan().getTongTien(),
                exchangeSession.getGiaoDichThanhToan().getMaGD(), exchangeSession.getGiaoDichThanhToan().getTienNhan(),
                exchangeSession.getGiaoDichThanhToan().getTienHoan(), exchangeSession.getGiaoDichThanhToan().isThanhToanTienMat());

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
        hoaDon.setTienNhan(0.0);
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
        for (VeSession v : listVeMoi) {
            String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
            VeDTO veDTO = v.getVe();
            Ve ve = veDAO.findById(veDTO.getVeID());

            HoaDonChiTiet hdctVe = HoaDonChiTiet.builder()
                    .hoaDonChiTietID(hdctVeID).hoaDon(hoaDon).ve(ve)
                    .tenDichVu("Vé HK: " + v).loaiDichVu(LoaiDichVuEnums.VE_BAN).donViTinh("Vé")
                    .soLuong(1).donGia(veDTO.getGia()).thanhTien(veDTO.getGia())
                    .build();
            dsHoaDonChiTiet.add(hdctVe);

            if (v.getPhieuDungPhongVIP() != null) {
                String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                PhieuDungPhongVIP phieuDungPhongVIP = phieuDungPhongVIPDAO.findById(v.getPhieuDungPhongVIP().getId());
                HoaDonChiTiet hdctPhieu = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctPhieuID).hoaDon(hoaDon).phieuDungPhongVIP(phieuDungPhongVIP).loaiDichVu(LoaiDichVuEnums.PHONG_VIP)
                        .donViTinh("Phiếu").soLuong(1).donGia(v.getPhiPhieuDungPhongChoVIP()).thanhTien(v.getPhiPhieuDungPhongChoVIP())
                        .build();
                dsHoaDonChiTiet.add(hdctPhieu);
            }

            if (veDTO.getKhachHangDTO().getLoaiDoiTuongID().equals(LoaiDoiTuongEnums.TRE_EM.toString())) {
                String hdctGiamDTID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctGiamDT = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctGiamDTID).hoaDon(hoaDon).ve(ve).tenDichVu("Giảm giá đối tượng trẻ em")
                        .loaiDichVu(LoaiDichVuEnums.KHUYEN_MAI).soLuong(1).donGia(-v.getGiamDoiTuong()).thanhTien(-v.getGiamDoiTuong())
                        .build();
                dsHoaDonChiTiet.add(hdctGiamDT);
            }

            if (v.getKhuyenMaiApDung() != null && v.getKhuyenMaiApDung().getId() != null) {
                String hdctKMID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctKM = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctKMID).hoaDon(hoaDon)
                        .tenDichVu(v.getKhuyenMaiApDung().getMaKhuyenMai() + ": " + v.getKhuyenMaiApDung().getMoTa())
                        .loaiDichVu(LoaiDichVuEnums.KHUYEN_MAI).soLuong(1).donGia(-v.getGiamKM()).thanhTien(-v.getGiamKM())
                        .build();
                v.getSuDungKhuyenMai().setHoaDonChiTiet(hdctKM);
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
            Ve ve = veDAO.findById(row.getVe().getVeID());
            HoaDonChiTiet hdctVeHoan = HoaDonChiTiet.builder()
                    .hoaDonChiTietID(hdctVeID).hoaDon(hoaDon).ve(ve)
                    .tenDichVu("Điều chỉnh giảm theo BB trả vé số: 2177975").loaiDichVu(LoaiDichVuEnums.VE_HOAN)
                    .donViTinh("Vé").soLuong(1).donGia(-ve.getGia()).thanhTien(-ve.getGia())
                    .build();
            dsHoaDonChiTiet.add(hdctVeHoan);

            PhieuDungPhongVIP phieu = phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(ve.getVeID());
            if (phieu != null) {
                String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctPhieu = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctPhieuID).hoaDon(hoaDon).phieuDungPhongVIP(phieu).loaiDichVu(LoaiDichVuEnums.PHIEU_HUY)
                        .tenDichVu("Hủy phiếu dùng phòng chờ VIP theo vé hoàn").donViTinh("Phiếu").soLuong(1).donGia(0.0).thanhTien(0.0)
                        .build();
                dsHoaDonChiTiet.add(hdctPhieu);
            }

            String hdctLePhiID = hoaDon.getHoaDonID() + "-" + (++stt);
            HoaDonChiTiet hdctLePhi = HoaDonChiTiet.builder()
                    .hoaDonChiTietID(hdctLePhiID).hoaDon(hoaDon).tenDichVu("Lệ phí hoàn vé").loaiDichVu(LoaiDichVuEnums.PHI_HOAN)
                    .soLuong(1).donGia(row.getLePhiHoanVe()).thanhTien(row.getLePhiHoanVe())
                    .build();
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
            Ve ve = veDAO.findById(listVeDoi.get(i).getVe().getVeID());
            HoaDonChiTiet hdctVeDoi = HoaDonChiTiet.builder()
                    .hoaDonChiTietID(hdctVeDoiID).hoaDon(hoaDon).ve(ve)
                    .tenDichVu("Điều chỉnh giảm theo BB trả vé số: 2177975").loaiDichVu(LoaiDichVuEnums.VE_DOI)
                    .donViTinh("Vé").soLuong(1).donGia(-ve.getGia()).thanhTien(-ve.getGia())
                    .build();
            dsHoaDonChiTiet.add(hdctVeDoi);

            // Dòng phiếu đổi (hủy)
            PhieuDungPhongVIPDTO phieuDoi = listVeDoi.get(i).getPhieuDungPhongVIP();
            if (phieuDoi != null) {
                String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                PhieuDungPhongVIP phieuDungPhongVIPDoi = PhieuDungPhongVIPMapper.INSTANCE.toEntity(phieuDoi);
                HoaDonChiTiet hdctPhieu = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctPhieuID).hoaDon(hoaDon).phieuDungPhongVIP(phieuDungPhongVIPDoi).loaiDichVu(LoaiDichVuEnums.PHONG_VIP)
                        .tenDichVu("Hủy phiếu dùng phòng chờ VIP theo vé đổi").donViTinh("Phiếu").soLuong(1).donGia(0.0).thanhTien(0.0)
                        .build();
                dsHoaDonChiTiet.add(hdctPhieu);
            }

            // Dòng vé mới
            String hdctVeMoiID = hoaDon.getHoaDonID() + "-" + (++stt);
            VeDTO veMoiDTO = listVeMoi.get(i).getVe();
            Ve veMoi = veDAO.findById(veMoiDTO.getVeID());
            HoaDonChiTiet hdctVeMoi = HoaDonChiTiet.builder()
                    .hoaDonChiTietID(hdctVeMoiID).hoaDon(hoaDon).ve(veMoi)
                    .tenDichVu("Vé HK: " + listVeMoi.get(i).toString()).loaiDichVu(LoaiDichVuEnums.VE_BAN)
                    .donViTinh("Vé").soLuong(1).donGia(veMoiDTO.getGia()).thanhTien(veMoiDTO.getGia())
                    .build();
            dsHoaDonChiTiet.add(hdctVeMoi);

            // Dòng phiếu mới (nếu có)
            PhieuDungPhongVIPDTO phieuMoi = listVeMoi.get(i).getPhieuDungPhongVIP();
            if (phieuMoi != null) {
                String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
                PhieuDungPhongVIP phieuDungPhongVIPMoi = phieuDungPhongVIPDAO.findById(phieuMoi.getId());
                HoaDonChiTiet hdctPhieu = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctPhieuID).hoaDon(hoaDon).phieuDungPhongVIP(phieuDungPhongVIPMoi).loaiDichVu(LoaiDichVuEnums.PHONG_VIP)
                        .tenDichVu("Phiếu dùng phòng chờ VIP Ga Sài Gòn").donViTinh("Phiếu").soLuong(1).donGia(20000.0).thanhTien(20000.0)
                        .build();
                dsHoaDonChiTiet.add(hdctPhieu);
            }

            // Dòng giảm giá đối tượng trẻ em
            if (veMoiDTO.getKhachHangDTO().getLoaiDoiTuongID().equals(LoaiDoiTuongEnums.TRE_EM.name())) {
                String hdctGiamDTID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctGiamDT = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctGiamDTID).hoaDon(hoaDon).ve(ve).tenDichVu("Giảm giá đối tượng trẻ em")
                        .loaiDichVu(LoaiDichVuEnums.KHUYEN_MAI).soLuong(1).donGia(listVeMoi.get(i).getGiamDoiTuong()).thanhTien(listVeMoi.get(i).getGiamDoiTuong())
                        .build();
                dsHoaDonChiTiet.add(hdctGiamDT);
            }

            if (listVeMoi.get(i).getKhuyenMaiApDung() != null
                    && listVeMoi.get(i).getKhuyenMaiApDung().getId() != null) {
                String hdctKMID = hoaDon.getHoaDonID() + "-" + (++stt);
                HoaDonChiTiet hdctKM = HoaDonChiTiet.builder()
                        .hoaDonChiTietID(hdctKMID).hoaDon(hoaDon)
                        .tenDichVu(listVeMoi.get(i).getKhuyenMaiApDung().getMaKhuyenMai() + ": " + listVeMoi.get(i).getKhuyenMaiApDung().getMoTa())
                        .loaiDichVu(LoaiDichVuEnums.KHUYEN_MAI).soLuong(1).donGia(-listVeMoi.get(i).getGiamKM()).thanhTien(-listVeMoi.get(i).getGiamKM())
                        .build();
                listVeMoi.get(i).getSuDungKhuyenMai().setHoaDonChiTiet(hdctKM);
                dsHoaDonChiTiet.add(hdctKM);

            }

            // Dòng phí đổi vé
            String hdctLePhiID = hoaDon.getHoaDonID() + "-" + (++stt);
            HoaDonChiTiet hdctLePhi = HoaDonChiTiet.builder()
                    .hoaDonChiTietID(hdctLePhiID).hoaDon(hoaDon).tenDichVu("Lệ phí đổi vé").loaiDichVu(LoaiDichVuEnums.PHI_DOI)
                    .soLuong(1).donGia(listVeDoi.get(i).getLePhiDoiVe()).thanhTien(listVeDoi.get(i).getLePhiDoiVe())
                    .build();
            dsHoaDonChiTiet.add(hdctLePhi);
        }
        return dsHoaDonChiTiet;
    }

    public boolean themHoaDon(HoaDon hoaDon) throws Exception {
        return hoaDonDAO.create(hoaDon) != null;
    }

    /**
     * @param dsHoaDonChiTiet
     */
    public void themCacHoaDonChiTiet(List<HoaDonChiTiet> dsHoaDonChiTiet) {
        for (HoaDonChiTiet hdct : dsHoaDonChiTiet) {
            hoaDonChiTietDAO.create(hdct);
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
    public List<HoaDonDTO> locHoaDonTheoCacTieuChi(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String khachHangID, Date tuNgay,
                                                   Date denNgay, String hinhThucTT, int page, int limit) {
        return hoaDonDAO.searchHoaDonByFilter(tuKhoaTraCuu, loaiTraCuu, loaiHD, khachHang, khachHangID, tuNgay, denNgay, hinhThucTT, page, limit)
                .stream().map(HoaDonMapper.INSTANCE::toDTO).toList();
    }

    /**
     * @param keyword
     * @param type
     * @return
     */
    public List<HoaDonDTO> layHoaDonTheoKeyWord(String keyword, String type, int page, int limit) {
        return hoaDonDAO.searchHoaDonByKeyword(keyword, type, page, limit).stream().map(HoaDonMapper.INSTANCE::toDTO).toList();
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10HoaDonID(String keyword) {
        return hoaDonDAO.getTop10HoaDonID(keyword);
    }


    /**
     * @param hoaDonID
     * @return
     */
    public List<HoaDonChiTietDTO> layCacHoaDonChiTietTheoHoaDonID(String hoaDonID) {
        return hoaDonChiTietDAO.getHoaDonChiTietByHoaDonID(hoaDonID).stream().map(HoaDonChiTietMapper.INSTANCE::toDTO).toList();
    }

    public int countAllHoaDon() {
        return hoaDonDAO.countAll();
    }

    public int countHoaDonByFilter(String tuKhoaTraCuu, String loaiTraCuu, String loaiHD, String khachHang, String soGiayTo, Date tuNgay, Date denNgay, String hinhThucTT) {
        return hoaDonDAO.countByFilter(tuKhoaTraCuu, loaiTraCuu, loaiHD, khachHang, soGiayTo, tuNgay, denNgay, hinhThucTT);
    }

    public int countHoaDonByKeyword(String keyword, String type) {
        return hoaDonDAO.countByKeyword(keyword, type);
    }

    public List<HoaDonDTO> getHoaDonByPage(int currentPage, int rowsPerPage) {
        return hoaDonDAO.getByPage(currentPage, rowsPerPage).stream().map(HoaDonMapper.INSTANCE::toDTO).toList();
    }

}