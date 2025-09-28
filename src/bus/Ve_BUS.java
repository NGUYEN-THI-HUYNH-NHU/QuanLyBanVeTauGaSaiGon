package bus;
/*
 * @(#) Ve_Bus.java  1.0  [10:10:54 AM] Sep 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import entity.Ve;
import gui.application.form.banVe.SessionVe;
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 27, 2025
 * @version: 1.0
 */

public class Ve_BUS {
    // for demo we keep an in-memory list. Replace with DAO that accesses DB.
    private final List<Ve> store = new ArrayList<>();

    public Ve_BUS() {
        // seed demo data
        for (int i = 1; i <= 8; i++) {
            Ve t = new Ve(UUID.randomUUID().toString());
            store.add(t);
        }
    }

    public List<Ve> getTickets(String q, String status) {
        List<Ve> out = new ArrayList<>();
        for (Ve t : store) {
            boolean match = true;
            if (q != null && !q.isEmpty()) {
                String lower = q.toLowerCase();
                match &= (t.getVeID().toLowerCase().contains(lower) || t.getHanhKhach().getHoTen().toLowerCase().contains(lower));
            }
           
            if (match) out.add(t);
        }
        return out;
    }

    public Ve sellTicket(SessionVe session) {
        // create a ticket using session info (demo uses random)
        Ve t = new Ve(UUID.randomUUID().toString());
        store.add(0, t); // newest first
        return t;
    }
}