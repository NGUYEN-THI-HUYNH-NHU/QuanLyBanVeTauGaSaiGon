package bus;
/*
 * @(#) PhieuGiuChoChiTiet_BUS.java  1.0  [3:11:31 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

import dao.impl.PhieuGiuChoChiTietDAO;
import dao.impl.PhieuGiuChoDAO;
import dto.VeDTO;
import entity.type.TrangThaiPhieuGiuCho;
import mapper.VeMapper;

import java.util.List;

public class PhieuGiuCho_BUS {
    private final PhieuGiuChoDAO pgcDAO = new PhieuGiuChoDAO();
    private final PhieuGiuChoChiTietDAO pgcctDAO = new PhieuGiuChoChiTietDAO();

    public void huyCacPhieuGiuChoChiTiet(List<VeDTO> listVe, TrangThaiPhieuGiuCho trangThai) {
        for (VeDTO ve : listVe) {
            pgcctDAO.updateTrangThaiPhieuGiuChoChiTietByVe(VeMapper.INSTANCE.toEntity(ve), trangThai);
        }
    }

    public void donDepPhieuHetHan(int expiryMinutes) {
        pgcctDAO.cleanUpExpiredPhieuGiuChoChiTiet(expiryMinutes);
        pgcDAO.cleanUpExpiredPhieuGiuCho(expiryMinutes);
    }
}
