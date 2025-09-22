package entity;
/*
 * @(#) PhieuGiuCho.java  1.0  [6:10:44 PM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

public class PhieuGiuCho {
	private String phieuGiuChoID;
	private DonDatCho donDatCho;
	private Chuyen chuyen;
	private Ghe ghe;
	private int sttGaDi;
	private int sttGaDen;
	private LocalDateTime ngayGioDat;
	private LocalDateTime ngayGioHetHan;
	private boolean isDaSuDung;
}
