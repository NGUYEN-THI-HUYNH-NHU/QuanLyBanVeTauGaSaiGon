package gui.tuyChinh;
/*
 * @(#) FlexibleTableResizer.java  1.0  [6:14:11 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class FlexibleTableResizer {

	public static void resize(JTable table, List<Integer> expandableCols) {

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		int totalPreferred = 0;

		// 1. AUTO-FIT WIDTH
		for (int col = 0; col < table.getColumnCount(); col++) {

			TableColumn column = table.getColumnModel().getColumn(col);

			int preferred = 30;
			int maxLimit = 400;

			// header
			TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
			Component h = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0,
					col);
			preferred = Math.max(preferred, h.getPreferredSize().width);

			// cells
			for (int row = 0; row < table.getRowCount(); row++) {
				Component r = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
				preferred = Math.max(preferred, r.getPreferredSize().width + 8);

				if (preferred > maxLimit) {
					preferred = maxLimit;
					break;
				}
			}

			column.setPreferredWidth(preferred);
			totalPreferred += preferred;
		}

		// 2. GET VIEWPORT WIDTH
		JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, table);

		if (viewport == null || viewport.getWidth() == 0) {
			SwingUtilities.invokeLater(() -> resize(table, expandableCols));
			return; // ← FIX QUAN TRỌNG NHẤT
		}

		int viewportWidth = viewport.getWidth();

		// 3. EXPAND if table < viewport
		if (totalPreferred < viewportWidth && !expandableCols.isEmpty()) {

			int extra = viewportWidth - totalPreferred;
			int perCol = extra / expandableCols.size();

			for (int colIndex : expandableCols) {
				TableColumn col = table.getColumnModel().getColumn(colIndex);
				col.setPreferredWidth(col.getPreferredWidth() + perCol);
			}
		}

		// 4. FORCE TABLE TO FIT VIEWPORT
		Dimension d = new Dimension(viewportWidth, table.getPreferredSize().height);
		table.setPreferredScrollableViewportSize(d);
		table.setPreferredSize(d);
	}
}
