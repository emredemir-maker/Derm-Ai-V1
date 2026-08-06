package com.example

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.data.database.MIGRATION_5_6
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration5To6Test {

    @Test
    fun migrate5To6_preservesExistingDataAndSetsDefaultUserName() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val dbName = "test_migration_5_6.db"
        context.deleteDatabase(dbName)

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(object : SupportSQLiteOpenHelper.Callback(5) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `skin_profile` (
                            `id` INTEGER NOT NULL PRIMARY KEY,
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

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
            })
            .build()

        val factory = FrameworkSQLiteOpenHelperFactory()
        val helper = factory.create(config)
        val db5 = helper.writableDatabase

        db5.execSQL(
            """
            INSERT INTO skin_profile (id, skinType, skinConcerns, skincareGoal, makeupPreference, allergies, lastAnalysisRoutine, lastAnalysisMakeup, lastAnalysisDate)
            VALUES (1, 'Karma', 'Akne', 'Sivilce Kontrolü', 'Doğal', '', 'Sabah Yıkama', 'Hafif Krem', 1700000000000)
            """.trimIndent()
        )

        // Apply MIGRATION_5_6
        MIGRATION_5_6.migrate(db5)

        val cursor = db5.query("SELECT * FROM skin_profile WHERE id = 1")
        assertNotNull(cursor)
        assertEquals(true, cursor.moveToFirst())

        val userNameIndex = cursor.getColumnIndex("userName")
        val skinTypeIndex = cursor.getColumnIndex("skinType")
        val skinConcernsIndex = cursor.getColumnIndex("skinConcerns")

        assertEquals("", cursor.getString(userNameIndex))
        assertEquals("Karma", cursor.getString(skinTypeIndex))
        assertEquals("Akne", cursor.getString(skinConcernsIndex))
        cursor.close()
        db5.close()
        context.deleteDatabase(dbName)
    }
}
