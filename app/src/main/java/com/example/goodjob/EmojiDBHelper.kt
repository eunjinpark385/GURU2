package com.example.goodjob

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class EmojiDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Emoji.db"
        private const val TABLE_NAME = "Emoji"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_EMOJI_NAME = "emoji_name"
        private const val COLUMN_YEAR = "year"
        private const val COLUMN_MONTH = "month"
        private const val COLUMN_DATE = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val sqlCreateTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USER_ID TEXT NOT NULL," +
                "$COLUMN_EMOJI_NAME TEXT NOT NULL," +
                "$COLUMN_YEAR TEXT NOT NULL," +
                "$COLUMN_MONTH TEXT NOT NULL," +
                "$COLUMN_DATE TEXT NOT NULL);")
        db?.execSQL(sqlCreateTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        val sqlDropTable = ("DROP TABLE IF EXISTS $TABLE_NAME")
        db!!.execSQL(sqlDropTable)
    }

    // Emoji Table 에 데이터 삽입
    fun insertData(userID: String, emojiName: String, year: String, month: String, date: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID, userID)
            put(COLUMN_EMOJI_NAME, emojiName)
            put(COLUMN_YEAR, year)
            put(COLUMN_MONTH, month)
            put(COLUMN_DATE, date)
        }
        val result = db?.insert(TABLE_NAME, null, values)
        db.close()
        // insert() : 오류 발생 시 -1L 리턴
        return result != -1L
    }

    // 이모지 업데이트
    fun updateData(userID: String, emojiName: String, date: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_EMOJI_NAME, emojiName)
        val result = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_USER_ID = ? AND $COLUMN_DATE = ?",
            arrayOf(userID, date)
        )
        db.close()
        return result != -1
    }

    // Emoji Name 반환 함수
    fun getEmojiName(userID: String, date: String): String {
        val db = this.readableDatabase
        val emojiName: String
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_EMOJI_NAME),
            "$COLUMN_USER_ID = ? AND $COLUMN_DATE = ?",
            arrayOf(userID, date),
            null,
            null,
            null
        )
        emojiName = if (cursor.count != 0) {
            cursor.moveToFirst()
            cursor.getString(0)
        } else {
            "NULL"
        }
        cursor.close()
        db.close()
        return emojiName
    }

    // 각 Emoji 가 사용된 개수 반환
    fun getEmojiCount(userID: String, year: String, month: String): Array<Emoji> {
        val array =
            arrayOf(
                Emoji("mood_angry", 0),
                Emoji("mood_basic", 0),
                Emoji("mood_best", 0),
                Emoji("mood_dejected", 0),
                Emoji("mood_depression", 0),
                Emoji("mood_happy", 0),
                Emoji("mood_proud", 0),
                Emoji("mood_sad", 0),
                Emoji("mood_sick", 0)
            )
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_EMOJI_NAME),
            "$COLUMN_USER_ID = ? AND $COLUMN_YEAR = ? AND $COLUMN_MONTH = ?",
            arrayOf(userID, year, month),
            null,
            null,
            null
        )
        if (cursor.count == 0)
            return array
        else {
            while (cursor.moveToNext()) {
                val moodName = cursor.getString(0)
                val index = getIndex(moodName)
                if (index == -1) {
                    return array
                } else {
                    (array[index].emojiCount)++
                }
            }
            array.sortByDescending { it.emojiCount }
        }
        cursor.close()
        db.close()
        return array
    }

    // Emoji Name 에 해당 하는 인덱스 반환
    private fun getIndex(emojiName: String): Int {
        var index = -1
        when (emojiName) {
            "mood_angry" -> index = 0
            "mood_basic" -> index = 1
            "mood_best" -> index = 2
            "mood_dejected" -> index = 3
            "mood_depression" -> index = 4
            "mood_happy" -> index = 5
            "mood_proud" -> index = 6
            "mood_sad" -> index = 7
            "mood_sick" -> index = 8
            "NULL" -> index = -1
        }
        return index
    }

    data class Emoji(val emojiName: String, var emojiCount: Int)
}