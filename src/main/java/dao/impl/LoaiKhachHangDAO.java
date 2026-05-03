/*
 * @(#) LoaiKhachHangDAO.java  1.0  [3:51 PM] 5/1/2026
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

import dao.ILoaiKhachHangDAO;
import entity.LoaiKhachHang;

public class LoaiKhachHangDAO extends AbstractGenericDAO<LoaiKhachHang, String> implements ILoaiKhachHangDAO {

    public LoaiKhachHangDAO(){
        super(LoaiKhachHang.class);
    }
}