package com.example.goodjob
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private var alertDialog: AlertDialog? = null

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

        imageView.setOnClickListener {
            showExpressionDialog()
        }


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

    private fun showExpressionDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.mood_dialog, null)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()
    }

    // 기분 이미지 클릭 처리
    fun onExpressionClick(view: View) {
        val expressionImageView = view as ImageView
        // 선택된 기분 이미지를 가져옴
        val selectedExpression = expressionImageView.drawable

        // 선택된 기분사진 설정함
        imageView.setImageDrawable(selectedExpression)

        // 대화 상자를 닫습니다.
        alertDialog?.dismiss()
    }
}
