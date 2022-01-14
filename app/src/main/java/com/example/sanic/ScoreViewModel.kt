package com.example.sanic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ScoreViewModel(application: Application) : AndroidViewModel(application) {


    private val highscore: MutableLiveData<Int> = MutableLiveData(getHighScore(application))

    fun getHighscore(): LiveData<Int> {
        return highscore
    }


    private fun getHighScore(application: Application): Int {
        val highScore = KeyValueStorage.getValue(application, R.string.highscorekey)
        if (highScore == null || highScore.isEmpty())
            return 0
        return highScore.toInt()
        // Do an asynchronous operation to fetch users.
    }


    private val currentScore: MutableLiveData<Int> = MutableLiveData(0)

    fun getCurrentScore(): LiveData<Int> {
        return currentScore
    }

    private fun setCurrentScore(item: Int) {
        currentScore.value = item
    }


    fun updateCurrentScore() {
        var value = getCurrentScore().value ?: return
        value++
        setCurrentScore(value)
        if (value > highscore.value!!) {
            highscore.value = value
        }
    }

    private val timerMillis: MutableLiveData<Long> = MutableLiveData(Long.MIN_VALUE)

    fun getTimerMillis(): LiveData<Long>
    {
        return timerMillis
    }

    fun setTimerMillis(newMillis: Long)
    {
        timerMillis.value = newMillis
    }


    private val timeUp: MutableLiveData<Boolean> = MutableLiveData(false)

    fun setTimeUp(timeUp: Boolean) {
        this.timeUp.value = timeUp
    }

    fun getTimeUp() : LiveData<Boolean>
    {
        return timeUp
    }


}