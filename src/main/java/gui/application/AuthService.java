package gui.application;
/*a
 * @(#) AuthService.java  1.0  [2:12:01 PM] Oct 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import dto.NhanVienDTO;

import java.util.concurrent.atomic.AtomicReference;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 27, 2025
 * @version: 1.0
 */
public final class AuthService {
    private static final AuthService INSTANCE = new AuthService();
    private final AtomicReference<NhanVienDTO> current = new AtomicReference<>();

    private AuthService() {
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }

    public NhanVienDTO getCurrentUser() {
        return current.get();
    }

    // gọi khi login thành công
    public void setCurrentUser(NhanVienDTO nv) {
        current.set(nv);
    }

    public void clear() {
        current.set(null);
    }
}
