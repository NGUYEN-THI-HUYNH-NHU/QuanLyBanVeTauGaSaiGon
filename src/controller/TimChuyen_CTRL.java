package controller;
/*
 * @(#) TimChuyen_CTRL.java  1.0  [12:41:58 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import bus.Chuyen_BUS;
import dao.Ga_DAO;
import gui.application.form.banVe.PanelBuoc1;

import java.util.List;

public class TimChuyen_CTRL {
    private PanelBuoc1 view;
    private Ga_DAO gaDAO;
    private Chuyen_BUS chuyen_BUS;

    public TimChuyen_CTRL(PanelBuoc1 view) {
        this.view = view;
        this.gaDAO = new Ga_DAO();
        this.chuyen_BUS = new Chuyen_BUS();

        setupAutoSuggestGaDi();
        setupAutoSuggestGaDen();
    }

    private void setupAutoSuggestGaDi() {
        view.getTxtGaDi().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { suggestGaDi(); }
            public void removeUpdate(DocumentEvent e) { suggestGaDi(); }
            public void changedUpdate(DocumentEvent e) { suggestGaDi(); }
        });
    }

    private void suggestGaDi() {
        String keyword = view.getGaDi().toLowerCase();
        List<String> danhSachGa = gaDAO.getAllTenGa();
        view.showGaDiSuggestions(danhSachGa.stream()
            .filter(ga -> ga.toLowerCase().contains(keyword))
            .toList());
    }

    private void setupAutoSuggestGaDen() {
        view.getTxtGaDen().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { suggestGaDen(); }
            public void removeUpdate(DocumentEvent e) { suggestGaDen(); }
            public void changedUpdate(DocumentEvent e) { suggestGaDen(); }
        });

        view.getTxtGaDi().addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) {
                suggestGaDen(); // cập nhật lại ga đến khi ga đi thay đổi
            }
        });
    }

    private void suggestGaDen() {
        String gaDi = view.getGaDi();
        String keyword = view.getGaDen().toLowerCase();
        List<String> danhSachGaDen = gaDAO.getAllGaDenTheoGaDi(gaDi);
        view.showGaDenSuggestions(danhSachGaDen.stream()
            .filter(ga -> ga.toLowerCase().contains(keyword))
            .toList());
    }
}