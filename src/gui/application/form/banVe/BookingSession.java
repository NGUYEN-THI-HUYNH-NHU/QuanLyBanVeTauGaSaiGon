package gui.application.form.banVe;
/*
 * @(#) SessionBanVe.java  1.0  [9:29:57 AM] Sep 30, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 30, 2025
 * @version: 1.0
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entity.Chuyen;
import entity.KhachHang;
import entity.PhieuGiuCho;

/**
 * BookingSession - lưu trạng thái phiên đặt vé. Lưu: search criteria/ results
 * cho cả 2 chiều (nếu có), và danh sách SelectedTicket cho mỗi chiều.
 *
 */
public class BookingSession {
	private static BookingSession instance;
	// Outbound (chiều đi)
	private SearchCriteria outboundCriteria;
	private List<Chuyen> outboundResults = new ArrayList<>();
	private final List<VeSession> outboundSelected = new ArrayList<>();
	// Return (chiều về) — có thể null nếu 1 chiều
	private SearchCriteria returnCriteria;
	private List<Chuyen> returnResults = new ArrayList<>();
	private final List<VeSession> returnSelected = new ArrayList<>();

	private KhachHang nguoiMua;
	private PhieuGiuCho pgc;

	public BookingSession() {
	}

	public static synchronized BookingSession getInstance() {
		if (instance == null) {
			instance = new BookingSession();
		}
		return instance;
	}

	public synchronized void setOutboundCriteria(SearchCriteria c) {
		this.outboundCriteria = c;
	}

	public synchronized SearchCriteria getOutboundCriteria() {
		return outboundCriteria;
	}

	public synchronized void setOutboundResults(List<Chuyen> results) {
		this.outboundResults = (results == null) ? new ArrayList<Chuyen>() : new ArrayList<Chuyen>(results);
	}

	public synchronized List<Chuyen> getOutboundResults() {
		return Collections.unmodifiableList(outboundResults);
	}

	// add/remove selected tickets for outbound
	public synchronized void addOutboundTicket(VeSession v) {
		if (v == null) {
			return;
		}
		if (!outboundSelected.contains(v)) {
			outboundSelected.add(v);
		}
	}

	public synchronized boolean removeOutboundTicket(VeSession v) {
		return outboundSelected.removeIf(x -> x.equals(v));
	}

	public synchronized List<VeSession> getOutboundSelectedTickets() {
		return Collections.unmodifiableList(new ArrayList<>(outboundSelected));
	}

	public synchronized void clearOutboundSelection() {
		outboundSelected.clear();
	}

	// --- Return getters / setters ---
	public synchronized void setReturnCriteria(SearchCriteria c) {
		this.returnCriteria = c;
	}

	public synchronized SearchCriteria getReturnCriteria() {
		return returnCriteria;
	}

	public synchronized void setReturnResults(List<Chuyen> results) {
		this.returnResults = (results == null) ? new ArrayList<>() : new ArrayList<>(results);
	}

	public synchronized List<Chuyen> getReturnResults() {
		return Collections.unmodifiableList(returnResults);
	}

	public KhachHang getNguoiMua() {
		return nguoiMua;
	}

	public void setNguoiMua(KhachHang nguoiMua) {
		this.nguoiMua = nguoiMua;
	}

	public synchronized void addReturnTicket(VeSession v) {
		if (v == null) {
			return;
		}
		if (!returnSelected.contains(v)) {
			returnSelected.add(v);
		}
	}

	public synchronized boolean removeReturnTicket(VeSession v) {
		return returnSelected.removeIf(x -> x.equals(v));
	}

	public synchronized List<VeSession> getReturnSelectedTickets() {
		return Collections.unmodifiableList(new ArrayList<>(returnSelected));
	}

	public synchronized void clearReturnSelection() {
		returnSelected.clear();
	}

	public synchronized void removeVeSession(VeSession v) {
		outboundResults.removeIf(x -> x.equals(v));
		returnResults.removeIf(x -> x.equals(v));
	}

	// --- Helpers ---
	public synchronized boolean isRoundTrip() {
		return outboundCriteria != null && outboundCriteria.isKhuHoi();
	}

	/**
	 * Lấy selected tickets theo tripIndex (0 = outbound, 1 = return)
	 */
	public synchronized List<VeSession> getSelectedTicketsForTrip(int tripIndex) {
		return tripIndex == 0 ? getOutboundSelectedTickets() : getReturnSelectedTickets();
	}

	/**
	 * Thêm ticket theo tripIndex
	 */
	public synchronized void addTicketForTrip(int tripIndex, VeSession v) {
		if (tripIndex == 0) {
			addOutboundTicket(v);
		} else {
			addReturnTicket(v);
		}
	}

	/**
	 * Xóa tất cả dữ liệu session (khi hủy hoặc hoàn tất)
	 */
	public synchronized void clearAll() {
		outboundCriteria = null;
		outboundResults.clear();
		outboundSelected.clear();
		returnCriteria = null;
		returnResults.clear();
		returnSelected.clear();
	}

	@Override
	public String toString() {
		return "BookingSession{" + "outBoundCriteria=" + outboundCriteria + ", outboundSelected=" + outboundSelected
				+ ", returnCriteria=" + returnCriteria + ", returnSelected=" + returnSelected + '}';
	}

	public PhieuGiuCho getPgc() {
		return pgc;
	}

	public void setPgc(PhieuGiuCho pgc) {
		this.pgc = pgc;
	}
}