package com.example.sanic.map

import androidx.activity.viewModels
import com.example.sanic.KeyValueStorage
import com.example.sanic.R
import com.example.sanic.ScoreViewModel

class ScoreManager(private val gameActivity: GameActivity) {
    private val scoreViewModel: ScoreViewModel by gameActivity.viewModels()

    init {
        scoreViewModel.getHighscore().observe(gameActivity, {
            KeyValueStorage.setValue(gameActivity, R.string.highscorekey, "$it")
        })
    }

    fun updateScore(){
        scoreViewModel.updateCurrentScore()
    }
}