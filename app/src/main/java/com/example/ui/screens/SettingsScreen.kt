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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.AmberBadge
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.RoseLight
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    state: KasKelasUiState,
    onSetInitialBalanceClick: () -> Unit,
    onEditRateClick: () -> Unit,
    onResetDataClick: () -> Unit,
    onOpenImportDialog: () -> Unit,
    onExportBackupJson: suspend () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val appShareUrl = "https://ais-pre-4gjws6t35xhqeralkam4pb-46151358573.asia-southeast1.run.app"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Pusat Unduhan & Instalasi APK Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF312E81), Color(0xFF4338CA), Color(0xFF6366F1))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("download_apk_section_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = AmberBadge,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "DOWNLOAD & INSTALASI",
                                    color = Color(0xFF0F172A),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Unduh Aplikasi KasKelas PRO (APK)",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Android,
                            contentDescription = "Android App",
                            tint = Color(0xFFA5B4FC),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Cara mengunduh file instalasi APK KasKelas PRO:\n" +
                                "1. Buka menu pengaturan (ikon gear ⚙️ / titik tiga) di bilah atas emulator browser ini.\n" +
                                "2. Pilih menu \"Export\" / \"Download APK\" untuk mengunduh file installer (.apk).\n" +
                                "3. Pasang langsung file APK di smartphone Android Anda.",
                        color = Color(0xFFE0E7FF),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(appShareUrl))
                                Toast.makeText(context, "Link aplikasi disalin: $appShareUrl", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = IndigoPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("copy_download_link_btn")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Salin Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        FilledTonalButton(
                            onClick = {
                                val shareText = buildString {
                                    appendLine("🚀 *Unduh Aplikasi KasKelas PRO*")
                                    appendLine("Aplikasi Manajemen Kas Kelas Terpadu untuk Bendahara & Siswa.")
                                    appendLine()
                                    appendLine("🔗 Link Akses & Preview: $appShareUrl")
                                    appendLine()
                                    appendLine("Cara unduh APK: Klik tombol menu Setting di browser emulator -> Pilih 'Download APK' atau 'Export Project'.")
                                }
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Bagikan Link Unduh KasKelas PRO"))
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF1E1B4B),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("share_apk_info_btn")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Bagikan Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Pengaturan Kas
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Konfigurasi Kas Kelas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SlateTextDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Saldo Awal Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Saldo Awal Kas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateTextDark)
                            Text(
                                text = CurrencyHelper.formatRupiah(state.initialBalance),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = IndigoPrimary
                            )
                        }
                        OutlinedButton(
                            onClick = onSetInitialBalanceClick,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ubah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SlateBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Tarif Kas Mingguan Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Tarif Kas Mingguan", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SlateTextDark)
                            Text(
                                text = "${CurrencyHelper.formatRupiah(state.weeklyRate)} / minggu",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = IndigoPrimary
                            )
                        }
                        OutlinedButton(
                            onClick = onEditRateClick,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ubah", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Backup & Restore Database
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Backup & Restore Database",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateTextDark
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Simpan seluruh data kas ke file JSON sebagai cadangan, atau pulihkan data dari file cadangan sebelumnya.",
                        fontSize = 11.sp,
                        color = SlateTextMuted,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val json = onExportBackupJson()
                                    clipboardManager.setText(AnnotatedString(json))
                                    Toast.makeText(context, "Backup JSON berhasil disalin ke clipboard!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("backup_json_btn")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Backup JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onOpenImportDialog,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("restore_json_btn")
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Restore JSON", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Zone Bahaya (Reset Data)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Color(0xFFFECDD3)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = RoseExpense,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Zone Bahaya (Reset Data)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = RoseExpense
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tindakan ini akan menghapus seluruh data transaksi, daftar siswa, dan catatan evaluasi secara permanen.",
                        fontSize = 11.sp,
                        color = SlateTextMuted,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onResetDataClick,
                        colors = ButtonDefaults.buttonColors(containerColor = RoseExpense),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("reset_all_data_btn")
                    ) {
                        Text("Hapus & Reset Semua Data Kas", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
