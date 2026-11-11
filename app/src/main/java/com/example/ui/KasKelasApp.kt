package com.example.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.MainActivity
import com.example.ui.components.AppTopBar
import com.example.ui.dialogs.AddEditTransactionDialog
import com.example.ui.dialogs.AddForumNoteDialog
import com.example.ui.dialogs.AddStudentDialog
import com.example.ui.dialogs.DownloadApkDialog
import com.example.ui.dialogs.JsonImportDialog
import com.example.ui.dialogs.SetInitialBalanceDialog
import com.example.ui.dialogs.UpdateWeeklyRateDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MembersScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.KasKelasTheme
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted

@Composable
fun KasKelasApp(viewModel: KasKelasViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showImportJsonDialog by remember { mutableStateOf(false) }

    // Handle toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Back handling: return to dashboard if on another tab
    if (uiState.currentTab != AppTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(AppTab.DASHBOARD)
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                onSetInitialBalanceClick = { viewModel.openInitialBalanceDialog() },
                onDownloadApkClick = { viewModel.openDownloadApkDialog() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar")
            ) {
                val tabs = listOf(
                    Triple(AppTab.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
                    Triple(AppTab.TRANSACTIONS, "Buku Kas", Icons.Default.ReceiptLong),
                    Triple(AppTab.MEMBERS, "Anggota", Icons.Default.Group),
                    Triple(AppTab.REPORTS, "Laporan", Icons.Default.Assessment),
                    Triple(AppTab.SETTINGS, "Pengaturan", Icons.Default.Settings)
                )

                tabs.forEach { (tab, label, icon) ->
                    val isSelected = uiState.currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            unselectedIconColor = SlateTextMuted,
                            unselectedTextColor = SlateTextMuted,
                            indicatorColor = Color(0xFFEEF2FF)
                        ),
                        modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentTab) {
                AppTab.DASHBOARD -> {
                    DashboardScreen(
                        state = uiState,
                        onNavigateToTab = { viewModel.selectTab(it) },
                        onAddTransactionClick = { viewModel.openAddTransactionDialog() },
                        onAddForumNoteClick = { viewModel.openAddForumNoteDialog() },
                        onGetWhatsAppText = { viewModel.getWhatsAppReminderText() }
                    )
                }
                AppTab.TRANSACTIONS -> {
                    TransactionsScreen(
                        state = uiState,
                        onSearchChange = { viewModel.setSearchTxQuery(it) },
                        onFilterChange = { viewModel.setFilterTxType(it) },
                        onAddClick = { viewModel.openAddTransactionDialog() },
                        onEditClick = { viewModel.openEditTransactionDialog(it) },
                        onDeleteClick = { viewModel.deleteTransaction(it) }
                    )
                }
                AppTab.MEMBERS -> {
                    MembersScreen(
                        state = uiState,
                        onSearchChange = { viewModel.setSearchStudentQuery(it) },
                        onAddStudentClick = { viewModel.openAddStudentDialog() },
                        onEditRateClick = { viewModel.openWeeklyRateDialog() },
                        onToggleWeek = { studentId, weekIdx -> viewModel.toggleStudentWeek(studentId, weekIdx) },
                        onDeleteStudent = { viewModel.deleteStudent(it) }
                    )
                }
                AppTab.REPORTS -> {
                    ReportsScreen(
                        state = uiState,
                        onMonthSelected = { viewModel.setSelectedReportMonth(it) },
                        onAddForumNote = { viewModel.addForumNote(it) },
                        onDeleteForumNote = { viewModel.deleteForumNote(it) },
                        onExportCsv = { viewModel.getTransactionsCsv() }
                    )
                }
                AppTab.SETTINGS -> {
                    SettingsScreen(
                        state = uiState,
                        onSetInitialBalanceClick = { viewModel.openInitialBalanceDialog() },
                        onEditRateClick = { viewModel.openWeeklyRateDialog() },
                        onResetDataClick = { viewModel.openResetConfirmDialog() },
                        onOpenImportDialog = { showImportJsonDialog = true },
                        onExportBackupJson = { viewModel.getJsonBackup() }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.showAddEditTxDialog) {
        AddEditTransactionDialog(
            transaction = uiState.editingTransaction,
            students = uiState.students,
            defaultWeeklyRate = uiState.weeklyRate,
            onDismiss = { viewModel.closeAddEditTransactionDialog() },
            onSave = { id, date, desc, category, type, amount, recordedBy ->
                viewModel.saveTransaction(id, date, desc, category, type, amount, recordedBy)
            }
        )
    }

    if (uiState.showInitialBalanceDialog) {
        SetInitialBalanceDialog(
            currentBalance = uiState.initialBalance,
            onDismiss = { viewModel.closeInitialBalanceDialog() },
            onSave = { viewModel.saveInitialBalance(it) }
        )
    }

    if (uiState.showWeeklyRateDialog) {
        UpdateWeeklyRateDialog(
            currentRate = uiState.weeklyRate,
            onDismiss = { viewModel.closeWeeklyRateDialog() },
            onSave = { viewModel.saveWeeklyRate(it) }
        )
    }

    if (uiState.showAddStudentDialog) {
        AddStudentDialog(
            onDismiss = { viewModel.closeAddStudentDialog() },
            onSave = { name, absen, gender -> viewModel.addStudent(name, absen, gender) }
        )
    }

    if (uiState.showAddForumNoteDialog) {
        AddForumNoteDialog(
            onDismiss = { viewModel.closeAddForumNoteDialog() },
            onSave = { viewModel.addForumNote(it) }
        )
    }

    if (uiState.showDownloadApkDialog) {
        DownloadApkDialog(
            onDismiss = { viewModel.closeDownloadApkDialog() }
        )
    }

    if (showImportJsonDialog) {
        JsonImportDialog(
            onDismiss = { showImportJsonDialog = false },
            onImport = { viewModel.importJsonBackup(it) }
        )
    }

    if (uiState.showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.closeResetConfirmDialog() },
            title = { Text("Reset Semua Data Kas?", fontWeight = FontWeight.Bold) },
            text = { Text("Tindakan ini akan menghapus seluruh data transaksi, daftar siswa, dan catatan evaluasi secara permanen. Apakah Anda yakin?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetAllData() },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseExpense)
                ) {
                    Text("Hapus Semua Data", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeResetConfirmDialog() }) {
                    Text("Batal")
                }
            }
        )
    }
}
