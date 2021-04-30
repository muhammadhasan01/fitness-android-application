# Fitness Android Application!

## Deskripsi Aplikasi

<!-- Buat Gambar Title -->

**Fitness Android Application** merupakan sebuah aplikasi android yang dapat menunjang kegiatan _workout_ yang berisi beberapa fitur seperti *Sport News*, *Training Tracker*, *Training History*, dan *Training Scheduler*.


## Cara Kerja Aplikasi

Project dapat dibuka dengan Android Studio dan melakukan beberapa *setup sync* dan kemudian melakukan *build project*:

| Spesifikasi | Penjelasan |
| --- | --- |
| Sport News API | Sport News API diambil melalui URL yang ada pada [news-api](https://newsapi.org/s/indonesia-sports-news-api) kemudian di *fetch* dengan Retrofit dan dimasukkan ke dalam RecyclerView News, data tersebut dimasukkan menggunakan *adapter*. Lalu, setiap *item* dibuat *listener* yang jika dilakukan *click* akan membuka sebuah *webview* dari URL berita tersebut. Selain itu dilakukan juga pengandalan landscape, agar *column* berita menjadi 2|
| Compass | Compass dibuat dengan mengacu pada referensi yang ada pada library, dengan kemudian ditaruh pada pojok kanan Maps |
| Halaman Log Detail | Halaman Log Detail dibuat dengan membuat fragment untuk Log Detail kemudian dibuat *listener* untuk setiap log info yang jika dilakukan *click* pada *item* maka akan membuka *detail* dari log tersebut |
| Training Scheduler | Scheduler menggunakan AlarmManager untuk melakukan repeating task. Fungsi yang digunakan adalah `setRepeating`. Data schedule kemudian disimpan pada Room. Ketika intent schedule diterima oleh class `AlarmReceiver`, maka akan diproses berdasarkan variabel yang dipassing dengan menggunakan fungsi `putExtra` pada intent tersebut. Misal, bila notifikasi untuk start training, maka teks nya akan berbeda dengan stop training. Bila user menginginkan run di background secara otomatis, maka service `TrackingService` akan dijalankan secara otomatis dengan `context.startService()`. Tampilan daftar schedule yang ada diimplementasikan dengan `RecyclerView`. Tampilan input untuk mensetup Schedule diimplementasikan dengan `AlertDialog` |



## Library yang digunakan

Berikut adalah beberapa *library/assets* yang kami gunakan, beserta justifikasi pemakainnya:

- [Chip NavBar](https://github.com/ismaeldivita/chip-navigation-bar) - Digunakan untuk *Custom Navigation Bar*
- Retrofit - Digunakan untuk melakukan *fetch* API
- [Compass](https://github.com/iutinvg/compass) - untuk melakukan wrap position sensor
- Material Design - membuat tampilan lebih rapi
- Lifecycle ViewModel - framework MVVM
- Room - melakukan akses database dan memudahkan implementasi DAO
- Coroutines - melakukan pemanggilan database secara async
- Kotlin-extensions - Mendapatkan beberapa ekstensi tambahan kotlin
- Google Maps Location Service - menggambar peta dan garis pada peta
- Dagger - framework untuk inject repository pada class tertentu
- Easy Permission - memudahkan meminta permission tertentu dari user
- Timber - memudahkan debugging


## Screenshot Aplikasi

### News:
<img src="screenshots/NewsList.jpg" width="300">
<img src="screenshots/NewsWebView.jpg" width="300">

### Training Tracker:
<img src="screenshots/TrainingTracker.jpg"  width="300">
<img src="screenshots/Cycling.jpg" width="300" />

### Notification
<img src="screenshots/NotifBackground.jpg" width="300" />
<img src="screenshots/NotifSchedule.jpg" width="300" />

### Pembagian Kerja

- Muhammad Hasan (13518012)
    - Fetch API News
    - Membuat Recycler News
    - Membuat GridView Landscape News
    - Membuat Compass
    - Membuat Halaman Log Detail
- Hafshy Yazid Albisthami (13518051)
    - Pembuatan Navbar dan template fragmentnya
    - Fitur Training Tracker (Cycling dan Running)
    - Membantu pembuatan Database
- Jonathan Yudi Gunawan (13518084)
    - Membuat Calendar History
    - Membuat History Log
    - Setup Database Room
    - Membuat List Schedule
    - Membuat Notifikasi Schedule

