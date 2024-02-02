package com.example.goodjob

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.goodjob.databinding.ActivityEmotionStatisticsBinding
import com.google.android.material.navigation.NavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EmotionStatistics : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityEmotionStatisticsBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var userName: String
    private lateinit var userID: String
    private lateinit var monthTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var diaryDBHelper: DiaryDBHelper
    private lateinit var emojiDBHelper: EmojiDBHelper
    private lateinit var spf: SharedPreferences
    private var monthText: String = "NULL"
    private var toast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 변수 연결
        drawerLayout = binding.activityEmotionStatisticsDL
        navigationView = binding.activityEmotionStatisticsNV
        toolbar = binding.activityEmotionStatisticsTb
        monthTV = binding.activityEmotionStatisticsTvMonth
        recyclerView = binding.activityEmotionStatisticsRV
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

        // 현재 Month 로 설정
        val monthFormat = SimpleDateFormat("M월", Locale.getDefault())
        val yearForEmojiDBFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val monthForEmojiDBFormat = SimpleDateFormat("M", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        monthText = monthFormat.format(calendar.time)
        monthTV.text = monthText

        // 이모지 가져 오기
        val year = yearForEmojiDBFormat.format(calendar.time)
        val month = monthForEmojiDBFormat.format(calendar.time)
        userID = spf.getString("userID", "UNKNOWN")!!
        diaryDBHelper = DiaryDBHelper(this)
        emojiDBHelper = EmojiDBHelper(this)
        if (diaryDBHelper.getDiaryCount(userID) == 0)
            Toast.makeText(this, "작성된 일기가 없습니다.", Toast.LENGTH_SHORT).show()
        else {
            val ranking = emojiDBHelper.getEmojiCount(userID, year, month)
            val first = getDrawableID(ranking[0].emojiName)
            val second = getDrawableID(ranking[1].emojiName)
            val third = getDrawableID(ranking[2].emojiName)
            val rvAdapter = RecyclerViewAdapter(ranking)
            val rvLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = rvLayoutManager
            recyclerView.adapter = rvAdapter
            binding.activityEmotionStatisticsIvFirst.setImageResource(first)
            binding.activityEmotionStatisticsIvSecond.setImageResource(second)
            binding.activityEmotionStatisticsIvThird.setImageResource(third)
        }
    }

    // 네비게이션 드로어 메뉴 클릭 리스너
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navi_menu_item_calendar -> {
                val intent = Intent(this, CalendarActivity::class.java)
                startActivity(intent)
            }

            R.id.navi_menu_item_emotion_statistics -> Toast.makeText(
                this,
                "현재 화면 입니다",
                Toast.LENGTH_SHORT
            ).show()

            R.id.navi_menu_item_badge -> {
                val intent = Intent(this, BadgeActivity::class.java)
                startActivity(intent)
            }
        }
        drawerLayout.closeDrawers() // 기능을 수행하고 네비게이션을 닫아준다.
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
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

    // Activity 종료 시, Toast message 취소
    override fun onStop() {
        super.onStop()
        toast?.cancel()
    }
}

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