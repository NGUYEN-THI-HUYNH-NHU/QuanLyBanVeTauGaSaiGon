# QuanLyBanVeTauGaSaiGon

Ứng dụng **quản lý bán vé tàu – Ga Sài Gòn** (Java) với kiến trúc phân lớp (Entity → DAO → BUS → Controller) và giao diện người dùng (GUI). Repository cũng bao gồm các trang **HTML** dùng làm nội dung/ghi chú hướng dẫn quy trình & quy định nghiệp vụ.

> Repo: https://github.com/NGUYEN-THI-HUYNH-NHU/QuanLyBanVeTauGaSaiGon

## Tính năng chính (high level)

- **Bán vé / đặt chỗ**: tạo hoá đơn, chi tiết hoá đơn, giữ chỗ.
- **Đổi vé / hoàn vé**: xử lý giao dịch đổi/hoàn và các chi tiết liên quan.
- **Quản lý chuyến/tuyến/ga/tàu/toa/ghế**: quản lý dữ liệu vận hành đường sắt.
- **Biểu giá & hệ số giá**: cấu hình giá vé và các hệ số theo loại tàu/hạng toa.
- **Khuyến mãi**: điều kiện khuyến mãi và theo dõi sử dụng khuyến mãi.
- **Tài khoản & nhân viên**: đăng nhập, phân quyền (nếu được triển khai đầy đủ), ca làm.
- **Thống kê / dashboard**: doanh thu, vé, khách hàng, nhân viên (thông qua các DAO tương ứng).
- **Nhật ký/Audit**: ghi nhận lịch sử thao tác (entity `NhatKyAudit`).

## Kiến trúc thư mục

- `src/entity/` – **Mô hình dữ liệu (domain model)**: `Ve`, `HoaDon`, `KhachHang`, `NhanVien`, `Chuyen`, `Tuyen`, `Ga`, `Tau`, `Toa`, …
- `src/dao/` – **Data Access Object**: truy vấn/ghi dữ liệu xuống DB (ví dụ: `Ve_DAO`, `HoaDon_DAO`, `Chuyen_DAO`, `ThongKeDoanhThu_DAO`, …)
- `src/bus/` – **Business layer**: xử lý nghiệp vụ (ví dụ: `BanVe_BUS`, `HoanVe_BUS`, `KhuyenMai_BUS`, …)
- `src/controller/` – **Controller**: điều phối tương tác UI ↔ nghiệp vụ (ví dụ: `DangNhap_Ctrl`, `QuanLyChuyen_CTRL`, …)
- `src/connectDB/` – **Kết nối CSDL** (`ConnectDB.java`)
- `src/gui/` – **Giao diện (GUI)** và tài nguyên liên quan
- `src/*.html` – Các trang HTML nội dung/hướng dẫn (welcome/about-us/quy trình/quy định…)
- `lib/` – Thư viện JAR phụ thuộc (nếu có)
- `data/` – Dữ liệu/SQL kèm theo dự án (tuỳ nội dung trong thư mục)
- `img/` – Hình ảnh dùng cho dự án

## Công nghệ

- **Java** (project kiểu IDE: có `.project`, `.classpath`, `.iml`)
- **SQL Server (JDBC)**
  - Project hiện đang trỏ tới DB local theo chuỗi kết nối trong `src/connectDB/ConnectDB.java`.
- **HTML** (các trang hướng dẫn/quy trình)

## Yêu cầu hệ thống

- JDK (khuyến nghị **Java 8+**)
- IDE: IntelliJ IDEA hoặc Eclipse
- **Microsoft SQL Server** (local hoặc remote)
- JDBC Driver cho SQL Server (thường nằm trong `lib/` hoặc được cấu hình trong IDE)

## Cấu hình cơ sở dữ liệu

Trong file `src/connectDB/ConnectDB.java` hiện sử dụng cấu hình mặc định:

- URL: `jdbc:sqlserver://localhost:1433;databaseName=HeThongQuanLyBanVeTauGaSaiGon_V9;encrypt=false;`
- USER: `sa`
- PASSWORD: `sapassword`

Bạn nên **đổi lại** thông tin DB cho phù hợp môi trường của mình.

> Gợi ý: tốt nhất là tách cấu hình DB ra file config (ví dụ `.env`, `config.properties`) và **không commit mật khẩu**.

## Cách chạy (gợi ý)

Vì repo là project theo IDE (không thấy Maven/Gradle ở root), cách chạy thường là:

1. Mở project bằng IntelliJ/Eclipse.
2. Thêm các JAR trong `lib/` vào classpath (nếu IDE chưa tự nhận).
3. Đảm bảo SQL Server đang chạy và DB đã được tạo/restore đúng tên.
4. Tìm class `main` trong phần `src/gui/...` (hoặc lớp khởi động ứng dụng) và **Run**.
