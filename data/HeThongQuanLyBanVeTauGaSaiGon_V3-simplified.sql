--USE MASTER
--ALTER DATABASE HeThongQuanLyBanVeTauGaSaiGon_V3
--SET SINGLE_USER
--WITH ROLLBACK IMMEDIATE;
--DROP DATABASE HeThongQuanLyBanVeTauGaSaiGon_V3;

--use master 
--create database HeThongQuanLyBanVeTauGaSaiGon_V3
--use HeThongQuanLyBanVeTauGaSaiGon_V3


-- Bảng tra cứu chính
CREATE TABLE LoaiTau (
    loaiTauID VARCHAR(20) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE HangToa (
    hangToaID VARCHAR(20) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE HangKhachHang (
    hangKhachHangID VARCHAR(20) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE LoaiDoiTuong (
    loaiDoiTuongID VARCHAR(20) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE VaiTroNhanVien (
    vaiTroNhanVienID VARCHAR(20) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE VaiTroTaiKhoan (
    vaiTroTaiKhoanID VARCHAR(20) NOT NULL PRIMARY KEY,
    moTa NVARCHAR(255) NOT NULL
);

CREATE TABLE TrangThaiDatCho (
    trangThaiDatChoID VARCHAR(20) NOT NULL PRIMARY KEY,   -- ví dụ. PENDING, CONFIRMED, EXPIRED, CANCELLED
    moTa NVARCHAR(255) NOT NULL
);

-- Thực thể vận chuyển
CREATE TABLE Ga (
    gaID VARCHAR(20) NOT NULL PRIMARY KEY,
    tenGa NVARCHAR(255) NOT NULL,
    tinhThanh NVARCHAR(255)
);

CREATE TABLE Tuyen (
    tuyenID VARCHAR(20) NOT NULL PRIMARY KEY,
    gaDiID VARCHAR(20) NOT NULL,
    gaDenID VARCHAR(20) NOT NULL,
    khoangCachKm DECIMAL(10,2) NOT NULL,
    thoiGianDuKienPhut INT NOT NULL,
    CONSTRAINT FK_Tuyen_Ga_gaDiID FOREIGN KEY (gaDiID) REFERENCES Ga(gaID),
    CONSTRAINT FK_Tuyen_Ga_gaDenID FOREIGN KEY (gaDenID)   REFERENCES Ga(gaID)
);

CREATE TABLE Tau (
    tauID VARCHAR(20) NOT NULL PRIMARY KEY,
    tenTau NVARCHAR(255) NOT NULL,
    loaiTauID VARCHAR(20) NOT NULL,
    soLuongToa INT NOT NULL,
    trangThai NVARCHAR(20) NOT NULL CHECK (trangThai IN ('ACTIVE','INACTIVE','MAINTENANCE')),
    CONSTRAINT FK_Tau_LoaiTau_loaiTauID FOREIGN KEY (loaiTauID) REFERENCES LoaiTau(loaiTauID)
);

CREATE TABLE Toa (
    toaID VARCHAR(20) NOT NULL PRIMARY KEY,
    tauID VARCHAR(20) NOT NULL,
    hangToaID VARCHAR(20) NOT NULL,       -- ví dụ. ghế, SLEEPER, COMPARTMENT
    sucChua INT NOT NULL,
    soToa VARCHAR(20) NOT NULL,
    CONSTRAINT FK_Toa_Tau_tauID FOREIGN KEY (tauID) REFERENCES Tau(tauID),
    CONSTRAINT FK_Toa_HangToa_hangToaID FOREIGN KEY (hangToaID) REFERENCES HangToa(hangToaID)
);

CREATE TABLE Ghe (
    gheID VARCHAR(20) NOT NULL PRIMARY KEY,
    toaID VARCHAR(20) NOT NULL,
    soGhe NVARCHAR(20) NOT NULL,
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_Ghe_Toa_toaID FOREIGN KEY (toaID) REFERENCES Toa(toaID)
);

-- Chỉ mục để tăng tốc tra cứu (ví dụ: tìm tất cả toa của một tàu, ghế của một toa)
CREATE NONCLUSTERED INDEX IDX_Toa_Tau ON Toa(tauID);
CREATE NONCLUSTERED INDEX IDX_Ghe_Toa ON Ghe(toaID);

-- Bảng Chuyen và các phân đoạn của Tuyen
CREATE TABLE Chuyen (
    chuyenID VARCHAR(20) NOT NULL PRIMARY KEY,
    tuyenID VARCHAR(20) NOT NULL,
    tauID VARCHAR(20) NOT NULL,
    gioKhoiHanh DATETIME2 NOT NULL,
    gioDen DATETIME2 NOT NULL,
    CONSTRAINT FK_Chuyen_Tuyen_tuyenID FOREIGN KEY (tuyenID) REFERENCES Tuyen(tuyenID),
    CONSTRAINT FK_Chuyen_Tau_tauID FOREIGN KEY (tauID) REFERENCES Tau(tauID)
);

CREATE TABLE ChuyenGa (
    chuyenGaID VARCHAR(20) NOT NULL PRIMARY KEY,
    chuyenID VARCHAR(20) NOT NULL,
    gaID VARCHAR(20) NOT NULL,
    thuTu INT NOT NULL CHECK (thuTu > 0),
    gioDen DATETIME2,
    gioKhoiHanh DATETIME2,
    CONSTRAINT FK_ChuyenGa_Chuyen_chuyenID FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID),
    CONSTRAINT FK_ChuyenGa_Ga_gaID FOREIGN KEY (gaID) REFERENCES Ga(gaID),
	CONSTRAINT UQ_ChuyenGa_Chuyen_ThuTu UNIQUE (chuyenID, thuTu),
    CONSTRAINT UQ_ChuyenGa_Chuyen_Ga UNIQUE (chuyenID, gaID)
);

--change
CREATE TABLE DoanDuong (
    doanDuongID VARCHAR(20) PRIMARY KEY,
    chuyenID VARCHAR(20) NOT NULL,
    gaDiID VARCHAR(20) NOT NULL,
    gaDenID VARCHAR(20) NOT NULL,
    thuTuGaDi INT NOT NULL,
    thuTuGaDen INT NOT NULL,
    khoangCachKm DECIMAL(10,2) NULL,
    thoiGianDuKienPhut INT NULL,
    CONSTRAINT FK_DoanDuong_Chuyen FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID),
    CONSTRAINT FK_DoanDuong_GaDi FOREIGN KEY (GaDiID) REFERENCES Ga(GaID),
    CONSTRAINT FK_DoanDuong_GaDen FOREIGN KEY (GaDenID) REFERENCES Ga(GaID),
    CONSTRAINT CHK_DoanDuong_ThuTu CHECK (ThuTuGaDi < ThuTuGaDen)
);
CREATE INDEX IDX_DoanDuong_Chuyen_ThuTu ON DoanDuong(chuyenID, thuTuGaDi, thuTuGaDen);

-- Chỉ mục để tối ưu tìm kiếm chuyến và truy vấn chiếm dụng ghế
CREATE NONCLUSTERED INDEX IDX_Chuyen_Tuyen_KhoiHanh ON Chuyen(tuyenID, gioKhoiHanh);
CREATE NONCLUSTERED INDEX IDX_ChuyenGa_Ga_thuTu ON ChuyenGa(gaID, thuTu);

-- Nhân viên, Khách hàng, Hành khách, Tài khoản
CREATE TABLE NhanVien (
    nhanVienID VARCHAR(20) NOT NULL PRIMARY KEY,
    vaiTroNhanVienID VARCHAR(20) NOT NULL,
    hoTen NVARCHAR(255) NOT NULL,
    soDienThoai NVARCHAR(20),
    email NVARCHAR(100) UNIQUE,         -- email duy nhất cho nhân viên
    diaChi NVARCHAR(255),
    ngayThamGia DATE NOT NULL,
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_NhanVien_VaiTroNhanVien_vaiTroNhanVienID FOREIGN KEY (vaiTroNhanVienID) REFERENCES VaiTroNhanVien(vaiTroNhanVienID)
);

CREATE TABLE KhachHang (
    khachHangID VARCHAR(20) NOT NULL PRIMARY KEY,
    hangKhachHangID VARCHAR(20) NOT NULL,
    hoTen NVARCHAR(255) NOT NULL,
    soDienThoai NVARCHAR(20),
    email NVARCHAR(100) UNIQUE,         -- email duy nhất cho khách hàng
    diaChi NVARCHAR(255),
    CONSTRAINT FK_KhachHang_HangKhachHang_hangKhachHangID FOREIGN KEY (hangKhachHangID) REFERENCES HangKhachHang(hangKhachHangID)
);

CREATE TABLE HanhKhach (
    hanhKhachID VARCHAR(20) NOT NULL PRIMARY KEY,
    loaiDoiTuongID VARCHAR(20) NOT NULL,
    khachHangID VARCHAR(20) NULL,    -- một hành khách có thể hoặc không là khách hàng
    hoTen NVARCHAR(255) NOT NULL,
    soGiayTo NVARCHAR(100) UNIQUE, -- ví dụ. passport hoặc CCCD
    CONSTRAINT FK_HanhKhach_LoaiDoiTuong_loaiDoiTuongID FOREIGN KEY (loaiDoiTuongID) REFERENCES LoaiDoiTuong(loaiDoiTuongID),
    CONSTRAINT FK_HanhKhach_KhachHang_khachHangID FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID)
);

CREATE TABLE TaiKhoan (
    taiKhoanID VARCHAR(20) NOT NULL PRIMARY KEY,
    vaiTroTaiKhoanID VARCHAR(20) NOT NULL,
    nhanVienID VARCHAR(20) NOT NULL,
    tenDangNhap VARCHAR(20) NOT NULL UNIQUE,
    matKhauHash VARBINARY(MAX) NOT NULL,
    thoiDiemTao DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_TaiKhoan_VaiTroTaiKhoan_vaiTroTaiKhoanID FOREIGN KEY (vaiTroTaiKhoanID) REFERENCES VaiTroTaiKhoan(vaiTroTaiKhoanID),
    CONSTRAINT FK_TaiKhoan_NhanVien_nhanVienID FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID)
);

-- Chỉ mục để tra cứu tài khoản
CREATE NONCLUSTERED INDEX IDX_TaiKhoan_NhanVien ON TaiKhoan(nhanVienID);

-- Đặt chỗ và phát hành vé
CREATE TABLE DonDatCho (
    donDatChoID VARCHAR(20) NOT NULL PRIMARY KEY,
    khachHangID VARCHAR(20) NOT NULL,
	chuyenID VARCHAR(20) NOT NULL,
    thoiDiemDatCho DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    thoiDiemHetHan DATETIME2 NOT NULL,
    tongTien DECIMAL(12,2) NOT NULL DEFAULT 0,
    trangThaiDatChoID VARCHAR(20) NOT NULL, -- Khóa ngoại tới bảng TrangThaiDatCho (trạng thái đặt chỗ)
    CONSTRAINT FK_DonDatCho_KhachHang_khachHangID FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID),
    CONSTRAINT FK_DonDatCho_TrangThaiDatCho_trangThaiDatChoID FOREIGN KEY (trangThaiDatChoID) REFERENCES TrangThaiDatCho(trangThaiDatChoID),
	CONSTRAINT FK_DonDatCho_Chuyen FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID)
);

