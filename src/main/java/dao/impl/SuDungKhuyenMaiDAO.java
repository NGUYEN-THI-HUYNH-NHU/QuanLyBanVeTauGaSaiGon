/*
 * @(#) SuDungKhuyenMaiDAO.java  1.0  [4:19 PM] 5/1/2026
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

import dao.ISuDungKhuyenMaiDAO;
import entity.SuDungKhuyenMai;

public class SuDungKhuyenMaiDAO extends AbstractGenericDAO<SuDungKhuyenMai, String> implements ISuDungKhuyenMaiDAO {
    public SuDungKhuyenMaiDAO() {
        super(SuDungKhuyenMai.class);
    }
}