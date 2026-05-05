package bus;
/*
 * @(#) Chuyen_BUS.java  1.0  [12:42:29 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import dao.*;
import dao.impl.*;
import dto.*;
import entity.*;
import entity.type.NhatKyAudit;
import entity.type.TrangThaiTau;
import mapper.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Chuyen_BUS implements IChuyenBUS {
    private final NhatKyAudit_BUS nhatKyAuditBus;
    private IGheDAO gheDAO;
    private IToaDAO toaDAO;
    private IChuyenDAO chuyenDAO;
    private ChuyenGaDAO chuyenGaDao;
    private IGaDAO gaDAO;
    private ITauDAO tauDao;

    public Chuyen_BUS() {
        gheDAO = new GheDAO();
        toaDAO = new Toa_DAO();
        chuyenDAO = new Chuyen_DAO();
        chuyenGaDao = new ChuyenGaDAO();
        gaDAO = new Ga_DAO();
        tauDao = new Tau_DAO();

        nhatKyAuditBus = new NhatKyAudit_BUS();
    }

    @Override
    public Map<String, String> layTrangThaiCacGheTrongToaCuaChuyen(String gaDiID, String gaDenID, String chuyenID,
                                                                   String toaID) {
        List<Ghe> gheList = gheDAO.getGheByGaDiGaDenChuyenToa(gaDiID, gaDenID, chuyenID, toaID);

        Map<String, String> result = new HashMap<>();
        if (gheList != null) {
            for (Ghe ghe : gheList) {
                result.put(ghe.getGheID(), ghe.toString());
            }
        }

        return result;
    }

    @Override
    public List<ChuyenDTO> timChuyenTheoGaDiGaDenNgayDi(String gaDi, String gaDen, LocalDate ngayDi) {
        List<ChuyenDTO> dsChuyen = chuyenDAO.getChuyenByGaDiGaDenNgayDi(gaDi, gaDen, ngayDi)
                .stream().map(ChuyenMapper.INSTANCE::toDTO).collect(Collectors.toList());
        dsChuyen.removeIf(
                c -> !LocalDateTime.now().plusHours(1).isBefore(LocalDateTime.of(c.getNgayDi(), c.getGioDi())));
        return dsChuyen;
    }

    // Gợi ý ga đi (tên)
    @Override
    public List<GaDTO> goiYGaDi(String prefix, int limit) {
        return gaDAO.searchGaByPrefix(prefix, limit).stream().map(GaMapper.INSTANCE::toDTO).toList();

    }

    // Gợi ý ga đến dựa trên ga đi đã chọn
    @Override
    public List<GaDTO> goiYGaDenTheoGaDi(String gaDiID, String prefixGaDen, int limit) {
        return gaDAO.searchGaDenKhaThiByGaDi(gaDiID, prefixGaDen, limit).stream().map(GaMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public GaDTO timGaTheoTenGa(String tenGa) {
        return GaMapper.INSTANCE.toDTO(gaDAO.getGaByTenGa(tenGa));
    }

    @Override
    public List<ToaDTO> layCacToaTheoChuyen(String chuyenID) {
        return toaDAO.getToaByChuyenID(chuyenID).stream().map(ToaMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public List<GheDTO> layCacGheTrongToaTrenChuyen(String gaDiID, String gaDenID, String chuyenID, String toaID) {
        return gheDAO.getGheByGaDiGaDenChuyenToa(gaDiID, gaDenID, chuyenID, toaID).stream().map(GheMapper.INSTANCE::toDTO).toList();
    }

    @Override
    public int layGiaGheTheoPhanDoan(String chuyenID, String gaDiID, String gaDenID, String loaiTauID,
                                     String hangToaID) {
        return gheDAO.calcGia(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);
    }

    @Override
    public double layKhuyenMaiTheoGhe(String tuyenID, String loaiTauID, String hangToaID, String loaiDoiTuongID,
                                      LocalDate ngayDi, double giaGhe) {
        return 0;
    }

    @Override
    public List<Chuyen> layDanhSachChuyen() {
        return chuyenDAO.getAllChuyen();
    }

    @Override
    public List<ChuyenGaDTO> layChiTietHanhTrinh(String maChuyen) {
        if (maChuyen == null || maChuyen.isEmpty()) {
            return new ArrayList<>();
        }

        List<ChuyenGa> dsHanhTrinhEntity = chuyenGaDao.getChiTietHanhTrinh(maChuyen);

        if (dsHanhTrinhEntity == null || dsHanhTrinhEntity.isEmpty()) {
            return new ArrayList<>();
        }

        return dsHanhTrinhEntity.stream()
                .map(ChuyenGaMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChuyenDTO layChuyenTheoMa(String maChuyen) {
        if (maChuyen == null || maChuyen.isEmpty()) {
            return null;
        }
        Chuyen chuyenEntity = chuyenDAO.layChuyenTheoMa(maChuyen);
        if (chuyenEntity == null) {
            return null;
        }

        return ChuyenMapper.INSTANCE.toDTO(chuyenEntity);
    }

    @Override
    public List<ChuyenDTO> timKiemChuyen(String maChuyen, String gaDi, String gaDen, String tenTau, LocalDate ngayDi) {
        List<Chuyen> dsChuyen;

        if (maChuyen.isEmpty() && gaDi.isEmpty() && gaDen.isEmpty() && tenTau.isEmpty() && ngayDi == null) {
            dsChuyen = chuyenDAO.getAllChuyen();
        } else {
            dsChuyen = chuyenDAO.timKiemChuyen(maChuyen, gaDi, gaDen, tenTau, ngayDi);
        }

        return dsChuyen.stream()
                .map(ChuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getListMaChuyen() {
        return chuyenDAO.getAllMaChuyenID();
    }

    @Override
    public List<String> getListTenGa() {
        return chuyenDAO.getAllTenGa();
    }

    @Override
    public List<String> getListTenTau() {
        return chuyenDAO.getAllTenTau();
    }

    @Override
    public List<String> getAllTauID() {
        return chuyenDAO.getAllTauID();
    }

    @Override
    public List<String> getAllTuyenID() {
        return chuyenDAO.getAllTuyenID();
    }

    @Override
    public String themChuyen(ChuyenDTO chuyenDTO, List<ChuyenGaDTO> lichTrinhDTO, NhanVienDTO nvDTO) {
        if (chuyenDAO.existsById(chuyenDTO.getId())) {
            return "Đã tồn tại chuyến " + chuyenDTO.getId();
        }

        Chuyen chuyenEntity = ChuyenMapper.INSTANCE.toEntity(chuyenDTO);
        List<ChuyenGa> lichTrinhEntities = lichTrinhDTO.stream()
                .map(ChuyenGaMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());

        boolean ok = chuyenDAO.themChuyenMoi(chuyenEntity, lichTrinhEntities);
        if (!ok) return "Không thể thêm chuyến (lỗi lưu dữ liệu)";

        String chiTietLog = String.format("%s %s Thêm Chuyến mới: %s (Tàu: %s, Ngày đi: %s, Giờ đi: %s)",
                nvDTO.getVaiTroNhanVienID() != null ? nvDTO.getVaiTroNhanVienID() : "",
                nvDTO.getHoTen(), chuyenDTO.getId(), chuyenDTO.getTauID(),
                chuyenDTO.getNgayDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                chuyenDTO.getGioDi().toString());

        ghiLogAudit(chuyenDTO.getId(), nvDTO, NhatKyAudit.THEM, chiTietLog);
        return null;
    }

    @Override
    public Map<String, String> getMapTenGaToID() {
        return chuyenDAO.getMapTenGaToID();
    }

    @Override
    public boolean capNhatChuyen(ChuyenDTO chuyenDTO, List<ChuyenGaDTO> lichTrinhDTO, NhanVienDTO nhanVienThucHienDTO) {
        String chuyenID = chuyenDTO.getId();
        Chuyen chuyenCu = chuyenDAO.layChuyenTheoMa(chuyenID);
        List<ChuyenGa> lichTrinhCu = chuyenGaDao.getChiTietHanhTrinh(chuyenID);

        Chuyen chuyenMoiEntity = ChuyenMapper.INSTANCE.toEntity(chuyenDTO);
        List<ChuyenGa> lichTrinhMoiEntities = lichTrinhDTO.stream()
                .map(ChuyenGaMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());

        String strLichTrinhCu = layChuoiLichTrinh(lichTrinhCu);
        String strLichTrinhMoi = layChuoiLichTrinh(lichTrinhMoiEntities);

        boolean ketQua = chuyenDAO.capNhatChuyen(chuyenMoiEntity, lichTrinhMoiEntities);

        if (ketQua) {
            List<String> cacThayDoi = new ArrayList<>();

            if (chuyenCu != null && !chuyenCu.getTau().getTauID().equals(chuyenDTO.getTauID())) {
                cacThayDoi.add(String.format("Cập nhật tàu (Cũ: %s -> Mới: %s)",
                        chuyenCu.getTau().getTauID(), chuyenDTO.getTauID()));
            }

            boolean doiNgay = !chuyenCu.getNgayDi().equals(chuyenDTO.getNgayDi());
            boolean doiGio = !chuyenCu.getGioDi().equals(chuyenDTO.getGioDi());

            if (doiNgay || doiGio) {
                String thoiGianCu = chuyenCu.getNgayDi() + " " + chuyenCu.getGioDi();
                String thoiGianMoi = chuyenDTO.getNgayDi() + " " + chuyenDTO.getGioDi();
                cacThayDoi.add(String.format("Cập nhật Thời gian đi (Cũ: %s -> Mới: %s)", thoiGianCu, thoiGianMoi));
            }

            if (!strLichTrinhCu.equals(strLichTrinhMoi)) {
                cacThayDoi.add(String.format("Cập nhật thông tin chặng (Cũ: [%s] -> Mới: [%s])",
                        strLichTrinhCu, strLichTrinhMoi));
            } else if (lichTrinhCu.size() == lichTrinhMoiEntities.size()) {
                if (kiemTraThayDoiGioChiTiet(lichTrinhCu, lichTrinhMoiEntities)) {
                    cacThayDoi.add("Điều chỉnh giờ đến/đi tại các ga trung gian");
                }
            }

            String tenChucVu = (nhanVienThucHienDTO.getVaiTroNhanVienID() != null)
                    ? nhanVienThucHienDTO.getVaiTroNhanVienID()
                    : "";
            StringBuilder sbLog = new StringBuilder();
            sbLog.append(String.format("%s %s Cập nhật chuyến %s",
                    tenChucVu, nhanVienThucHienDTO.getHoTen(), chuyenID));

            if (!cacThayDoi.isEmpty()) {
                sbLog.append(" : ").append(String.join(", ", cacThayDoi));
            } else {
                sbLog.append(" : Không có thông tin thay đổi");
            }

            ghiLogAudit(chuyenID, nhanVienThucHienDTO, NhatKyAudit.SUA, sbLog.toString());
        }
        return ketQua;
    }

    @Override
    public List<GaDTO> layDsGaCuaTuyen(String tuyenID) {
        return chuyenDAO.getDsGaTheoTuyen(tuyenID).stream()
                .map(GaMapper.INSTANCE::toDTO)
                .toList();
    }

    @Override
    public int layTocDoTau(String tauID) {
        return chuyenDAO.getTocDoTau(tauID);
    }

    @Override
    public List<String> getListTauHoatDongFormatted() {
        List<String[]> rawData = chuyenDAO.getTauHoatDong();
        List<String> result = new ArrayList<>();
        for (String[] row : rawData) {
            result.add(row[0] + " (" + row[1] + ")");
        }
        return result;
    }

    @Override
    public List<GaDTO> layDsGaChoLichTrinh(String tuyenID, String loaiTau) {
        List<Ga> allGa = chuyenDAO.getDsGaVaTrangThaiLonTheoTuyen(tuyenID);
        List<Ga> filteredEntities = new ArrayList<>();

        if (loaiTau.toUpperCase().contains("DU_LICH") || loaiTau.toUpperCase().contains("TAU_DU_LICH")) {
            filteredEntities = allGa;
        } else if (loaiTau.toUpperCase().contains("NHANH") || loaiTau.toUpperCase().contains("TAU_NHANH")) {
            if (allGa.isEmpty()) {
                return new ArrayList<>();
            }

            filteredEntities.add(allGa.get(0));

            for (int i = 1; i < allGa.size() - 1; i++) {
                if (allGa.get(i).isGaLon()) {
                    filteredEntities.add(allGa.get(i));
                }
            }

            if (allGa.size() > 1) {
                filteredEntities.add(allGa.get(allGa.size() - 1));
            }
        } else {
            filteredEntities = allGa;
        }

        return filteredEntities.stream()
                .map(GaMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String themChuyenBatch(List<ChuyenDTO> dsChuyenDTO, List<List<ChuyenGaDTO>> dsLichTrinhDTO, NhanVienDTO nvDTO) {
        List<Chuyen> dsChuyenEntities = dsChuyenDTO.stream()
                .map(ChuyenMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());

        List<List<ChuyenGa>> dsLichTrinhEntities = dsLichTrinhDTO.stream()
                .map(list -> list.stream()
                        .map(ChuyenGaMapper.INSTANCE::toEntity)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        boolean ok = chuyenDAO.themChuyenBatch(dsChuyenEntities, dsLichTrinhEntities);

        if (!ok) {
            return "Lỗi hệ thống khi lưu hàng loạt dữ liệu!";
        }

        String chiTietLog = String.format("%s đã tạo hàng loạt %d chuyến theo chu kỳ.",
                nvDTO.getHoTen(), dsChuyenDTO.size());

        ghiLogAudit("BATCH_GEN", nvDTO, NhatKyAudit.THEM, chiTietLog);

        return null;
    }

    @Override
    public TrangThaiTau layTrangThaiTauTheoID(String tauID) {
        return tauDao.layTrangThaiTau(tauID);
    }

    @Override
    public String capNhatChuyenBatch(List<ChuyenDTO> dsChuyenDTO, List<List<ChuyenGaDTO>> dsLichTrinhDTO, NhanVienDTO nvDTO) {
        if (dsChuyenDTO == null || dsChuyenDTO.isEmpty()) {
            return "Danh sách cập nhật trống!";
        }

        List<Chuyen> dsChuyenEntities = dsChuyenDTO.stream()
                .map(ChuyenMapper.INSTANCE::toEntity)
                .collect(Collectors.toList());

        List<List<ChuyenGa>> dsLichTrinhEntities = dsLichTrinhDTO.stream()
                .map(list -> list.stream()
                        .map(ChuyenGaMapper.INSTANCE::toEntity)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        boolean ok = chuyenDAO.capNhatChuyenBatch(dsChuyenEntities, dsLichTrinhEntities);

        if (ok) {
            String chiTiet = String.format("%s cập nhật chu kỳ cho tàu %s, tổng số %d chuyến.",
                    nvDTO.getHoTen(),
                    dsChuyenDTO.get(0).getTauID(),
                    dsChuyenDTO.size());

            ghiLogAudit("BATCH_UPDATE", nvDTO, NhatKyAudit.SUA, chiTiet);

            return null;
        }

        return "Lỗi hệ thống khi cập nhật chu kỳ!";
    }

    @Override
    public List<ChuyenDTO> layDanhSachChuyenTheoNgay(LocalDate ngay) {
        List<Chuyen> dsChuyenEntities = chuyenDAO.getChuyenTheoNgay(ngay);

        return dsChuyenEntities.stream()
                .map(ChuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * @param chuyenID
     * @param gaDiID
     * @param gaDenID
     * @return
     */
    @Override
    public int[] layThongKeCho(String chuyenID, String gaDiID, String gaDenID) {
        // TODO Auto-generated method stub
        return chuyenDAO.getThongKeCho(chuyenID, gaDiID, gaDenID);
    }
}