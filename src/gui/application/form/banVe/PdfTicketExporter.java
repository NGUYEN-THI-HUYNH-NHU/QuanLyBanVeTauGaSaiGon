package gui.application.form.banVe;
/*
 * @(#) PdfTicketExporter.java  1.0  [5:39:21 PM] Dec 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 9, 2025
 * @version: 1.0
 */

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font; // Hỗ trợ Unicode (Tiếng Việt)
// Hoặc PDType1Font.HELVETICA nếu không cần tiếng Việt có dấu

import gui.application.form.doiVe.ExchangeSession;

public class PdfTicketExporter {
	private final DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");

	// Kích thước giấy in nhiệt K80 (80mm ~ 226 points)
	private static final float PAGE_WIDTH = 226f;
	private static final float PAGE_HEIGHT = 500f; // Chiều dài linh hoạt
	private static final float MARGIN_X = 10f; // Lề an toàn
	private static final float MARGIN_Y = 10f;

	private PDType0Font fontRegular;
	private PDType0Font fontBold;

	public void exportTicketsToPdf(BookingSession session) {
		if (session == null || session.getAllSelectedTickets().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Không có vé nào để xuất.", "Lỗi", JOptionPane.WARNING_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Lưu vé tàu (Khổ in nhiệt K80)");
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
		String suggestedFileName = session.getDonDatCho().getDonDatChoID() + "_" + session.getKhachHang().getHoTen()
				+ ".pdf";
		fileChooser.setSelectedFile(new File(suggestedFileName));

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
				fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
			}

			try (PDDocument document = new PDDocument()) {
				// 1. Load Font
				try {
					InputStream fontStreamReg = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf");
					InputStream fontStreamBold = getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf");

					if (fontStreamReg == null || fontStreamBold == null) {
						throw new IOException("Không tìm thấy file font trong /resources/fonts/");
					}
					fontRegular = PDType0Font.load(document, fontStreamReg);
					fontBold = PDType0Font.load(document, fontStreamBold);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Lỗi font: " + e.getMessage());
					return;
				}

				// 2. Duyệt từng vé
				List<VeSession> tickets = session.getAllSelectedTickets();
				for (VeSession ticket : tickets) {
					PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
					document.addPage(page);

					try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
						drawTicketK80(cs, ticket);
					}
				}

				document.save(fileToSave);

