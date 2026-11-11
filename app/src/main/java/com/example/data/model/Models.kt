package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.text.NumberFormat
import java.util.Locale

@Entity(tableName = "transactions")
@JsonClass(generateAdapter = true)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val desc: String,
    val category: String,
    val type: String, // "in" or "out"
    val amount: Long,
    val recordedBy: String
)

@Entity(tableName = "students")
@JsonClass(generateAdapter = true)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val absen: Int,
    val gender: String, // "L" or "P"
    val w1: Boolean = false,
    val w2: Boolean = false,
    val w3: Boolean = false,
    val w4: Boolean = false
) {
    val unpaidWeeksCount: Int
        get() = listOf(w1, w2, w3, w4).count { !it }

    val unpaidWeeksString: String
        get() = buildList {
            if (!w1) add("M1")
            if (!w2) add("M2")
            if (!w3) add("M3")
            if (!w4) add("M4")
        }.joinToString(", ")
}

@Entity(tableName = "forum_notes")
@JsonClass(generateAdapter = true)
data class ForumNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val content: String
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey
    val key: String,
    val value: String
)

@JsonClass(generateAdapter = true)
data class BackupDataDto(
    val initialBalance: Long = 0,
    val weeklyRate: Long = 5000,
    val transactions: List<TransactionEntity> = emptyList(),
    val students: List<StudentEntity> = emptyList(),
    val forumNotes: List<ForumNoteEntity> = emptyList()
)

object CurrencyHelper {
    fun formatRupiah(amount: Long): String {
        val localeID = Locale("in", "ID")
        val formatter = NumberFormat.getCurrencyInstance(localeID).apply {
            maximumFractionDigits = 0
        }
        return formatter.format(amount)
    }
}
