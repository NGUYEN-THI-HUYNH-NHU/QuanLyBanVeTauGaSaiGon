/*
 * @(#) DieuKienKhuyenMaiDAO.java  1.0  [4:12 PM] 5/1/2026
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

import dao.IDieuKienKhuyenMaiDAO;
import entity.DieuKienKhuyenMai;

public class DieuKienKhuyenMaiDAO extends AbstractGenericDAO<DieuKienKhuyenMai, String> implements IDieuKienKhuyenMaiDAO {
    public DieuKienKhuyenMaiDAO(){
        super(DieuKienKhuyenMai.class);
    }

}