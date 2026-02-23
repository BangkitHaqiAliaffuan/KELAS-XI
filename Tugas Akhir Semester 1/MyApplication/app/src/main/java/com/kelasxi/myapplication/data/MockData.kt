package com.kelasxi.myapplication.data

import com.kelasxi.myapplication.model.*

object MockData {

    val currentUser = UserProfile(
        name = "Budi Santoso",
        email = "budi.santoso@email.com",
        memberSince = "Januari 2024",
        totalPickups = 12,
        itemsSold = 5,
        co2Saved = 30f
    )

    val recentPickups = listOf(
        PickupRequest(
            id = "1",
            date = "22 Feb 2026",
            time = "09:00",
            address = "Jl. Sudirman No. 12, Jakarta",
            trashTypes = listOf(TrashType.PLASTIC, TrashType.ORGANIC),
            status = PickupStatus.DONE
        ),
        PickupRequest(
            id = "2",
            date = "18 Feb 2026",
            time = "14:00",
            address = "Jl. Gatot Subroto No. 45, Jakarta",
            trashTypes = listOf(TrashType.ELECTRONIC),
            status = PickupStatus.ON_THE_WAY
        ),
        PickupRequest(
            id = "3",
            date = "10 Feb 2026",
            time = "10:30",
            address = "Jl. Thamrin No. 7, Jakarta",
            trashTypes = listOf(TrashType.GLASS, TrashType.PLASTIC),
            status = PickupStatus.PENDING
        ),
        PickupRequest(
            id = "4",
            date = "01 Feb 2026",
            time = "08:00",
            address = "Jl. Kebayoran Baru No. 3, Jakarta",
            trashTypes = listOf(TrashType.ORGANIC),
            status = PickupStatus.DONE
        )
    )

    val products = listOf(
        Product(
            id = "1",
            name = "Kursi Kayu Vintage Jati",
            price = 350_000,
            sellerName = "Toko Hijau",
            sellerRating = 4.8f,
            description = "Kursi kayu jati vintage dengan finishing natural. Kondisi sangat baik, hanya ada sedikit goresan di bagian kaki. Cocok untuk ruang tamu atau kafe.",
            category = ProductCategory.FURNITURE,
            condition = ProductCondition.GOOD
        ),
        Product(
            id = "2",
            name = "Laptop ASUS ROG Bekas",
            price = 4_500_000,
            sellerName = "TechRecycle",
            sellerRating = 4.5f,
            description = "Laptop gaming bekas pakai 1 tahun. RAM 16GB, SSD 512GB, GPU GTX 1650. Semua komponen berfungsi normal.",
            category = ProductCategory.ELECTRONICS,
            condition = ProductCondition.LIKE_NEW
        ),
        Product(
            id = "3",
            name = "Dress Batik Tulis Solo",
            price = 85_000,
            sellerName = "BatikLestari",
            sellerRating = 4.9f,
            description = "Dress batik tulis asli Solo, ukuran M. Hanya dipakai 2x, kondisi sangat baik.",
            category = ProductCategory.CLOTHING,
            condition = ProductCondition.LIKE_NEW
        ),
        Product(
            id = "4",
            name = "Set Buku Novel Tere Liye",
            price = 120_000,
            sellerName = "BukuBekas",
            sellerRating = 4.3f,
            description = "Paket 5 novel Tere Liye kondisi baik. Lengkap dengan cover dan halaman utuh.",
            category = ProductCategory.BOOKS,
            condition = ProductCondition.GOOD
        ),
        Product(
            id = "5",
            name = "Lemari Plastik 3 Pintu",
            price = 200_000,
            sellerName = "FurniBekas",
            sellerRating = 4.1f,
            description = "Lemari plastik 3 pintu merk Lion Star. Bersih, tidak ada retakan, semua engsel berfungsi baik.",
            category = ProductCategory.FURNITURE,
            condition = ProductCondition.FAIR
        ),
        Product(
            id = "6",
            name = "Speaker Bluetooth JBL",
            price = 250_000,
            sellerName = "ElektronikBaik",
            sellerRating = 4.7f,
            description = "Speaker JBL Flip 4 bekas. Suara masih jernih, baterai tahan 8 jam. Dijual karena upgrade.",
            category = ProductCategory.ELECTRONICS,
            condition = ProductCondition.GOOD
        )
    )

    val statsCards = listOf(
        StatCard("12", "Pickups Done", "🚛"),
        StatCard("5", "Items Sold", "🛒"),
        StatCard("30kg", "Recycled", "🌱"),
        StatCard("15kg", "CO₂ Saved", "🌍")
    )

    val onboardingPages = listOf(
        OnboardingPage(
            emoji = "🚛",
            title = "Jadwalkan Pickup-mu",
            description = "Pesan penjemputan sampah di lokasi kamu kapan saja. Tim kami siap hadir tepat waktu!"
        ),
        OnboardingPage(
            emoji = "♻️",
            title = "Jual Barang Bekasmu",
            description = "Berikan kehidupan baru untuk barang-barang bekasmu. Jual di marketplace kami dan dapatkan uang tambahan!"
        ),
        OnboardingPage(
            emoji = "🌍",
            title = "Selamatkan Bumi Bersama",
            description = "Setiap aksi kecilmu berarti besar. Bersama kita bisa membuat bumi lebih hijau dan bersih!"
        )
    )
}
