package com.example.goodjob

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
        private const val COLUMN_DATE = "date"
        private const val COLUMN_WEATHER = "weather"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT1 = "content1"
        private const val COLUMN_CONTENT2 = "content2"
    }


    //데이터 베이스 생성
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE =
            ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_DATE TEXT, $COLUMN_WEATHER TEXT, $COLUMN_TITLE TEXT, $COLUMN_CONTENT1 TEXT, $COLUMN_CONTENT2 TEXT)")
        db.execSQL(CREATE_TABLE)
    }

    //데이터베이스 업그레이드
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }


    //일기 저장
    fun saveDiary(date: String, weather: String, title: String, content1: String, content2: String): Boolean {
        val values = ContentValues()
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
}
