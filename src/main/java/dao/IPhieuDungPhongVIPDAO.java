package dao;

import entity.PhieuDungPhongVIP;
import entity.type.TrangThaiPDPVIP;

public interface IPhieuDungPhongVIPDAO {
    boolean insertPhieuDungPhongVIP(PhieuDungPhongVIP phieuDungPhongChoVIP);

    PhieuDungPhongVIP getPhieuDungPhongVIPByVeID(String veID);

    PhieuDungPhongVIP getPhieuDungPhongVIPByID(String phieuDungPhongChoVIPID);

    boolean updateTrangThaiPhieuDungPhongVIP(String phieuDungPhongChoVIPID,
                                             TrangThaiPDPVIP trangThai);
}
