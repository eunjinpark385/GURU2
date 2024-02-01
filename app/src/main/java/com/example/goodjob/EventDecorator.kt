package com.example.goodjob

import android.graphics.Color
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan

class EventDecorator(dates: Collection<CalendarDay>) : DayViewDecorator {

    private val dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    //점 추가하는 부분 (DotSpan(반지름, 색))
    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(5f, Color.MAGENTA))
    }
}