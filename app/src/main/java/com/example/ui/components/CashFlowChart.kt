package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyHelper
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted

@Composable
fun CashFlowChart(
    income: Long,
    expense: Long,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = IndigoPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Visualisasi Arus Kas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SlateTextDark
                    )
                    Text(
                        text = "Perbandingan Total Pemasukan vs Pengeluaran",
                        fontSize = 11.sp,
                        color = SlateTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Chart Canvas
            val maxAmount = maxOf(income, expense, 1L).toFloat()
            val incomeRatio = (income.toFloat() / maxAmount).coerceIn(0.04f, 1f)
            val expenseRatio = (expense.toFloat() / maxAmount).coerceIn(0.04f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val w = size.width
                    val h = size.height
                    val barWidth = (w * 0.22f).coerceAtMost(90f)

                    // Baseline
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(0f, h - 2f),
                        end = Offset(w, h - 2f),
                        strokeWidth = 2f
                    )

                    // Grid line 50%
                    drawLine(
                        color = Color(0xFFF1F5F9),
                        start = Offset(0f, h / 2f),
                        end = Offset(w, h / 2f),
                        strokeWidth = 1f
                    )

                    // Bar 1: Income (Emerald)
                    val bar1X = (w * 0.28f) - (barWidth / 2f)
                    val bar1Height = (h - 10f) * incomeRatio
                    val bar1Y = h - bar1Height

                    drawRoundRect(
                        color = Color(0xFF10B981),
                        topLeft = Offset(bar1X, bar1Y),
                        size = Size(barWidth, bar1Height),
                        cornerRadius = CornerRadius(10f, 10f)
                    )

                    // Bar 2: Expense (Rose)
                    val bar2X = (w * 0.72f) - (barWidth / 2f)
                    val bar2Height = (h - 10f) * expenseRatio
                    val bar2Y = h - bar2Height

                    drawRoundRect(
                        color = Color(0xFFF43F5E),
                        topLeft = Offset(bar2X, bar2Y),
                        size = Size(barWidth, bar2Height),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Labels below bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(EmeraldIncome, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pemasukan",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )
                    }
                    Text(
                        text = CurrencyHelper.formatRupiah(income),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldIncome
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(RoseExpense, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pengeluaran",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextMuted
                        )
                    }
                    Text(
                        text = CurrencyHelper.formatRupiah(expense),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = RoseExpense
                    )
                }
            }
        }
    }
}