				// Mở file sau khi lưu
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(fileToSave);
				} else {
					JOptionPane.showMessageDialog(null, "Xuất vé thành công!");
				}

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Lỗi xuất PDF: " + e.getMessage());
			}
		}
	}

	public void exportTicketsToPdf(ExchangeSession exchangeSession) {
		if (exchangeSession == null || exchangeSession.getListVeMoiDangChon().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Không có vé nào để xuất.", "Lỗi", JOptionPane.WARNING_MESSAGE);
			return;
		}

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Lưu vé tàu (Khổ in nhiệt K80)");
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
		String suggestedFileName = exchangeSession.getDonDatChoMoi().getDonDatChoID() + "_"
				+ exchangeSession.getKhachHang().getHoTen() + ".pdf";
		fileChooser.setSelectedFile(new File(suggestedFileName));

		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
				fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
			}

			try (PDDocument document = new PDDocument()) {
				// 1. Load Font
				try {
					InputStream fontStreamReg = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf");
					InputStream fontStreamBold = getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf");

					if (fontStreamReg == null || fontStreamBold == null) {
						throw new IOException("Không tìm thấy file font trong /resources/fonts/");
					}
					fontRegular = PDType0Font.load(document, fontStreamReg);
					fontBold = PDType0Font.load(document, fontStreamBold);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Lỗi font: " + e.getMessage());
					return;
				}

				// 2. Duyệt từng vé
				List<VeSession> tickets = exchangeSession.getListVeMoiDangChon();
				for (VeSession ticket : tickets) {
					PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
					document.addPage(page);

					try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
						drawTicketK80(cs, ticket);
					}
				}

				document.save(fileToSave);

				// Mở file sau khi lưu
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().open(fileToSave);
				} else {
					JOptionPane.showMessageDialog(null, "Xuất vé thành công!");
				}

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Lỗi xuất PDF: " + e.getMessage());
			}
		}
	}

	// --- HÀM VẼ CHÍNH ---
	private void drawTicketK80(PDPageContentStream cs, VeSession ticket) throws IOException {
		float centerX = PAGE_WIDTH / 2;
		float y = PAGE_HEIGHT - MARGIN_Y - 10;

		// 1. HEADER
		cs.setNonStrokingColor(0, 0, 0); // Màu đen
		drawCenteredText(cs, fontBold, 10, "CÔNG TY CỔ PHẦN VẬN TẢI", centerX, y);
		y -= 12;
		drawCenteredText(cs, fontBold, 10, "ĐƯỜNG SẮT SÀI GÒN", centerX, y);
		y -= 20;

		// 2. TITLE
		drawCenteredText(cs, fontBold, 16, "THẺ LÊN TÀU HỎA", centerX, y);
		y -= 12;
		drawCenteredText(cs, fontRegular, 10, "BOARDING PASS", centerX, y);
		y -= 25;

		// 3. QR CODE (Placeholder - Vẽ khung vuông)
		float qrSize = 80;
		float qrX = (PAGE_WIDTH - qrSize) / 2;
		cs.setLineWidth(1f);
		cs.addRect(qrX, y - qrSize, qrSize, qrSize);
		cs.stroke();
		y -= (qrSize + 15);

		// 4. MÃ VÉ
		drawCenteredText(cs, fontRegular, 9, "Mã vé/TicketID: " + ticket.getVe().getVeID(), centerX, y);
		y -= 25;

		// 5. GA ĐI - GA ĐẾN (SỬA LẠI TỌA ĐỘ CHO CHUẨN)
		// Lề trái bắt đầu từ MARGIN_X
		// Lề phải kết thúc tại PAGE_WIDTH - MARGIN_X
		float leftX = MARGIN_X;
		float rightX = PAGE_WIDTH - MARGIN_X;

		// Dòng tiêu đề nhỏ: "Ga đi" ------ "Ga đến"
		drawLeftText(cs, fontRegular, 9, "Ga đi", leftX, y);
		drawRightText(cs, fontRegular, 9, "Ga đến", rightX, y);
		y -= 15;

		// Dòng tên ga lớn: "SÀI GÒN" ------ "HÀ NỘI"
		String gaDi = ticket.getVe().getGaDi().getTenGa().toUpperCase();
		String gaDen = ticket.getVe().getGaDen().getTenGa().toUpperCase();

		drawLeftText(cs, fontBold, 12, gaDi, leftX, y);
		drawRightText(cs, fontBold, 12, gaDen, rightX, y);
		y -= 25;

		// 6. THÔNG TIN CHI TIẾT
		// Chia cột: Cột nhãn rộng khoảng 70px, cột giá trị nằm ngay sau đó

		y = drawRow(cs, "Tàu/Train:", ticket.getVe().getGhe().getToa().getTau().getTauID(), y);
		y = drawRow(cs, "Ngày đi/Date:", ticket.getVe().getNgayGioDi().format(dtfDate), y);
		y = drawRow(cs, "Giờ đi/Time:", ticket.getVe().getNgayGioDi().format(dtfTime), y);

		// Dòng Toa/Chỗ (Chia đôi)
		cs.beginText();
		cs.setFont(fontBold, 9);
		cs.newLineAtOffset(leftX, y);
		cs.showText("Toa/Coach: " + ticket.getVe().getGhe().getToa().getSoToa());
		cs.endText();

		cs.beginText();
		cs.setFont(fontBold, 9);
		// Vẽ phần Chỗ lệch sang phải một chút (quá giữa trang)
		cs.newLineAtOffset(centerX + 10, y);
		cs.showText("Chỗ/Seat: " + ticket.getSoGhe());
		cs.endText();
		y -= 15;

		// Các thông tin khác
		y = drawRow(cs, "Loại chỗ/Class:", ticket.getVe().getGhe().getToa().getHangToa().getDescription(), y);

		y = drawRow(cs, "Đối tượng:", ticket.getVe().getKhachHang().getLoaiDoiTuong().getDescription(), y);

		String tenKH = ticket.getVe().getKhachHang() != null ? ticket.getVe().getKhachHang().getHoTen().toUpperCase()
				: "";
		y = drawRow(cs, "Họ tên/Name:", tenKH, y);

		String giayTo = ticket.getVe().getKhachHang() != null ? ticket.getVe().getKhachHang().getSoGiayTo() : "";
		y = drawRow(cs, "Giấy tờ/ID:", giayTo, y);

		// Kẻ đường ngang kết thúc
		y -= 5;
		cs.moveTo(leftX, y);
		cs.lineTo(rightX, y);
		cs.stroke();
		y -= 15;

		drawCenteredText(cs, fontRegular, 8, "Vui lòng có mặt trước giờ tàu chạy 30 phút", centerX, y);
	}

	// --- CÁC HÀM HỖ TRỢ ĐÃ SỬA LỖI ---

	// 1. Vẽ text canh trái (Left Align)
	private void drawLeftText(PDPageContentStream cs, PDType0Font font, float size, String text, float x, float y)
			throws IOException {
		cs.beginText();
		cs.setFont(font, size);
		cs.newLineAtOffset(x, y);
		cs.showText(text);
		cs.endText();
	}

	// 2. Vẽ text canh phải (Right Align) - Cực kỳ quan trọng để không bị mất chữ
	// "Ga đến"
	private void drawRightText(PDPageContentStream cs, PDType0Font font, float size, String text, float rightX, float y)
			throws IOException {
		float textWidth = font.getStringWidth(text) / 1000 * size;
		float startX = rightX - textWidth; // Tính điểm bắt đầu bằng cách lùi lại từ lề phải

		cs.beginText();
		cs.setFont(font, size);
		cs.newLineAtOffset(startX, y);
		cs.showText(text);
		cs.endText();
	}

	// 3. Vẽ text canh giữa (Center Align)
	private void drawCenteredText(PDPageContentStream cs, PDType0Font font, float size, String text, float centerX,
			float y) throws IOException {
		float textWidth = font.getStringWidth(text) / 1000 * size;
		float startX = centerX - (textWidth / 2);

		cs.beginText();
		cs.setFont(font, size);
		cs.newLineAtOffset(startX, y);
		cs.showText(text);
		cs.endText();
	}

	// 4. Vẽ một dòng dữ liệu (Nhãn + Giá trị)
	private float drawRow(PDPageContentStream cs, String label, String value, float y) throws IOException {
		float labelX = MARGIN_X;
		float valueX = MARGIN_X + 85; // Cột giá trị cách lề trái 85 điểm ảnh

		// Vẽ nhãn (Đậm)
		cs.beginText();
		cs.setFont(fontBold, 9);
		cs.newLineAtOffset(labelX, y);
		cs.showText(label);
		cs.endText();

		// Vẽ giá trị (Thường) - Nếu giá trị quá dài bạn có thể cần logic xuống dòng,
		// nhưng vé tàu thường ngắn
		cs.beginText();
		cs.setFont(fontRegular, 9);
		cs.newLineAtOffset(valueX, y);
		cs.showText(value);
		cs.endText();

		return y - 15; // Trả về tọa độ y cho dòng tiếp theo
	}
}