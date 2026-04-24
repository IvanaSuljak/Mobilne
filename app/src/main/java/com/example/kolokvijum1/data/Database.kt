package com.example.kolokvijum1.data

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

class Database(context: Context) {
    private val driver: SqlDriver = AndroidSqliteDriver(
        KolokvijumDatabase.Schema,
        context,
        "kolokvijum.db"
    )
    
    val database = KolokvijumDatabase(driver)
    
    fun close() {
        driver.close()
    }
}
