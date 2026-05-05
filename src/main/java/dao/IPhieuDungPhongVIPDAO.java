package dao;

import entity.PhieuDungPhongVIP;
import entity.type.TrangThaiPDPVIP;

public interface IPhieuDungPhongVIPDAO {

    PhieuDungPhongVIP getPhieuDungPhongVIPByVeID(String veID);


    boolean updateTrangThaiPhieuDungPhongVIP(String phieuDungPhongChoVIPID,
                                             TrangThaiPDPVIP trangThai);
}
