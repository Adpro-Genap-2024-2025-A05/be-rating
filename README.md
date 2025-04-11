# BeRating – 🌟 Fitur Review Pengguna untuk Layanan

BeRating adalah microservice yang menangani fitur ulasan (review) antar pengguna, memungkinkan pasien memberikan rating dan komentar kepada dokter atau pihak layanan lainnya.

---

## Deployment

Link: [BE-Rating Deployment](https://mixed-antonia-be-rating-bf0ca734.koyeb.app/)
Link: [Postman API Collection](https://www.postman.com/api-ristek/workspace/berating/collection/12345678-xxxx-yyyy-zzzz-abcdefghijkl?action=share)
---

## 🚀 Fitur Utama

Fitur Review memungkinkan interaksi pengguna dalam bentuk penilaian dan komentar. Cakupan use case-nya adalah:

- **Menambahkan Review**: Pasien dapat memberikan komentar dan rating terhadap dokter. (C)
- **Melihat Review**: Mendapatkan data review berdasarkan ID. (R)
- **Mengubah Review**: Memperbarui komentar atau rating. (U)
- **Menghapus Review**: Menghapus review secara logis dari sistem (soft delete). (D)

---

## 🧱 Arsitektur Proyek – Model View Controller (MVC)

Aplikasi ini mengimplementasikan **MVC Pattern** untuk memisahkan logika aplikasi menjadi tiga bagian utama:

### 1. **Model (Entity)**
- Mewakili struktur data yang akan disimpan di database.
- Contoh: `Review`, `User`, dan `ReviewStatus` (enum)

### 2. **View**
- Karena aplikasi ini berupa REST API, maka "View" diwakili oleh response JSON yang dihasilkan oleh controller.
- Tidak menggunakan template engine atau UI.

### 3. **Controller**
- Menangani HTTP request dan response.
- Mengatur routing endpoint dan meneruskan ke service yang sesuai.
- Contoh: `ReviewController`

### 4. **Service**
- Menangani logika bisnis aplikasi seperti validasi, pemrosesan data, dan transformasi.
- Contoh: `ReviewService`, `ReviewServiceImpl`

### 5. **Repository**
- Berinteraksi langsung dengan database menggunakan Spring Data JPA.
- Contoh: `ReviewRepository`, `UserRepository`

---

## 🔁 Alur Eksekusi Singkat

1. Client melakukan request ke endpoint `/api/reviews`.
2. `ReviewController` menerima request dan meneruskannya ke `ReviewService`.
3. `ReviewService` mengelola logika pembuatan/pengambilan/modifikasi review.
4. `ReviewRepository` menangani akses data ke database.
5. Controller mengembalikan response dalam format JSON ke client.

---

## 📦 API Endpoint

| Method | Endpoint                    | Description                                         |
|--------|-----------------------------|-----------------------------------------------------|
| POST   | `/api/reviews`              | Membuat review baru                                 |
| GET    | `/api/reviews/{id}`         | Melihat review berdasarkan ID                       |
| PUT    | `/api/reviews/{id}`         | Memperbarui isi review                              |
| DELETE | `/api/reviews/{id}`         | Menghapus review dari sistem (soft delete)          |

---

## ✅ Alasan Pemilihan MVC Pattern

- **Sederhana & Terstruktur**: Cocok untuk aplikasi CRUD sederhana dengan alur data yang jelas.
- **Maintainable**: Mudah untuk dikembangkan dan diuji per komponen (controller, service, repo).
- **Skalabel**: Dapat dengan mudah ditambahkan validasi atau middleware seperti logging, auth, dsb.
lam bentuk gambar atau markdown. Mau sekalian?