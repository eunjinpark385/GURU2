package com.example.goodjob

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.goodjob.databinding.ActivityDiaryBinding

class DiaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiaryBinding
    private lateinit var dbHelper: DiaryDBHelper
    private lateinit var emojiDBHelper: EmojiDBHelper
    private lateinit var spf: SharedPreferences
    private lateinit var imageView: ImageView
    private lateinit var textViewDate: TextView
    private lateinit var weatherEditText: EditText
    private lateinit var editTitle: EditText
    private lateinit var editText: EditText
    private lateinit var editText2: EditText
    private lateinit var saveButton: Button
    private var moodName: String = "NULL"
    private var alertDialog: AlertDialog? = null
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 변수 연결
        imageView = binding.activityDiaryImage
        textViewDate = binding.activityDiaryTvDate
        weatherEditText = binding.activityDiaryEdtWeather
        editTitle = binding.activityDiaryEdtTitle
        editText = binding.activityDiaryEdtText
        editText2 = binding.activityDiaryEdtText2
        saveButton = binding.activityDiaryBtnSave
        spf = getSharedPreferences("user_info", MODE_PRIVATE)

        // dbHelper 초기화
        dbHelper = DiaryDBHelper(this)
        emojiDBHelper = EmojiDBHelper(this)

        imageView.setOnClickListener {
            showExpressionDialog()
        }

        // userID 가져 오기
        val userID = spf.getString("userID", "UNKNOWN")

        // Calendar Activity 로부터 사용자 가 선택한 날짜 받아옴
        val date = intent.getStringExtra("date")
        val dateForDiaryDB = intent.getStringExtra("dateForDiaryDB")
        val yearForEmojiDB = intent.getStringExtra("yearForEmojiDB")
        val monthForEmojiDB = intent.getStringExtra("monthForEmojiDB")
        textViewDate.text = date

        // 선택된 날짜에 이미 작성된 일기가 있다면, 수정하도록 설정
        val contentArray = dbHelper.getDate(userID!!, dateForDiaryDB!!)
        var isExist = false
        if (contentArray[0] != "NULL") {
            isExist = true
            moodName = emojiDBHelper.getEmojiName(userID, dateForDiaryDB)
            val src = getDrawableID(moodName)
            // String 을 Editable 객체로 변환해 적용 하기 위해 setText 메소드 사용
            imageView.setImageResource(src)
            weatherEditText.setText(contentArray[0])
            editTitle.setText(contentArray[1])
            editText.setText(contentArray[2])
            editText2.setText(contentArray[3])
            makeToast("수정할 부분을 새로 작성해주세요")
        }

        // Diary 저장 버튼 클릭
        saveButton.setOnClickListener {
            // 입력된 내용 가져옴
            val weather = weatherEditText.text.toString()
            val title = editTitle.text.toString()
            val content1 = editText.text.toString()
            val content2 = editText2.text.toString()

            // 입력 처리
            if (weather.isBlank())
                makeToast("날씨를 입력해주세요.")
            else if (title.isBlank())
                makeToast("제목을 입력해주세요.")
            else if (content1.isBlank())
                makeToast("내용을 입력해주세요.")
            else if (content2.isBlank())
                makeToast("칭찬 내용을 입력해주세요.")
            else if (moodName == "NULL")
                makeToast("이모지를 선택해주세요.")
            else if (!isExist) {
                // 데이터베이스에 저장
                val success = dbHelper.saveDiary(
                    userID,
                    dateForDiaryDB,
                    weather,
                    title,
                    content1,
                    content2,
                )
                val isMoodSaveSuccess = emojiDBHelper.insertData(
                    userID!!,
                    moodName,
                    yearForEmojiDB!!,
                    monthForEmojiDB!!,
                    dateForDiaryDB
                )
                // 데이터베이스에 저장 여부 메세지 표시
                if (success && isMoodSaveSuccess) {
                    val intent = Intent(this, CalendarActivity::class.java)
                    makeToast("저장되었습니다.")
                    startActivity(intent)
                } else {
                    makeToast("저장 실패")
                }
            } else {
                val isUpdateSuccess = dbHelper.updateDiary(
                    userID,
                    dateForDiaryDB,
                    weather,
                    title,
                    content1,
                    content2
                )
                val isMoodUpdateSuccess = emojiDBHelper.updateData(
                    userID,
                    moodName,
                    dateForDiaryDB
                )
                // 업데이트 성공 여부 표시
                if (isUpdateSuccess && isMoodUpdateSuccess) {
                    val intent = Intent(this, CalendarActivity::class.java)
                    makeToast("내용이 업데이트 되었습니다.")
                    startActivity(intent)
                } else {
                    makeToast("내용 업데이트 실패")
                }
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

        // 마지막으로 선택된 이미지의 리소스 ID 저장
        moodName = view.resources.getResourceEntryName(view.id)
    }

    // Activity 종료 시, Toast message 취소
    override fun onStop() {
        super.onStop()
        toast?.cancel()
    }

    // Toast message 생성 함수
    private fun makeToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast?.show()
    }

    // Drawable ID 가져 오는 함수
    private fun getDrawableID(moodName: String): Int {
        var id: Int = -1
        when (moodName) {
            "mood_angry" -> id = R.drawable.emoji_angry
            "mood_basic" -> id = R.drawable.emoji_basic
            "mood_best" -> id = R.drawable.emoji_best
            "mood_dejected" -> id = R.drawable.emoji_dejected
            "mood_depression" -> id = R.drawable.emoji_depression
            "mood_happy" -> id = R.drawable.emoji_happy
            "mood_proud" -> id = R.drawable.emoji_proud
            "mood_sad" -> id = R.drawable.emoji_sad
            "mood_sick" -> id = R.drawable.emoji_sick
        }
        return id
    }
}
