/*
 * @(#) CaLamDAO.java  1.0  [4:26 PM] 5/1/2026
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

import dao.ICaLamDAO;
import entity.CaLam;

public class CaLamDAO extends AbstractGenericDAO<CaLam, String> implements ICaLamDAO {
    public CaLamDAO(){
        super(CaLam.class);
    }

}