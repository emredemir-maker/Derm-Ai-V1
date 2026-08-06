package com.example

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.database.AppDatabase
import com.example.data.database.MIGRATION_5_6
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class Migration5To6Test {
    private val TEST_DB = "migration-test-5-6"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate5To6() {
        val db = helper.createDatabase(TEST_DB, 5).apply {
            execSQL("INSERT INTO skin_profile (id, skinType, skinConcerns, skincareGoal, makeupPreference, allergies, lastAnalysisRoutine, lastAnalysisMakeup, lastAnalysisDate) VALUES (1, 'Karma', 'Akne', 'Nemlendirme', 'Doğal', '', null, null, 0)")
            close()
        }

        val migratedDb = helper.runMigrationsAndValidate(TEST_DB, 6, true, MIGRATION_5_6)

        val cursor = migratedDb.query("SELECT * FROM skin_profile WHERE id = 1")
        assertTrue(cursor.moveToFirst())

        val skinTypeIndex = cursor.getColumnIndex("skinType")
        val skinConcernsIndex = cursor.getColumnIndex("skinConcerns")
        val userNameIndex = cursor.getColumnIndex("userName")

        assertEquals("Karma", cursor.getString(skinTypeIndex))
        assertEquals("Akne", cursor.getString(skinConcernsIndex))
        assertEquals("", cursor.getString(userNameIndex))

        cursor.close()
    }
}
