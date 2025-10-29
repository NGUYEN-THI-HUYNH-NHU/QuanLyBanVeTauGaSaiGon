package gui.application;
/*
 * @(#) AuthService.java  1.0  [2:12:01 PM] Oct 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.concurrent.atomic.AtomicReference;

import entity.NhanVien;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 27, 2025
 * @version: 1.0
 */
public final class AuthService {
	private static final AuthService INSTANCE = new AuthService();
	private final AtomicReference<NhanVien> current = new AtomicReference<>();
	
	private AuthService() {}
	
	public static AuthService getInstance() {
		return INSTANCE;
	}
	
	// gọi khi login thành công
	public void setCurrentUser(NhanVien nv) {
	    current.set(nv);
	}
	public NhanVien getCurrentUser() {
	    return current.get();
	}
	
	public void clear() {
	    current.set(null);
	}
	
	public boolean isAuthenticated() {
	    return current.get() != null;
	}
}
