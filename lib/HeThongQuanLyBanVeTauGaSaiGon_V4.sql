USE MASTER
ALTER DATABASE HeThongQuanLyBanVeTauGaSaiGon_V4
SET SINGLE_USER
WITH ROLLBACK IMMEDIATE;
DROP DATABASE HeThongQuanLyBanVeTauGaSaiGon_V4;

use master 
create database HeThongQuanLyBanVeTauGaSaiGon_V4
use HeThongQuanLyBanVeTauGaSaiGon_V4

-- =========================
-- Lookup tables
-- =========================
CREATE TABLE LoaiTau (
    loaiTauID VARCHAR(50) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE HangToa (
    hangToaID VARCHAR(50) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE LoaiDoiTuong (
    loaiDoiTuongID VARCHAR(50) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE VaiTroNhanVien (
    vaiTroNhanVienID VARCHAR(50) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE VaiTroTaiKhoan (
    vaiTroTaiKhoanID VARCHAR(50) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);
-- =========================
-- Stations, Routes, Trains, Carriages, Seats
-- =========================
CREATE TABLE Ga (
    gaID VARCHAR(50) NOT NULL PRIMARY KEY,
    tenGa NVARCHAR(255) NOT NULL,
	isGaLon BIT NOT NULL DEFAULT 1,
    tinhThanh NVARCHAR(255) NULL
);

CREATE TABLE Tuyen (
    tuyenID VARCHAR(50) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

-- TuyenChiTiet: sequence of stations for a route
CREATE TABLE TuyenChiTiet(
    tuyenID VARCHAR(50) NOT NULL,
    gaID VARCHAR(50) NOT NULL,
    thuTu INT NOT NULL CHECK (thuTu > 0),
	khoangCachTuGaXuatPhatKm INT NOT NULL CHECK (khoangCachTuGaXuatPhatKm > 0),
    CONSTRAINT PK_TuyenChiTiet PRIMARY KEY (tuyenID, gaID),
    CONSTRAINT FK_TuyenChiTiet_Tuyen FOREIGN KEY (tuyenID) REFERENCES Tuyen(tuyenID),
    CONSTRAINT FK_TuyenChiTiet_Ga FOREIGN KEY (gaID) REFERENCES Ga(gaID),
	CONSTRAINT UQ_TuyenChiTiet_Tuyen_thuTu UNIQUE (tuyenID, thuTu)
);

CREATE TABLE Tau (
    tauID VARCHAR(50) NOT NULL PRIMARY KEY,
    tenTau NVARCHAR(255) NOT NULL,
    loaiTauID VARCHAR(50) NOT NULL,
    soLuongToa INT NOT NULL,
    trangThai NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_Tau_LoaiTau FOREIGN KEY (loaiTauID) REFERENCES LoaiTau(loaiTauID),
	CONSTRAINT CHK_Tau_trangThai CHECK (trangThai IN ('HOAT_DONG','KHONG_HOAT_DONG','BAO_TRI'))
);

CREATE TABLE Toa (
    toaID VARCHAR(50) NOT NULL PRIMARY KEY,
    tauID VARCHAR(50) NOT NULL,
    hangToaID VARCHAR(50) NOT NULL,
    sucChua INT NOT NULL,
    soToa INT NOT NULL,
    CONSTRAINT FK_Toa_Tau FOREIGN KEY (tauID) REFERENCES Tau(tauID),
    CONSTRAINT FK_Toa_HangToa FOREIGN KEY (hangToaID) REFERENCES HangToa(hangToaID),
	CONSTRAINT UQ_Toa_Tau_SoToa UNIQUE (tauID, soToa)
);

CREATE TABLE Ghe (
    gheID VARCHAR(50) NOT NULL PRIMARY KEY,
    toaID VARCHAR(50) NOT NULL,
    soGhe INT NOT NULL,
    CONSTRAINT FK_Ghe_Toa FOREIGN KEY (toaID) REFERENCES Toa(toaID),
	CONSTRAINT UQ_Ghe_Toa_SoGhe UNIQUE (toaID, soGhe)
);

-- useful indexes
CREATE NONCLUSTERED INDEX IDX_Toa_Tau ON Toa(tauID);
CREATE NONCLUSTERED INDEX IDX_Ghe_Toa ON Ghe(toaID);

-- =========================
-- Chuyen (Trip) and stops (ChuyenGa)
-- =========================
CREATE TABLE Chuyen (
    chuyenID VARCHAR(50) NOT NULL PRIMARY KEY,
    tuyenID VARCHAR(50) NOT NULL,
    tauID VARCHAR(50) NOT NULL,
    ngayDi DATE NOT NULL,
    gioDi TIME NULL,
    CONSTRAINT FK_Chuyen_Tuyen FOREIGN KEY (tuyenID) REFERENCES Tuyen(tuyenID),
    CONSTRAINT FK_Chuyen_Tau FOREIGN KEY (tauID) REFERENCES Tau(tauID)
);
CREATE NONCLUSTERED INDEX IDX_Chuyen_Tuyen_NgayDi_GioDi ON Chuyen(tuyenID, ngayDi, gioDi);

CREATE TABLE ChuyenGa (
    chuyenID VARCHAR(50) NOT NULL,
    gaID VARCHAR(50) NOT NULL,
    thuTu INT NOT NULL CHECK (thuTu > 0),
    gioDen TIME NULL,
    gioDi TIME NULL,
	CONSTRAINT PK_ChuyenGa PRIMARY KEY (chuyenID, gaID),
    CONSTRAINT FK_ChuyenGa_Chuyen FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID),
    CONSTRAINT FK_ChuyenGa_Ga FOREIGN KEY (gaID) REFERENCES Ga(gaID),
    CONSTRAINT UQ_ChuyenGa_Chuyen_thuTu UNIQUE (chuyenID, thuTu)
);
CREATE NONCLUSTERED INDEX IDX_ChuyenGa_Ga_thuTu ON ChuyenGa(gaID, thuTu);
CREATE NONCLUSTERED INDEX IDX_ChuyenGa_Ga_Chuyen_thuTu ON ChuyenGa(gaID, chuyenID, thuTu);

-- =========================
-- Users, Staff, Accounts, Customers, Passengers
-- =========================
CREATE TABLE NhanVien (
    nhanVienID VARCHAR(50) NOT NULL PRIMARY KEY,
    vaiTronhanVienID VARCHAR(50) NOT NULL,
    hoTen NVARCHAR(255) NOT NULL,
    isNu BIT NOT NULL,
    ngaySinh DATE NOT NULL,
    soDienThoai NVARCHAR(50) NULL,
    email NVARCHAR(100) NULL UNIQUE,
    diaChi NVARCHAR(255) NULL,
    ngayThamGia DATE NOT NULL,
    isHoatDong BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_NhanVien_VaiTro FOREIGN KEY (vaiTronhanVienID) REFERENCES VaiTroNhanVien(vaiTronhanVienID)
);

CREATE TABLE KhachHang (
    khachHangID VARCHAR(50) NOT NULL PRIMARY KEY,
    hoTen NVARCHAR(255) NOT NULL,
    soDienThoai VARCHAR(10) NULL,
    email VARCHAR(100) NULL UNIQUE,
    diaChi NVARCHAR(255) NULL
);


CREATE TABLE HanhKhach (
    hanhKhachID VARCHAR(50) NOT NULL PRIMARY KEY,
    loaiDoiTuongID VARCHAR(50) NOT NULL,
    khachHangID VARCHAR(50) NULL,
    hoTen NVARCHAR(255) NOT NULL,
    soGiayTo NVARCHAR(100) NULL,
    CONSTRAINT FK_HanhKhach_LoaiDoiTuong FOREIGN KEY (loaiDoiTuongID) REFERENCES LoaiDoiTuong(loaiDoiTuongID),
    CONSTRAINT FK_HanhKhach_KhachHang FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID)
);

CREATE TABLE TaiKhoan (
    taiKhoanID VARCHAR(50) NOT NULL PRIMARY KEY,
    vaiTroTaiKhoanID VARCHAR(50) NOT NULL,
    nhanVienID VARCHAR(50) NOT NULL,
    tenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    matKhauHash NVARCHAR(500) NOT NULL,
    thoiDiemTao DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_TaiKhoan_VaiTro FOREIGN KEY (vaiTroTaiKhoanID) REFERENCES VaiTroTaiKhoan(vaiTroTaiKhoanID),
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID)
);
CREATE NONCLUSTERED INDEX IDX_TaiKhoan_NhanVien ON TaiKhoan(nhanVienID);

-- =====================================
-- DonDatCho, Ve, VePhanDoan, DonHoanDoi
-- =====================================
CREATE TABLE DonDatCho (
    donDatChoID VARCHAR(50) NOT NULL PRIMARY KEY,
    nhanVienID VARCHAR(50) NOT NULL,
    khachHangID VARCHAR(50) NOT NULL,
    thoiDiemDatCho DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_DonDatCho_KhachHang FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID),
    CONSTRAINT FK_DonDatCho_NhanVien FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID)
);
CREATE INDEX IDX_DonDatCho_NhanVien_ThoiDiem ON DonDatCho(nhanVienID, thoiDiemDatCho);

