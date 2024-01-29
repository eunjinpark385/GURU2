package com.example.goodjob

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent

class LoginActivity : AppCompatActivity() {

    private lateinit var edtID: EditText
    private lateinit var edtPW: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnReg: Button
    private lateinit var dbHelper: UsersDBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtID = findViewById(R.id.activity_login_edtID)
        edtPW = findViewById(R.id.activity_login_edtPW)
        btnLogin = findViewById(R.id.activity_login_btnLogin)
        btnReg = findViewById(R.id.activity_login_btnReg)

        //데이터베이스헬퍼 초기화
        dbHelper = UsersDBHelper(this)
        btnLogin.setOnClickListener {
            //사용자가 입력한 아이디 패스워드 가져옴
            val username = edtID.text.toString()
            val password = edtPW.text.toString()

            //데이터베이스에서 사용자 찾기
            val user = dbHelper.getUser(username, password)

            //사용자를 찾으면 성공  못찾으면 실패 메세지
            if (user != null) {
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }

        //회원 가입 버튼 클릭시 회원 가입 화면으로 이동함
        btnReg.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
        }
    }
}
