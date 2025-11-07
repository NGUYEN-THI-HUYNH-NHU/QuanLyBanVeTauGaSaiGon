package gui.application.form.banVe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import bus.Chuyen_BUS;
import bus.DatCho_BUS;
import entity.Chuyen;
import entity.Ga;
import entity.Ghe;
import entity.Toa;
import entity.Tuyen;

public class PanelBuoc2Controller {
	private final PanelChieuLabel panelChieuLabel;
	private final PanelChuyenTau panelChuyenTau;
	private final PanelDoanTau panelDoanTau;
	private final PanelSoDoCho panelSoDoCho;

	private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
	private final DatCho_BUS datChoBUS = new DatCho_BUS();

	private final List<SeatSelectedListener> seatSelectedListeners = new ArrayList<>();

	private BookingSession bookingSession;
	private int currentTripIndex = 0;
	private List<Chuyen> chuyenList;
	private Chuyen selectedChuyen;
	private Toa selectedToa;

	public interface SeatSelectedListener {
		void onSeatSelected(VeSession v);
	}

	public void addSeatSelectedListener(SeatSelectedListener listener) {
		if (listener != null) {
			this.seatSelectedListeners.add(listener);
		}
	}

	public PanelBuoc2Controller(PanelChieuLabel chieuLabel, PanelChuyenTau chuyenTau, PanelDoanTau doanTau,
			PanelSoDoCho soDoCho) {
		this.panelChieuLabel = chieuLabel;
		this.panelChuyenTau = chuyenTau;
		this.panelDoanTau = doanTau;
		this.panelSoDoCho = soDoCho;

		panelChuyenTau.setController(this);
		panelDoanTau.setController(this);
		panelSoDoCho.setController(this);
	}

	public void setBookingSession(BookingSession s) {
		this.bookingSession = s;
	}

	public void setCurrentTripIndex(int idx) {
		this.currentTripIndex = idx;
	}

	public int getCurrentTripIndex() {
		return this.currentTripIndex;
	}

	public SearchCriteria getCurrentTripCriteria() {
		if (getBookingSession() == null) {
			return null;
		}
		return (currentTripIndex == 0) ? getBookingSession().getOutboundCriteria()
				: getBookingSession().getReturnCriteria();
	}

	public int getGiaForTooltip(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID) {
		return chuyenBUS.layGiaGheTheoPhanDoan(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);
	}

	public void displayChuyenList(SearchCriteria criteria, List<Chuyen> chuyens, int tripIndex) {
		if (criteria == null || chuyens == null || chuyens.isEmpty()) {
			// Có thể ẩn hoặc xóa trắng panel
			return;
		}

		// 1. Set tripIndex để controller biết đang xử lý chiều đi hay về
		setCurrentTripIndex(tripIndex);

		String gaDiName = criteria.getGaDiName();
		String gaDenName = criteria.getGaDenName();

		this.chuyenList = chuyens;
		panelChieuLabel.setText(gaDiName + " - " + gaDenName + ": " + chuyens.get(0).getNgayDi());
		panelChuyenTau.showChuyenList(chuyens);

		if (chuyens != null && !chuyens.isEmpty()) {
			panelChuyenTau.selectChuyenById(chuyens.get(0).getChuyenID());
			onChuyenSelected(chuyens.get(0));
		}
	}

