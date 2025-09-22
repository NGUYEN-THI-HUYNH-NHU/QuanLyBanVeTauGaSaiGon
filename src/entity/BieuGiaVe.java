package entity;
/*
 * @(#) BangGia.java  1.0  [12:54:26 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

import entity.type.HangToa;

import java.time.LocalDate;
import java.time.LocalDateTime;

import entity.type.LoaiTau;

public class BieuGiaVe {
	private String bieuGiaVeID;
	private Tuyen tuyenApDung;
	private HangToa hangToaApDung;
	private LoaiTau loaiTauApDung;
	private int minKm;
	private int maxKm;
	private double donGiaTrenKm;
	private double giaCoDinh;
	private double phuPhiCaoDiem;
	private LocalDate ngayCoHieuLuc;
	private LocalDate ngayHetHieuLuc;
	private int doUuTien;
	private boolean isCoHieuLuc;
	private LocalDateTime thoiDiemTao;
	private LocalDateTime thoiDiemSua;
}