CREATE TABLE DonDatChoChiTiet (
    donDatChoChiTietID VARCHAR(20) PRIMARY KEY,
    donDatChoID VARCHAR(20) NOT NULL,
    gheID VARCHAR(20) NULL,
    hanhKhachID VARCHAR(20) NULL,
    thuTuGaDi INT NOT NULL,
	thuTuGaDen INT NOT NULL,
    gia DECIMAL(12,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_DonDatChoChiTiet_DonDatCho_donDatChoID FOREIGN KEY (donDatChoID) REFERENCES DonDatCho(donDatChoID),
    CONSTRAINT FK_DonDatChoChiTiet_DonDatCho_Ghe FOREIGN KEY (gheID) REFERENCES Ghe(gheID),
    CONSTRAINT FK_DonDatChoChiTiet_HanhKhach FOREIGN KEY (hanhKhachID) REFERENCES HanhKhach(hanhKhachID),
    CONSTRAINT CHK_DonDatChoChiTiet_ThuTu CHECK (thuTuGaDi < thuTuGaDen)
);

CREATE INDEX IDX_DonDatCho_chuyen ON DonDatCho(chuyenID);
CREATE INDEX IDX_DonDatChoChiTiet_ghe ON DonDatChoChiTiet(gheID);

CREATE TABLE Ve (
    veID VARCHAR(20) NOT NULL PRIMARY KEY,
    donDatChoID VARCHAR(20) NOT NULL,
	chuyenID VARCHAR(20) NOT NULL,
    gheID VARCHAR(20) NOT NULL,
    hanhKhachID VARCHAR(20) NOT NULL,
	thuTuGaDi INT NOT NULL,
	thuTuGaDen INT NOT NULL,
    gia DECIMAL(12,2) NOT NULL CHECK (gia >= 0),
    trangThai NVARCHAR(20) NOT NULL CHECK (trangThai IN ('RESERVED','BOOKED','USED','EXPIRED','REFUNDED','EXCHANGED')),
	ngayBan DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_Ve_Chuyen_chuyenID FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID),
    CONSTRAINT FK_Ve_Ghe_gheID FOREIGN KEY (gheID) REFERENCES Ghe(gheID),
    CONSTRAINT FK_Ve_HanhKhach_hanhKhachID FOREIGN KEY (hanhKhachID) REFERENCES HanhKhach(hanhKhachID),
    CONSTRAINT FK_Ve_DonDatCho_donDatChoID FOREIGN KEY (donDatChoID) REFERENCES DonDatCho(donDatChoID),
	CONSTRAINT CHK_Ve_ThoiGian CHECK (thuTuGaDi < thuTuGaDen)
);

