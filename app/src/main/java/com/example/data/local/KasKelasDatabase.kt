package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ForumNoteEntity
import com.example.data.model.SettingEntity
import com.example.data.model.StudentEntity
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TransactionEntity::class,
        StudentEntity::class,
        ForumNoteEntity::class,
        SettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class KasKelasDatabase : RoomDatabase() {

    abstract fun kasKelasDao(): KasKelasDao

    companion object {
        @Volatile
        private var INSTANCE: KasKelasDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): KasKelasDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KasKelasDatabase::class.java,
                    "kaskelas_pro.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.kasKelasDao())
                }
            }
        }

        suspend fun populateInitialData(dao: KasKelasDao) {
            dao.setSetting(SettingEntity("initial_balance", "0"))
            dao.setSetting(SettingEntity("weekly_rate", "5000"))

            val initialStudents = listOf(
                StudentEntity(name = "Ahmad Fauzi", absen = 1, gender = "L", w1 = true, w2 = true, w3 = false, w4 = false),
                StudentEntity(name = "Budi Santoso", absen = 2, gender = "L", w1 = true, w2 = false, w3 = false, w4 = false),
                StudentEntity(name = "Citra Dewi", absen = 3, gender = "P", w1 = true, w2 = true, w3 = true, w4 = false),
                StudentEntity(name = "Dian Sastro", absen = 4, gender = "P", w1 = true, w2 = true, w3 = true, w4 = true)
            )
            dao.insertAllStudents(initialStudents)

            val initialTransactions = listOf(
                TransactionEntity(
                    date = "2026-11-01",
                    desc = "Kas Mingguan M1 - Ahmad Fauzi",
                    category = "Kas Mingguan",
                    type = "in",
                    amount = 5000,
                    recordedBy = "Bendahara 1"
                ),
                TransactionEntity(
                    date = "2026-11-03",
                    desc = "Beli Spidol & Penghapus Board",
                    category = "Alat Tulis & Kelas",
                    type = "out",
                    amount = 35000,
                    recordedBy = "Bendahara 1"
                )
            )
            dao.insertAllTransactions(initialTransactions)

            val initialNotes = listOf(
                ForumNoteEntity(
                    date = "01 Nov 2026",
                    content = "Pembayaran kas kelas disepakati Rp 5.000 per minggu per siswa."
                )
            )
            dao.insertAllForumNotes(initialNotes)
        }
    }
}