	public void onChuyenSelected(Chuyen c) {
		if (c == null) {
			return;
		}
		this.setSelectedChuyen(c);

		new SwingWorker<List<Toa>, Void>() {
			@Override
			protected List<Toa> doInBackground() throws Exception {
				return getChuyenBUS().layCacToaTheoChuyen(c.getChuyenID());
			}

			@Override
			protected void done() {
				try {
					List<Toa> list = get();
					panelDoanTau.showToaList(list, toa -> onToaSelected(toa));
					if (list != null && !list.isEmpty()) {
						panelDoanTau.selectToaById(list.get(0).getToaID());
						panelSoDoCho.setToaList(list);
						panelSoDoCho.setCurrentToa(list.get(0));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.execute();
	}

	public void onToaSelected(Toa toa) {
		this.selectedToa = toa;
		panelSoDoCho.setCurrentToa(toa);
	}

	private boolean isMissingId(String s) {
		return s == null || s.trim().isEmpty() || "null".equalsIgnoreCase(s.trim());
	}

	public void loadSeatsForToa(Toa toa, Consumer<List<Ghe>> callback) {
		if (toa == null) {
			callback.accept(Collections.emptyList());
			return;
		}

		// 1) fetch current chuyen/toa
		String chuyenID = (getSelectedChuyen() != null) ? getSelectedChuyen().getChuyenID() : null;
		String toaID = toa.getToaID();

		// 2) try to get gaDi/gaDen from bookingSession (based on currentTripIndex)
		String gaDiID = null;
		String gaDenID = null;
		if (getBookingSession() != null) {
			SearchCriteria sc = (currentTripIndex == 0) ? getBookingSession().getOutboundCriteria()
					: getBookingSession().getReturnCriteria();
			if (sc != null) {
				gaDiID = sc.getGaDiId(); // ensure method name matches your class
				gaDenID = sc.getGaDenId();
			}
		}

		// 3) Normalize: treat literal "null" or empty as missing
		if (isMissingId(gaDiID)) {
			gaDiID = null;
		}
		if (isMissingId(gaDenID)) {
			gaDenID = null;
		}

		// 4) Fallback: try get from selectedChuyen's tuyen (if entity supports it)
		if ((gaDiID == null || gaDenID == null) && getSelectedChuyen() != null) {
			try {
				Tuyen tuyen = getSelectedChuyen().getTuyen(); // adjust to your entity API
				if (tuyen != null) {
//                    if (gaDiID == null && tuyen.getGaDi() != null) gaDiID = tuyen.getGaDi().getGaID();
//                    if (gaDenID == null && tuyen.getGaDen() != null) gaDenID = tuyen.getGaDen().getGaID();
				}
			} catch (Throwable ignored) {
				// nếu entity khác, xử lý tương ứng
			}
		}

		// 5) Fallback 2: try resolve by name via BUS (use SearchCriteria names)
		if ((gaDiID == null || gaDenID == null) && getBookingSession() != null) {
			SearchCriteria sc = getBookingSession().getOutboundCriteria();
			if (sc == null && currentTripIndex == 1) {
				sc = getBookingSession().getReturnCriteria();
			}
			if (sc != null) {
				try {
					if (gaDiID == null && sc.getGaDiName() != null && !sc.getGaDiName().trim().isEmpty()) {
						Ga g = getChuyenBUS().timGaTheoTenGa(sc.getGaDiName().trim());
						if (g != null) {
							gaDiID = g.getGaID();
						}
					}
					if (gaDenID == null && sc.getGaDenName() != null && !sc.getGaDenName().trim().isEmpty()) {
						Ga g2 = getChuyenBUS().timGaTheoTenGa(sc.getGaDenName().trim());
						if (g2 != null) {
							gaDenID = g2.getGaID();
						}
					}
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		}

		// 6) final check
		if (isMissingId(gaDiID) || isMissingId(gaDenID) || isMissingId(chuyenID) || isMissingId(toaID)) {
			System.err.println(String.format("loadSeatsForToa: missing param gaDi=%s gaDen=%s chuyen=%s toa=%s", gaDiID,
					gaDenID, chuyenID, toaID));
			callback.accept(Collections.emptyList());
			return;
		}

		// 7) call core loader (thứ tự tham số đúng theo Chuyen_BUS)
		loadSeatsForToa(gaDiID, gaDenID, chuyenID, toaID, callback);
	}

	public void loadSeatsForToa(String gaDiID, String gaDenID, String chuyenID, String toaID,
			Consumer<List<Ghe>> callback) {
		new SwingWorker<List<Ghe>, Void>() {
			@Override
			protected List<Ghe> doInBackground() throws Exception {
				if (gaDiID == null || gaDenID == null || chuyenID == null || toaID == null) {
					return Collections.emptyList();
				}
				return getChuyenBUS().layCacGheTrongToaTrenChuyen(gaDiID, gaDenID, chuyenID, toaID);
			}

			@Override
			protected void done() {
				try {
					callback.accept(get());
				} catch (Exception ex) {
					ex.printStackTrace();
					callback.accept(Collections.emptyList());
				}
			}
		}.execute();
	}

	public void highlightToa(Toa toa) {
		if (toa == null) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			try {
				if (panelDoanTau != null) {
					panelDoanTau.selectToaById(toa.getToaID());
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		});
	}

	// user clicked a seat button
	public void onSeatClicked(Toa toa, Ghe ghe) {
		if (ghe == null || toa == null) {
			return;
		}

		final int tripIndex = getCurrentTripIndex();

		new SwingWorker<VeSession, Void>() {
			@Override
			protected VeSession doInBackground() throws Exception {
				VeSession v = createVeSessionForSeat(toa, ghe, tripIndex);

				if (v == null) {
					return null;
				}

				if (tripIndex == 0) {
					bookingSession.addOutboundTicket(v);
				} else {
					bookingSession.addReturnTicket(v);
				}

				return v;
			}

			@Override
			protected void done() {
				try {
					VeSession v = get();
					if (v != null) {
						for (SeatSelectedListener listener : seatSelectedListeners) {
							listener.onSeatSelected(v);
						}

						panelSoDoCho.setCurrentToa(toa);
					} else {
						JOptionPane.showMessageDialog(null, "Không thể giữ ghế (lỗi tạo vé).");
						panelSoDoCho.setCurrentToa(toa);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.execute();
	}

	/**
	 * Xử lý khi người dùng bấm vào một ghế ĐÃ ĐƯỢC CHỌN (để bỏ chọn).
	 */
	public void handleSeatDeselection(Toa toa, Ghe ghe) {
		if (toa == null || ghe == null || getBookingSession() == null) {
			return;
		}

		// 1. Lấy thông tin định danh của ghế
		String currentChuyenID = getSelectedChuyen().getChuyenID();
		String currentToaID = toa.getToaID();
		int currentSoGhe = ghe.getSoGhe();

		// 2. Lấy danh sách vé của CHUYẾN HIỆN TẠI
		List<VeSession> currentTripTickets = bookingSession.getSelectedTicketsForTrip(getCurrentTripIndex());

		// 3. Tìm VeSession THỰC SỰ đang có trong danh sách
		VeSession veToRemove = currentTripTickets.stream().filter(v -> v.getChuyenID().equals(currentChuyenID)
				&& v.getToaID().equals(currentToaID) && v.getSoGhe() == currentSoGhe).findFirst().orElse(null);

		if (veToRemove != null) {
			// 4. Gọi onRemoveVe với ĐÚNG đối tượng đã tìm thấy
			onRemoveVe(veToRemove);
		} else {
			// Lỗi: Không tìm thấy vé để xóa
			if (selectedToa != null) {
				panelSoDoCho.setCurrentToa(selectedToa);
			}
		}
	}

	// user clicked trash icon or timer expired -> remove ticket
	public void onRemoveVe(VeSession v) {
		if (v == null || bookingSession == null) {
			return;
		}

		// Xác định đúng danh sách cần xóa (chiều đi hay về)
		boolean removed = false;
		if (currentTripIndex == 0) {
			removed = bookingSession.removeOutboundTicket(v);
		} else {
			removed = bookingSession.removeReturnTicket(v);
		}

		// Luôn refresh sơ đồ ghế để cập nhật màu sắc
		if (selectedToa != null) {
			refreshSeatOnDelete(v);
		}
	}

	/**
	 * Hàm này được gọi khi một vé bị xóa TỪ BẤT CỨ ĐÂU. Nó kiểm tra và cập nhật lại
	 * PanelSoDoCho NẾU cần thiết. * @param veSessionBiXoa Vé vừa bị xóa khỏi
	 * BookingSession
	 */
	public void refreshSeatOnDelete(VeSession veSessionBiXoa) {
		if (veSessionBiXoa == null) {
			return;
		}

		// Kiểm tra xem vé vừa bị xóa có nằm trên SƠ ĐỒ đang hiển thị không
		if (selectedToa != null && selectedChuyen != null && veSessionBiXoa.getToaID().equals(selectedToa.getToaID())
				&& veSessionBiXoa.getChuyenID().equals(selectedChuyen.getChuyenID())) {
			// Có, vé này nằm trên sơ đồ đang xem.
			// Yêu cầu PanelSoDoCho cập nhật MỘT nút (false = bỏ chọn)
			panelSoDoCho.updateSeatVisual(veSessionBiXoa.getSoGhe(), false);
		}
		// Nếu không (vé bị xóa ở toa khác/chuyến khác), thì không làm gì cả.
	}

	public void releaseHoldAndRemoveVe(VeSession v) {
		if (v == null || bookingSession == null) {
			return;
		}

		boolean removedOutbound = bookingSession.removeOutboundTicket(v);
		boolean removedReturn = bookingSession.removeReturnTicket(v);

		bookingSession.removeVeSession(v);

		datChoBUS.xoaPhieuGiuChoChiTietByPgcctID(v.getPhieuGiuChoChiTiet().getPhieuGiuChoChiTietID());
		if (bookingSession.getOutboundSelectedTickets().size() == 0
				&& bookingSession.getReturnSelectedTickets().size() == 0) {
			datChoBUS.xoaPhieuGiuCho(bookingSession.getPhieuGiuCho().getPhieuGiuChoID());
		}

		SwingUtilities.invokeLater(
				() -> JOptionPane.showMessageDialog(null, "Giữ chỗ cho vé " + v.prettyString() + " đã hết hạn."));

		// Refresh sơ đồ ghế nếu vé hết hạn thuộc toa đang xem
		if (selectedToa != null && v.getToaID().equals(selectedToa.getToaID()) && (removedOutbound || removedReturn)) {
			// Kiểm tra xem vé có thuộc CHUYẾN ĐANG XEM không
			if (selectedChuyen != null && v.getChuyenID().equals(selectedChuyen.getChuyenID())) {
				panelSoDoCho.updateSeatVisual(v.getSoGhe(), false);
			}
		}
	}

	private VeSession createVeSessionForSeat(Toa toa, Ghe ghe, int tripIndex) {
		SearchCriteria criteria = (tripIndex == 0) ? bookingSession.getOutboundCriteria()
				: bookingSession.getReturnCriteria();

		if (criteria == null) {
			System.err.println("createVeSessionForSeat: Không tìm thấy SearchCriteria cho tripIndex " + tripIndex);
			return null;
		}

		String chuyenID = selectedChuyen.getChuyenID();
		String tauID = selectedChuyen.getTau().getTauID();

		// Dùng criteria
		String tenGaDi = criteria.getGaDiName();
		String maGaDi = criteria.getGaDiId();
		String tenGaDen = criteria.getGaDenName();
		String maGaDen = criteria.getGaDenId();

		LocalDate ngayDi = selectedChuyen.getNgayDi();
		LocalTime gioDi = selectedChuyen.getGioDi();
		String toaID = (toa != null) ? toa.getToaID() : null;
		String hangToa = toa.getHangToa().toString();
		int soToa = toa.getSoToa();
		String gheID = ghe.getGheID();
		int soGhe = ghe.getSoGhe();
		LocalDateTime thoiDiemHetHan = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);

		int gia = chuyenBUS.layGiaGheTheoPhanDoan(chuyenID, criteria.getGaDiId(), criteria.getGaDenId(),
				selectedChuyen.getTau().getLoaiTau().toString(), toa.getHangToa().toString());

		String khuyenMaiCode = "";
		int giam = 0;

		return new VeSession(chuyenID, tauID, tenGaDi, maGaDi, tenGaDen, maGaDen, ngayDi, gioDi, toaID, hangToa, soToa,
				gheID, soGhe, gia, khuyenMaiCode, giam, thoiDiemHetHan);

	}

	public Set<Integer> getSelectedSoGhe(Toa currentToa) {
		if (currentToa == null || getSelectedChuyen() == null || getBookingSession() == null) {
			return Collections.emptySet();
		}

		String currentChuyenID = getSelectedChuyen().getChuyenID();
		String currentToaID = currentToa.getToaID();

		Set<Integer> selectedSoGheSet = getBookingSession().getSelectedTicketsForTrip(getCurrentTripIndex()).stream()
				.filter(v -> currentChuyenID.equals(v.getChuyenID()) && currentToaID.equals(v.getToaID()))
				.map(VeSession::getSoGhe).collect(Collectors.toSet());

		return selectedSoGheSet;
	}

	public BookingSession getBookingSession() {
		return bookingSession;
	}

	public Chuyen getSelectedChuyen() {
		return selectedChuyen;
	}

	public void setSelectedChuyen(Chuyen selectedChuyen) {
		this.selectedChuyen = selectedChuyen;
	}

	public Chuyen_BUS getChuyenBUS() {
		return chuyenBUS;
	}
}
