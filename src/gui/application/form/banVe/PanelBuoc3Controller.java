package gui.application.form.banVe;
/*
 * @(#) PanelBuoc3Controller.java  1.0  [8:06:26 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

import java.util.List;

import entity.Chuyen;

public class PanelBuoc3Controller {
	private InfoCompleteListener infoCompleteListener;

	
	public interface InfoCompleteListener {
	    void onSearchSuccess(List<Chuyen> results, SearchCriteria criteria);
	    void onSearchFailure();
	}

	public void setSearchListener(InfoCompleteListener listener) {
        this.infoCompleteListener = listener;
    }
}