package gui.application.form.doiVe;
/*
 * @(#) ExchangeSesssion.java  1.0  [2:51:50 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import dto.PhieuGiuChoDTO;
import entity.*;
import gui.application.AuthService;
import gui.application.form.banVe.SearchCriteria;
import gui.application.form.banVe.VeSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExchangeSession {
    private static ExchangeSession instance;

    // --- DỮ LIỆU VÉ CŨ (Input từ Giai đoạn 1) ---
    private List<VeDoiRow> listVeTimDuoc = new ArrayList<>();
    private List<VeDoiRow> listVeCuCanDoi = new ArrayList<>();

    // --- DỮ LIỆU VÉ MỚI (Xử lý ở Giai đoạn 2) ---
    private SearchCriteria criteriaTimKiem;
    private List<Chuyen> listChuyenTauTimDuoc = new ArrayList<>();
    private List<VeSession> listVeMoiDangChon = new ArrayList<>();

    // --- DỮ LIỆU MAPPING (Xử lý ở Bước 6) ---
    // Key: ID của VeDoiRow (hoặc Ve ID) -> Value: VeSession mới
    private Map<String, VeSession> mapVeCuVoiVeMoi = new HashMap<>();

    // --- DỮ LIỆU CHUNG ---
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private PhieuGiuChoDTO phieuGiuCho;
    private GiaoDichThanhToan giaoDichThanhToan;
    private DonDatCho donDatChoCu;
    private DonDatCho donDatChoMoi;
    private HoaDon hoaDon;

    private ExchangeSession() {
        this.nhanVien = AuthService.getInstance().getCurrentUser();
    }

    public static synchronized ExchangeSession getInstance() {
        if (instance == null) {
            instance = new ExchangeSession();
        }
        return instance;
    }

    public static void setInstance(ExchangeSession instance) {
        ExchangeSession.instance = instance;
    }

    // --- Getters & Setters ---
    public List<VeDoiRow> getListVeTimDuoc() {
        return listVeTimDuoc;
    }

    public void setListVeTimDuoc(List<VeDoiRow> listVeTimDuoc) {
        this.listVeTimDuoc = listVeTimDuoc;
    }

    public List<VeDoiRow> getListVeCuCanDoi() {
        return listVeCuCanDoi;
    }

    public void setListVeCuCanDoi(List<VeDoiRow> listVeCuCanDoi) {
        this.listVeCuCanDoi = listVeCuCanDoi;
    }

    public SearchCriteria getCriteriaTimKiem() {
        return criteriaTimKiem;
    }

    public void setCriteriaTimKiem(SearchCriteria criteriaTimKiem) {
        this.criteriaTimKiem = criteriaTimKiem;
    }

    public List<Chuyen> getListChuyenTauTimDuoc() {
        return listChuyenTauTimDuoc;
    }

    public void setListChuyenTauTimDuoc(List<Chuyen> listChuyenTauTimDuoc) {
        this.listChuyenTauTimDuoc = listChuyenTauTimDuoc;
    }

    public List<VeSession> getListVeMoiDangChon() {
        return listVeMoiDangChon;
    }

    public void setListVeMoiDangChon(List<VeSession> listVeMoiDangChon) {
        this.listVeMoiDangChon = listVeMoiDangChon;
    }

    public void addVeMoi(VeSession ve) {
        if (ve != null && !listVeMoiDangChon.contains(ve)) {
            listVeMoiDangChon.add(ve);
        }
    }

    public boolean removeVeMoi(VeSession ve) {
        return listVeMoiDangChon.remove(ve);
    }

    // --- Helpers Logic ---

    public void clearAll() {
        listVeCuCanDoi.clear();
        criteriaTimKiem = null;
        listChuyenTauTimDuoc.clear();
        listVeMoiDangChon.clear();
        mapVeCuVoiVeMoi.clear();
    }

    /**
     * Lấy Ga Đi mặc định từ danh sách vé cũ (để điền vào form tìm kiếm)
     */
    public String getGaDiMacDinh() {
        if (listVeCuCanDoi == null || listVeCuCanDoi.isEmpty()) {
            return "";
        }
        // Logic: Lấy ga đi của vé đầu tiên trong danh sách đổi
        return listVeCuCanDoi.get(0).getVe().getGaDi().getTenGa();
    }

    public String getGaDenMacDinh() {
        if (listVeCuCanDoi == null || listVeCuCanDoi.isEmpty()) {
            return "";
        }
        return listVeCuCanDoi.get(0).getVe().getGaDen().getTenGa();
    }

    public String getGaDiIdMacDinh() {
        if (listVeCuCanDoi == null || listVeCuCanDoi.isEmpty()) {
            return null;
        }
        return listVeCuCanDoi.get(0).getVe().getGaDi().getGaID();
    }

    public String getGaDenIdMacDinh() {
        if (listVeCuCanDoi == null || listVeCuCanDoi.isEmpty()) {
            return null;
        }
        return listVeCuCanDoi.get(0).getVe().getGaDen().getGaID();
    }

    public PhieuGiuChoDTO getPhieuGiuCho() {
        return phieuGiuCho;
    }

    public void setPhieuGiuCho(PhieuGiuChoDTO phieuGiuCho) {
        this.phieuGiuCho = phieuGiuCho;
    }

    public Map<String, VeSession> getMapVeCuVoiVeMoi() {
        return mapVeCuVoiVeMoi;
    }

    public void setMapVeCuVoiVeMoi(Map<String, VeSession> mapVeCuVoiVeMoi) {
        this.mapVeCuVoiVeMoi = mapVeCuVoiVeMoi;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public GiaoDichThanhToan getGiaoDichThanhToan() {
        return giaoDichThanhToan;
    }

    public void setGiaoDichThanhToan(GiaoDichThanhToan giaoDichThanhToan) {
        this.giaoDichThanhToan = giaoDichThanhToan;
    }

    public DonDatCho getDonDatChoCu() {
        return donDatChoCu;
    }

    public void setDonDatChoCu(DonDatCho donDatChoCu) {
        this.donDatChoCu = donDatChoCu;
    }

    public DonDatCho getDonDatChoMoi() {
        return donDatChoMoi;
    }

    public void setDonDatChoMoi(DonDatCho donDatChoMoi) {
        this.donDatChoMoi = donDatChoMoi;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }
}