CREATE TABLE Ve (
    veID VARCHAR(50) NOT NULL PRIMARY KEY,
	hanhKhachID VARCHAR(50) NOT NULL,
    donDatChoID VARCHAR(50) NOT NULL,
    chuyenID VARCHAR(50) NOT NULL,
	gheID VARCHAR(50) NOT NULL,
	gaDiID VARCHAR(50) NOT NULL,
	gaDenID VARCHAR(50) NOT NULL,
	thuTuGaDi INT NOT NULL,
	thuTuGaDen INT NOT NULL,
	gia DECIMAL(12,2) NOT NULL DEFAULT 0,
	thoiDiemBan DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    trangThai NVARCHAR(50),
    CONSTRAINT FK_Ve_DonDatCho FOREIGN KEY (donDatChoID) REFERENCES DonDatCho(donDatChoID),
    CONSTRAINT FK_Ve_Chuyen FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID),
	CONSTRAINT FK_Ve_Ghe FOREIGN KEY (gheID) REFERENCES Ghe(gheID),
	CONSTRAINT FK_Ve_GaDi FOREIGN KEY (gaDiID) REFERENCES Ga(gaID),
    CONSTRAINT FK_Ve_GaDen FOREIGN KEY (gaDenID) REFERENCES Ga(gaID),
    CONSTRAINT FK_Ve_HanhKhach FOREIGN KEY (hanhKhachID) REFERENCES HanhKhach(hanhKhachID),
	CONSTRAINT CHK_Ve_trangThai CHECK (trangThai IN ('DA_BAN','DA_DUNG','HET_HAN','DA_HOAN','DA_DOI'))
);
CREATE INDEX IDX_Ve_Chuyen_Ghe_TrangThai ON Ve(trangThai, chuyenID, gheID);
CREATE INDEX IDX_Ve_Chuyen_GaDi_GaDen ON Ve(chuyenID, gaDiID, gaDenID);
CREATE NONCLUSTERED INDEX IDX_Ve_Chuyen_Ghe_thuTu
  ON Ve(chuyenID, gheID, thuTuGaDi, thuTuGaDen)
  INCLUDE (trangThai, gia, veID);

