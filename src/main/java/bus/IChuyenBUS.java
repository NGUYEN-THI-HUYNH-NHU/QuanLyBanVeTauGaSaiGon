package bus;/*
 * @ (#) IChuyenBUS.java   1.0     05/05/2026
package bus;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import dto.*;
import entity.Chuyen;
import entity.ChuyenGa;
import entity.type.NhatKyAudit;
import entity.type.TrangThaiTau;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface IChuyenBUS {
    Map<String, String> layTrangThaiCacGheTrongToaCuaChuyen(String gaDiID, String gaDenID, String chuyenID,
                                                            String toaID);

    List<ChuyenDTO> timChuyenTheoGaDiGaDenNgayDi(String gaDi, String gaDen, LocalDate ngayDi);

    // Gợi ý ga đi (tên)
    List<GaDTO> goiYGaDi(String prefix, int limit);

    // Gợi ý ga đến dựa trên ga đi đã chọn
    List<GaDTO> goiYGaDenTheoGaDi(String gaDiID, String prefixGaDen, int limit);

    GaDTO timGaTheoTenGa(String tenGa);

    List<ToaDTO> layCacToaTheoChuyen(String chuyenID);

    List<GheDTO> layCacGheTrongToaTrenChuyen(String gaDiID, String gaDenID, String chuyenID, String toaID);

    int layGiaGheTheoPhanDoan(String chuyenID, String gaDiID, String gaDenID, String loaiTauID,
                              String hangToaID);

    double layKhuyenMaiTheoGhe(String tuyenID, String loaiTauID, String hangToaID, String loaiDoiTuongID,
                               LocalDate ngayDi, double giaGhe);

    List<Chuyen> layDanhSachChuyen();

    List<ChuyenGaDTO> layChiTietHanhTrinh(String maChuyen);

    ChuyenDTO layChuyenTheoMa(String maChuyen);

    List<ChuyenDTO> timKiemChuyen(String maChuyen, String gaDi, String gaDen, String tenTau, LocalDate ngayDi);

    List<String> getListMaChuyen();

    List<String> getListTenGa();

    List<String> getListTenTau();

    List<String> getAllTauID();

    List<String> getAllTuyenID();

    String themChuyen(ChuyenDTO chuyenDTO, List<ChuyenGaDTO> lichTrinhDTO, NhanVienDTO nvDTO);

    Map<String, String> getMapTenGaToID();

    boolean capNhatChuyen(ChuyenDTO chuyenDTO, List<ChuyenGaDTO> lichTrinhDTO, NhanVienDTO nhanVienThucHienDTO);

    default String layChuoiLichTrinh(List<ChuyenGa> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        list.sort((o1, o2) -> Integer.compare(o1.getThuTu(), o2.getThuTu()));

        return list.stream().map(cg -> cg.getGa().getTenGa()).collect(Collectors.joining(" -> "));
    }

    default boolean kiemTraThayDoiGioChiTiet(List<ChuyenGa> cu, List<ChuyenGa> moi) {
        for (int i = 0; i < cu.size(); i++) {
            ChuyenGa c = cu.get(i);
            ChuyenGa m = moi.get(i);
            // So sánh giờ đến hoặc giờ đi
            if ((c.getGioDen() != null && !c.getGioDen().equals(m.getGioDen()))
                    || (c.getGioDi() != null && !c.getGioDi().equals(m.getGioDi()))) {
                return true;
            }
        }
        return false;
    }

    default void ghiLogAudit(String doiTuongID, NhanVienDTO nvDTO, NhatKyAudit loaiThaoTac, String chiTiet) {
        if (nvDTO == null) return;
        try {
            NhatKyAudit_BUS nhatKyAuditBus = new NhatKyAudit_BUS();
            String maLog = nhatKyAuditBus.taoMaNhatKyAuditMoi();
            entity.NhatKyAudit log = new entity.NhatKyAudit(
                    maLog,
                    doiTuongID,
                    nvDTO.getId(),
                    LocalDateTime.now(),
                    loaiThaoTac,
                    chiTiet,
                    "Chuyen"
            );
            nhatKyAuditBus.ghiNhatKyAudit(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<GaDTO> layDsGaCuaTuyen(String tuyenID);

    int layTocDoTau(String tauID);

    List<String> getListTauHoatDongFormatted();

    List<GaDTO> layDsGaChoLichTrinh(String tuyenID, String loaiTau);

    String themChuyenBatch(List<ChuyenDTO> dsChuyenDTO, List<List<ChuyenGaDTO>> dsLichTrinhDTO, NhanVienDTO nvDTO);

    TrangThaiTau layTrangThaiTauTheoID(String tauID);

    String capNhatChuyenBatch(List<ChuyenDTO> dsChuyenDTO, List<List<ChuyenGaDTO>> dsLichTrinhDTO, NhanVienDTO nvDTO);

    List<ChuyenDTO> layDanhSachChuyenTheoNgay(LocalDate ngay);

    int[] layThongKeCho(String chuyenID, String gaDiID, String gaDenID);
}
