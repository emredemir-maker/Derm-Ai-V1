package com.example

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.database.AppDatabase
import com.example.data.database.MIGRATION_4_5
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class Migration4To5Test {
    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate4To5() {
        val db = helper.createDatabase(TEST_DB, 4).apply {
            execSQL("INSERT INTO inventory_items (id, name, brand, type, category, openedDate, shelfLifeMonths, compatibilityScore, notes) VALUES (1, 'Test Cream', 'Test Brand', 'Cilt Bakımı', 'Nemlendirici', 123456789, 12, 0, 'Test Note')")
            close()
        }

        val migratedDb = helper.runMigrationsAndValidate(TEST_DB, 5, true, MIGRATION_4_5)

        val cursor = migratedDb.query("SELECT * FROM inventory_items WHERE id = 1")
        assertTrue(cursor.moveToFirst())

        val nameIndex = cursor.getColumnIndex("name")
        val brandIndex = cursor.getColumnIndex("brand")
        val categoryIndex = cursor.getColumnIndex("category")
        val notesIndex = cursor.getColumnIndex("notes")
        val ingredientsIndex = cursor.getColumnIndex("ingredients")

        assertEquals("Test Cream", cursor.getString(nameIndex))
        assertEquals("Test Brand", cursor.getString(brandIndex))
        assertEquals("Nemlendirici", cursor.getString(categoryIndex))
        assertEquals("Test Note", cursor.getString(notesIndex))
        assertEquals("", cursor.getString(ingredientsIndex))

        cursor.close()
    }
}
