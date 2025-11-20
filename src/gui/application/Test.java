package gui.application;
/*
 * @(#) Test.java  1.0  [4:41:18 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import javax.swing.JFrame;

import gui.application.form.doiVe.PanelDoiVe1;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

//Dua cac panel vao day test cho tien

public class Test extends JFrame {

	public Test() {
		super("Test panel");

		add(new PanelDoiVe1());

		setSize(1100, 760);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Test();
	}
}
