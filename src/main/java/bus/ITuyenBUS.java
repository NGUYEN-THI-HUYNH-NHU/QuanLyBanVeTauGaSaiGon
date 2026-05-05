package bus;/*
 * @ (#) ITuyenBUS.java   1.0     05/05/2026
package bus;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import dao.ITuyenChiTietDAO;
import dto.NhanVienDTO;
import dto.TuyenChiTietDTO;
import dto.TuyenDTO;
import entity.NhatKyAudit;
import entity.Tuyen;
import entity.TuyenChiTiet;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface ITuyenBUS {
    List<TuyenDTO> getAllTuyen();

    List<TuyenDTO> getTuyenByID(String tuyenID);

    List<String> timIDTuyenChoGoiY(String input);

    List<TuyenDTO> timTuyenTheoGa(String gaDi, String gaDen);

    List<Object[]> getDuLieuBang();

    default List<Object[]> convertTuyenListToTableData(List<TuyenDTO> dsTuyen) {
        List<Object[]> dsDuLieuBang = new ArrayList<>();

        for (TuyenDTO tuyen : dsTuyen) {
            ITuyenChiTietDAO tuyenChiTietDao;
            List<TuyenChiTietDTO> dsTuyenChiTiet = getDanhSachTuyenChiTiet(tuyen.getId());

            if (dsTuyenChiTiet != null && dsTuyenChiTiet.size() >= 2) {
                TuyenChiTietDTO gaDiTCT = dsTuyenChiTiet.get(0);
                TuyenChiTietDTO gaDenTCT = dsTuyenChiTiet.get(dsTuyenChiTiet.size() - 1);
                int khoangCach = gaDenTCT.getKhoangCachTuGaXuatPhatKm();

                String gaTrungGian;
                if (dsTuyenChiTiet.size() > 2) {
                    gaTrungGian = dsTuyenChiTiet.subList(1, dsTuyenChiTiet.size() - 1).stream()
                            .map(TuyenChiTietDTO::getTenGa)
                            .collect(Collectors.joining(" -> "));
                } else {
                    gaTrungGian = "-";
                }

                String trangThaiHienThi = tuyen.isTrangThai() ? "Hoạt động" : "Không hoạt động";

                Object[] rowData = new Object[]{
                        tuyen.getId(),
                        gaDiTCT.getTenGa(),
                        gaDenTCT.getTenGa(),
                        gaTrungGian,
                        khoangCach,
                        trangThaiHienThi
                };
                dsDuLieuBang.add(rowData);
            }
        }
        return dsDuLieuBang;
    }

    List<Object[]> getDuLieuBangTheoTuyenID(String tuyenID);

    List<Object[]> getDuLieuBangTheoGa(String gaDi, String gaDen);

    String getChiTietTuyen(String tuyenID);

    List<Object[]> getDuLieuGaTrungGianChiTiet(String tuyenID);

    List<TuyenChiTietDTO> getDanhSachTuyenChiTiet(String tuyenID);

    String taoMaTuyenCoSo(String tenGaDi, String tenGaDen);

    boolean kiemTraMaTuyuenDaTonTai(String maTuyen);

    boolean themTuyen(TuyenDTO tuyenMoiDTO, List<TuyenChiTietDTO> dsTCTDTO, NhanVienDTO nvDTO);

    boolean capNhatTuyen(TuyenDTO tuyenCapNhatDTO, List<TuyenChiTietDTO> dsChiTietMoiDTO, NhanVienDTO nvDTO);

    default void ghiNhatKy(String doiTuongID, NhanVienDTO nv, entity.type.NhatKyAudit loai, String chiTiet) {
        if (nv == null) return;
        NhatKyAudit_BUS nhatKyAuditBus = new NhatKyAudit_BUS();
        String maLog = nhatKyAuditBus.taoMaNhatKyAuditMoi();
        nhatKyAuditBus.ghiNhatKyAudit(new NhatKyAudit(maLog, doiTuongID, nv.getId(), LocalDateTime.now(), loai, chiTiet, "Tuyen"));
    }

    default String layChuoiGaTrungGian(List<TuyenChiTiet> list) {
        if (list == null || list.size() <= 2) {
            return "";
        }
        return list.subList(1, list.size() - 1).stream()
                .map(tct -> tct.getGa().getTenGa())
                .collect(Collectors.joining(", "));
    }

    int tinhKhoangCachTongDijsktra(String gaID_Dau, String gaID_Cuoi);

    Map<String, Map<String, Integer>> getGraphKhoangCachChuan();

    List<TuyenDTO> layKiemTop10Tuyen(String keyword);

    TuyenDTO getTuyenTheoMa(String maTuyen);

    List<TuyenChiTietDTO> layDanhSachTuyenChiTiet(String maTuyen);

    List<String> getAllMaVaTenTuyen();

    int getKhoangCachGiuaHaiGa(String gaDiID, String gaDenID);
}
