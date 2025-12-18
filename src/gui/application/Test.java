package gui.application;
/*
 * @(#) Test.java  1.0  [4:41:18 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

//Dua cac panel vao day test cho tien
import javax.swing.JFrame;

import gui.application.form.doiVe.PanelDoiVe3;

public class Test {

	public static void main(String[] args) {
		// Tạo cửa sổ JFrame
		JFrame frame = new JFrame("Test");
		frame.add(new PanelDoiVe3());
		frame.setSize(1080, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
