package dao;

import entity.Ve;
import entity.type.TrangThaiPhieuGiuCho;

public interface IPhieuGiuChoChiTietDAO {
    boolean checkConflict(String chuyenID, String tenGaDi, String tenGaDen, int soToa,
                          int soGhe);

    boolean updateTrangThaiPhieuGiuChoChiTietByPhieuGiuChoID(String phieuGiuChoID,
                                                             String newTrangThai) throws Exception;

    int cleanUpExpiredPhieuGiuChoChiTiet(int expiryMinutes);

    boolean deletePhieuGiuChoChiTietByPgcID(String phieuGiuChoID);

    boolean updateTrangThaiPhieuGiuChoChiTietByVe(Ve ve, TrangThaiPhieuGiuCho trangThai);
}
