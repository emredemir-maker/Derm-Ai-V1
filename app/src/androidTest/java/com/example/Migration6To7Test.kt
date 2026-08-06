package com.example

import android.content.Context
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.database.AppDatabase
import com.example.data.database.MIGRATION_6_7
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration6To7Test {
    private val testDb = "migration-6-7-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate6To7PreservesProfileAndAddsDemographics() {
        helper.createDatabase(testDb, 6).apply {
            execSQL("INSERT INTO skin_profile (id, userName, skinType, skinConcerns, skincareGoal, makeupPreference, allergies, lastAnalysisRoutine, lastAnalysisMakeup, lastAnalysisDate) VALUES (1, 'Deniz', 'Yağlı', 'Akne', 'Sivilce Kontrolü', '', '', null, null, 0)")
            close()
        }

        helper.runMigrationsAndValidate(testDb, 7, true, MIGRATION_6_7).use { db ->
            db.query("SELECT userName, age, gender FROM skin_profile WHERE id = 1").use { cursor ->
                cursor.moveToFirst()
                assertEquals("Deniz", cursor.getString(0))
                assertEquals(0, cursor.getInt(1))
                assertEquals("", cursor.getString(2))
            }
        }
    }
}
