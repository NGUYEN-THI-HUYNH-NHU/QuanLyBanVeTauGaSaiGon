package bus;
/*
 * @(#) BieuGiaVe_BUS.java  1.0  [8:27:54 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */

import dao.impl.BieuGiaVeDAO;
import dao.impl.HangToaDAO;
import dao.impl.LoaiTauDAO;
import dao.impl.Tuyen_DAO;
import dto.BieuGiaVeDTO;
import dto.NhanVienDTO;
import entity.BieuGiaVe;
import entity.type.HangToaEnums;
import entity.type.LoaiTauEnums;
import entity.type.NhatKyAudit;
import entity.type.VaiTroNhanVienEnums;
import mapper.BieuGiaVeMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BieuGiaVe_BUS {
    private final BieuGiaVeDAO bieuGiaVeDAO = new BieuGiaVeDAO();
    private final HangToaDAO hangToaDAO = new HangToaDAO();
    private final LoaiTauDAO loaiTauDAO = new LoaiTauDAO();
    private final Tuyen_DAO tuyenDAO = new Tuyen_DAO();
    private final NhatKyAudit_BUS nhatKyAuditBus = new NhatKyAudit_BUS();

    public BieuGiaVe_BUS() {
    }

    public List<BieuGiaVeDTO> layDanhSachBieuGia() {
        return bieuGiaVeDAO.findAll().stream().map(BieuGiaVeMapper.INSTANCE::toDTO).toList();
    }

    public List<BieuGiaVeDTO> timKiem(String tuKhoa, String tuyenID, String loaiTauID) {
        return bieuGiaVeDAO.getBieuGiaTheoTieuChi(tuKhoa, tuyenID, loaiTauID).stream().map(BieuGiaVeMapper.INSTANCE::toDTO).toList();
    }

    public String themBieuGia(BieuGiaVeDTO bg, NhanVienDTO nv) {
        String loi = kiemTraHopLe(bg);
        if (loi != null) return loi;

        String newID = taoMaBieuGiaNgauNhien();
        bg.setId(newID);

        try {
            BieuGiaVe bieuGiaVe = BieuGiaVeMapper.INSTANCE.toEntity(bg);
            normalizeBieuGiaVe(bieuGiaVe, bg);
            if (bieuGiaVeDAO.create(bieuGiaVe) != null) {
                String tenChucVu = (nv.getVaiTroNhanVienID() != null) ? VaiTroNhanVienEnums.valueOf(nv.getVaiTroNhanVienID()).getDescription() : "";
                String giaLog = (bg.getDonGiaTrenKm() > 0)
                        ? String.format("%.0f đ/km", bg.getDonGiaTrenKm())
                        : String.format("Cố định %.0f VNĐ", bg.getGiaCoBan());

                String chiTietLog = String.format("%s %s Thêm Biểu giá: %s (Độ ưu tiên: %s, Giá: %s)",
                        tenChucVu, nv.getHoTen(), bg.getId(), bg.getDoUuTien(), giaLog
                );

                ghiLogAudit(bg.getId(), nv, NhatKyAudit.THEM, chiTietLog);
                return "Thêm thành công";
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("FOREIGN KEY")) {
                return "Lỗi: Tuyến tàu hoặc Loại tàu không tồn tại trong hệ thống.";
            }
            if (e.getMessage().contains("PRIMARY KEY")) {
                return "Lỗi: Mã biểu giá này đã tồn tại.";
            }
            return "Lỗi cơ sở dữ liệu: " + e.getMessage();
        }
        return "Thêm thất bại (Lỗi không xác định)";
    }

    private String taoMaBieuGiaNgauNhien() {
        Random random = new Random();
        String newID;
        boolean biTrung;

        do {
            int number = random.nextInt(1000);
            newID = String.format("BGV_%03d", number);
            biTrung = (bieuGiaVeDAO.findById(newID) != null);

        } while (biTrung);

        return newID;
    }

    public String capNhatBieuGia(BieuGiaVeDTO bgMoi, NhanVienDTO nv) {
        String loi = kiemTraHopLe(bgMoi);
        if (loi != null) {
            return loi;
        }
        BieuGiaVeDTO bgCu = BieuGiaVeMapper.INSTANCE.toDTO(bieuGiaVeDAO.findById(bgMoi.getId()));
        try {
            BieuGiaVe bieuGiaVeMoi = BieuGiaVeMapper.INSTANCE.toEntity(bgMoi);
            normalizeBieuGiaVe(bieuGiaVeMoi, bgMoi);

            if (bieuGiaVeDAO.update(bieuGiaVeMoi) != null) {
                List<String> thayDoi = new ArrayList<>();

                if (bgCu != null) {

                    String tuyenCu = (bgCu.getTuyenApDungID() != null) ? bgCu.getTuyenApDungID() : "Tất cả";
                    String tuyenMoi = (bgMoi.getTuyenApDungID() != null) ? bgMoi.getTuyenApDungID() : "Tất cả";
                    if (!tuyenCu.equals(tuyenMoi)) {
                        thayDoi.add(String.format("Tuyến (%s -> %s)", tuyenCu, tuyenMoi));
                    }

                    // 2. So sánh Loại Tàu
                    String tauCu = (bgCu.getLoaiTauApDungID() != null) ? LoaiTauEnums.valueOf(bgCu.getLoaiTauApDungID()).getDescription() : "Tất cả";
                    String tauMoi = (bgMoi.getLoaiTauApDungID() != null) ? LoaiTauEnums.valueOf(bgMoi.getLoaiTauApDungID()).getDescription() : "Tất cả";
                    if (!tauCu.equals(tauMoi)) {
                        thayDoi.add(String.format("Loại tàu (%s -> %s)", tauCu, tauMoi));
                    }

                    // 3. So sánh Hạng Toa
                    String toaCu = (bgCu.getHangToaApDungID() != null) ? HangToaEnums.valueOf(bgCu.getHangToaApDungID()).getDescription() : "Tất cả";
                    String toaMoi = (bgMoi.getHangToaApDungID() != null) ? HangToaEnums.valueOf(bgMoi.getHangToaApDungID()).getDescription() : "Tất cả";
                    if (!toaCu.equals(toaMoi)) {
                        thayDoi.add(String.format("Hạng toa (%s -> %s)", toaCu, toaMoi));
                    }

                    // 4. So sánh Đơn giá / Km
                    if (Double.compare(bgCu.getDonGiaTrenKm(), bgMoi.getDonGiaTrenKm()) != 0) {
                        thayDoi.add(String.format("Đơn giá/km (%.0f -> %.0f)", bgCu.getDonGiaTrenKm(), bgMoi.getDonGiaTrenKm()));
                    }

                    // 5. So sánh Giá cơ bản
                    if (Double.compare(bgCu.getGiaCoBan(), bgMoi.getGiaCoBan()) != 0) {
                        thayDoi.add(String.format("Giá cơ bản (%.0f -> %.0f)", bgCu.getGiaCoBan(), bgMoi.getGiaCoBan()));
                    }

                    // 6. So sánh Phụ phí cao điểm
                    if (Double.compare(bgCu.getPhuPhiCaoDiem(), bgMoi.getPhuPhiCaoDiem()) != 0) {
                        thayDoi.add(String.format("Phụ phí (%.0f -> %.0f)", bgCu.getPhuPhiCaoDiem(), bgMoi.getPhuPhiCaoDiem()));
                    }

                    // 7. So sánh Phạm vi Km
                    if (bgCu.getMinKm() != bgMoi.getMinKm() || bgCu.getMaxKm() != bgMoi.getMaxKm()) {
                        thayDoi.add(String.format("Phạm vi Km (%d-%d -> %d-%d)",
                                bgCu.getMinKm(), bgCu.getMaxKm(), bgMoi.getMinKm(), bgMoi.getMaxKm()));
                    }

                    // 8. So sánh Ngày Bắt đầu
                    if (!Objects.equals(bgCu.getNgayBatDau(), bgMoi.getNgayBatDau())) {
                        thayDoi.add(String.format("Ngày bắt đầu (%s -> %s)", bgCu.getNgayBatDau(), bgMoi.getNgayBatDau()));
                    }

                    // 9. So sánh Ngày Kết thúc
                    boolean oldDateNull = bgCu.getNgayKetThuc() == null;
                    boolean newDateNull = bgMoi.getNgayKetThuc() == null;
                    if (oldDateNull != newDateNull || (!oldDateNull && !bgCu.getNgayKetThuc().equals(bgMoi.getNgayKetThuc()))) {
                        String d1 = oldDateNull ? "Vô thời hạn" : bgCu.getNgayKetThuc().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        String d2 = newDateNull ? "Vô thời hạn" : bgMoi.getNgayKetThuc().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        thayDoi.add(String.format("Ngày kết thúc (%s -> %s)", d1, d2));
                    }

                    // 10. So sánh Độ ưu tiên
                    if (bgCu.getDoUuTien() != bgMoi.getDoUuTien()) {
                        thayDoi.add(String.format("Độ ưu tiên (%d -> %d)", bgCu.getDoUuTien(), bgMoi.getDoUuTien()));
                    }
                }

                if (!thayDoi.isEmpty()) {
                    String tenChucVu = (nv.getVaiTroNhanVienID() != null) ? VaiTroNhanVienEnums.valueOf(nv.getVaiTroNhanVienID()).getDescription() : "";
                    StringBuilder sbLog = new StringBuilder();
                    sbLog.append(String.format("%s %s Cập nhật biểu giá %s", tenChucVu, nv.getHoTen(), bgMoi.getId()));
                    sbLog.append(" : ").append(String.join(", ", thayDoi));

                    ghiLogAudit(bgMoi.getId(), nv, NhatKyAudit.SUA, sbLog.toString());
                }

                return "Cập nhật thành công";
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("FOREIGN KEY")) {
                return "Lỗi cập nhật: Dữ liệu liên quan (Tuyến/Tàu) không hợp lệ.";
            }
            return "Lỗi cập nhật: " + e.getMessage();
        }
        return "Cập nhật thất bại (Lỗi không xác định)";
    }

    private void ghiLogAudit(String doiTuongID, NhanVienDTO nv, NhatKyAudit loaiThaoTac, String chiTiet) {
        if (nv == null) return;
        try {
            String maLog = nhatKyAuditBus.taoMaNhatKyAuditMoi();
            entity.NhatKyAudit log = new entity.NhatKyAudit(
                    maLog,
                    doiTuongID,
                    nv.getId(),
                    LocalDateTime.now(),
                    loaiThaoTac,
                    chiTiet,
                    "BieuGiaVe"
            );
            nhatKyAuditBus.ghiNhatKyAudit(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Logic kiểm tra dữ liệu đầu vào
    private String kiemTraHopLe(BieuGiaVeDTO bg) {
        if (bg.getMinKm() < 0 || bg.getMaxKm() < 0) {
            return "Khoảng cách Km không được âm.";
        }
        if (bg.getMinKm() >= bg.getMaxKm()) {
            return "Min Km phải nhỏ hơn Max Km.";
        }

        // Kiểm tra logic ngày
        if (bg.getNgayBatDau() == null) {
            return "Ngày bắt đầu không được để trống.";
        }
        if (bg.getNgayKetThuc() != null && bg.getNgayBatDau().isAfter(bg.getNgayKetThuc())) {
            return "Ngày kết thúc phải sau ngày bắt đầu.";
        }

        // Kiểm tra giá: phải có 1 trong 2 loại giá
        boolean hasKmPrice = bg.getDonGiaTrenKm() > 0;
        boolean hasFixPrice = bg.getGiaCoBan() > 0;

        if (!hasKmPrice && !hasFixPrice) {
            return "Phải nhập Đơn giá/Km hoặc Giá cố định (>0).";
        }
        if (hasKmPrice && hasFixPrice) {
            // Logic DB cho phép 1 cái null, nên ta ưu tiên chọn 1 cái trong UI,
            // nhưng ở BUS nên clear cái kia về 0 để DAO xử lý đúng.
            // (Ở Form đã xử lý disable input, nhưng ở đây check cho chắc)
        }

        return null;
    }

    private void normalizeBieuGiaVe(BieuGiaVe bieuGiaVe, BieuGiaVeDTO bieuGiaVeDTO) {
        bieuGiaVe.setHangToaApDung(bieuGiaVeDTO.getHangToaApDungID() != null
                ? hangToaDAO.findById(bieuGiaVeDTO.getHangToaApDungID()) : null);
        bieuGiaVe.setLoaiTauApDung(bieuGiaVeDTO.getLoaiTauApDungID() != null
                ? loaiTauDAO.findById(bieuGiaVeDTO.getLoaiTauApDungID()) : null);
        bieuGiaVe.setTuyenApDung(bieuGiaVeDTO.getTuyenApDungID() != null
                ? tuyenDAO.findById(bieuGiaVeDTO.getTuyenApDungID()) : null);
    }
}