-- Chỉ mục để tăng tốc truy vấn vé
CREATE INDEX IDX_Ve_chuyen_ghe ON Ve(chuyenID, gheID);
CREATE INDEX IDX_Ve_trangThai ON Ve(trangThai);

----PhanDoanChiemDung là bảng tối ưu hoá lưu các phân đoạn đã bị chiếm dụng (materialized): 
----mỗi dòng biểu diễn một đoạn (thuTugaDi → thuTugaDen) của một ghế (gheID) trên một chuyến (chuyenID) đã được bán (từ Ve). Mục đích chính:
----Tăng tốc kiểm tra khả dụng ghế khi bán/giữ chỗ: thay vì phải join/so sánh toàn bộ Ve (có thể nhiều) ta dò nhanh PhanDoanChiemDung với chỉ mục (chuyenID, gheID, thuTuGaDi, thuTuGaDen).
----Dễ xử lý chồng lấp đoạn (overlap) bằng điều kiện so sánh số (thuTu), rất nhanh với index.
----Tách concern: Ve giữ thông tin vé; PhanDoanChiemDung tối ưu hoá truy vấn sẵn cho nghiệp vụ đặt ghế.
--CREATE TABLE PhanDoanChiemDung (
--    phanDoanChiemDungID VARCHAR(20) NOT NULL PRIMARY KEY,
--    veID VARCHAR(20) NOT NULL,
--	chuyenID VARCHAR(20) NOT NULL,
--	gheID VARCHAR(20) NOT NULL,
--	thuTuGaDi INT NOT NULL,
--	thuTuGaDen INT NOT NULL,
--    CONSTRAINT FK_PhanDoanChiemDung_Ve_veID FOREIGN KEY (veID) REFERENCES Ve(veID),
--	CONSTRAINT FK_PhanDoanChiemDung_Chuyen_chuyenID FOREIGN KEY (chuyenID) REFERENCES Chuyen(chuyenID),
--	CONSTRAINT FK_PhanDoanChiemDung_Ghe_gheID FOREIGN KEY (gheID) REFERENCES Ghe(gheID),
--	CONSTRAINT CHK_PhanDoanChiemDung_ThuTu CHECK (thuTuGaDi < thuTuGaDen)
--);

