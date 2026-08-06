package com.example

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.database.MIGRATION_6_7
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration6To7Test {
    @Test
    fun migrate6To7PreservesProfileAndAddsDemographics() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val dbName = "test_migration_6_7.db"
        context.deleteDatabase(dbName)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(6) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `skin_profile` (
                            `id` INTEGER NOT NULL PRIMARY KEY,
                            `userName` TEXT NOT NULL,
                            `skinType` TEXT NOT NULL,
                            `skinConcerns` TEXT NOT NULL,
                            `skincareGoal` TEXT NOT NULL,
                            `makeupPreference` TEXT NOT NULL,
                            `allergies` TEXT NOT NULL,
                            `lastAnalysisRoutine` TEXT,
                            `lastAnalysisMakeup` TEXT,
                            `lastAnalysisDate` INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit
            })
            .build()

        val helper = FrameworkSQLiteOpenHelperFactory().create(config)
        val db6 = helper.writableDatabase
        db6.execSQL(
            "INSERT INTO skin_profile VALUES (1, 'Deniz', 'Yağlı', 'Akne', 'Sivilce Kontrolü', '', '', null, null, 0)"
        )

        MIGRATION_6_7.migrate(db6)

        db6.query("SELECT userName, age, gender FROM skin_profile WHERE id = 1").use { cursor ->
            cursor.moveToFirst()
            assertEquals("Deniz", cursor.getString(0))
            assertEquals(0, cursor.getInt(1))
            assertEquals("", cursor.getString(2))
        }
        db6.close()
        context.deleteDatabase(dbName)
    }
}
