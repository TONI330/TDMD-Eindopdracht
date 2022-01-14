package com.example.sanic.map

import android.os.CountDownTimer
import androidx.activity.viewModels
import com.example.sanic.ScoreViewModel

class TimeManager(gameActivity: GameActivity)
{
    private val scoreViewModel: ScoreViewModel by gameActivity.viewModels()
    private var timer: GameTimer = GameTimer(scoreViewModel, Long.MIN_VALUE, Long.MAX_VALUE) {}

    fun setTimer(timerMillis: Long, task: () -> Unit) {
        timer.cancel()
        timer = GameTimer(scoreViewModel,timerMillis,1000L,task)
        timer.start()
    }

    fun setTimer(timerMillis: Long) {
        setTimer(timerMillis) { scoreViewModel.setTimeUp(true) }
    }

    fun cancelTimer() {
        timer.cancel()
    }

    class GameTimer(private var scoreViewModel: ScoreViewModel, millisInFuture: Long, countDownInterval: Long,private var task:  () -> Unit) :
        CountDownTimer(millisInFuture, countDownInterval) {

        override fun onTick(millisUntilFinished: Long) = scoreViewModel.setTimerMillis(millisUntilFinished)

        override fun onFinish() = task()
    }

}