-- Bảng biểu giá và hệ số điều chỉnh
CREATE TABLE BieuGiaVe (
    bieuGiaveID VARCHAR(20) NOT NULL PRIMARY KEY,
    tuyenApDungID VARCHAR(20) NOT NULL,
    hangToaApDungID VARCHAR(20) NOT NULL,
    LoaiTauApDungID  VARCHAR(20) NOT NULL,
    minKm INT NOT NULL CHECK (minKm >= 0),
    maxKm INT NOT NULL CHECK (maxKm >= 0),
    donGiaTrenKm DECIMAL(10,4) NULL,
    giaCoDinh DECIMAL(12,2) NULL,
    phuPhiCaoDiem DECIMAL(5,2) NULL,
    ngayCoHieuLuc DATE NOT NULL,
    ngayHetHieuLuc DATE NOT NULL,
    doUuTien INT NOT NULL DEFAULT 0,
    isCoHieuLuc BIT NOT NULL DEFAULT 1,
    thoiDiemTao DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    thoiDiemSua DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_BieuGiaVe_Tuyen_tuyenApDungID FOREIGN KEY (tuyenApDungID) REFERENCES Tuyen(tuyenID),
    CONSTRAINT FK_BieuGiaVe_LoaiTau_LoaiTauApDungID FOREIGN KEY (LoaiTauApDungID) REFERENCES LoaiTau(loaiTauID),
    CONSTRAINT FK_BieuGiaVe_HangToa_hangToaApDungID FOREIGN KEY (hangToaApDungID) REFERENCES HangToa(hangToaID),
	CONSTRAINT CHK_BieuGiaVe_Dates CHECK (ngayHetHieuLuc >= ngayCoHieuLuc),
    CONSTRAINT CHK_BieuGiaVe_Price CHECK ((donGiaTrenKm IS NOT NULL) OR (giaCoDinh IS NOT NULL))
);

CREATE TABLE HeSoGiaLoaiTau (
    hsgLoaiTauID VARCHAR(20) NOT NULL PRIMARY KEY,
    loaiTauID VARCHAR(20) NOT NULL,
    hsg DECIMAL(5,2) NOT NULL,
    ngayCoHieuLuc DATE NOT NULL,
    ngayHetHieuLuc   DATE NOT NULL,
    CONSTRAINT FK_HeSoGiaLoaiTau_LoaiTau_loaiTauID FOREIGN KEY (loaiTauID) REFERENCES LoaiTau(loaiTauID),
	CONSTRAINT CHK_HeSoLoaiTau_Ngay CHECK (ngayHetHieuLuc >= ngayCoHieuLuc)
);

CREATE TABLE HeSoGiaHangToa (
    hsgHangToaID VARCHAR(20) NOT NULL PRIMARY KEY,
    hangToaID VARCHAR(20) NOT NULL,
    hsg DECIMAL(5,2) NOT NULL,
    ngayCoHieuLuc DATE NOT NULL,
    ngayHetHieuLuc   DATE NOT NULL,
    CONSTRAINT FK_HeSoGiaHangToa_HangToa_hangToaID FOREIGN KEY (hangToaID) REFERENCES HangToa(hangToaID),
	CONSTRAINT CHK_HeSoHangToa_Ngay CHECK (ngayHetHieuLuc >= ngayCoHieuLuc)
);

-- Chỉ mục cho quy tắc giá
CREATE NONCLUSTERED INDEX IDX_BieuGiaVe_LoaiTuyenToaTau 
    ON BieuGiaVe(tuyenApDungID, hangToaApDungID, LoaiTauApDungID, ngayCoHieuLuc);

-- Hóa đơn, Hóa đơn chi tiết, Thanh toán
CREATE TABLE HoaDon (
    hoaDonID VARCHAR(20) NOT NULL PRIMARY KEY,
    khachHangID VARCHAR(20) NOT NULL,
    nhanVienID VARCHAR(20) NOT NULL,
    thoiDiemTao DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    tamTinh DECIMAL(12,2) NOT NULL DEFAULT 0,
    tongGiamGia DECIMAL(12,2) NOT NULL DEFAULT 0,
    tongThue DECIMAL(12,2) NOT NULL DEFAULT 0,
    tongTien DECIMAL(12,2) NOT NULL DEFAULT 0,
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_DonHang_KhachHang_khachHangID FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID),
    CONSTRAINT FK_DonHang_NhanVien_nhanVienID FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID)
);

