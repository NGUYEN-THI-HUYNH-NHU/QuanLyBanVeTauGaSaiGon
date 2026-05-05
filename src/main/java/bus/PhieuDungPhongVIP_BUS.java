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
import dto.PhieuDungPhongVIPDTO;
import dto.VeDTO;
import entity.DichVuPhongChoVIP;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPDPVIP;
import gui.application.form.banVe.VeSession;
import mapper.PhieuDungPhongVIPMapper;

import java.util.ArrayList;
import java.util.List;

public class PhieuDungPhongVIP_BUS {
    private final PhieuDungPhongVIPDAO phieuDungPhongVIPDAO = new PhieuDungPhongVIPDAO();

    public List<PhieuDungPhongVIP> taoCacPhieuDungPhongChoVIP(List<VeSession> listVeSession) {
        List<PhieuDungPhongVIP> dsPhieu = new ArrayList<PhieuDungPhongVIP>();
        for (VeSession v : listVeSession) {
            if (v.getPhiPhieuDungPhongChoVIP() != 0) {
                String phieuID = "PVIP-" + v.getVe().getVeID().substring(3);
                PhieuDungPhongVIP phieu = new PhieuDungPhongVIP(phieuID, new DichVuPhongChoVIP("DVVIP001")
                        , new Ve(v.getVe().getVeID()), TrangThaiPDPVIP.CHUA_DUNG);
                v.setPhieuDungPhongVIP(PhieuDungPhongVIPMapper.INSTANCE.toDTO(phieu));
                dsPhieu.add(phieu);
            }
        }
        return dsPhieu;
    }

    public boolean themCacPhieuDungPhongChoVIP(List<PhieuDungPhongVIP> dsPhieuDungPhongVIP)
            throws Exception {
        if (dsPhieuDungPhongVIP != null) {
            for (PhieuDungPhongVIP phieu : dsPhieuDungPhongVIP) {
                phieuDungPhongVIPDAO.create(phieu);
            }
            return true;
        }
        return false;
    }

    /**
     * @param listVe
     */
    public void capNhatPhieuDungPhongChoVIP(List<VeDTO> listVe, TrangThaiPDPVIP trangThai) {
        for (VeDTO ve : listVe) {
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
    public List<PhieuDungPhongVIPDTO> timCacPhieuTheoVe(List<VeDTO> danhSachVe) {
        List<PhieuDungPhongVIPDTO> listPhieu = new ArrayList<>();
        for (VeDTO ve : danhSachVe) {
            // listPhieu[i] = null nghĩa là danhSachVe[i] không sử dụng phiếu
            listPhieu.add(PhieuDungPhongVIPMapper.INSTANCE.toDTO(phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(ve.getVeID())));
        }
        return listPhieu;
    }
}
