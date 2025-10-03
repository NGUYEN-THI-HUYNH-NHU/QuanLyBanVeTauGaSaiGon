package entity;
/*
 * @(#) Chuyen.java  1.0  [10:09:55 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.Objects;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

public class Chuyen {
	private String chuyenID;
	private Tuyen tuyen;
	private Tau tau;
	private LocalDateTime ngayDi;
	private LocalDateTime gioDi;
	
	public Chuyen(String chuyenID, Tuyen tuyen, Tau tau, LocalDateTime ngayDi, LocalDateTime gioDi) {
		super();
		this.chuyenID = chuyenID;
		this.tuyen = tuyen;
		this.tau = tau;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
	}
	
	public Chuyen(String chuyenID, Tau tau, LocalDateTime ngayDi, LocalDateTime gioDi) {
		super();
		this.chuyenID = chuyenID;
		this.tau = tau;
		this.ngayDi = ngayDi;
		this.gioDi = gioDi;
	}

	public String getChuyenID() {
		return chuyenID;
	}

	public void setChuyenID(String chuyenID) {
		if(chuyenID != null && !chuyenID.trim().isEmpty()){
			this.chuyenID = chuyenID;
		}else{
			throw new IllegalArgumentException("ChuyenID không được rỗng");
		}
	}

	public Tuyen getTuyen() {
		return tuyen;
	}

	public void setTuyen(Tuyen tuyen) {
		if(tuyen != null) {
			this.tuyen = tuyen;
		}else{
			throw new IllegalArgumentException("Tuyến không được rỗng");
		}
	}

	public Tau getTau() {
		return tau;
	}

	public void setTau(Tau tau) {
		if(tau != null) {
			this.tau = tau;
		}else{
			throw new IllegalArgumentException("Tàu không được rỗng");
		}
	}

	public LocalDateTime getNgayDi() {
		return ngayDi;
	}

	public void setNgayDi(LocalDateTime ngayDi) {
		if(ngayDi == null || ngayDi.isBefore(LocalDateTime.now())) {
			throw new IllegalArgumentException("Ngày đi không được rỗng và phải là ngày trong tương lai");
		}
		this.ngayDi = ngayDi;
	}

	public LocalDateTime getGioDi() {
		return gioDi;
	}

	public void setGioDi(LocalDateTime gioDi) {
		if(gioDi == null || (ngayDi != null && gioDi.isBefore(ngayDi))) {
			throw new IllegalArgumentException("Giờ đi không được rỗng và phải sau ngày đi");
		}
		this.gioDi = gioDi;
	}

	@Override
	public String toString() {
		return chuyenID + ";" + tuyen + ";" + tau + ";"
				+ ngayDi + ";" + gioDi;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Chuyen chuyen = (Chuyen) o;
		return Objects.equals(chuyenID, chuyen.chuyenID);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(chuyenID);
	}
}