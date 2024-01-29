package com.example.goodjob
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodjob.DiaryDBHelper
import com.example.goodjob.R

class DiaryActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var editTextDate: EditText
    private lateinit var weatherEditText: EditText
    private lateinit var editTitle: EditText
    private lateinit var editText: EditText
    private lateinit var editText2: EditText
    private lateinit var saveButton: Button

    private lateinit var dbHelper: DiaryDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        imageView = findViewById(R.id.activity_diary_image)
        editTextDate = findViewById(R.id.activity_diary_edtDate)
        weatherEditText = findViewById(R.id.activity_diary_edtWeather)
        editTitle = findViewById(R.id.activity_diary_edtTitle)
        editText = findViewById(R.id.activity_diary_edtText)
        editText2 = findViewById(R.id.activity_diary_edtText2)
        saveButton = findViewById(R.id.activity_diary_btnSave)

        //dbHelper 초기화
        dbHelper = DiaryDBHelper(this)


        saveButton.setOnClickListener {
            //입력된 내용 가져옴
            val date = editTextDate.text.toString()
            val weather = weatherEditText.text.toString()
            val title = editTitle.text.toString()
            val content1 = editText.text.toString()
            val content2 = editText2.text.toString()

            // 데이터베이스에 저장
            val success = dbHelper.saveDiary(date, weather, title, content1, content2)

            // 데이터베이스에 저장 여부 메세지 표시
            if (success) {
                Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