CREATE TABLE HoaDonChiTiet (
    hoaDonChiTietID VARCHAR(20) NOT NULL PRIMARY KEY,
    hoaDonID VARCHAR(20) NOT NULL,
    loaiDichVu VARCHAR(20) NOT NULL,    -- ví dụ. vé, VIP_LOUNGE, REFUND, FEE, PROMO_ADJ
    matHangID VARCHAR(20) NOT NULL,
    tenMatHang NVARCHAR(255),
    donGia DECIMAL(12,2) NOT NULL,
    soLuong INT NOT NULL DEFAULT 1,
    soTien DECIMAL(12,2) NOT NULL,
    thue DECIMAL(12,2) NOT NULL DEFAULT 0,
	veID VARCHAR (20) NULL,
    CONSTRAINT FK_HoaDonChiTiet_DonHang_hoaDonID FOREIGN KEY (hoaDonID) REFERENCES [HoaDon](hoaDonID),
	CONSTRAINT FK_HoaDonChiTiet_Ve FOREIGN KEY (veID) REFERENCES Ve(veID)
);

CREATE TABLE GiaoDichThanhToan (
    giaoDichThanhToanID VARCHAR(20) NOT NULL PRIMARY KEY,
    hoaDonID VARCHAR(20) NOT NULL,
    khachHangID VARCHAR(20) NOT NULL,
    soTien DECIMAL(12,2) NOT NULL,
    thoiDiemThanhToan DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    isThanhToanTienMat BIT NOT NULL DEFAULT 1,
    trangThai BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_GiaoDichThanhToan_DonHang_hoaDonID FOREIGN KEY (hoaDonID) REFERENCES [HoaDon](hoaDonID),
    CONSTRAINT FK_GiaoDichThanhToan_KhachHang_khachHangID FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID)
);

-- Chỉ mục tra cứu hóa đơn
CREATE NONCLUSTERED INDEX IDX_HoaDon_KhachHang ON HoaDon(khachHangID);
CREATE NONCLUSTERED INDEX IDX_GiaoDichThanhToan_HoaDon ON GiaoDichThanhToan(HoaDonID);

-- Đơn hoàn/đổi vé
CREATE TABLE DonHoanDoi (
    donHoanDoiID VARCHAR(20) NOT NULL PRIMARY KEY,
    donDatChoID VARCHAR(20) NOT NULL,
    khachHangID VARCHAR(20) NOT NULL,
    nhanVienID VARCHAR(20) NOT NULL,
    laDonHoan BIT NOT NULL,              -- 1 = refund, 0 = exchange
    ngayYeuCau DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    tongTienHoan DECIMAL(12,2) NOT NULL DEFAULT 0,
    trangThai VARCHAR(20) NOT NULL,       -- ví dụ. PENDING, APPROVED, REJECTED
    CONSTRAINT FK_DonHoanDoi_DonDatCho_donDatChoID FOREIGN KEY (donDatChoID) REFERENCES DonDatCho(donDatChoID),
    CONSTRAINT FK_DonHoanDoi_KhachHang_khachHangID FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID),
    CONSTRAINT FK_DonHoanDoi_NhanVien_nhanVienID FOREIGN KEY (nhanVienID) REFERENCES NhanVien(nhanVienID)
);

CREATE TABLE DonHoanDoiChiTiet (
    donHoanDoiChiTietID VARCHAR(20) NOT NULL PRIMARY KEY,
    donHoanDoiID VARCHAR(20) NOT NULL,
    veCuID VARCHAR(20) NOT NULL,
    veMoiID VARCHAR(20) NULL,  -- null for refunds without reissue
    soTienHoan DECIMAL(12,2) NOT NULL DEFAULT 0,
    phiPhatSinh DECIMAL(12,2) NOT NULL DEFAULT 0,
    ghiChu NVARCHAR(MAX),
    CONSTRAINT FK_DonHoanDoiChiTiet_DonHoanDoi_donHoanDoiID FOREIGN KEY (donHoanDoiID) REFERENCES DonHoanDoi(donHoanDoiID),
    CONSTRAINT FK_DonHoanDoiChiTiet_Ve_veCuID FOREIGN KEY (veCuID) REFERENCES Ve(veID),
    CONSTRAINT FK_DonHoanDoiChiTiet_Ve_veMoiID FOREIGN KEY (veMoiID) REFERENCES Ve(veID)
);

-- Khuyến mãi, điều kiện áp dụng và lịch sử sử dụng
CREATE TABLE KhuyenMai (
    khuyenMaiID VARCHAR(20) NOT NULL PRIMARY KEY,
    maKhuyenMai VARCHAR(20) NOT NULL UNIQUE,
    moTa NVARCHAR(MAX),
    tyLeGiamGia DECIMAL(5,2) NULL CHECK (tyLeGiamGia BETWEEN 0 AND 100),   -- percent (0-100)
    tienGiamGia DECIMAL(12,2) NULL CHECK (tienGiamGia > 0), -- fixed discount
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    soLuong INT NOT NULL DEFAULT 0,
    gioiHanMoiKhachHang INT NOT NULL DEFAULT 0,
    thoiDiemTao DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    thoiDiemSua DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    trangThai BIT NOT NULL DEFAULT 1,
	CONSTRAINT CHK_KhuyenMai_Ngay CHECK (ngayKetThuc >= ngayBatDau),
    CONSTRAINT CHK_KhuyenMai_GiamGia CHECK (tyLeGiamGia IS NOT NULL OR tienGiamGia IS NOT NULL)
);