CREATE TABLE DonHoanDoi (
    donHoanDoiID VARCHAR(50) NOT NULL PRIMARY KEY,
    khachHangID VARCHAR(50) NOT NULL,
    nhanVienID VARCHAR(50) NOT NULL,
    laDonHoan BIT NOT NULL,
    ngayYeuCau DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    tongTienHoan DECIMAL(12,2) NOT NULL DEFAULT 0,
    trangThai VARCHAR(50) NOT NULL,
    CONSTRAINT FK_DonHoanDoi_KhachHang FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID),
    CONSTRAINT FK_DonHoanDoi_NhanVien FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID)
);

CREATE TABLE DonHoanDoiChiTiet (
    donHoanDoiChiTietID VARCHAR(50) NOT NULL PRIMARY KEY,
    donHoanDoiID VARCHAR(50) NOT NULL,
    veCuID VARCHAR(50) NOT NULL,
    veMoiID VARCHAR(50) NULL,
    soTienHoan DECIMAL(12,2) NOT NULL DEFAULT 0,
    phiPhatSinh DECIMAL(12,2) NOT NULL DEFAULT 0,
    ghiChu NVARCHAR(MAX) NULL,
    CONSTRAINT FK_DonHoanDoiChiTiet_DonHoanDoi FOREIGN KEY (donHoanDoiID) REFERENCES DonHoanDoi(donHoanDoiID),
    CONSTRAINT FK_DonHoanDoiChiTiet_VeCu FOREIGN KEY (veCuID) REFERENCES Ve(veID),
    CONSTRAINT FK_DonHoanDoiChiTiet_VeMoi FOREIGN KEY (veMoiID) REFERENCES Ve(veID)
);

