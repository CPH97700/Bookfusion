package com.example.bookfusion.model

data class FirestoreBook(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val coverUrl: String = "",
    val status: String = "FAVORITE",
    val rating: Double? = null,
    val updatedAt: Long = System.currentTimeMillis()

)
