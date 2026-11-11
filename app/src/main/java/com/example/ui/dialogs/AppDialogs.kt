package com.example.ui.dialogs

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
import com.example.data.model.TransactionEntity
import com.example.ui.theme.AmberBadge
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionDialog(
    transaction: TransactionEntity?,
    students: List<StudentEntity>,
    defaultWeeklyRate: Long,
    onDismiss: () -> Unit,
    onSave: (id: Long?, date: String, desc: String, category: String, type: String, amount: Long, recordedBy: String) -> Unit
) {
    var type by remember { mutableStateOf(transaction?.type ?: "in") }

    val categoriesIn = listOf("Kas Mingguan", "Sumbangan", "Pemasukan Acara", "Lain-lain")
    val categoriesOut = listOf("Alat Tulis & Kelas", "Acara / Lomba", "Sosialisasi / Duka", "Lain-lain")
    val availableCategories = if (type == "in") categoriesIn else categoriesOut

    var category by remember {
        mutableStateOf(transaction?.category ?: if (type == "in") "Kas Mingguan" else "Alat Tulis & Kelas")
    }

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var date by remember { mutableStateOf(transaction?.date ?: todayStr) }
    var desc by remember { mutableStateOf(transaction?.desc ?: "") }
    var amountText by remember {
        mutableStateOf(transaction?.amount?.toString() ?: if (category == "Kas Mingguan") defaultWeeklyRate.toString() else "")
    }
    var recordedBy by remember { mutableStateOf(transaction?.recordedBy ?: "Bendahara 1") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var studentExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (transaction != null) "Edit Transaksi Kas" else "Catat Transaksi Baru",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Type Selector Tabs (In vs Out)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isIncome = type == "in"
                    Surface(
                        color = if (isIncome) EmeraldIncome else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                type = "in"
                                category = "Kas Mingguan"
                                if (amountText.isEmpty()) amountText = defaultWeeklyRate.toString()
                            }
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Pemasukan (+)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isIncome) Color.White else SlateTextMuted
                            )
                        }
                    }

                    Surface(
                        color = if (!isIncome) RoseExpense else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                type = "out"
                                category = "Alat Tulis & Kelas"
                            }
                    ) {
                        Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Pengeluaran (-)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (!isIncome) Color.White else SlateTextMuted
                            )
                        }
                    }
                }

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori", fontSize = 11.sp) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        availableCategories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, fontSize = 12.sp) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                    if (cat == "Kas Mingguan" && amountText.isEmpty()) {
                                        amountText = defaultWeeklyRate.toString()
                                    }
                                }
                            )
                        }
                    }
                }

                // Student Picker (Optional shortcut if Kas Mingguan)
                if (category == "Kas Mingguan" && students.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = studentExpanded,
                        onExpandedChange = { studentExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Pilih Nama Siswa (Opsional)", fontSize = 11.sp) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = studentExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = studentExpanded,
                            onDismissRequest = { studentExpanded = false }
                        ) {
                            students.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("${s.absen}. ${s.name}", fontSize = 12.sp) },
                                    onClick = {
                                        desc = "Kas Mingguan - ${s.name}"
                                        studentExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Keterangan / Deskripsi", fontSize = 11.sp) },
                    placeholder = { Text("Contoh: Kas Mingguan / Beli Spidol", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_desc_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Jumlah Nominal (Rp)", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_amount_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                // Date & Recorded By
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Tanggal (YYYY-MM-DD)", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = recordedBy,
                        onValueChange = { recordedBy = it },
                        label = { Text("Pencatat", fontSize = 10.sp) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    if (desc.isNotBlank() && amount > 0) {
                        onSave(
                            transaction?.id,
                            date.ifBlank { todayStr },
                            desc.trim(),
                            category,
                            type,
                            amount,
                            recordedBy.ifBlank { "Bendahara" }
                        )
                    }
                },
                enabled = desc.isNotBlank() && (amountText.toLongOrNull() ?: 0L) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_tx_confirm_btn")
            ) {
                Text(if (transaction != null) "Perbarui" else "Simpan Transaksi")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun SetInitialBalanceDialog(
    currentBalance: Long,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var amountText by remember { mutableStateOf(currentBalance.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Set Saldo Awal Kas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Nominal saldo awal kas kelas (ditambahkan ke saldo kas permanen):",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nominal Saldo Awal (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("initial_balance_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 0L
                    onSave(amt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun UpdateWeeklyRateDialog(
    currentRate: Long,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var rateText by remember { mutableStateOf(currentRate.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Tarif Kas Mingguan", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tarif kas standar yang ditagihkan setiap minggu per siswa:",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
                OutlinedTextField(
                    value = rateText,
                    onValueChange = { rateText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Tarif Per Minggu (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("weekly_rate_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val r = rateText.toLongOrNull() ?: 5000L
                    onSave(r)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Terapkan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, absen: Int, gender: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var absenText by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("L") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Siswa Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap Siswa") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_name_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = absenText,
                    onValueChange = { absenText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("No Absen / NIS") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("student_absen_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Column {
                    Text("Jenis Kelamin:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateTextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender == "L", onClick = { gender = "L" })
                            Text("Laki-laki (L)", fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = gender == "P", onClick = { gender = "P" })
                            Text("Perempuan (P)", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val absen = absenText.toIntOrNull() ?: 1
                    if (name.isNotBlank()) {
                        onSave(name.trim(), absen, gender)
                    }
                },
                enabled = name.isNotBlank() && absenText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("save_student_confirm_btn")
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun AddForumNoteDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tulis Catatan / Pengumuman Kas", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Isi Catatan / Pengumuman") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("forum_note_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) onSave(content.trim())
                },
                enabled = content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Posting")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
fun DownloadApkDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val appShareUrl = "https://ais-pre-4gjws6t35xhqeralkam4pb-46151358573.asia-southeast1.run.app"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Android, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Unduh Aplikasi KasKelas PRO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Aplikasi KasKelas PRO dapat langsung diunduh dan dipasang di smartphone Android Anda.",
                    fontSize = 12.sp,
                    color = SlateTextDark,
                    lineHeight = 17.sp
                )

                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Petunjuk Download APK:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "1. Klik tombol 'Settings' (ikon gerigi atau titik tiga) di bar atas emulator web AI Studio.\n" +
                                    "2. Klik menu 'Download APK' atau 'Export Project'.\n" +
                                    "3. File APK akan otomatis terunduh ke komputer/HP Anda dan siap dipasang.",
                            fontSize = 11.sp,
                            color = SlateTextMuted,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(appShareUrl))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salin Link", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val shareText = "🚀 Unduh Aplikasi KasKelas PRO untuk Android: $appShareUrl"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Bagikan Link KasKelas PRO"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bagikan", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
            ) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun JsonImportDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var jsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore Database Kas dari JSON", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tempelkan (Paste) isi teks file backup JSON KasKelas ke dalam kotak di bawah ini:",
                    fontSize = 12.sp,
                    color = SlateTextMuted
                )
                OutlinedTextField(
                    value = jsonText,
                    onValueChange = { jsonText = it },
                    placeholder = { Text("{\"initialBalance\": 0, \"transactions\": [...]}") },
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth().testTag("import_json_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (jsonText.isNotBlank()) onImport(jsonText.trim())
                    onDismiss()
                },
                enabled = jsonText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("confirm_import_json_btn")
            ) {
                Text("Pulihkan Data")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
