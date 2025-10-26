package bus;
/*
 * @(#) VeSession_BUS.java  1.0  [12:57:04 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */
import gui.application.form.banVe.VeSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple in-memory ticket cart. For production you may persist to DB or session.
 */
public class VeSession_BUS {
    private static VeSession_BUS instance;
    private final List<VeSession> dsVeSession;

    private VeSession_BUS() {
        dsVeSession = new ArrayList<>();
    }

    public static synchronized VeSession_BUS getInstance() {
        if (instance == null)
        	instance = new VeSession_BUS();
        return instance;
    }

    public synchronized void addVeSession(VeSession v) {
        dsVeSession.add(v);
    }

    public synchronized void removeVeSession(VeSession v) {
        dsVeSession.removeIf(x -> x.equals(v));
    }

    public synchronized List<VeSession> getAllVeSessions() {
        return new ArrayList<>(dsVeSession);
    }

    public synchronized void clearAll() {
    	dsVeSession.clear();
    }
}
