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
		contentView.getVerticalScrollBar().setUnitIncrement(20);

		// 3. Ghép 2 phần bằng JSplitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(contentView);
		splitPane.setDividerLocation(240);
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
		category1.add(new DefaultMutableTreeNode("quy_dinh_ban_ve"));
		category1.add(new DefaultMutableTreeNode("quy_trinh_ban_ve"));

		DefaultMutableTreeNode category2 = new DefaultMutableTreeNode("Hoàn vé");
		category2.add(new DefaultMutableTreeNode("quy_dinh_hoan_ve"));
		category2.add(new DefaultMutableTreeNode("quy_trinh_hoan_ve"));

		DefaultMutableTreeNode category3 = new DefaultMutableTreeNode("Đổi vé");
		category3.add(new DefaultMutableTreeNode("quy_dinh_doi_ve"));
		category3.add(new DefaultMutableTreeNode("quy_trinh_doi_ve"));

		DefaultMutableTreeNode category4 = new DefaultMutableTreeNode("Thêm Tuyến");
		category4.add(new DefaultMutableTreeNode("quy_dinh_them_tuyen"));
		category4.add(new DefaultMutableTreeNode("quy_trinh_them_tuyen"));

		DefaultMutableTreeNode category5 = new DefaultMutableTreeNode("Thêm Chuyến");
		category5.add(new DefaultMutableTreeNode("quy_dinh_them_chuyen"));
		category5.add(new DefaultMutableTreeNode("quy_trinh_them_chuyen"));

		DefaultMutableTreeNode category6 = new DefaultMutableTreeNode("Thêm Biểu Giá");
		category6.add(new DefaultMutableTreeNode("quy_dinh_them_bieu_gia"));
		category6.add(new DefaultMutableTreeNode("quy_trinh_them_bieu_gia"));

		top.add(category1);
		top.add(category2);
		top.add(category3);
		top.add(category4);
		top.add(category5);
		top.add(category6);
	}

	private void loadPage(String nodeName) {
		displayURL(nodeName + ".html");
	}

	private void displayURL(String filename) {
		URL url = getClass().getResource("/" + filename);
		if (filename.contains(" ")) {
			return;
		}

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