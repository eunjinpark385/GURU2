package com.example.goodjob

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DiaryDBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "DiaryDatabase.db"
        private const val TABLE_NAME = "diary"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_WEATHER = "weather"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT1 = "content1"
        private const val COLUMN_CONTENT2 = "content2"
    }

    //데이터 베이스 생성
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE =
            ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USER_ID TEXT, $COLUMN_DATE TEXT, $COLUMN_WEATHER TEXT, $COLUMN_TITLE TEXT, $COLUMN_CONTENT1 TEXT, $COLUMN_CONTENT2 TEXT);")
        db.execSQL(CREATE_TABLE)
    }

    //데이터베이스 업그레이드
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    //일기 저장
    fun saveDiary(
        userID: String?,
        date: String?,
        weather: String,
        title: String,
        content1: String,
        content2: String
    ): Boolean {
        val values = ContentValues()
        values.put(COLUMN_USER_ID, userID)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_WEATHER, weather)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_CONTENT1, content1)
        values.put(COLUMN_CONTENT2, content2)

        //쓸 수 있는 데이터베이스 가져옴
        val db = this.writableDatabase
        // 일기 정보를 테이블에 삽입하고, 삽입된 행의 ID를 반환
        val id = db.insert(TABLE_NAME, null, values)
        db.close()

        //id가 -1이 아니라면 true 반환 -> insert메서드 성공여부
        return id != -1L
    }

    // 일기 내용 업데이트 함수
    fun updateDiary(
        userID: String,
        date: String,
        weather: String,
        title: String,
        content1: String,
        content2: String
    ): Boolean {
        val values = ContentValues()
        values.put(COLUMN_USER_ID, userID)
        values.put(COLUMN_DATE, date)
        values.put(COLUMN_WEATHER, weather)
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_CONTENT1, content1)
        values.put(COLUMN_CONTENT2, content2)

        //쓸 수 있는 데이터베이스 가져옴
        val db = this.writableDatabase
        // 일기 정보를 테이블에 삽입하고, 삽입된 행의 ID를 반환
        val row = db.update(
            TABLE_NAME,
            values,
            "$COLUMN_USER_ID = ? AND $COLUMN_DATE = ?",
            arrayOf(userID, date)
        )
        db.close()
        return row != -1
    }

    // 사용자 별 총 일기 개수 반환
    fun getDiaryCount(userID: String): Int {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_USER_ID),
            "$COLUMN_USER_ID = ?",
            arrayOf(userID),
            null,
            null,
            null
        )
        val cursorCount = cursor.count
        db.close()
        cursor.close()
        return cursorCount
    }

    // 작성된 일기 반환 함수
    fun getDate(userID: String, date: String): Array<String> {
        val contentArray = arrayOf("NULL", "NULL", "NULL", "NULL")
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_WEATHER, COLUMN_TITLE, COLUMN_CONTENT1, COLUMN_CONTENT2),
            "$COLUMN_USER_ID = ? AND $COLUMN_DATE = ?",
            arrayOf(userID, date),
            null,
            null,
            null
        )
        if (cursor.count != 0) {
            while (cursor.moveToNext()) {
                contentArray[0] = cursor.getString(0) // 저장된 날씨 불러오기
                contentArray[1] = cursor.getString(1) // 저장된 제목 불러오기
                contentArray[2] = cursor.getString(2) // 저장된 내용 불러오기
                contentArray[3] = cursor.getString(3) // 저장된 내용 불러오기
            }
        } else {
            return contentArray
        }
        db.close()
        cursor.close()
        return contentArray
    }
}
