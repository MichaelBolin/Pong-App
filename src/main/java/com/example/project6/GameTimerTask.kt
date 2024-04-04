// Michael Bolin
package com.example.project6

import java.util.TimerTask

class GameTimerTask : TimerTask {
    private lateinit var activity : MainActivity
    private var gameStarted : Boolean = false

    constructor( mainActivity : MainActivity ) {
        activity = mainActivity
    }

    fun startGame() {
        gameStarted = true
    }


    override fun run() {
        // update the view
        activity.updateView()
        if (gameStarted)
            activity.updateModel()
    }
}