CREATE TABLE DieuKienKhuyenMai (
    dieuKienID VARCHAR(20) NOT NULL PRIMARY KEY,
    khuyenMaiID VARCHAR(20) NOT NULL,
    tuyenID VARCHAR(20) NULL,
    loaiTauID VARCHAR(20) NULL,
    hangToaID VARCHAR(20) NULL,
    hangKhachHangID VARCHAR(20) NULL,
    loaiDoiTuongID VARCHAR(20) NULL,
    ngayTrongTuan INT NULL CHECK (ngayTrongTuan BETWEEN 1 AND 7),
    ngayLe BIT NULL,
    minGiaTriDonHang DECIMAL(12,2) NULL,
    CONSTRAINT FK_DieuKienKhuyenMai_KhuyenMai_khuyenMaiID FOREIGN KEY (khuyenMaiID) REFERENCES KhuyenMai(khuyenMaiID),
    CONSTRAINT FK_DieuKienKhuyenMai_Tuyen_tuyenID FOREIGN KEY (tuyenID) REFERENCES Tuyen(tuyenID),
    CONSTRAINT FK_DieuKienKhuyenMai_LoaiTau_loaiTauID FOREIGN KEY (loaiTauID) REFERENCES LoaiTau(loaiTauID),
    CONSTRAINT FK_DieuKienKhuyenMai_HangToa_hangToaID FOREIGN KEY (hangToaID) REFERENCES HangToa(hangToaID),
    CONSTRAINT FK_DieuKienKhuyenMai_HangKhachHang_hangKhachHangID FOREIGN KEY (hangKhachHangID) REFERENCES HangKhachHang(hangKhachHangID),
    CONSTRAINT FK_DieuKienKhuyenMai_LoaiDoiTuong_loaiDoiTuongID FOREIGN KEY (loaiDoiTuongID) REFERENCES LoaiDoiTuong(loaiDoiTuongID)
);

CREATE TABLE SuDungKhuyenMai (
    suDungKhuyenMaiID VARCHAR(20) NOT NULL PRIMARY KEY,
    khuyenMaiID VARCHAR(20) NOT NULL,
    hoaDonChiTietID VARCHAR(20) NOT NULL,
    khachHangID VARCHAR(20) NOT NULL,
    thoiDiemDung DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    CONSTRAINT FK_SuDungKhuyenMai_KhuyenMai_khuyenMaiID FOREIGN KEY (khuyenMaiID) REFERENCES KhuyenMai(khuyenMaiID),
    CONSTRAINT FK_SuDungKhuyenMai_HoaDonChiTiet_hoaDonChiTietID FOREIGN KEY (hoaDonChiTietID) REFERENCES HoaDonChiTiet(hoaDonChiTietID),
    CONSTRAINT FK_SuDungKhuyenMai_KhachHang_khachHangID FOREIGN KEY (khachHangID) REFERENCES KhachHang(khachHangID)
);

-- Chỉ mục lọc: chỉ khuyến mãi đang hoạt động
CREATE NONCLUSTERED INDEX IDX_KhuyenMai_HoatDong 
    ON KhuyenMai(ngayKetThuc) 
    WHERE trangThai = 1;

-- Dịch vụ phòng VIP và đơn hàng
CREATE TABLE DichVuPhongChoVIP (
    dichVuPhongChoVIPID VARCHAR(20) NOT NULL PRIMARY KEY,
    gia DECIMAL(8,2) NOT NULL CHECK (gia >= 0),
    moTa NVARCHAR(MAX),
    ngayCoHieuLuc DATE NOT NULL,
    ngayHetHieuLuc DATE NOT NULL,
    trangThai BIT NOT NULL DEFAULT 1,
	CONSTRAINT CHK_DichVuPhongVIP_Ngay CHECK (ngayHetHieuLuc >= ngayCoHieuLuc)
);

CREATE TABLE PhieuDungPhongVIP (
    phieuDungPhongVIPID VARCHAR(20) NOT NULL PRIMARY KEY,
    hoaDonChiTietID VARCHAR(20) NOT NULL,
    dichVuPhongChoVIPID VARCHAR(20) NOT NULL,
    hanhKhachID VARCHAR(20) NOT NULL,
    soLuong INT NOT NULL CHECK (soLuong > 0),
    CONSTRAINT FK_PhieuDungPhongVIP_HoaDonChiTiet_hoaDonChiTietID FOREIGN KEY (hoaDonChiTietID) REFERENCES HoaDonChiTiet(hoaDonChiTietID),
    CONSTRAINT FK_PhieuDungPhongVIP_DichVuPhongChoVIP_dichVuPhongChoVIPID FOREIGN KEY (dichVuPhongChoVIPID) REFERENCES DichVuPhongChoVIP(dichVuPhongChoVIPID),
    CONSTRAINT FK_PhieuDungPhongVIP_HanhKhach_hanhKhachID FOREIGN KEY (hanhKhachID) REFERENCES HanhKhach(hanhKhachID)
);

