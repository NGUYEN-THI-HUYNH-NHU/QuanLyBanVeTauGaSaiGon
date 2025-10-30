package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2.java  1.0  [10:39:25 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import entity.Chuyen;

public class PanelBuoc2 extends JPanel {
	private PanelChieuLabel panelChieuLabel;
	private PanelChuyenTau panelChuyenTau;
	private PanelDoanTau panelDoanTau;
	private PanelSoDoCho panelSoDoCho;
	private PanelGioVe panelGioVe;
	private JPanel centerPanel;

	private PanelBuoc2Controller controller;
//	private JPanel pnlChuThich;

	public PanelBuoc2() {
		setLayout(new BorderLayout(2, 0));
		setBorder(new TitledBorder(""));
		setPreferredSize(new Dimension(0, 400));

//		pnlChuThich = new JPanel();
//		pnlChuThich.setPreferredSize(new Dimension(10, 20));
////        ImageIcon iconTrong = new ImageIcon(getClass().getResource("/gui/icon/png/toa-tau.png"));
////        ImageIcon iconDay = new ImageIcon(getClass().getResource("/gui/icon/png/toa-tau.png"));
//		ImageIcon iconDangChon = new ImageIcon(getClass().getResource("/gui/icon/png/chu-thich.png"));
//		JLabel muc1 = new JLabel("", iconDangChon, JLabel.CENTER);
//		pnlChuThich.add(muc1);

		panelChieuLabel = new PanelChieuLabel();
		panelChuyenTau = new PanelChuyenTau();
		panelDoanTau = new PanelDoanTau();
		panelSoDoCho = new PanelSoDoCho();
		panelGioVe = new PanelGioVe();

		centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
		centerPanel.setPreferredSize(getPreferredSize());
		centerPanel.add(panelChieuLabel);
		centerPanel.add(panelChuyenTau);
		centerPanel.add(panelDoanTau);
		centerPanel.add(panelSoDoCho);
//		centerPanel.add(pnlChuThich);

		add(centerPanel, BorderLayout.CENTER);
		add(panelGioVe, BorderLayout.EAST);

		controller = new PanelBuoc2Controller(panelChieuLabel, panelChuyenTau, panelDoanTau, panelSoDoCho, panelGioVe);
		panelChuyenTau.setController(controller);
		panelDoanTau.setController(controller);
		panelSoDoCho.setController(controller);
		panelGioVe.setController(controller);
	}

	public PanelChieuLabel getPanelChieuLabel() {
		return panelChieuLabel;
	}

	public PanelChuyenTau getPanelChuyenTau() {
		return panelChuyenTau;
	}

	public PanelDoanTau getPanelDoanTau() {
		return panelDoanTau;
	}

	public PanelSoDoCho getPanelSoDoCho() {
		return panelSoDoCho;
	}

	public PanelGioVe getPanelGioVe() {
		return panelGioVe;
	}

	public void enter(SearchCriteria criteria, List<Chuyen> results, int tripIndex, BookingSession session) {
		System.out.println(
				"PanelBuoc2.enter called: criteria=" + criteria + " tripIndex=" + tripIndex + " session=" + session);
		controller.setBookingSession(session);
		controller.setCurrentTripIndex(tripIndex);
		controller.displayChuyenList(criteria, results, tripIndex);
	}

	public void setComponentsEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (panelChieuLabel != null) {
			panelChieuLabel.setEnabled(enabled);
		}
		if (panelChuyenTau != null) {
			panelChuyenTau.setEnabled(enabled);
		}
		if (panelDoanTau != null) {
			panelDoanTau.setEnabled(enabled);
		}
		if (panelSoDoCho != null) {
			panelSoDoCho.setEnabled(enabled);
		}
		if (panelGioVe != null) {
			panelGioVe.setEnabled(enabled);
		}
	}
}