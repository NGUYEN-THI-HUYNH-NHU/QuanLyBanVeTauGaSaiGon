/*
 * @(#) VaiTroNhanVienDAO.java  1.0  [4:16 PM] 5/1/2026
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

import dao.IVaiTroNhanVienDAO;
import entity.VaiTroNhanVien;

public class VaiTroNhanVienDAO extends AbstractGenericDAO<VaiTroNhanVien, String> implements IVaiTroNhanVienDAO {
    public VaiTroNhanVienDAO(){
        super(VaiTroNhanVien.class);
    }

}