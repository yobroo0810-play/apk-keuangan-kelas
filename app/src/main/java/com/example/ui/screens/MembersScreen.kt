package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyHelper
import com.example.data.model.StudentEntity
import com.example.ui.KasKelasUiState
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.EmeraldIncome
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseExpense
import com.example.ui.theme.RoseLight
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateTextDark
import com.example.ui.theme.SlateTextMuted

@Composable
fun MembersScreen(
    state: KasKelasUiState,
    onSearchChange: (String) -> Unit,
    onAddStudentClick: () -> Unit,
    onEditRateClick: () -> Unit,
    onToggleWeek: (Long, Int) -> Unit,
    onDeleteStudent: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteCandidateId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))

            // Tarif Kas Configuration Bar
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(IndigoLight, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Tarif Kas Mingguan Standard",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextDark
                            )
                            Text(
                                text = "${CurrencyHelper.formatRupiah(state.weeklyRate)} / minggu",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = IndigoPrimary
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onEditRateClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("edit_rate_btn")
                    ) {
                        Text("Ubah Tarif", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Unpaid students alert widget
        item {
            if (state.unpaidStudents.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberLight),
                    border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth().testTag("unpaid_students_banner")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AmberWarning,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Penunggak Kas Minggu Ini",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF78350F)
                                )
                            }
                            Surface(
                                color = Color(0xFFFDE68A),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "${state.unpaidStudents.size} Siswa",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF78350F),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            state.unpaidStudents.take(5).forEach { s ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${s.absen}. ${s.name}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SlateTextDark
                                    )
                                    Surface(
                                        color = RoseLight,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "Belum: ${s.unpaidWeeksString}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoseExpense,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            if (state.unpaidStudents.size > 5) {
                                Text(
                                    text = "+${state.unpaidStudents.size - 5} siswa lainnya...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }
                }
            } else {
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = EmeraldIncome,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Luar biasa! Seluruh anggota kelas lunas kas bulan ini.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                    }
                }
            }
        }

        // Action & Search Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.searchStudentQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Cari nama / no absen...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SlateTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.searchStudentQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_student_input")
                )

                Button(
                    onClick = onAddStudentClick,
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_student_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Siswa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Matrix Header description
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Matrix Pembayaran Kas (${state.filteredStudents.size} Siswa)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextDark
                )
                Text(
                    text = "Klik M1-M4 untuk toggle lunas",
                    fontSize = 11.sp,
                    color = SlateTextMuted
                )
            }
        }

        // Student Matrix Cards
        items(state.filteredStudents, key = { it.id }) { student ->
            StudentMatrixCard(
                student = student,
                onToggleWeek = { weekIdx -> onToggleWeek(student.id, weekIdx) },
                onDelete = { deleteCandidateId = student.id }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Delete Student Confirmation Dialog
    if (deleteCandidateId != null) {
        AlertDialog(
            onDismissRequest = { deleteCandidateId = null },
            title = { Text("Hapus Anggota Kelas?", fontWeight = FontWeight.Bold) },
            text = { Text("Data siswa ini akan dihapus dari daftar anggota kas.") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteCandidateId?.let { onDeleteStudent(it) }
                        deleteCandidateId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoseExpense)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteCandidateId = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun StudentMatrixCard(
    student: StudentEntity,
    onToggleWeek: (Int) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                // Absen Badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = student.absen.toString().padStart(2, '0'),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = SlateTextMuted
                    )
                }

                Column {
                    Text(
                        text = student.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextDark
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        color = if (student.gender == "L") Color(0xFFEFF6FF) else Color(0xFFFDF2F8),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (student.gender == "L") "Laki-laki" else "Perempuan",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (student.gender == "L") Color(0xFF2563EB) else Color(0xFFDB2777),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // M1 to M4 Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val weeks = listOf(student.w1, student.w2, student.w3, student.w4)
                weeks.forEachIndexed { index, isPaid ->
                    Surface(
                        color = if (isPaid) EmeraldIncome else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(8.dp),
                        border = if (!isPaid) BorderStroke(1.dp, SlateBorder) else null,
                        modifier = Modifier
                            .size(width = 34.dp, height = 34.dp)
                            .clickable { onToggleWeek(index) }
                            .testTag("toggle_week_${index + 1}_${student.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "M${index + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isPaid) Color.White else SlateTextMuted
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp).testTag("delete_student_btn_${student.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus Siswa",
                        tint = SlateTextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
