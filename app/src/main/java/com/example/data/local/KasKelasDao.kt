package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ForumNoteEntity
import com.example.data.model.SettingEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KasKelasDao {

    // Transactions
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<TransactionEntity>)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    // Students
    @Query("SELECT * FROM students ORDER BY absen ASC")
    fun getAllStudentsFlow(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students ORDER BY absen ASC")
    suspend fun getAllStudents(): List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    @Query("DELETE FROM students")
    suspend fun clearStudents()

    // Forum Notes
    @Query("SELECT * FROM forum_notes ORDER BY id DESC")
    fun getAllForumNotesFlow(): Flow<List<ForumNoteEntity>>

    @Query("SELECT * FROM forum_notes ORDER BY id DESC")
    suspend fun getAllForumNotes(): List<ForumNoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForumNote(note: ForumNoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllForumNotes(notes: List<ForumNoteEntity>)

    @Query("DELETE FROM forum_notes WHERE id = :id")
    suspend fun deleteForumNoteById(id: Long)

    @Query("DELETE FROM forum_notes")
    suspend fun clearForumNotes()

    // Settings
    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    suspend fun getSettingValue(key: String): String?

    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    fun getSettingValueFlow(key: String): Flow<String?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: SettingEntity)

    @Query("DELETE FROM settings")
    suspend fun clearSettings()
}