-- Nhật ký kiểm toán (Kiểm toán Log)
CREATE TABLE NhatKyAudit (
    nhatKyID VARCHAR(20) NOT NULL PRIMARY KEY,
    tenThucThe NVARCHAR(100) NOT NULL,
    idThucThe VARCHAR(50) NOT NULL,
    thucHienBoi NVARCHAR(50) NOT NULL,
    loaiThaoTac VARCHAR(50) NOT NULL,
    thoiGianThaoTac DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
    chiTiet NVARCHAR(MAX)
);

--------------------------------------Start Trigger--------------------------------------
/***************************************************************
  0. NOTE: run these statements in your application DB (not master).
       Make sure the tables in your schema exist as in your file.
       The schema referenced is the one you uploaded. 
       (See file you've provided.) 
***************************************************************/

-- 1) Helper: sinh ID ngắn (20 chars) từ NEWID() để phù hợp VARCHAR(20)
--    Ví dụ: 20 ký tự hex từ NEWID() (không có dấu '-')
GO
CREATE FUNCTION dbo.fn_ShortIDFromGUID(@guid UNIQUEIDENTIFIER)
RETURNS VARCHAR(20)
AS
BEGIN
    RETURN LEFT(REPLACE(CONVERT(VARCHAR(36), @guid), '-', ''), 20);
END;
GO

-- 2) Trigger: kiểm tra Ghế thuộc Toa và Toa.tauID == Chuyen.tauID khi insert Ve
--    Trigger này sẽ rollback nếu không thỏa.
IF OBJECT_ID('dbo.trg_Ve_ValidateVeThuocChuyen','TR') IS NOT NULL
    DROP TRIGGER dbo.trg_Ve_ValidateVeThuocChuyen;
GO
CREATE TRIGGER dbo.trg_Ve_ValidateVeThuocChuyen
ON dbo.Ve
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (
        SELECT 1
        FROM inserted i
        LEFT JOIN Ghe g ON g.gheID = i.gheID
        LEFT JOIN Toa t ON t.toaID = g.toaID
        LEFT JOIN Chuyen c ON c.chuyenID = i.chuyenID
        WHERE i.gheID IS NOT NULL
          AND (t.toaID IS NULL OR c.tauID IS NULL OR t.tauID <> c.tauID)
    )
    BEGIN
        RAISERROR('Cannot insert ticket: seat does not belong to the train of the trip',16,1);
        ROLLBACK TRAN;
        RETURN;
    END
END;
GO

-- 3) Stored Procedure: usp_TaoVeDuyNhat
--    - Params are VARCHAR(20) to match schema
--    - Uses sp_getapplock to avoid race on same chuyen+ghe
--    - Checks overlap on Ve (no BookedSegment used)
--    - Inserts a single Ve row and returns generated ID via output parameter
IF OBJECT_ID('dbo.usp_TaoVeDuyNhat','P') IS NOT NULL
    DROP PROCEDURE dbo.usp_TaoVeDuyNhat;
GO
CREATE PROCEDURE dbo.usp_TaoVeDuyNhat
    @veID_out VARCHAR(20) OUTPUT,
    @donDatChoID VARCHAR(20) = NULL,   -- optional
    @chuyenID VARCHAR(20),
    @gheID VARCHAR(20),
    @hanhKhachID VARCHAR(20),
    @thuTuGaDi INT,
    @thuTuGaDen INT,
    @gia DECIMAL(12,2)
AS
BEGIN
    SET NOCOUNT ON;
    DECLARE @lockResource NVARCHAR(200) = N'SeatLock_' + ISNULL(@chuyenID,'') + N'_' + ISNULL(@gheID,'');
    DECLARE @rc INT;

    -- Acquire application lock to prevent concurrency for same seat+trip
    EXEC @rc = sp_getapplock @Resource = @lockResource, @LockMode = 'Exclusive', @LockTimeout = 5000;
    IF @rc NOT IN (0,1)
    BEGIN
        THROW 51000, 'Khong the chiem dung ghe. Thu lai.', 1;
    END

    BEGIN TRY
        BEGIN TRAN;

        -- Basic validation of inputs
        IF @thuTuGaDi >= @thuTuGaDen
        BEGIN
            THROW 51002, 'Phan doan khong hop le: thuTuGaDi phai < thuTuGaDen', 1;
        END

        -- Check overlapping tickets already booked/reserved that occupy the same seat on overlapping segment
        IF EXISTS (
            SELECT 1
            FROM Ve v
            WHERE v.chuyenID = @chuyenID
              AND v.gheID = @gheID
              AND v.trangThai IN ('RESERVED','BOOKED','USED') -- consider RESERVED/BOOKED as occupied; adjust if needed
              AND NOT (v.thuTuGaDen <= @thuTuGaDi OR v.thuTuGaDi >= @thuTuGaDen)
        )
        BEGIN
            THROW 51001, 'Ghe da duoc dat trong phan doan chong lan', 1;
        END

        -- generate ID compatible with VARCHAR(20) PKs
        SET @veID_out = dbo.fn_ShortIDFromGUID(NEWID());

        -- Insert new ticket
        INSERT INTO Ve (veID, donDatChoID, chuyenID, gheID, hanhKhachID, thuTuGaDi, thuTuGaDen, gia, trangThai, ngayBan)
        VALUES (@veID_out, @donDatChoID, @chuyenID, @gheID, @hanhKhachID, @thuTuGaDi, @thuTuGaDen, @gia, 'BOOKED', SYSUTCDATETIME());

        COMMIT TRAN;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0 ROLLBACK TRAN;
        -- release lock before rethrow
        EXEC sp_releaseapplock @Resource = @lockResource, @LockOwner = 'Session';
        DECLARE @ErrMsg NVARCHAR(4000) = ERROR_MESSAGE();
        DECLARE @ErrNum INT = ERROR_NUMBER();
        THROW @ErrNum, @ErrMsg, 1;
    END CATCH

    -- release lock
    EXEC sp_releaseapplock @Resource = @lockResource, @LockOwner = 'Session';
