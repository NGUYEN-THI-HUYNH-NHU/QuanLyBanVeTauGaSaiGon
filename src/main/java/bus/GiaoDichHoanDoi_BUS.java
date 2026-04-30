package bus;

/*
 * @(#) GiaoDichHoanDoi_BUS.java  1.0  [2:20:33 PM] Nov 16, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 16, 2025
 * @version: 1.0
 */

import dao.impl.GiaoDichHoanDoiDAO;
import entity.GiaoDichHoanDoi;
import entity.HoaDon;
import entity.NhanVien;
import entity.type.LoaiGiaoDich;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import gui.application.form.doiVe.VeDoiRow;
import gui.application.form.hoanVe.VeHoanRow;
import mapper.VeMapper;

import java.util.ArrayList;
import java.util.List;

public class GiaoDichHoanDoi_BUS {
    private final GiaoDichHoanDoiDAO giaoDichHoanDoiDAO = new GiaoDichHoanDoiDAO();

    /**
     * @param hoaDon
     * @param nhanVien
     * @param listVeHoanRow
     * @return
     */
    public List<GiaoDichHoanDoi> taoCacGiaoDichHoanVe(HoaDon hoaDon, NhanVien nhanVien, List<VeHoanRow> listVeHoanRow) {
        List<GiaoDichHoanDoi> dsGiaoDichHoanDoi = new ArrayList<GiaoDichHoanDoi>();
        for (VeHoanRow r : listVeHoanRow) {
            String gdhdID = "GDHV-" + r.getVe().getVeID().substring(3);
            GiaoDichHoanDoi gdhd = new GiaoDichHoanDoi(gdhdID, nhanVien, hoaDon, r.getVe(), LoaiGiaoDich.HOAN_VE,
                    r.getLyDo(), hoaDon.getThoiDiemTao(), r.getLePhiHoanVe(), r.getVe().getGia() - r.getLePhiHoanVe());
            dsGiaoDichHoanDoi.add(gdhd);
        }
        return dsGiaoDichHoanDoi;
    }

    /**
     * @param exchangeSession
     * @return
     */
    public List<GiaoDichHoanDoi> taoCacGiaoDichDoiVe(ExchangeSession exchangeSession) {
        List<VeDoiRow> listVeDoi = exchangeSession.getListVeCuCanDoi();
        List<VeSession> listVeMoi = exchangeSession.getListVeMoiDangChon();
        NhanVien nhanVien = exchangeSession.getNhanVien();
        HoaDon hoaDon = exchangeSession.getHoaDon();
        List<GiaoDichHoanDoi> dsGiaoDichHoanDoi = new ArrayList<GiaoDichHoanDoi>();
        int soLuongVeDoi = listVeDoi.size();

        for (int i = 0; i < soLuongVeDoi; i++) {
            String gdhdID = "GDDV-" + listVeDoi.get(i).getVe().getVeID().substring(3);
            GiaoDichHoanDoi gdhd = new GiaoDichHoanDoi(gdhdID, nhanVien, hoaDon, listVeDoi.get(i).getVe(),
                    VeMapper.INSTANCE.toEntity(listVeMoi.get(i).getVe()), LoaiGiaoDich.DOI_VE, listVeDoi.get(i).getLyDo(), hoaDon.getThoiDiemTao(),
                    listVeDoi.get(i).getLePhiDoiVe(), listVeMoi.get(i).getVe().getGia()
                    + listVeDoi.get(i).getLePhiDoiVe() - listVeDoi.get(i).getVe().getGia());

            dsGiaoDichHoanDoi.add(gdhd);
        }
        return dsGiaoDichHoanDoi;
    }

    /**
     * @param dsGdhd
     */
    public void themCacGiaoDichHoanDoi(List<GiaoDichHoanDoi> dsGdhd) throws Exception {
        for (GiaoDichHoanDoi gd : dsGdhd) {
            giaoDichHoanDoiDAO.insertGiaoDichHoanDoi(gd);
        }
    }
}