-- =========================
-- PhieuGiuCho + ChiTiet (Hold + items). Fixed: add FKs and checks
-- =========================
CREATE TABLE PhieuGiuCho(
    phieuGiuChoID VARCHAR(50) NOT NULL PRIMARY KEY,
    nhanVienID VARCHAR(50) NOT NULL,
	thoiGianGiuChoS INT DEFAULT 600,
	thoiDiemTao DATETIME2 NOT NULL,
    trangThai NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_PhieuGiuCho_NhanVien FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID),
	CONSTRAINT CHK_PhieuGiuCho_trangThai CHECK (trangThai IN ('DANG_GIU', 'HET_HAN', 'XAC_NHAN'))
);

CREATE TABLE PhieuGiuChoChiTiet(
    phieuGiuChoChiTietID VARCHAR(50) NOT NULL PRIMARY KEY,
    phieuGiuChoID VARCHAR(50) NOT NULL,
    chuyenID VARCHAR(50) NOT NULL,
    gheID VARCHAR(50) NOT NULL,
    thuTuGaDi INT NOT NULL,
    thuTuGaDen INT NOT NULL,
    thoiDiemHetGiuCho DATETIME2 NOT NULL,
    trangThai NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_PGCCT_PhieuGiuCho FOREIGN KEY (phieuGiuChoID) REFERENCES PhieuGiuCho(phieuGiuChoID),
    CONSTRAINT FK_PGCCT_Chuyen FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID),
    CONSTRAINT FK_PGCCT_Ghe FOREIGN KEY (gheID) REFERENCES Ghe(gheID),
    CONSTRAINT CHK_PGCCT_thuTu CHECK (thuTuGaDi < thuTuGaDen),
	CONSTRAINT CHK_PGCCT_trangThai CHECK (trangThai IN ('DANG_GIU','HET_GIU','XAC_NHAN'))
);
CREATE NONCLUSTERED INDEX IDX_PGCC_Chuyen_Ghe_thuTu 
	ON PhieuGiuChoChiTiet(chuyenID, gheID, thuTuGaDi, thuTuGaDen);
CREATE NONCLUSTERED INDEX IDX_PGCC_Chuyen_Ghe_thuTu_thoiHan 
	ON PhieuGiuChoChiTiet(chuyenID, gheID, thuTuGaDi, thuTuGaDen)
	INCLUDE (trangThai, thoiDiemHetGiuCho);
-- =========================
-- Pricing tables
-- =========================
CREATE TABLE BieuGiaVe (
    bieuGiaVeID VARCHAR(50) NOT NULL PRIMARY KEY,
    hangToaApDungID VARCHAR(50) NOT NULL,
    loaiTauApDungID  VARCHAR(50) NOT NULL,
    minKm INT NOT NULL CHECK (minKm >= 0),
    maxKm INT NOT NULL CHECK (maxKm >= 0),
    donGiaTrenKm DECIMAL(10,4) NULL,
    giaCoBan DECIMAL(12,2) NULL,
    phuPhiCaoDiem DECIMAL(12,2) NULL,
    doUuTien INT NOT NULL DEFAULT 0,
	isCoHieuLuc BIT DEFAULT 1,
    CONSTRAINT FK_BieuGiaVe_LoaiTau FOREIGN KEY (loaiTauApDungID) REFERENCES LoaiTau(loaiTauID),
    CONSTRAINT FK_BieuGiaVe_HangToa FOREIGN KEY (hangToaApDungID) REFERENCES HangToa(hangToaID),
    CONSTRAINT CHK_BieuGiaVe_Gia CHECK ((donGiaTrenKm IS NOT NULL) OR (giaCoBan IS NOT NULL))
);
CREATE NONCLUSTERED INDEX IDX_BieuGiaVe_LoaiToaTau ON BieuGiaVe(hangToaApDungID, loaiTauApDungID);

