package bus;
/*
 * @(#) PhieuDungPhongVIP_BUS.java  1.0  [8:57:21 PM] Nov 7, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 7, 2025
 * @version: 1.0
 */

import dao.impl.PhieuDungPhongVIPDAO;
import entity.DichVuPhongChoVIP;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPDPVIP;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;

import java.util.ArrayList;
import java.util.List;

public class PhieuDungPhongVIP_BUS {
    private final PhieuDungPhongVIPDAO phieuDungPhongVIPDAO = new PhieuDungPhongVIPDAO();

    public List<PhieuDungPhongVIP> taoCacPhieuDungPhongChoVIP(List<VeSession> listVeSession) {
        List<PhieuDungPhongVIP> dsPhieu = new ArrayList<PhieuDungPhongVIP>();
        for (VeSession v : listVeSession) {
            if (v.getPhiPhieuDungPhongChoVIP() != 0) {
                String phieuID = "PVIP-" + v.getVe().getVeID().substring(3);
                PhieuDungPhongVIP phieu = new PhieuDungPhongVIP(phieuID, new DichVuPhongChoVIP("DVVIP001"), v.getVe(),
                        TrangThaiPDPVIP.CHUA_DUNG);
                v.setPhieuDungPhongVIP(phieu);
                dsPhieu.add(phieu);
            }
        }
        return dsPhieu;
    }

    /**
     * @param exchangeSession
     * @return
     */
    public List<PhieuDungPhongVIP> taoCacPhieuDungPhongChoVIP(ExchangeSession exchangeSession) {
        List<PhieuDungPhongVIP> dsPhieu = new ArrayList<PhieuDungPhongVIP>();
        List<VeSession> dsVe = exchangeSession.getListVeMoiDangChon();
        for (VeSession v : dsVe) {
            if (v.getPhiPhieuDungPhongChoVIP() != 0) {
                String phieuID = "PVIP-" + v.getVe().getVeID().substring(3);
                PhieuDungPhongVIP phieu = new PhieuDungPhongVIP(phieuID, new DichVuPhongChoVIP("DVVIP001"), v.getVe(),
                        TrangThaiPDPVIP.CHUA_DUNG);
                v.setPhieuDungPhongVIP(phieu);
                dsPhieu.add(phieu);
            }
        }
        return dsPhieu;
    }

    public boolean themCacPhieuDungPhongChoVIP(List<PhieuDungPhongVIP> dsPhieuDungPhongVIP)
            throws Exception {
        if (dsPhieuDungPhongVIP != null) {
            for (PhieuDungPhongVIP phieu : dsPhieuDungPhongVIP) {
                phieuDungPhongVIPDAO.insertPhieuDungPhongVIP(phieu);
            }
            return true;
        }
        return false;
    }

    /**
     * @param listVe
     */
    public void capNhatPhieuDungPhongChoVIP(List<Ve> listVe, TrangThaiPDPVIP trangThai) {
        for (Ve ve : listVe) {
            PhieuDungPhongVIP phieu = phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(ve.getVeID());
            if (phieu != null) {
                phieuDungPhongVIPDAO.updateTrangThaiPhieuDungPhongVIP(phieu.getPhieuDungPhongVIPID(),
                        trangThai);
            }
        }
    }

    /**
     * @param danhSachVe
     * @return
     */
    public List<PhieuDungPhongVIP> timCacPhieuTheoVe(List<Ve> danhSachVe) {
        List<PhieuDungPhongVIP> listPhieu = new ArrayList<PhieuDungPhongVIP>();
        for (Ve ve : danhSachVe) {
            // listPhieu[i] = null nghĩa là danhSachVe[i] không sử dụng phiếu
            listPhieu.add(phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(ve.getVeID()));
        }
        return listPhieu;
    }
}
