package com.example

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.database.AppDatabase
import com.example.data.database.MIGRATION_7_8
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration7To8Test {
    private val testDb = "migration-test-7-8"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate7To8PreservesProfileAndAddsFaceAnalysisFields() {
        helper.createDatabase(testDb, 7).apply {
            execSQL("INSERT INTO skin_profile (id, userName, age, gender, skinType, skinConcerns, skincareGoal, makeupPreference, allergies, lastAnalysisRoutine, lastAnalysisMakeup, lastAnalysisDate) VALUES (1, 'Test', 30, 'Erkek', 'Yağlı', 'Akne', 'Sivilce Kontrolü', '', '', null, null, 0)")
            close()
        }

        val migratedDb = helper.runMigrationsAndValidate(testDb, 8, true, MIGRATION_7_8)
        val cursor = migratedDb.query("SELECT * FROM skin_profile WHERE id = 1")
        assertTrue(cursor.moveToFirst())
        assertEquals("Test", cursor.getString(cursor.getColumnIndex("userName")))
        assertNull(cursor.getString(cursor.getColumnIndex("lastFaceAnalysisJson")))
        assertNull(cursor.getString(cursor.getColumnIndex("lastFacePhotoPath")))
        assertEquals(0L, cursor.getLong(cursor.getColumnIndex("lastFaceAnalysisDate")))
        cursor.close()
    }
}
