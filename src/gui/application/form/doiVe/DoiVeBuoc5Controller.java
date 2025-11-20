package gui.application.form.doiVe;
/*
 * @(#) PanelBuoc2Controller.java  1.0  [12:53:22 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import entity.Ghe;
import entity.Toa;
import gui.application.form.banVe.PanelChieuLabel;
import gui.application.form.banVe.SearchCriteria;
import gui.application.form.banVe.VeSession;

public class DoiVeBuoc5Controller {
	private final PanelChieuLabel panelChieuLabel;
	private final PanelChuyenTauDoiVe panelChuyenTau;
	private final PanelDoanTauDoiVe panelDoanTau;
	private final PanelSoDoChoDoiVe panelSoDoCho;

	private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
	private final DatCho_BUS datChoBUS = new DatCho_BUS();

	private final List<SeatSelectedListener> seatSelectedListeners = new ArrayList<>();

	private ExchangeSession exchangeSession;
	private List<Chuyen> chuyenList;
	private Chuyen selectedChuyen;
	private Toa selectedToa;

	protected interface SeatSelectedListener {
		void onSeatSelected(VeSession v);

		void onSeatDeselected(VeSession v);
	}

	public void addSeatSelectedListener(SeatSelectedListener listener) {
		if (listener != null) {
			this.seatSelectedListeners.add(listener);
		}
	}

	public DoiVeBuoc5Controller(PanelChieuLabel chieuLabel, PanelChuyenTauDoiVe chuyenTau, PanelDoanTauDoiVe doanTau,
			PanelSoDoChoDoiVe soDoCho) {
		this.panelChieuLabel = chieuLabel;
		this.panelChuyenTau = chuyenTau;
		this.panelDoanTau = doanTau;
		this.panelSoDoCho = soDoCho;

		panelChuyenTau.setController(this);
		panelDoanTau.setController(this);
		panelSoDoCho.setController(this);

		this.exchangeSession = ExchangeSession.getInstance();
	}

	public void setExchangeSession(ExchangeSession s) {
		this.exchangeSession = s;
	}

	public SearchCriteria getCurrentTripCriteria() {
		return exchangeSession.getCriteriaTimKiem();
	}

	public int getGiaForTooltip(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID) {
		return chuyenBUS.layGiaGheTheoPhanDoan(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);
	}

	public void displayChuyenList(SearchCriteria criteria, List<Chuyen> chuyens) {
		if (criteria == null || chuyens == null || chuyens.isEmpty()) {
			return;
		}

		String gaDiName = criteria.getGaDiName();
		String gaDenName = criteria.getGaDenName();

		this.chuyenList = chuyens;
		panelChieuLabel.setText(gaDiName + " - " + gaDenName + ": "
				+ chuyens.get(0).getNgayDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
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
		SearchCriteria sc = exchangeSession.getCriteriaTimKiem();
		if (sc == null || selectedChuyen == null) {
			callback.accept(Collections.emptyList());
			return;
		}

		loadSeatsForToa(sc.getGaDiId(), sc.getGaDenId(), selectedChuyen.getChuyenID(), toa.getToaID(), callback);
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

		new SwingWorker<VeSession, Void>() {
			@Override
			protected VeSession doInBackground() throws Exception {
				VeSession v = createVeSessionForSeat(toa, ghe);
				if (v == null) {
					return null;
				}
				exchangeSession.addVeMoi(v);
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
		if (toa == null || ghe == null || getExchangeSession() == null) {
			return;
		}

		// 1. Lấy thông tin định danh của ghế
		String currentChuyenID = getSelectedChuyen().getChuyenID();
		String currentToaID = toa.getToaID();
		int currentSoGhe = ghe.getSoGhe();

		// 2. Lấy danh sách vé của CHUYẾN HIỆN TẠI
		List<VeSession> currentTripTickets = exchangeSession.getListVeMoiDangChon();

		// 3. Tìm VeSession THỰC SỰ đang có trong danh sách
		VeSession veToRemove = currentTripTickets.stream().filter(v -> v.getChuyenID().equals(currentChuyenID)
				&& v.getToaID().equals(currentToaID) && v.getSoGhe() == currentSoGhe).findFirst().orElse(null);

		if (veToRemove != null) {
			// Báo cho BanVe1Controller
			for (SeatSelectedListener listener : seatSelectedListeners) {
				listener.onSeatDeselected(veToRemove);
			}
		} else {
			// Lỗi: Không tìm thấy vé để xóa
			if (selectedToa != null) {
				panelSoDoCho.setCurrentToa(selectedToa);
			}
		}
	}

	// user clicked trash icon or timer expired -> remove ticket
	public void onRemoveVe(VeSession v) {
		exchangeSession.removeVeMoi(v);
		// Refresh UI
		if (selectedToa != null) {
			panelSoDoCho.updateSeatVisual(v.getSoGhe(), false);
		}
	}

	public void refreshSeatOnDelete(VeSession veSessionBiXoa) {
		if (veSessionBiXoa == null) {
			return;
		}
		panelSoDoCho.updateSeatVisual(veSessionBiXoa.getSoGhe(), false);
	}

	public void releaseHoldAndRemoveVe(VeSession v) {
		if (v == null || exchangeSession == null) {
			return;
		}

		exchangeSession.removeVeMoi(v);

		datChoBUS.xoaPhieuGiuChoChiTietByPgcctID(v.getPhieuGiuChoChiTiet().getPhieuGiuChoChiTietID());
		if (exchangeSession.getListVeMoiDangChon().size() == 0) {
			datChoBUS.xoaPhieuGiuCho(exchangeSession.getPhieuGiuCho().getPhieuGiuChoID());
		}

		SwingUtilities.invokeLater(
				() -> JOptionPane.showMessageDialog(null, "Giữ chỗ cho vé " + v.prettyString() + " đã hết hạn."));

		// Refresh sơ đồ ghế nếu vé hết hạn thuộc toa đang xem
		if (selectedToa != null && v.getToaID().equals(selectedToa.getToaID())) {
			// Kiểm tra xem vé có thuộc CHUYẾN ĐANG XEM không
			if (selectedChuyen != null && v.getChuyenID().equals(selectedChuyen.getChuyenID())) {
				panelSoDoCho.updateSeatVisual(v.getSoGhe(), false);
			}
		}
	}

	private VeSession createVeSessionForSeat(Toa toa, Ghe ghe) {
		SearchCriteria criteria = exchangeSession.getCriteriaTimKiem();

		if (criteria == null) {
			System.err.println("createVeSessionForSeat: Không tìm thấy SearchCriteria ");
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
		if (currentToa == null || selectedChuyen == null) {
			return Collections.emptySet();
		}

		String currentChuyenID = selectedChuyen.getChuyenID();
		String currentToaID = currentToa.getToaID();

		// Lọc từ list vé mới trong ExchangeSession
		return exchangeSession.getListVeMoiDangChon().stream()
				.filter(v -> currentChuyenID.equals(v.getChuyenID()) && currentToaID.equals(v.getToaID()))
				.map(VeSession::getSoGhe).collect(Collectors.toSet());
	}

	public ExchangeSession getExchangeSession() {
		return exchangeSession;
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
