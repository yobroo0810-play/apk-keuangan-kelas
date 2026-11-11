package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyHelper
import com.example.ui.KasKelasUiState
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun ReportsScreen(
    state: KasKelasUiState,
    onMonthSelected: (String) -> Unit,
    onAddForumNote: (String) -> Unit,
    onDeleteForumNote: (Long) -> Unit,
    onExportCsv: suspend () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var newNoteContent by remember { mutableStateOf("") }

    // Helper to calculate previous / next month
    fun shiftMonth(offset: Int) {
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        val cal = Calendar.getInstance()
        try {
            val date = sdf.parse(state.selectedReportMonth)
            if (date != null) cal.time = date
        } catch (_: Exception) {}
        cal.add(Calendar.MONTH, offset)
        onMonthSelected(sdf.format(cal.time))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Month Selector Bar & Export Buttons
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bulan Laporan",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextDark
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { shiftMonth(-1) }, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Bulan Sebelumnya", modifier = Modifier.size(16.dp))
                            }
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                Text(
                                    text = state.selectedReportMonth,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = IndigoPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            IconButton(onClick = { shiftMonth(1) }, modifier = Modifier.size(30.dp)) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Bulan Berikutnya", modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    val csv = onExportCsv()
                                    clipboardManager.setText(AnnotatedString(csv))
                                    Toast.makeText(context, "Data CSV kas berhasil disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("export_csv_btn")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                val reportText = buildString {
                                    appendLine("===============================")
                                    appendLine("LAPORAN REKAPITULASI KAS KELAS")
                                    appendLine("Periode: ${state.selectedReportMonth}")
                                    appendLine("===============================")
                                    appendLine("Total Pemasukan  : ${CurrencyHelper.formatRupiah(state.monthIncome)}")
                                    appendLine("Total Pengeluaran: ${CurrencyHelper.formatRupiah(state.monthExpense)}")
                                    appendLine("Saldo Bersih     : ${CurrencyHelper.formatRupiah(state.monthNetBalance)}")
                                    appendLine("Total Saldo Kas  : ${CurrencyHelper.formatRupiah(state.currentBalance)}")
                                    appendLine("-------------------------------")
                                    appendLine("Rincian Pengeluaran:")
                                    if (state.monthExpensesList.isEmpty()) {
                                        appendLine("Tidak ada pengeluaran pada bulan ini.")
                                    } else {
                                        state.monthExpensesList.forEachIndexed { i, e ->
                                            appendLine("${i + 1}. [${e.date}] ${e.desc} (${e.category}): ${CurrencyHelper.formatRupiah(e.amount)}")
                                        }
                                    }
                                    appendLine("===============================")
                                    appendLine("Dicetak dari KasKelas PRO")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, reportText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Laporan Kas"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("share_report_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bagikan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Printable Sheet / Resume Recap Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, Color(0xFF0F172A).copy(alpha = 0.15f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().testTag("report_recap_sheet")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header Rekapitulasi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LAPORAN REKAPITULASI KAS KELAS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = SlateTextDark,
                                letterSpacing = (-0.2).sp
                            )
                            Text(
                                text = "Periode: ${state.selectedReportMonth}",
                                fontSize = 11.sp,
                                color = SlateTextMuted
                            )
                        }
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "KasKelas PRO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = IndigoPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFF0F172A), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // 3 Box Summary
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "PEMASUKAN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(state.monthIncome),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = EmeraldIncome
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(SlateBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "PENGELUARAN", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(state.monthExpense),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = RoseExpense
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(SlateBorder)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "SALDO BERSIH", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = CurrencyHelper.formatRupiah(state.monthNetBalance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = IndigoPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Rincian Pengeluaran Table
                    Text(
                        text = "Rincian Pengeluaran Bulan Ini",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.monthExpensesList.isEmpty()) {
                        Text(
                            text = "Tidak ada rincian pengeluaran pada bulan ini.",
                            fontSize = 11.sp,
                            color = SlateTextMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            state.monthExpensesList.forEach { exp ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = exp.desc,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = SlateTextDark
                                        )
                                        Text(
                                            text = "${exp.date} • ${exp.category}",
                                            fontSize = 10.sp,
                                            color = SlateTextMuted
                                        )
                                    }
                                    Text(
                                        text = CurrencyHelper.formatRupiah(exp.amount),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoseExpense
                                    )
                                }
                                HorizontalDivider(color = Color(0xFFE2E8F0))
                            }
                        }
                    }
                }
            }
        }

        // Forum Catatan & Evaluasi Akhir Bulan
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Forum Catatan & Evaluasi Kas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input Form
                    OutlinedTextField(
                        value = newNoteContent,
                        onValueChange = { newNoteContent = it },
                        placeholder = { Text("Tuliskan hasil pertanggungjawaban kas akhir bulan...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = SlateBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = {
                                if (newNoteContent.isNotBlank()) {
                                    onAddForumNote(newNoteContent.trim())
                                    newNoteContent = ""
                                }
                            },
                            enabled = newNoteContent.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("post_evaluation_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Posting Evaluasi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Feed Notes
                    if (state.forumNotes.isEmpty()) {
                        Text(
                            text = "Belum ada catatan evaluasi.",
                            fontSize = 11.sp,
                            color = SlateTextMuted
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.forumNotes.forEach { note ->
                                Surface(
                                    color = Color(0xFFF8FAFC),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, SlateBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = note.date,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SlateTextMuted
                                            )
                                            IconButton(
                                                onClick = { onDeleteForumNote(note.id) },
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Hapus", modifier = Modifier.size(14.dp), tint = SlateTextMuted)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = note.content,
                                            fontSize = 12.sp,
                                            color = SlateTextDark,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
