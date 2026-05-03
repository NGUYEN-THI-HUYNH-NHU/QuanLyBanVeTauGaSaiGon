/*
 * @(#) VaiTroTaiKhoanDAO.java  1.0  [4:21 PM] 5/1/2026
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

import dao.IVaiTroTaiKhoanDAO;
import entity.VaiTroTaiKhoan;

public class VaiTroTaiKhoanDAO extends AbstractGenericDAO<VaiTroTaiKhoan, String> implements IVaiTroTaiKhoanDAO {
    public VaiTroTaiKhoanDAO(){
        super(VaiTroTaiKhoan.class);
    }
}