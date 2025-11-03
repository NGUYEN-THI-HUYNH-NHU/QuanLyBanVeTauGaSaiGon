package gui.application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal; // Sử dụng BigDecimal nếu bạn đã refactor
import java.text.DecimalFormat; // Hoặc NumberFormat
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font; // Hỗ trợ Unicode (Tiếng Việt)
// Hoặc PDType1Font.HELVETICA nếu không cần tiếng Việt có dấu

import entity.KhachHang;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;

public class PdfTicketExporter {
	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private final DecimalFormat currencyFormatter = new DecimalFormat("#,### VND"); // Hoặc NumberFormat

	// --- Font (Quan trọng cho Tiếng Việt) ---
	private PDType0Font fontRegular; // Font thường
	private PDType0Font fontBold; // Font đậm

	public void exportTicketsToPdf(BookingSession session) {
		if (session == null) {
			JOptionPane.showMessageDialog(null, "Không có thông tin đặt vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// --- Lấy dữ liệu từ Session ---
		KhachHang nguoiMua = session.getKhachHang();
		List<VeSession> tickets = session.getOutboundSelectedTickets(); // Lấy vé chiều đi
		if (session.isRoundTrip()) {
			tickets.addAll(session.getReturnSelectedTickets()); // Thêm vé chiều về nếu có
		}

		if (tickets.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Không có vé nào được chọn.", "Lỗi", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// --- Chọn nơi lưu file ---
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Lưu file PDF vé tàu");
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents", "pdf"));
		// Gợi ý tên file
		String suggestedFileName = "VeTau_" + nguoiMua.getHoTen().replace(" ", "_") + ".pdf";
		fileChooser.setSelectedFile(new File(suggestedFileName));

		int userSelection = fileChooser.showSaveDialog(null); // Hiển thị dialog lưu file

		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			// Đảm bảo file có đuôi .pdf
			if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".pdf")) {
				fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
			}

			// --- Tạo PDF ---
			try (PDDocument document = new PDDocument()) {
				try {
					InputStream fontStreamReg = PdfTicketExporter.class
							.getResourceAsStream("/fonts/Roboto-Regular.ttf");
					InputStream fontStreamBold = PdfTicketExporter.class.getResourceAsStream("/fonts/Roboto-Bold.ttf");
					if (fontStreamReg == null || fontStreamBold == null) {
						throw new IOException(
								"Không tìm thấy file font trong resources/fonts/. Hãy đảm bảo thư mục fonts nằm trong resources và tên file đúng.");
					}

					fontRegular = PDType0Font.load(document, fontStreamReg);
					fontBold = PDType0Font.load(document, fontStreamBold);

				} catch (IOException e) {
					System.err.println("Không thể load font Unicode: " + e.getMessage());
					throw new IOException("Lỗi font: Không thể tải font hỗ trợ tiếng Việt. " + e.getMessage(), e);
				}

				document.save(fileToSave); // Lưu file
				JOptionPane.showMessageDialog(null, "Đã xuất vé thành công:\n" + fileToSave.getAbsolutePath(),
						"Thông báo", JOptionPane.INFORMATION_MESSAGE);

				try {
					if (Desktop.isDesktopSupported()) {
						Desktop.getDesktop().open(fileToSave);
					}
				} catch (IOException ex) {
					System.err.println("Không thể tự động mở file PDF: " + ex.getMessage());
				}

			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Lỗi khi tạo file PDF: " + e.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Hàm vẽ nội dung một vé lên trang PDF
	 */
	private void drawTicket(PDPageContentStream contentStream, VeSession ticket, KhachHang nguoiMua, PDPage page)
			throws IOException {
		float margin = 50;
		float yStart = page.getMediaBox().getHeight() - margin;
		float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
		float yPosition = yStart;
		float fontSize = 10;
		float leading = 1.5f * fontSize; // Khoảng cách dòng

		// --- Tiêu đề ---
		contentStream.beginText();
		contentStream.setFont(fontBold, 16);
		contentStream.newLineAtOffset(margin, yPosition);
		contentStream.showText("VÉ TÀU HỎA ĐIỆN TỬ");
		contentStream.endText();
		yPosition -= leading * 2;

		// --- Thông tin chung ---
		contentStream.beginText();
		contentStream.setFont(fontRegular, fontSize);
		contentStream.setLeading(leading); // Đặt khoảng cách dòng
		contentStream.newLineAtOffset(margin, yPosition);

		contentStream.showText("Hành khách: ");
		contentStream.setFont(fontBold, fontSize);
		contentStream.showText(ticket.getHanhKhach() != null ? ticket.getHanhKhach().getHoTen().toUpperCase() : "N/A");
		contentStream.setFont(fontRegular, fontSize);
		contentStream.newLine(); // Xuống dòng

		contentStream.showText(
				"Số giấy tờ: " + (ticket.getHanhKhach() != null ? ticket.getHanhKhach().getSoGiayTo() : "N/A"));
		contentStream.newLine();

		contentStream.showText("Người mua vé: " + (nguoiMua != null ? nguoiMua.getHoTen() : "N/A"));
		contentStream.newLine();
		contentStream.showText("SĐT người mua: " + (nguoiMua != null ? nguoiMua.getSoDienThoai() : "N/A"));
		contentStream.newLine();
		contentStream.newLine(); // Thêm dòng trống

		// --- Thông tin chuyến đi ---
		contentStream.setFont(fontBold, fontSize);
		contentStream.showText("Chuyến tàu: " + ticket.getTenTau()); // Sử dụng tenTau từ VeSession
		contentStream.setFont(fontRegular, fontSize);
		contentStream.newLine();

		contentStream.showText("Ga đi: " + ticket.getTenGaDi() + " (" + ticket.getGioDi().format(timeFormatter) + " "
				+ ticket.getNgayDi().format(dateFormatter) + ")");
		contentStream.newLine();
		contentStream.showText("Ga đến: " + ticket.getTenGaDen()); // Bạn có thể thêm giờ đến, ngày đến nếu VeSession có
		contentStream.newLine();

		contentStream.showText("Toa: " + ticket.getSoToa() + " - Chỗ: ");
		contentStream.setFont(fontBold, fontSize);
		contentStream.showText(String.valueOf(ticket.getSoGhe()));
		contentStream.setFont(fontRegular, fontSize);
		// Bạn có thể thêm Hạng toa (ticket.getHangToa()) nếu cần
		contentStream.newLine();
		contentStream.newLine();

		// --- Thông tin giá ---
		// Chuyển đổi int sang BigDecimal nếu cần format
		BigDecimal giaVe = new BigDecimal(ticket.getGia()); // Giả sử getGia() trả về int/double
		BigDecimal giamGia = new BigDecimal(ticket.getGiam()); // Giả sử getGiam() trả về int/double
		BigDecimal thanhTien = giaVe.subtract(giamGia);

		contentStream.showText("Giá vé: " + currencyFormatter.format(giaVe.doubleValue())); // Format
		contentStream.newLine();
		contentStream.showText("Giảm giá/KM: " + currencyFormatter.format(giamGia.doubleValue()));
		contentStream.newLine();
		contentStream.setFont(fontBold, fontSize);
		contentStream.showText("Thành tiền: " + currencyFormatter.format(thanhTien.doubleValue()));
		contentStream.setFont(fontRegular, fontSize);
		contentStream.newLine();
		contentStream.newLine();

		// --- Ghi chú ---
		contentStream.showText("Lưu ý: Vui lòng có mặt tại ga trước giờ tàu chạy 30 phút.");
		contentStream.newLine();
		contentStream.showText("Mang theo giấy tờ tùy thân trùng khớp với thông tin trên vé.");

		contentStream.endText();

		// (Tùy chọn) Vẽ thêm đường kẻ, logo...
		// contentStream.moveTo(margin, yPosition - leading);
		// contentStream.lineTo(margin + tableWidth, yPosition - leading);
		// contentStream.stroke();
	}
}