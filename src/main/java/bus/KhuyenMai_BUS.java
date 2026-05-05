package bus;

import dao.impl.KhuyenMaiDAO;
import dto.KhuyenMaiDTO;
import dto.VeDTO;
import entity.*;
import entity.type.TrangThaiSDKM;
import gui.application.AuthService;
import gui.application.form.banVe.VeSession;
import mapper.KhuyenMaiMapper;
import mapper.NhanVienMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class KhuyenMai_BUS {
    private final KhuyenMaiDAO khuyenMaiDAO;
    private final NhanVien nhanVienHienTai;
    private final NhatKyAudit_BUS nhatKyAudit_bus;

    public KhuyenMai_BUS() {
        this.khuyenMaiDAO = new KhuyenMaiDAO();
        this.nhanVienHienTai = NhanVienMapper.INSTANCE.toEntity(AuthService.getInstance().getCurrentUser());
        this.nhatKyAudit_bus = new NhatKyAudit_BUS();
    }

    // ================= LOGIC NGHIỆP VỤ (PROMOTION LOGIC) =================

    public static KhuyenMaiDTO getBestPromotion(VeSession veSession, List<KhuyenMaiDTO> listKM) {
        if (listKM == null || listKM.isEmpty() || veSession == null || veSession.getVe() == null) {
            return null;
        }

        KhuyenMaiDTO bestKM = null;
        double maxDiscountAmount = -1.0;
        double giaVeGoc = veSession.getVe().getGia();

        for (KhuyenMaiDTO km : listKM) {
            if (km == null || !km.getTrangThai()) continue;
            double discountAmount = 0;

            if (km.getTyLeGiamGia() > 0) {
                double tyLe = km.getTyLeGiamGia();
                if (tyLe >= 1.0) {
                    tyLe = tyLe / 100.0;
                }
                discountAmount = giaVeGoc * tyLe;
            } else if (km.getTienGiamGia() > 0) {
                discountAmount = km.getTienGiamGia();
            }

            if (discountAmount > giaVeGoc) {
                discountAmount = giaVeGoc;
            }

            if (discountAmount > maxDiscountAmount) {
                maxDiscountAmount = discountAmount;
                bestKM = km;
            }
        }

        return bestKM;
    }

    // ================= LẤY DỮ LIỆU =================

    public List<KhuyenMai> layDanhSachKhuyenMai() {
        return khuyenMaiDAO.getAllKhuyenMai();
    }

    public List<KhuyenMai> layKhuyenMaiTheoLoai(String loai) {
        List<KhuyenMai> all = khuyenMaiDAO.getAllKhuyenMai();
        if (loai == null || loai.equals("Tất cả")) {
            return all;
        }

        String prefix = switch (loai) {
            case "Mùa" -> "MUA";
            case "Lễ hội" -> "LE";
            case "Đối tượng" -> "DOITUONG";
            case "Tuyến" -> "TUYEN";
            case "Hạng vé" -> "HANGVE";
            case "Loại tàu" -> "LOAITAU";
            case "Hạng toa" -> "HANGTOA";
            case "Ngày trong tuần" -> "NGAYTRONGTUAN";
            case "Min giá hóa đơn" -> "MINGIA";
            default -> "";
        };

        if (prefix.isEmpty()) return all;

        return all.stream()
                .filter(km -> km.getMaKhuyenMai() != null && km.getMaKhuyenMai().toUpperCase().startsWith(prefix))
                .toList();
    }

    public List<KhuyenMai> timKiemKhuyenMai(String tuKhoa, String maTuyen, Boolean trangThai, LocalDate ngayBatDau,
                                            LocalDate ngayKetThuc, LoaiTau loaiTau, HangToa hangToa, LoaiDoiTuong loaiDoiTuong) {
        return khuyenMaiDAO.timKhuyenMai(tuKhoa, maTuyen, trangThai, ngayBatDau, ngayKetThuc, loaiTau, hangToa, loaiDoiTuong);
    }

    public String layDieuKienKhuyenMaiTheoMaKhuyenMai(String khuyenMaiID) {
        return khuyenMaiDAO.layDieuKienKhuyenMai(khuyenMaiID);
    }

    public DieuKienKhuyenMai layKhuyenMaiTheoMaKhuyenMaiObj(String khuyenMaiID) {
        return khuyenMaiDAO.layDieuKienKhuyenMaiTheoKhuyenMai(khuyenMaiID);
    }

    public List<Tuyen> layDanhSachTuyen() {
        return khuyenMaiDAO.layDanhSachTuyen();
    }

    public List<LoaiTau> layDanhSachLoaiTau() {
        return khuyenMaiDAO.layDanhSachLoaiTau();
    }

    public List<HangToa> layDanhSachHangToa() {
        return khuyenMaiDAO.layDanhSachHangToa();
    }

    public List<LoaiDoiTuong> layDanhSachLoaiDoiTuong() {
        return khuyenMaiDAO.layDanhSachLoaiDoiTuong();
    }

    public KhuyenMai layKhuyenMaiTheoID(String khuyenMaiID) {
        return khuyenMaiDAO.timKiemKhuyenMaiByID(khuyenMaiID);
    }

    public List<KhuyenMaiDTO> getDanhSachKhuyenMaiPhuHop(VeSession veSession) {
        return khuyenMaiDAO.getDanhSachKhuyenMaiPhuHop(veSession)
                .stream().map(KhuyenMaiMapper.INSTANCE::toDTO).toList();
    }

    // ================= THAO TÁC CẬP NHẬT / THÊM MỚI =================

    public boolean capNhatTrangThaiKhuyenMai() {
        return khuyenMaiDAO.tuDongCapNhatTrangThai();
    }

    public String taoMaKhuyenMaiTuDong() {
        return khuyenMaiDAO.taoMaKhuyenMaiTuDong();
    }

    public String taoDieuKienKhuyenMaiTuDong() {
        return khuyenMaiDAO.taoMaDieuKienTuDong();
    }

    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        return themKhuyenMai(km, dkkm, nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : "SYSTEM");
    }

    public boolean themKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm, String nguoiThucHienID) {
        if (km == null || dkkm == null) return false;

        if (dkkm.getNgayTrongTuan() == null || dkkm.getNgayTrongTuan() == 0) {
            dkkm.setNgayTrongTuan(null);
        }

        boolean ok = khuyenMaiDAO.themKhuyenMai(km, dkkm);
        if (ok) {
            ghiLog(km.getKhuyenMaiID(), nguoiThucHienID,
                    entity.type.NhatKyAudit.THEM, "Thêm khuyến mãi: " + km.getMaKhuyenMai() + " - " + km.getMoTa());
        }
        return ok;
    }

    public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm) {
        return suaKhuyenMai(km, dkkm, nhanVienHienTai != null ? nhanVienHienTai.getNhanVienID() : "SYSTEM");
    }

    public boolean suaKhuyenMai(KhuyenMai km, DieuKienKhuyenMai dkkm, String nguoiThucHienID) {
        if (km == null || dkkm == null) return false;

        KhuyenMai khuyenMaiCu = khuyenMaiDAO.timKiemKhuyenMaiByID(km.getKhuyenMaiID());
        DieuKienKhuyenMai dkCu = khuyenMaiDAO.layDieuKienKhuyenMaiTheoKhuyenMai(km.getKhuyenMaiID());
        if (khuyenMaiCu == null || dkCu == null) {
            return false;
        }

        if (dkkm.getNgayTrongTuan() == null || dkkm.getNgayTrongTuan() == 0) {
            dkkm.setNgayTrongTuan(null);
        }

        boolean ok = khuyenMaiDAO.suaKhuyenMai(km, dkkm);
        if (!ok) return false;

        String thanhPhan = thanhPhanDaBiSua(km, dkkm, khuyenMaiCu, dkCu);
        if (thanhPhan != null && !thanhPhan.isBlank()) {
            ghiLog(km.getKhuyenMaiID(), nguoiThucHienID,
                    entity.type.NhatKyAudit.SUA, "Cập nhật khuyến mãi. " + thanhPhan);
        }
        return true;
    }

    // ================= XỬ LÝ KHI BÁN VÉ / ĐỔI TRẢ VÉ (JPA) =================

    public void ganDanhSachSuDungKhuyenMai(List<VeSession> listVeSession) {
        if (listVeSession == null) return;
        for (VeSession ve : listVeSession) {
            if (ve.getKhuyenMaiApDung() != null && ve.getKhuyenMaiApDung().getId() != null) {
                String sdkmID = "SD-" + UUID.randomUUID();
                ve.setSuDungKhuyenMai(new SuDungKhuyenMai(sdkmID, KhuyenMaiMapper.INSTANCE.toEntity(ve.getKhuyenMaiApDung()), null, TrangThaiSDKM.DA_AP_DUNG));
            }
        }
    }

    public boolean themDanhSachSuDungKhuyenMai(List<VeSession> listVeSession) {
        if (listVeSession == null || listVeSession.isEmpty()) return true;
        try {
            return khuyenMaiDAO.doInTransaction(em -> {
                for (VeSession ve : listVeSession) {
                    if (ve.getKhuyenMaiApDung() != null && ve.getSuDungKhuyenMai() != null) {
                        em.persist(ve.getSuDungKhuyenMai());

                        int updated = em.createQuery(
                                        "UPDATE KhuyenMai km SET km.soLuong = km.soLuong - 1 WHERE km.khuyenMaiID = :id AND km.soLuong > 0")
                                .setParameter("id", ve.getKhuyenMaiApDung().getId())
                                .executeUpdate();

                        if (updated == 0) {
                            throw new RuntimeException("Khuyến mãi đã hết lượt sử dụng!");
                        }
                    }
                }
                return true;
            });
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, Integer> layDanhSachKhuyenMaiCanHoan(List<VeDTO> listVe) {
        return khuyenMaiDAO.getDanhSachKhuyenMaiCanHoan(listVe);
    }

    public boolean congSoLuongKhuyenMai(String kmID, int soLuongCanCong) {
        return khuyenMaiDAO.updateSoLuongKhuyenMai(kmID, soLuongCanCong);
    }

    // ================= LOGGING & VALIDATION =================

    public void ghiLog(String doiTuongID, String nguoiThucHienID, entity.type.NhatKyAudit loai, String chiTiet) {
        if (nhatKyAudit_bus == null) return;
        String nguoi = (nguoiThucHienID == null || nguoiThucHienID.isBlank()) ? "SYSTEM" : nguoiThucHienID;

        NhatKyAudit audit = new NhatKyAudit(nhatKyAudit_bus.taoMaNhatKyAuditMoi(), doiTuongID, nguoi,
                LocalDateTime.now(), loai, chiTiet, "KHUYEN_MAI");
        nhatKyAudit_bus.ghiNhatKyAudit(audit);
    }

    public boolean kiemTraCodeKhuyenMai(String code) {
        String regex = "^[A-Z][A-Z0-9_]{4,30}$";
        return code != null && code.matches(regex);
    }

    public boolean kiemMoTa(String moTa) {
        return moTa != null && moTa.length() <= 100;
    }

    public boolean kiemTraTyLeGiamGia(double tyLeGiamGia) {
        return tyLeGiamGia >= 0 && tyLeGiamGia <= 100;
    }

    public boolean kiemTraTienGiamGia(double tiemGiamGia) {
        return tiemGiamGia >= 0;
    }

    public boolean kiemTraSoLuong(double soLuong) {
        return soLuong >= 0;
    }

    public boolean kiemTraGioiHanMoiKhachHang(int gioiHan) {
        return gioiHan >= 0;
    }

    public boolean kiemTraNgayBatDau(LocalDate ngayBatDau) {
        return ngayBatDau != null && !ngayBatDau.isBefore(LocalDate.now());
    }

    public boolean kiemTraNgayKetThuc(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return ngayBatDau != null && ngayKetThuc != null && !ngayKetThuc.isBefore(ngayBatDau);
    }

    public boolean kiemTraMinGiaTriDonHang(double minGiaTri) {
        return minGiaTri >= 0;
    }

    public boolean kiemTraNgayTrongTuan(int ngayTrongTuan) {
        return ngayTrongTuan >= 0 && ngayTrongTuan <= 7;
    }

    // ================= LẤY THÀNH PHẦN BỊ THAY ĐỔI =================

    public String thanhPhanDaBiSua(KhuyenMai kmMoi, DieuKienKhuyenMai dkkmMoi, KhuyenMai kmCu, DieuKienKhuyenMai dkkmCu) {
        StringBuilder thayDoi = new StringBuilder();

        if (!Objects.equals(kmMoi.getMaKhuyenMai(), kmCu.getMaKhuyenMai())) {
            thayDoi.append(String.format("Cập nhật code: ('%s' -> '%s')\n", kmCu.getMaKhuyenMai(), kmMoi.getMaKhuyenMai()));
        }
        if (!Objects.equals(kmMoi.getMoTa(), kmCu.getMoTa())) {
            thayDoi.append(String.format("Cập nhật mô tả: ('%s' -> '%s')\n", kmCu.getMoTa(), kmMoi.getMoTa()));
        }
        if (kmMoi.getTyLeGiamGia() != kmCu.getTyLeGiamGia()) {
            thayDoi.append(String.format("Cập nhật tỉ lệ giảm giá: (%.2f%% -> %.2f%%)\n", kmCu.getTyLeGiamGia(), kmMoi.getTyLeGiamGia()));
        }
        if (kmMoi.getTienGiamGia() != kmCu.getTienGiamGia()) {
            thayDoi.append(String.format("Cập nhật tiền giảm giá: (%.2f -> %.2f)\n", kmCu.getTienGiamGia(), kmMoi.getTienGiamGia()));
        }
        if (kmMoi.getSoLuong() != kmCu.getSoLuong()) {
            thayDoi.append(String.format("Cập nhật số lượng: (%.0f -> %.0f)\n", kmCu.getSoLuong(), kmMoi.getSoLuong()));
        }
        if (kmMoi.isTrangThai() != kmCu.isTrangThai()) {
            thayDoi.append(String.format("Cập nhật trạng thái: ('%b' -> '%b')\n", kmCu.isTrangThai(), kmMoi.isTrangThai()));
        }

        if (dkkmMoi != null && dkkmCu != null) {
            if (dkkmMoi.getMinGiaTriDonHang() != dkkmCu.getMinGiaTriDonHang()) {
                thayDoi.append(String.format("Cập nhật giá trị tối thiểu: (%.2f -> %.2f)\n", dkkmCu.getMinGiaTriDonHang(), dkkmMoi.getMinGiaTriDonHang()));
            }

            String tuyenCuID = dkkmCu.getTuyen() != null ? dkkmCu.getTuyen().getTuyenID() : "Trống";
            String tuyenMoiID = dkkmMoi.getTuyen() != null ? dkkmMoi.getTuyen().getTuyenID() : "Trống";
            if (!Objects.equals(tuyenCuID, tuyenMoiID)) {
                thayDoi.append(String.format("Cập nhật tuyến: ('%s' -> '%s')\n", tuyenCuID, tuyenMoiID));
            }

            String hangToaCu = dkkmCu.getHangToa() != null ? dkkmCu.getHangToa().getMoTa() : "Trống";
            String hangToaMoi = dkkmMoi.getHangToa() != null ? dkkmMoi.getHangToa().getMoTa() : "Trống";
            if (!Objects.equals(hangToaCu, hangToaMoi)) {
                thayDoi.append(String.format("Cập nhật hạng toa: ('%s' -> '%s')\n", hangToaCu, hangToaMoi));
            }

            Integer nttMoi = dkkmMoi.getNgayTrongTuan();
            Integer nttCu = dkkmCu.getNgayTrongTuan();
            if (!Objects.equals(nttMoi, nttCu)) {
                thayDoi.append(String.format("Cập nhật ngày áp dụng trong tuần: (%s -> %s)\n",
                        nttCu != null ? nttCu : "Trống", nttMoi != null ? nttMoi : "Trống"));
            }

            String loaiTauCu = dkkmCu.getLoaiTau() != null ? dkkmCu.getLoaiTau().getMoTa() : "Trống";
            String loaiTauMoi = dkkmMoi.getLoaiTau() != null ? dkkmMoi.getLoaiTau().getMoTa() : "Trống";
            if (!Objects.equals(loaiTauCu, loaiTauMoi)) {
                thayDoi.append(String.format("Cập nhật loại tàu: ('%s' -> '%s')\n", loaiTauCu, loaiTauMoi));
            }
        }

        return thayDoi.toString();
    }

    public KhuyenMai timKhuyenMaiChoVe(Ve ve) {
        return new KhuyenMai();
    }
}