package com.example.sanic.map

import android.os.CountDownTimer
import androidx.activity.viewModels
import com.example.sanic.ScoreViewModel
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2
import kotlin.math.log

class TimeManager(private val gameActivity: GameActivity)
{
    private val scoreViewModel: ScoreViewModel by gameActivity.viewModels()
    private var timer: GameTimer = GameTimer(scoreViewModel, Long.MAX_VALUE, 1000L) {}


    fun setTimer(timerMillis: Long, task: () -> Unit) {
        timer.cancel()
        timer = GameTimer(scoreViewModel,timerMillis,1000L,task)
        timer.start()
    }

    fun setTimer(timerMillis: Long): Unit {
        setTimer(timerMillis) { scoreViewModel.setTimeUp(true) }
    }

    class GameTimer(private var scoreViewModel: ScoreViewModel, millisInFuture: Long, countDownInterval: Long,private var task:  () -> Unit) :
        CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) = scoreViewModel.setTimerMillis(millisUntilFinished)

        override fun onFinish() = task()
    }











}