END;
GO

-- 4) Procedure: sinh unit segments vào DoanDuong cho 1 Chuyen
--    - Tính sơ bộ khoangCachKm cho mỗi unit bằng (Tuyen.khoangCachKm / (nStops-1))
--    - Chỉ thêm các đoạn i -> i+1 (unit)
IF OBJECT_ID('dbo.usp_PhanBoDoanDuongChoChuyen','P') IS NOT NULL
    DROP PROCEDURE dbo.usp_PhanBoDoanDuongChoChuyen;
GO
CREATE PROCEDURE dbo.usp_PhanBoDoanDuongChoChuyen
    @chuyenID VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @tuyenID VARCHAR(20), @totalKm DECIMAL(10,2), @nStops INT, @perSegKm DECIMAL(10,2);

    SELECT @tuyenID = chuyen.tuyenID
    FROM Chuyen chuyen
    WHERE chuyen.chuyenID = @chuyenID;

    IF @tuyenID IS NULL
    BEGIN
        THROW 52000, 'Khong tim thay chuyen', 1;
    END

    SELECT @totalKm = tuyen.khoangCachKm
    FROM Tuyen tuyen
    WHERE tuyen.tuyenID = @tuyenID;

    SELECT @nStops = COUNT(*) FROM ChuyenGa WHERE chuyenID = @chuyenID;

    IF @nStops IS NULL OR @nStops <= 1
    BEGIN
        -- nothing to insert
        RETURN;
    END

    SET @perSegKm = CASE WHEN @nStops > 1 THEN ROUND(@totalKm / (@nStops - 1), 2) ELSE @totalKm END;

    -- delete existing doanDuong for safety (optional)
    DELETE FROM DoanDuong WHERE chuyenID = @chuyenID;

    -- insert unit segments a -> b where b.thuTu = a.thuTu+1
    INSERT INTO DoanDuong (doanDuongID, chuyenID, gaDiID, gaDenID, thuTuGaDi, thuTuGaDen, khoangCachKm, thoiGianDuKienPhut)
    SELECT dbo.fn_ShortIDFromGUID(NEWID()), a.chuyenID, a.gaID, b.gaID, a.thuTu, b.thuTu, @perSegKm, NULL
    FROM ChuyenGa a
    JOIN ChuyenGa b ON a.chuyenID = b.chuyenID AND b.thuTu = a.thuTu + 1
    WHERE a.chuyenID = @chuyenID;
END;
GO

-- 5) Optional: small helper to calculate total distance between two thuTu on a Chuyen (uses DoanDuong unit segments)
IF OBJECT_ID('dbo.fn_GetKhoangCachCuaDoanDuongTrenChuyen','FN') IS NOT NULL
    DROP FUNCTION dbo.fn_GetKhoangCachCuaDoanDuongTrenChuyen;
GO
CREATE FUNCTION dbo.fn_GetKhoangCachCuaDoanDuongTrenChuyen(
    @chuyenID VARCHAR(20),
    @thuTuDi INT,
    @thuTuDen INT
)
RETURNS DECIMAL(12,2)
AS
BEGIN
    DECLARE @res DECIMAL(12,2);
    IF @thuTuDi >= @thuTuDen RETURN 0;
    SELECT @res = SUM(khoangCachKm)
    FROM DoanDuong d
    WHERE d.chuyenID = @chuyenID
      AND d.thuTuGaDi >= @thuTuDi
      AND d.thuTuGaDen <= @thuTuDen;
    RETURN ISNULL(@res,0);
END;
GO

-- 6) Optional: helper to mark expired reservations (sample)
IF OBJECT_ID('dbo.usp_ExpireReservations','P') IS NOT NULL
    DROP PROCEDURE dbo.usp_ExpireReservations;
GO
CREATE PROCEDURE dbo.usp_ExpireReservations
AS
BEGIN
    SET NOCOUNT ON;
    -- expire reservations where thoiDiemHetHan <= now AND trangThaiDatChoID = 'PENDING'
    UPDATE DonDatCho
    SET trangThaiDatChoID = 'EXPIRED'
    WHERE trangThaiDatChoID = 'PENDING'
      AND thoiDiemHetHan <= SYSUTCDATETIME();
END;
GO
--------------------------------------End Trigger--------------------------------------

SELECT 
    fk.name AS ForeignKeyName,
    OBJECT_NAME(fk.parent_object_id) AS TableName,
    OBJECT_NAME(fk.referenced_object_id) AS ReferencedTable
FROM 
    sys.foreign_keys fk
ORDER BY 
    TableName;