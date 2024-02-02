package com.example.goodjob

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.goodjob.databinding.ActivityBadgeBinding
import com.google.android.material.navigation.NavigationView

class BadgeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var binding: ActivityBadgeBinding
    private lateinit var textView3days: TextView
    private lateinit var textView1week: TextView
    private lateinit var textView21days: TextView
    private lateinit var textView66days: TextView
    private lateinit var textView100days: TextView
    private lateinit var userName: String
    private lateinit var userID: String
    private lateinit var spf: SharedPreferences
    private var toast: Toast? = null
    private lateinit var diaryDBHelper: DiaryDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //변수 연결(배지 텍스트)
        textView3days = binding.activityBadge3daysText
        textView1week = binding.activityBadge1weekText
        textView21days = binding.activityBadge21daysText
        textView66days = binding.activityBadge66daysText
        textView100days = binding.activityBadge100daysText
        spf = getSharedPreferences("user_info", MODE_PRIVATE)

        // 네비게이션 드로어 위한 변수
        drawerLayout = binding.activityBadgeDL
        navigationView = binding.activityBadgeNV
        toolbar = binding.activityBadgeTb

        // 네비게이션 드로어 사용 설정
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        // 네비게이션 드로어 헤더에 받아온 사용자 이름 설정
        userName = spf.getString("userName", "UNKNOWN")!!
        val header = navigationView.getHeaderView(0)
        header.findViewById<TextView>(R.id.navi_header_tvUserName).text = userName

        // 네비게이션 드로어 메뉴 선택 리스너 등록
        navigationView.setNavigationItemSelectedListener(this)
        // 받아온 사용자 이름 출력
        Toast.makeText(this, userName + "님 환영합니다!", Toast.LENGTH_SHORT).show()

        //받아온 ID를 변수로 저장 - str_userID
        userID = spf.getString("userID", "UNKNOWN")!!

        //일기 작성 일수 불러오기
        diaryDBHelper = DiaryDBHelper(this)
        val diaryCount = diaryDBHelper.getDiaryCount(userID)

        //텍스트 색상 초기화
        textView3days.setTextColor(Color.LTGRAY)
        textView1week.setTextColor(Color.LTGRAY)
        textView21days.setTextColor(Color.LTGRAY)
        textView66days.setTextColor(Color.LTGRAY)
        textView100days.setTextColor(Color.LTGRAY)

        //badgeUnlock(days)
        badgeUnlock(diaryCount)
    }

    //배지 텍스트 색상 변경, 토스트 메시지 설정
    private fun badgeUnlock(days: Int) {

        if (days < 3) {
            makeToast("이제 시작이에요! '작심삼일' 달성까지 ${3 - days}일 남았어요!")
        }

        if (days >= 3) {
            makeToast("'칭찬 스타터' 달성까지 ${7 - days}일 남았어요.")
            textView3days.text = "3일을 넘긴 당신! 멋져요! 시작할 준비가 되었군요!"
            textView3days.setTextColor(Color.BLACK)
        }

        if (days >= 7) {
            makeToast("당신은 칭찬 스타터! '칭찬 주니어' 달성까지 ${21 - days}일 남았어요.")
            textView1week.text = "7일을 넘긴 당신! 일주일간 노력했군요!"
            textView1week.setTextColor(Color.BLACK)
        }

        if (days >= 21) {
            makeToast("당신은 칭찬 주니어! '칭찬 시니어' 달성까지 ${66 - days}일 남았어요.")
            textView21days.text = "21일간 열심히 한 멋진 당신! 행동이 습관화가 되었어요!"
            textView21days.setTextColor(Color.BLACK)
        }

        if (days >= 66) {
            makeToast("당신은 칭찬 시니어! '칭찬 마스터' 달성까지 ${100 - days}일 남았어요.")
            textView66days.text = "66일간 열심히 한 당신! 습관이 자리를 잡았네요."
            textView66days.setTextColor(Color.BLACK)
        }

        if (days >= 100) {
            makeToast("당신은 칭찬 마스터!!!")
            textView100days.text = "100일간 노력했네요! 당신은 100일 전보다 훨씬 멋있어졌을 거예요!"
            textView100days.setTextColor(Color.BLACK)
        }
    }

    // 네비게이션 드로어 관련 메소드
    // 네비게이션 드로어 메뉴 클릭 리스너
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navi_menu_item_calendar -> {
                val intent = Intent(this, CalendarActivity::class.java)
                startActivity(intent)
            }

            R.id.navi_menu_item_emotion_statistics -> {
                val intent = Intent(this, EmotionStatistics::class.java)
                startActivity(intent)
            }

            R.id.navi_menu_item_badge -> makeToast("현재 화면 입니다")
        }
        drawerLayout.closeDrawers() // 기능을 수행하고 네비게이션을 닫아 준다.
        return true
    }

    // 툴바의 메뉴 버튼 클릭 -> 네비게이션 드로어 열어줌
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)
        return super.onOptionsItemSelected(item)
    }

    // 뒤로 가기 버튼 클릭 처리
    override fun onBackPressed() { // 네비게이션 드로어가 열려 있으면 닫음
        drawerLayout.closeDrawers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
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
}