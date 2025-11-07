package entity;

import java.util.Objects;

import entity.type.TrangThaiPDPVIP;

/*
 * @(#) PhieuDungPhongChoVIP.java  1.0  [3:45:53 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */
public class PhieuDungPhongVIP {
	private String phieuDungPhongChoVIPID;
	private DichVuPhongChoVIP dichVuPhongChoVIP;
	private Ve ve;
	private TrangThaiPDPVIP trangThai;

	public PhieuDungPhongVIP(String phieuDungPhongChoVIPID, DichVuPhongChoVIP dichVuPhongChoVIP, Ve ve,
			TrangThaiPDPVIP trangThai) {
		super();
		this.phieuDungPhongChoVIPID = phieuDungPhongChoVIPID;
		this.dichVuPhongChoVIP = dichVuPhongChoVIP;
		this.ve = ve;
		this.trangThai = trangThai;
	}

	public TrangThaiPDPVIP getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(TrangThaiPDPVIP trangThai) {
		this.trangThai = trangThai;
	}

	public Ve getVe() {
		return ve;
	}

	public void setVe(Ve ve) {
		this.ve = ve;
	}

	public String getPhieuDungPhongChoVIPID() {
		return phieuDungPhongChoVIPID;
	}

	public DichVuPhongChoVIP getDichVuPhongChoVIP() {
		return dichVuPhongChoVIP;
	}

	public void setPhieuDungPhongChoVIPID(String phieuDungPhongChoVIPID) {
		if(phieuDungPhongChoVIPID == null || phieuDungPhongChoVIPID.isEmpty()) {
			throw new IllegalArgumentException("PhieuDungPhongChoVIPID không được để trống!");
		}
		this.phieuDungPhongChoVIPID = phieuDungPhongChoVIPID;
	}


	public void setDichVuPhongChoVIP(DichVuPhongChoVIP dichVuPhongChoVIP) {
		this.dichVuPhongChoVIP = dichVuPhongChoVIP;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PhieuDungPhongVIP that = (PhieuDungPhongVIP) o;
		return Objects.equals(getPhieuDungPhongChoVIPID(), that.getPhieuDungPhongChoVIPID());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getPhieuDungPhongChoVIPID());
	}
}