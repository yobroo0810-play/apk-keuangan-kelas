package com.example.data.repository

import com.example.data.local.KasKelasDao
import com.example.data.model.BackupDataDto
import com.example.data.model.ForumNoteEntity
import com.example.data.model.SettingEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TransactionEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KasKelasRepository(
    private val dao: KasKelasDao
) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val backupAdapter = moshi.adapter(BackupDataDto::class.java).indent("  ")

    // Flows
    val transactionsFlow: Flow<List<TransactionEntity>> = dao.getAllTransactionsFlow()
    val studentsFlow: Flow<List<StudentEntity>> = dao.getAllStudentsFlow()
    val forumNotesFlow: Flow<List<ForumNoteEntity>> = dao.getAllForumNotesFlow()

    val initialBalanceFlow: Flow<Long> = dao.getSettingValueFlow("initial_balance").map {
        it?.toLongOrNull() ?: 0L
    }

    val weeklyRateFlow: Flow<Long> = dao.getSettingValueFlow("weekly_rate").map {
        it?.toLongOrNull() ?: 5000L
    }

    // Transactions
    suspend fun addTransaction(tx: TransactionEntity): Long {
        return dao.insertTransaction(tx)
    }

    suspend fun updateTransaction(tx: TransactionEntity) {
        dao.updateTransaction(tx)
    }

    suspend fun deleteTransaction(id: Long) {
        dao.deleteTransactionById(id)
    }

    // Students
    suspend fun addStudent(name: String, absen: Int, gender: String): Long {
        val student = StudentEntity(
            name = name.trim(),
            absen = absen,
            gender = gender
        )
        return dao.insertStudent(student)
    }

    suspend fun deleteStudent(id: Long) {
        dao.deleteStudentById(id)
    }

    suspend fun toggleStudentWeek(studentId: Long, weekIndex: Int): Boolean {
        val allStudents = dao.getAllStudents()
        val target = allStudents.find { it.id == studentId } ?: return false

        val newW1 = if (weekIndex == 0) !target.w1 else target.w1
        val newW2 = if (weekIndex == 1) !target.w2 else target.w2
        val newW3 = if (weekIndex == 2) !target.w3 else target.w3
        val newW4 = if (weekIndex == 3) !target.w4 else target.w4

        val isNowPaid = when (weekIndex) {
            0 -> newW1
            1 -> newW2
            2 -> newW3
            3 -> newW4
            else -> false
        }

        val updated = target.copy(w1 = newW1, w2 = newW2, w3 = newW3, w4 = newW4)
        dao.updateStudent(updated)

        // If marked paid, automatically add a transaction
        if (isNowPaid) {
            val rate = dao.getSettingValue("weekly_rate")?.toLongOrNull() ?: 5000L
            if (rate > 0) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayStr = dateFormat.format(Date())
                val newTx = TransactionEntity(
                    date = todayStr,
                    desc = "Kas M${weekIndex + 1} - ${target.name}",
                    category = "Kas Mingguan",
                    type = "in",
                    amount = rate,
                    recordedBy = "Sistem Matrix Kas"
                )
                dao.insertTransaction(newTx)
            }
        }

        return isNowPaid
    }

    // Forum Notes
    suspend fun addForumNote(content: String): Long {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
        val dateStr = dateFormat.format(Date())
        val note = ForumNoteEntity(
            date = dateStr,
            content = content.trim()
        )
        return dao.insertForumNote(note)
    }

    suspend fun deleteForumNote(id: Long) {
        dao.deleteForumNoteById(id)
    }

    // Settings
    suspend fun setInitialBalance(balance: Long) {
        dao.setSetting(SettingEntity("initial_balance", balance.toString()))
    }

    suspend fun setWeeklyRate(rate: Long) {
        dao.setSetting(SettingEntity("weekly_rate", rate.toString()))
    }

    suspend fun resetAllData() {
        dao.clearTransactions()
        dao.clearStudents()
        dao.clearForumNotes()
        dao.clearSettings()
        dao.setSetting(SettingEntity("initial_balance", "0"))
        dao.setSetting(SettingEntity("weekly_rate", "5000"))
    }

    // Backup & Restore
    suspend fun exportJsonBackup(): String {
        val initialBalance = dao.getSettingValue("initial_balance")?.toLongOrNull() ?: 0L
        val weeklyRate = dao.getSettingValue("weekly_rate")?.toLongOrNull() ?: 5000L
        val transactions = dao.getAllTransactions()
        val students = dao.getAllStudents()
        val forumNotes = dao.getAllForumNotes()

        val backup = BackupDataDto(
            initialBalance = initialBalance,
            weeklyRate = weeklyRate,
            transactions = transactions,
            students = students,
            forumNotes = forumNotes
        )

        return backupAdapter.toJson(backup)
    }

    suspend fun importJsonBackup(jsonString: String): Boolean {
        return try {
            val backup = backupAdapter.fromJson(jsonString) ?: return false
            dao.clearTransactions()
            dao.clearStudents()
            dao.clearForumNotes()

            dao.setSetting(SettingEntity("initial_balance", backup.initialBalance.toString()))
            dao.setSetting(SettingEntity("weekly_rate", backup.weeklyRate.toString()))

            if (backup.transactions.isNotEmpty()) {
                dao.insertAllTransactions(backup.transactions)
            }
            if (backup.students.isNotEmpty()) {
                dao.insertAllStudents(backup.students)
            }
            if (backup.forumNotes.isNotEmpty()) {
                dao.insertAllForumNotes(backup.forumNotes)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // CSV Export
    suspend fun exportTransactionsCsv(): String {
        val transactions = dao.getAllTransactions()
        val sb = StringBuilder()
        sb.append("Tanggal,Keterangan,Kategori,Jenis,Jumlah (Rp),Pencatat\n")
        transactions.forEach { t ->
            sb.append("\"${t.date}\",\"${t.desc}\",\"${t.category}\",\"${t.type}\",${t.amount},\"${t.recordedBy}\"\n")
        }
        return sb.toString()
    }

    // WhatsApp Broadcast Message
    fun generateWhatsAppBroadcast(unpaidStudents: List<StudentEntity>): String {
        if (unpaidStudents.isEmpty()) {
            return "Alhamdulillah, seluruh anggota kelas sudah melunasi uang kas minggu ini! Terima kasih semuanya. 🙏"
        }
        val sb = StringBuilder()
        sb.append("*[PENGINGAT KAS KELAS]* 📢\n\n")
        sb.append("Hallo teman-teman, berikut daftar siswa yang belum menyelesaikan uang kas minggu ini:\n\n")
        unpaidStudents.forEachIndexed { index, student ->
            val unpaidWeeks = buildList {
                if (!student.w1) add("Minggu 1")
                if (!student.w2) add("Minggu 2")
                if (!student.w3) add("Minggu 3")
                if (!student.w4) add("Minggu 4")
            }.joinToString(", ")
            sb.append("${index + 1}. *${student.name}* ($unpaidWeeks)\n")
        }
        sb.append("\nMohon segera melunasi ke bendahara kelas ya. Terima kasih! 🙏")
        return sb.toString()
    }
}
