/*
 * @(#) LoaiDoiTuongDAO.java  1.0  [3:53 PM] 5/1/2026
 *
 * Copyright (c) 2026 IUH. All rights reserved.
 */

/*
 * @description
 * @author: Yen
 * @date: 5/1/2026
 * @version: 1.0
 */

package dao.impl;

import dao.ILoaiDoiTuongDAO;
import entity.LoaiDoiTuong;

public class LoaiDoiTuongDAO extends AbstractGenericDAO<LoaiDoiTuong, String> implements ILoaiDoiTuongDAO {
    public LoaiDoiTuongDAO(){
        super(LoaiDoiTuong.class);
    }

}