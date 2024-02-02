package com.example.goodjob

import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.goodjob.databinding.ActivityCalendarBinding
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var usersDBHelper: UsersDBHelper
    private lateinit var sqLiteDatabase: SQLiteDatabase
    private lateinit var diaryDBHelper: DiaryDBHelper

    private lateinit var binding: ActivityCalendarBinding

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private lateinit var userName: String
    private lateinit var userID: String
    private lateinit var spf: SharedPreferences
    private var toast: Toast? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 변수 연결
        drawerLayout = binding.activityCalendarDL
        navigationView = binding.activityCalendarNV
        toolbar = binding.activityCalendarTb
        calendarView = binding.activityCalendarCalendarView
        calendarView.selectedDate = CalendarDay.today()
        spf = getSharedPreferences("user_info", MODE_PRIVATE)

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

        //받아온 ID를 변수로 저장
        userID = spf.getString("userID", "UNKNOWN")!!

        // 저장된 일기가 있는 경우
        diaryDBHelper = DiaryDBHelper(this)
        val diaryCount = diaryDBHelper.getDiaryCount(userID)
        if(diaryCount > 0) {
            // 현재 사용자가 작성한 모든 일기의 날짜들을 ArrayList로 반환합니다.
            // 날짜들의 형식은 "yyyy-MM-dd" 형태이며, String 타입입니다. 예시: 2024-02-02
            val allDate = diaryDBHelper.getAllDate(userID)
        }
        // 캘린더 날짜 선택 시, DiaryActivity 전환
        val intent = Intent(this, DiaryActivity::class.java)
        val dateFormat = SimpleDateFormat("yyyy년 M월 d일", Locale.getDefault())
        val dateForDiaryDBFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val yearForEmojiDBFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthForEmojiDBFormat = SimpleDateFormat("M", Locale.getDefault())
        // 날짜 선택 이벤트 메소드
        calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                intent.putExtra("date", dateFormat.format(date.date))
                intent.putExtra("dateForDiaryDB", dateForDiaryDBFormat.format(date.date))
                intent.putExtra("yearForEmojiDB", yearForEmojiDBFormat.format(date.date))
                intent.putExtra("monthForEmojiDB", monthForEmojiDBFormat.format(date.date))
                startActivity(intent)
            }
        }
    }

    // 네비게이션 드로어 관련 메소드
    // 네비게이션 드로어 메뉴 클릭 리스너
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navi_menu_item_calendar -> makeToast("현재 화면 입니다")

            R.id.navi_menu_item_emotion_statistics -> {
                val intent = Intent(this, EmotionStatistics::class.java)
                startActivity(intent)
            }

            R.id.navi_menu_item_badge -> {
                val intent = Intent(this, BadgeActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawers() // 기능을 수행하고 네비게이션을 닫아준다.
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