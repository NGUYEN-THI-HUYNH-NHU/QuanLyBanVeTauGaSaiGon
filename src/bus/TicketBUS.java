package bus;
/*
 * @(#) TicketBUS.java  1.0  [12:57:04 PM] Sep 29, 2025
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
public class TicketBUS {
    private static TicketBUS instance;
    private final List<VeSession> tickets;

    private TicketBUS() {
        tickets = new ArrayList<>();
    }

    public static synchronized TicketBUS getInstance() {
        if (instance == null)
        	instance = new TicketBUS();
        return instance;
    }

    public synchronized void addTicket(VeSession v) {
        tickets.add(v);
    }

    public synchronized void removeTicket(VeSession v) {
        tickets.removeIf(x -> x.equals(v));
    }

    public synchronized List<VeSession> getAllTickets() {
        return new ArrayList<>(tickets);
    }

    public synchronized void clearAll() {
    	tickets.clear();
    }
}
