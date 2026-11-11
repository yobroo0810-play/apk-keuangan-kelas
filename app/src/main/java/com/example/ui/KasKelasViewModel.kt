package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.KasKelasDatabase
import com.example.data.model.ForumNoteEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TransactionEntity
import com.example.data.repository.KasKelasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String) {
    DASHBOARD("Dashboard"),
    TRANSACTIONS("Buku Kas"),
    MEMBERS("Anggota"),
    REPORTS("Laporan"),
    SETTINGS("Pengaturan")
}

data class KasKelasUiState(
    val currentTab: AppTab = AppTab.DASHBOARD,
    val initialBalance: Long = 0L,
    val weeklyRate: Long = 5000L,
    val transactions: List<TransactionEntity> = emptyList(),
    val students: List<StudentEntity> = emptyList(),
    val forumNotes: List<ForumNoteEntity> = emptyList(),
    val searchTxQuery: String = "",
    val filterTxType: String = "all", // "all", "in", "out"
    val searchStudentQuery: String = "",
    val selectedReportMonth: String = "2026-11",
    // Dialog states
    val showAddEditTxDialog: Boolean = false,
    val editingTransaction: TransactionEntity? = null,
    val showInitialBalanceDialog: Boolean = false,
    val showWeeklyRateDialog: Boolean = false,
    val showAddStudentDialog: Boolean = false,
    val showAddForumNoteDialog: Boolean = false,
    val showResetConfirmDialog: Boolean = false,
    val showDownloadApkDialog: Boolean = false,
    val toastMessage: String? = null
) {
    val totalIncome: Long
        get() = transactions.filter { it.type == "in" }.sumOf { it.amount }

    val totalExpense: Long
        get() = transactions.filter { it.type == "out" }.sumOf { it.amount }

    val currentBalance: Long
        get() = initialBalance + totalIncome - totalExpense

    val unpaidStudents: List<StudentEntity>
        get() = students.filter { !it.w1 || !it.w2 || !it.w3 || !it.w4 }

    val filteredTransactions: List<TransactionEntity>
        get() = transactions.filter { tx ->
            val matchQuery = tx.desc.contains(searchTxQuery, ignoreCase = true) ||
                    tx.category.contains(searchTxQuery, ignoreCase = true) ||
                    tx.recordedBy.contains(searchTxQuery, ignoreCase = true)
            val matchType = when (filterTxType) {
                "in" -> tx.type == "in"
                "out" -> tx.type == "out"
                else -> true
            }
            matchQuery && matchType
        }

    val filteredStudents: List<StudentEntity>
        get() = students.filter {
            it.name.contains(searchStudentQuery, ignoreCase = true) ||
                    it.absen.toString().contains(searchStudentQuery)
        }

    val monthTransactions: List<TransactionEntity>
        get() = transactions.filter { it.date.startsWith(selectedReportMonth) }

    val monthIncome: Long
        get() = monthTransactions.filter { it.type == "in" }.sumOf { it.amount }

    val monthExpense: Long
        get() = monthTransactions.filter { it.type == "out" }.sumOf { it.amount }

    val monthNetBalance: Long
        get() = monthIncome - monthExpense

    val monthExpensesList: List<TransactionEntity>
        get() = monthTransactions.filter { it.type == "out" }
}

class KasKelasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: KasKelasRepository
    private val _uiStateInternal = MutableStateFlow(
        KasKelasUiState(
            selectedReportMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())
        )
    )

    init {
        val database = KasKelasDatabase.getDatabase(application, viewModelScope)
        repository = KasKelasRepository(database.kasKelasDao())
    }

    private val dataFlow = combine(
        repository.transactionsFlow,
        repository.studentsFlow,
        repository.forumNotesFlow
    ) { transactions, students, notes ->
        Triple(transactions, students, notes)
    }

    val uiState: StateFlow<KasKelasUiState> = combine(
        _uiStateInternal,
        repository.initialBalanceFlow,
        repository.weeklyRateFlow,
        dataFlow
    ) { internalState, initialBalance, weeklyRate, (transactions, students, notes) ->
        internalState.copy(
            initialBalance = initialBalance,
            weeklyRate = weeklyRate,
            transactions = transactions,
            students = students,
            forumNotes = notes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = KasKelasUiState()
    )

    fun selectTab(tab: AppTab) {
        _uiStateInternal.update { it.copy(currentTab = tab) }
    }

    fun setSearchTxQuery(query: String) {
        _uiStateInternal.update { it.copy(searchTxQuery = query) }
    }

    fun setFilterTxType(type: String) {
        _uiStateInternal.update { it.copy(filterTxType = type) }
    }

    fun setSearchStudentQuery(query: String) {
        _uiStateInternal.update { it.copy(searchStudentQuery = query) }
    }

    fun setSelectedReportMonth(monthYear: String) {
        _uiStateInternal.update { it.copy(selectedReportMonth = monthYear) }
    }

    fun showToast(message: String) {
        _uiStateInternal.update { it.copy(toastMessage = message) }
    }

    fun clearToast() {
        _uiStateInternal.update { it.copy(toastMessage = null) }
    }

    // Dialog handlers
    fun openAddTransactionDialog() {
        _uiStateInternal.update { it.copy(showAddEditTxDialog = true, editingTransaction = null) }
    }

    fun openEditTransactionDialog(transaction: TransactionEntity) {
        _uiStateInternal.update { it.copy(showAddEditTxDialog = true, editingTransaction = transaction) }
    }

    fun closeAddEditTransactionDialog() {
        _uiStateInternal.update { it.copy(showAddEditTxDialog = false, editingTransaction = null) }
    }

    fun openInitialBalanceDialog() {
        _uiStateInternal.update { it.copy(showInitialBalanceDialog = true) }
    }

    fun closeInitialBalanceDialog() {
        _uiStateInternal.update { it.copy(showInitialBalanceDialog = false) }
    }

    fun openWeeklyRateDialog() {
        _uiStateInternal.update { it.copy(showWeeklyRateDialog = true) }
    }

    fun closeWeeklyRateDialog() {
        _uiStateInternal.update { it.copy(showWeeklyRateDialog = false) }
    }

    fun openAddStudentDialog() {
        _uiStateInternal.update { it.copy(showAddStudentDialog = true) }
    }

    fun closeAddStudentDialog() {
        _uiStateInternal.update { it.copy(showAddStudentDialog = false) }
    }

    fun openAddForumNoteDialog() {
        _uiStateInternal.update { it.copy(showAddForumNoteDialog = true) }
    }

    fun closeAddForumNoteDialog() {
        _uiStateInternal.update { it.copy(showAddForumNoteDialog = false) }
    }

    fun openResetConfirmDialog() {
        _uiStateInternal.update { it.copy(showResetConfirmDialog = true) }
    }

    fun closeResetConfirmDialog() {
        _uiStateInternal.update { it.copy(showResetConfirmDialog = false) }
    }

    fun openDownloadApkDialog() {
        _uiStateInternal.update { it.copy(showDownloadApkDialog = true) }
    }

    fun closeDownloadApkDialog() {
        _uiStateInternal.update { it.copy(showDownloadApkDialog = false) }
    }

    // Data Actions
    fun saveTransaction(
        id: Long?,
        date: String,
        desc: String,
        category: String,
        type: String,
        amount: Long,
        recordedBy: String
    ) {
        viewModelScope.launch {
            if (id != null && id > 0) {
                repository.updateTransaction(
                    TransactionEntity(
                        id = id,
                        date = date,
                        desc = desc,
                        category = category,
                        type = type,
                        amount = amount,
                        recordedBy = recordedBy
                    )
                )
                showToast("Transaksi berhasil diperbarui!")
            } else {
                repository.addTransaction(
                    TransactionEntity(
                        date = date,
                        desc = desc,
                        category = category,
                        type = type,
                        amount = amount,
                        recordedBy = recordedBy
                    )
                )
                showToast("Transaksi kas berhasil disimpan!")
            }
            closeAddEditTransactionDialog()
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
            showToast("Transaksi kas dihapus.")
        }
    }

    fun saveInitialBalance(balance: Long) {
        viewModelScope.launch {
            repository.setInitialBalance(balance)
            closeInitialBalanceDialog()
            showToast("Saldo awal kas berhasil diperbarui!")
        }
    }

    fun saveWeeklyRate(rate: Long) {
        viewModelScope.launch {
            repository.setWeeklyRate(rate)
            closeWeeklyRateDialog()
            showToast("Tarif kas mingguan diubah!")
        }
    }

    fun addStudent(name: String, absen: Int, gender: String) {
        viewModelScope.launch {
            repository.addStudent(name, absen, gender)
            closeAddStudentDialog()
            showToast("Anggota kelas berhasil ditambahkan!")
        }
    }

    fun deleteStudent(id: Long) {
        viewModelScope.launch {
            repository.deleteStudent(id)
            showToast("Anggota kelas berhasil dihapus.")
        }
    }

    fun toggleStudentWeek(studentId: Long, weekIndex: Int) {
        viewModelScope.launch {
            val isNowPaid = repository.toggleStudentWeek(studentId, weekIndex)
            if (isNowPaid) {
                showToast("Status Minggu ${weekIndex + 1} lunas & otomatis dicatat ke buku kas!")
            } else {
                showToast("Status Minggu ${weekIndex + 1} diubah menjadi belum bayar.")
            }
        }
    }

    fun addForumNote(content: String) {
        viewModelScope.launch {
            repository.addForumNote(content)
            closeAddForumNoteDialog()
            showToast("Catatan evaluasi akhir bulan diposting!")
        }
    }

    fun deleteForumNote(id: Long) {
        viewModelScope.launch {
            repository.deleteForumNote(id)
            showToast("Catatan forum dihapus.")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            closeResetConfirmDialog()
            showToast("Seluruh database kas berhasil direset.")
        }
    }

    suspend fun getJsonBackup(): String {
        return repository.exportJsonBackup()
    }

    fun importJsonBackup(jsonString: String) {
        viewModelScope.launch {
            val success = repository.importJsonBackup(jsonString)
            if (success) {
                showToast("Database KasKelas berhasil dipulihkan dari file JSON!")
            } else {
                showToast("Gagal mengimpor file backup. Format JSON tidak sesuai.")
            }
        }
    }

    suspend fun getTransactionsCsv(): String {
        return repository.exportTransactionsCsv()
    }

    fun getWhatsAppReminderText(): String {
        return repository.generateWhatsAppBroadcast(uiState.value.unpaidStudents)
    }
}
