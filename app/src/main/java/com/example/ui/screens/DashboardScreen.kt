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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.AppTab
import com.example.ui.KasKelasUiState
import com.example.ui.components.CashFlowChart
import com.example.ui.components.SummaryCard
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.RoseLight
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted

@Composable
fun DashboardScreen(
    state: KasKelasUiState,
    onNavigateToTab: (AppTab) -> Unit,
    onAddTransactionClick: () -> Unit,
    onAddForumNoteClick: () -> Unit,
    onGetWhatsAppText: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // 4 Summary Cards Grid
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryCard(
                        title = "Total Saldo Kas",
                        value = CurrencyHelper.formatRupiah(state.currentBalance),
                        subtitle = "Saldo Awal: ${CurrencyHelper.formatRupiah(state.initialBalance)}",
                        icon = Icons.Default.Wallet,
                        iconColor = IndigoPrimary,
                        iconBackgroundColor = IndigoLight,
                        valueColor = IndigoPrimary,
                        modifier = Modifier.weight(1f).testTag("card_total_balance")
                    )

                    val incomeCount = state.transactions.count { it.type == "in" }
                    SummaryCard(
                        title = "Pemasukan",
                        value = CurrencyHelper.formatRupiah(state.totalIncome),
                        subtitle = "$incomeCount Transaksi",
                        icon = Icons.Default.TrendingUp,
                        iconColor = EmeraldIncome,
                        iconBackgroundColor = EmeraldLight,
                        valueColor = EmeraldIncome,
                        modifier = Modifier.weight(1f).testTag("card_total_income")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val expenseCount = state.transactions.count { it.type == "out" }
                    SummaryCard(
                        title = "Pengeluaran",
                        value = CurrencyHelper.formatRupiah(state.totalExpense),
                        subtitle = "$expenseCount Transaksi",
                        icon = Icons.Default.TrendingDown,
                        iconColor = RoseExpense,
                        iconBackgroundColor = RoseLight,
                        valueColor = RoseExpense,
                        modifier = Modifier.weight(1f).testTag("card_total_expense")
                    )

                    SummaryCard(
                        title = "Tunggakan",
                        value = "${state.unpaidStudents.size} Siswa",
                        subtitle = "Lihat Daftar Tunggakan",
                        icon = Icons.Default.PersonOutline,
                        iconColor = AmberWarning,
                        iconBackgroundColor = AmberLight,
                        valueColor = AmberWarning,
                        onClick = { onNavigateToTab(AppTab.MEMBERS) },
                        modifier = Modifier.weight(1f).testTag("card_unpaid_students")
                    )
                }
            }
        }

        // Cash Flow Visualizer Chart
        item {
            CashFlowChart(
                income = state.totalIncome,
                expense = state.totalExpense
            )
        }

        // WhatsApp Broadcast Penagihan Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF047857), Color(0xFF0D9488))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .testTag("whatsapp_broadcast_card")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "WHATSAPP BROADCAST",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tagih Tunggakan Kas Kelas",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Broadcast WA",
                            tint = Color(0xFFA7F3D0),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Kirim atau salin format pesan pengingat tagihan kas otomatis ke grup WhatsApp kelas untuk siswa yang belum melunasi kas mingguan.",
                        color = Color(0xFFD1FAE5),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val text = onGetWhatsAppText()
                                clipboardManager.setText(AnnotatedString(text))
                                Toast.makeText(context, "Teks penagihan disalin ke clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF065F46)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("copy_wa_text_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin Teks",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Salin Teks",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        FilledTonalButton(
                            onClick = {
                                val text = onGetWhatsAppText()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, text)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Bagikan Penagihan Kas")
                                context.startActivity(shareIntent)
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFF064E3B),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("share_wa_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Bagikan",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bagikan WA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Pengumuman Kas Terbaru
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = null,
                                tint = AmberWarning,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pengumuman Kas Terbaru",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SlateTextDark
                            )
                        }

                        OutlinedButton(
                            onClick = onAddForumNoteClick,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("add_announcement_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Tulis", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val latestNote = state.forumNotes.firstOrNull()
                    if (latestNote != null) {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SlateBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = latestNote.content,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SlateTextDark,
                                    lineHeight = 17.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = latestNote.date,
                                    fontSize = 10.sp,
                                    color = SlateTextMuted
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Belum ada pengumuman kas kelas.",
                            fontSize = 12.sp,
                            color = SlateTextMuted,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }

        // Quick navigation shortcuts
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Akses Cepat",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onAddTransactionClick,
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("quick_add_tx_btn")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Catat Kas", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onNavigateToTab(AppTab.MEMBERS) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("quick_members_btn")
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Kelola Siswa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