CREATE TABLE HeSoGiaLoaiTau (
    hsgloaiTauID VARCHAR(50) NOT NULL PRIMARY KEY,
    loaiTauID VARCHAR(50) NOT NULL,
    hsg DECIMAL(5,2) NOT NULL,
	isCoHieuLuc BIT DEFAULT 1,
    CONSTRAINT FK_HsgLoaiTau_LoaiTau FOREIGN KEY (loaiTauID) REFERENCES LoaiTau(loaiTauID)
);

CREATE TABLE HeSoGiaHangToa (
    hsgHangToaID VARCHAR(50) NOT NULL PRIMARY KEY,
    hangToaID VARCHAR(50) NOT NULL,
    hsg DECIMAL(5,2) NOT NULL,
	isCoHieuLuc BIT DEFAULT 1,
    CONSTRAINT FK_HsgHangToa_HangToa FOREIGN KEY (hangToaID) REFERENCES HangToa(hangToaID)
);

-- =========================
-- Invoice / Payment
-- =========================
CREATE TABLE HoaDon (
    hoaDonID VARCHAR(50) NOT NULL PRIMARY KEY,
    khachHangID VARCHAR(50) NOT NULL,
    nhanVienID VARCHAR(50) NOT NULL,
    thoiDiemTao DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID),
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID)
);
CREATE NONCLUSTERED INDEX IDX_HoaDon_KhachHang ON HoaDon(khachHangID);

CREATE TABLE HoaDonChiTiet (
    hoaDonChiTietID VARCHAR(50) NOT NULL PRIMARY KEY,
    hoaDonID VARCHAR(50) NOT NULL,
	veID VARCHAR(50) NULL,
    tenDichVu VARCHAR(100) NOT NULL,
	donViTinh NVARCHAR(20) NULL,
	soLuong INT NOT NULL DEFAULT 1,
    donGia DECIMAL(12,2) NOT NULL,
	thue DECIMAL(12,2) NOT NULL DEFAULT 0,
    thanhTien DECIMAL(12,2) NOT NULL,
    CONSTRAINT FK_HoaDonChiTiet_HoaDon FOREIGN KEY (hoaDonID) REFERENCES HoaDon(hoaDonID),
    CONSTRAINT FK_HoaDonChiTiet_Ve FOREIGN KEY (veID) REFERENCES Ve(veID)
);

CREATE TABLE GiaoDichThanhToan (
    giaoDichThanhToanID VARCHAR(50) NOT NULL PRIMARY KEY,
    hoaDonID VARCHAR(50) NOT NULL,
    khachHangID VARCHAR(50) NOT NULL,
    tienNhan DECIMAL(12,2) NOT NULL,
	tienHoan DECIMAL(12,2) NULL,
    thoiDiemThanhToan DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    isThanhToanTienMat BIT NOT NULL DEFAULT 1,
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_GiaoDichThanhToan_HoaDon FOREIGN KEY (hoaDonID) REFERENCES HoaDon(hoaDonID),
    CONSTRAINT FK_GiaoDichThanhToan_KhachHang FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID)
);
CREATE NONCLUSTERED INDEX IDX_GiaoDichThanhToan_HoaDon ON GiaoDichThanhToan(hoaDonID);

-- =========================
-- Promotions
-- =========================
CREATE TABLE KhuyenMai (
    khuyenMaiID VARCHAR(50) NOT NULL PRIMARY KEY,
    maKhuyenMai VARCHAR(50) NOT NULL UNIQUE,
    moTa NVARCHAR(MAX) NULL,
    tyLeGiamGia DECIMAL(5,2) NULL CHECK (tyLeGiamGia BETWEEN 0 AND 100),
    tienGiamGia DECIMAL(12,2) NULL CHECK (tienGiamGia >= 0),
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    soLuong INT NOT NULL DEFAULT 0,
    gioiHanMoiKhachHang INT NOT NULL DEFAULT 0,
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT CHK_KhuyenMai_Ngay CHECK (ngayKetThuc >= ngayBatDau),
    CONSTRAINT CHK_KhuyenMai_GiamGia CHECK (tyLeGiamGia IS NOT NULL OR tienGiamGia IS NOT NULL)
);

