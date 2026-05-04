package bus;
/*
 * @(#) DonDatCho_BUS.java  1.0  [2:11:25 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dao.impl.DonDatChoDAO;
import dto.DonDatChoDTO;
import mapper.DonDatChoMapper;

import java.util.Date;
import java.util.List;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

public class DonDatCho_BUS {
    private final DonDatChoDAO donDatChoDAO = new DonDatChoDAO();

    /**
     * @param donDatChoID
     * @param soGiayTo
     * @return
     */
    public DonDatChoDTO timDonDatChoTheoIDVaSoGiayTo(String donDatChoID, String soGiayTo) {
        return DonDatChoMapper.INSTANCE.toDTO(donDatChoDAO.findDonDatChoByIDVaSoGiayTo(donDatChoID, soGiayTo));
    }

    public List<DonDatChoDTO> layDonDatChoTheoKeyword(String keyword, String type, int page, int limit) {
        return donDatChoDAO.searchDonDatChoByKeyword(keyword, type, page, limit)
                .stream().map(DonDatChoMapper.INSTANCE::toDTO).toList();

    }

    /**
     * @param tuNgay
     * @param denNgay
     * @return
     */
    public List<DonDatChoDTO> locDonDatChoTheoCacTieuChi(String tuKhoaTraCuu, String loaiTraCuu, Date tuNgay, Date denNgay, int page, int limit) {
        return donDatChoDAO.searchDonDatChoByFilter(tuKhoaTraCuu, loaiTraCuu, tuNgay, denNgay, page, limit)
                .stream().map(DonDatChoMapper.INSTANCE::toDTO).toList();
    }

    public int countAllDonDatCho() {
        return donDatChoDAO.countAll();
    }

    public List<DonDatChoDTO> getDonDatChoByPage(int page, int limit) {
        return donDatChoDAO.getDonDatChoByPage(page, limit)
                .stream().map(DonDatChoMapper.INSTANCE::toDTO).toList();
    }

    /**
     * @param keyword
     * @return
     */
    public List<String> layTop10DonDatChoID(String keyword) {
        return donDatChoDAO.getTop10DonDatChoID(keyword);
    }
    
    public int countDonDatChoByFilter(String tuKhoaTraCuu, String loaiTraCuu, Date tuNgay, Date denNgay) {
        return donDatChoDAO.countDonDatChoByFilter(tuKhoaTraCuu, loaiTraCuu, tuNgay, denNgay);
    }

    public int countDonDatChoByKeyword(String keyword, String type) {
        return donDatChoDAO.countDonDatChoByKeyword(keyword, type);
    }
}
