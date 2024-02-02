package com.example.goodjob

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.goodjob.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    // ViewBinding 사용
    private lateinit var binding: ActivitySignupBinding
    private lateinit var userName: EditText
    private lateinit var userID: EditText
    private lateinit var userPassword: EditText
    private lateinit var signUpBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var signUpDBHelper: UsersDBHelper
    private lateinit var spf: SharedPreferences
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 변수 연결
        userName = binding.activitySignupEtUserName
        userID = binding.activitySignupEtUserID
        userPassword = binding.activitySignupEtUserPassword
        signUpBtn = binding.activitySignupBtnSignUp
        loginBtn = binding.activitySignupBtnLogin
        spf = getSharedPreferences("user_info", MODE_PRIVATE)

        // 회원 가입 버튼 리스너 : Users.db에 새로운 유저 레코드 생성
        signUpDBHelper = UsersDBHelper(this)
        signUpBtn.setOnClickListener {
            val name = userName.text.toString()
            val id = userID.text.toString()
            val password = userPassword.text.toString()
            // isBlank() : 문자열 길이가 0 이거나, 공백(white space)으로만 이루어진 경우 true 리턴
            val isNameBlank = name.isBlank()
            val isIDBlank = id.isBlank()
            val isPasswordBlank = password.isBlank()

            // 값이 입력 되어 있는지 검사
            if (isNameBlank)
                makeToast("사용자 이름을 입력하세요.")
            else if (isIDBlank)
                makeToast("아이디를 입력하세요.")
            else if (isPasswordBlank)
                makeToast("비밀번호를 입력하세요.")
            // ID 유효성 검사
            else if (signUpDBHelper.checkUserID(id))
                makeToast("이미 존재하는 ID입니다.")
            // 회원 가입
            else if (signUpDBHelper.insertData(name, id, password)) {
                userName.text.clear()
                userID.text.clear()
                userPassword.text.clear()
                val intent = Intent(this, CalendarActivity::class.java)
                val editor = spf.edit()
                editor.putString("userName", name).apply()
                editor.putString("userID", id).apply()
                makeToast("회원가입 성공")
                startActivity(intent)
            } else
                makeToast("오류 발생")
        }

        // 로그인 버튼 리스너 : 로그인 버튼 클릭 시, Login Activity 로 되돌아 감
        loginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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

    // 화면 터치 시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}