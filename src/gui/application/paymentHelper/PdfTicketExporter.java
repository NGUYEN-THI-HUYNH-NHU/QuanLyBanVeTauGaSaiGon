package gui.application.paymentHelper;
/*
 * @(#) PdfTicketExporter.java  1.0  [5:39:21 PM] Dec 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Desktop;
import java.awt.image.BufferedImage;
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
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import entity.DonDatCho;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;

public class PdfTicketExporter {
	private final DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HH:mm");

	// Kích thước giấy in nhiệt K80 (80mm ~ 226 points)
	private static final float PAGE_WIDTH = 226f;
	private static final float PAGE_HEIGHT = 460f; // Chiều dài linh hoạt
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
			processExport(fileChooser.getSelectedFile(), session.getAllSelectedTickets(), session.getDonDatCho());
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
			processExport(fileChooser.getSelectedFile(), exchangeSession.getListVeMoiDangChon(),
					exchangeSession.getDonDatChoMoi());
		}
	}

	// Hàm xử lý chung để tránh lặp code (Refactor)
	private void processExport(File fileToSave, List<VeSession> tickets, DonDatCho donDatCho) {
		if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
			fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
		}

		try (PDDocument document = new PDDocument()) {
			// 1. Load Font
			try {
				InputStream fontStreamReg = getClass().getResourceAsStream("/fonts/Roboto-Regular.ttf");
				InputStream fontStreamBold = getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf");

				if (fontStreamReg == null || fontStreamBold == null) {
					throw new IOException("Không tìm thấy file font trong /fonts/");
				}
				fontRegular = PDType0Font.load(document, fontStreamReg);
				fontBold = PDType0Font.load(document, fontStreamBold);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Lỗi font: " + e.getMessage());
				return;
			}

			// 2. Duyệt từng vé
			for (VeSession ticket : tickets) {
				PDPage page = new PDPage(new PDRectangle(PAGE_WIDTH, PAGE_HEIGHT));
				document.addPage(page);

				try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
					// [NEW] Truyền thêm 'document' vào hàm vẽ để tạo ảnh QR
					drawTicketK80(document, cs, ticket, donDatCho.getDonDatChoID());
				}
			}

			document.save(fileToSave);

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

	// --- HÀM VẼ CHÍNH ---
	private void drawTicketK80(PDDocument document, PDPageContentStream cs, VeSession ticket, String donDatChoID)
			throws IOException {
		float centerX = PAGE_WIDTH / 2;
		float y = PAGE_HEIGHT - MARGIN_Y - 10;

		// 1. HEADER
		cs.setNonStrokingColor(0, 0, 0);
		drawCenteredText(cs, fontBold, 10, "CÔNG TY CỔ PHẦN VẬN TẢI", centerX, y);
		y -= 12;
		drawCenteredText(cs, fontBold, 10, "ĐƯỜNG SẮT SÀI GÒN", centerX, y);
		y -= 20;

		// 2. TITLE
		drawCenteredText(cs, fontBold, 16, "THẺ LÊN TÀU HỎA", centerX, y);
		y -= 12;
		drawCenteredText(cs, fontRegular, 10, "BOARDING PASS", centerX, y);
		y -= 25;

		// 3. QR CODE (SỬ DỤNG ZXING)
		float qrSize = 100; // Kích thước hiển thị trên PDF
		float qrX = (PAGE_WIDTH - qrSize) / 2;

		try {
			// A. Tạo nội dung chuỗi QR
			// Tạo chuỗi JSON thủ công
			String qrContent = "{" + "\"id\":\"" + ticket.getVe().getVeID() + "\"," + "\"trangThai\":\"" + "TODO"
					+ "\"," + "\"tau\":\"" + ticket.getVe().getGhe().getToa().getTau().getTauID() + "\"," + "\"toa\":\""
					+ ticket.getVe().getGhe().getToa().getSoToa() + "\"," + "\"ghe\":\"" + ticket.getSoGhe() + "\","
					+ "\"cccd\":\"" + ticket.getVe().getKhachHang().getSoGiayTo() + "\"," + "\"hoTen\":\""
					+ ticket.getVe().getKhachHang().getHoTen() + "\"" + "}";

			// B. Tạo BufferedImage từ ZXing
			BufferedImage qrImage = createQRCode(qrContent, 200, 200);

			// C. Chuyển sang đối tượng ảnh của PDFBox bằng LosslessFactory [QUAN TRỌNG]
			PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrImage);

			// D. Vẽ lên PDF
			cs.drawImage(pdImage, qrX, y - qrSize, qrSize, qrSize);

		} catch (Exception e) {
			e.printStackTrace();
			// Fallback: Vẽ khung vuông nếu lỗi
			cs.addRect(qrX, y - qrSize, qrSize, qrSize);
			cs.stroke();
		}

		y -= (qrSize + 15);

		// 4. MÃ VÉ + ĐƠN ĐẶT CHỖ
		drawCenteredText(cs, fontRegular, 9, "Mã vé/TicketID: " + ticket.getVe().getVeID(), centerX, y);
		y -= 15;
		drawCenteredText(cs, fontRegular, 9, "Mã đặt chỗ/BookingID: " + donDatChoID, centerX, y);
		y -= 25;

		// 5. GA ĐI - GA ĐẾN
		float leftX = MARGIN_X;
		float rightX = PAGE_WIDTH - MARGIN_X;

		drawLeftText(cs, fontRegular, 9, "Ga đi", leftX, y);
		drawRightText(cs, fontRegular, 9, "Ga đến", rightX, y);
		y -= 15;

		String gaDi = ticket.getVe().getGaDi().getTenGa().toUpperCase();
		String gaDen = ticket.getVe().getGaDen().getTenGa().toUpperCase();

		drawLeftText(cs, fontBold, 12, gaDi, leftX, y);
		drawRightText(cs, fontBold, 12, gaDen, rightX, y);
		y -= 25;

		// 6. THÔNG TIN CHI TIẾT
		y = drawRow(cs, "Tàu/Train:", ticket.getVe().getGhe().getToa().getTau().getTauID(), y);
		y = drawRow(cs, "Ngày đi/Date:", ticket.getVe().getNgayGioDi().format(dtfDate), y);
		y = drawRow(cs, "Giờ đi/Time:", ticket.getVe().getNgayGioDi().format(dtfTime), y);

		// Dòng Toa/Chỗ
		cs.beginText();
		cs.setFont(fontBold, 9);
		cs.newLineAtOffset(leftX, y);
		cs.showText("Toa/Coach: " + ticket.getVe().getGhe().getToa().getSoToa());
		cs.endText();

		cs.beginText();
		cs.setFont(fontBold, 9);
		cs.newLineAtOffset(centerX + 10, y);
		cs.showText("Chỗ/Seat: " + ticket.getSoGhe());
		cs.endText();
		y -= 15;

		y = drawRow(cs, "Loại chỗ/Class:", ticket.getVe().getGhe().getToa().getHangToa().getDescription(), y);
		y = drawRow(cs, "Đối tượng:", ticket.getVe().getKhachHang().getLoaiDoiTuong().getDescription(), y);

		String tenKH = ticket.getVe().getKhachHang() != null ? ticket.getVe().getKhachHang().getHoTen().toUpperCase()
				: "";
		y = drawRow(cs, "Họ tên/Name:", tenKH, y);

		String giayTo = ticket.getVe().getKhachHang() != null ? ticket.getVe().getKhachHang().getSoGiayTo() : "";
		y = drawRow(cs, "Số giấy tờ/ID:", giayTo, y);

		// Footer
		y -= 5;
		cs.moveTo(leftX, y);
		cs.lineTo(rightX, y);
		cs.stroke();
		y -= 15;

		drawCenteredText(cs, fontRegular, 8, "Vui lòng có mặt trước giờ tàu chạy 30 phút", centerX, y);
	}

	private BufferedImage createQRCode(String text, int width, int height) throws Exception {
		QRCodeWriter barcodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = barcodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		return MatrixToImageWriter.toBufferedImage(bitMatrix);
	}

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

		cs.beginText();
		cs.setFont(fontRegular, 9);
		cs.newLineAtOffset(valueX, y);
		cs.showText(value);
		cs.endText();

		return y - 15; // Trả về tọa độ y cho dòng tiếp theo
	}
}