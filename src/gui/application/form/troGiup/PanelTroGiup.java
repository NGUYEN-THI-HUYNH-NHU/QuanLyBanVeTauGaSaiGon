package gui.application.form.troGiup;
/*
 * @(#) PanelTroGiup.java  1.0  [12:43:43 PM] Oct 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 28, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class PanelTroGiup extends JPanel {
	private JTree treeMenu;
	private JEditorPane contentPane;

	public PanelTroGiup() {
		setLayout(new BorderLayout());

		// 1. Tạo Menu bên trái (Cây thư mục)
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Hướng dẫn sử dụng");
		createNodes(root);
		treeMenu = new JTree(root);
		JScrollPane treeView = new JScrollPane(treeMenu);

		// 2. Tạo khung hiển thị nội dung bên phải (Hỗ trợ HTML)
		contentPane = new JEditorPane();
		contentPane.setEditable(false);
		contentPane.setContentType("text/html");
		JScrollPane contentView = new JScrollPane(contentPane);

		// 3. Ghép 2 phần bằng JSplitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(contentView);
		splitPane.setDividerLocation(240); // Chiều rộng menu

		add(splitPane, BorderLayout.CENTER);

		// 4. Sự kiện click vào menu
		treeMenu.addTreeSelectionListener(e -> {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeMenu.getLastSelectedPathComponent();
			if (node == null) {
				return;
			}

			// Logic load file dựa trên tên node
			loadPage(node.toString());
		});

		// Load trang chào mừng ban đầu
		displayURL("welcome.html");
	}

	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category1 = new DefaultMutableTreeNode("Bán vé");
		category1.add(new DefaultMutableTreeNode("quy_trinh_ban_ve"));
		category1.add(new DefaultMutableTreeNode("tra_cuu_tau"));
		category1.add(new DefaultMutableTreeNode("chon_ghe"));

		DefaultMutableTreeNode category2 = new DefaultMutableTreeNode("Đổi trả vé");
		category2.add(new DefaultMutableTreeNode("quy_dinh_doi_ve"));

		top.add(category1);
		top.add(category2);
	}

	private void loadPage(String nodeName) {
		displayURL(nodeName + ".html");
	}

	private void displayURL(String filename) {
		URL url = getClass().getResource("/" + filename);
		try {
			if (url != null) {
				contentPane.setPage(url);
			} else {
				contentPane.setText("<html><h2>Không tìm thấy tài liệu</h2></html>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}