CREATE TABLE DieuKienKhuyenMai (
    dieuKienID VARCHAR(50) NOT NULL PRIMARY KEY,
    khuyenMaiID VARCHAR(50) NOT NULL,
    tuyenID VARCHAR(50) NULL,
    loaiTauID VARCHAR(50) NULL,
    hangToaID VARCHAR(50) NULL,
    loaiDoiTuongID VARCHAR(50) NULL,
    ngayTrongTuan INT NULL CHECK (NgayTrongTuan BETWEEN 1 AND 7),
    ngayLe BIT NULL,
    minGiaTriDonHang DECIMAL(12,2) NULL,
    CONSTRAINT FK_DieuKienKhuyenMai_KhuyenMai FOREIGN KEY (khuyenMaiID) REFERENCES KhuyenMai(khuyenMaiID),
    CONSTRAINT FK_DieuKienKhuyenMai_Tuyen FOREIGN KEY (tuyenID) REFERENCES Tuyen(tuyenID),
    CONSTRAINT FK_DieuKienKhuyenMai_LoaiTau FOREIGN KEY (loaiTauID) REFERENCES LoaiTau(loaiTauID),
    CONSTRAINT FK_DieuKienKhuyenMai_HangToa FOREIGN KEY (hangToaID) REFERENCES HangToa(hangToaID),
    CONSTRAINT FK_DieuKienKhuyenMai_LoaiDoiTuong FOREIGN KEY (loaiDoiTuongID) REFERENCES LoaiDoiTuong(loaiDoiTuongID)
);

CREATE TABLE SuDungKhuyenMai (
    suDungKhuyenMaiID VARCHAR(50) NOT NULL PRIMARY KEY,
    khuyenMaiID VARCHAR(50) NOT NULL,
    hoaDonChiTietID VARCHAR(50) NOT NULL,
    khachHangID VARCHAR(50) NOT NULL,
    thoiDiemDung DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_SuDungKhuyenMai_KhuyenMai FOREIGN KEY (khuyenMaiID) REFERENCES KhuyenMai(khuyenMaiID),
    CONSTRAINT FK_SuDungKhuyenMai_HoaDonChiTiet FOREIGN KEY (hoaDonChiTietID) REFERENCES HoaDonChiTiet(hoaDonChiTietID),
    CONSTRAINT FK_SuDungKhuyenMai_KhachHang FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID)
);
CREATE NONCLUSTERED INDEX IDX_KhuyenMai_HoatDong ON KhuyenMai(ngayKetThuc) WHERE trangThai = 1;

-- =========================
-- VIP Service
-- =========================
CREATE TABLE DichVuPhongChoVIP (
    dichVuPhongChoVIPID VARCHAR(50) NOT NULL PRIMARY KEY,
    gia DECIMAL(8,2) NOT NULL CHECK (Gia >= 0),
    moTa NVARCHAR(MAX) NULL,
    ngayCoHieuLuc DATE NOT NULL,
    ngayHetHieuLuc DATE NOT NULL,
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT CHK_DichVuPhongVIP_Ngay CHECK (ngayHetHieuLuc >= ngayCoHieuLuc)
);

CREATE TABLE PhieuDungPhongVIP (
    phieuDungPhongVIPID VARCHAR(50) NOT NULL PRIMARY KEY,
    hoaDonChiTietID VARCHAR(50) NOT NULL,
    dichVuPhongChoVIPID VARCHAR(50) NOT NULL,
    hanhKhachID VARCHAR(50) NOT NULL,
    CONSTRAINT FK_PhieuDungPhongVIP_HoaDonChiTiet FOREIGN KEY (hoaDonChiTietID) REFERENCES HoaDonChiTiet(hoaDonChiTietID),
    CONSTRAINT FK_PhieuDungPhongVIP_DichVu FOREIGN KEY (dichVuPhongChoVIPID) REFERENCES DichVuPhongChoVIP(dichVuPhongChoVIPID),
    CONSTRAINT FK_PhieuDungPhongVIP_HanhKhach FOREIGN KEY (hanhKhachID) REFERENCES HanhKhach(hanhKhachID)
);

-- =========================
-- Audit log
-- =========================
CREATE TABLE NhatKyAudit (
    nhatKyID VARCHAR(50) NOT NULL PRIMARY KEY,
    veID VARCHAR(50) NULL,
    nhanVienID VARCHAR(50) NULL,
    thoiDiemThaoTac DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    loaiThaoTac NVARCHAR(50) NULL,
    chiTiet NVARCHAR(MAX) NULL
);