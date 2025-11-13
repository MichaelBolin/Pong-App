// Michael Bolin
package com.example.project6

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.media.SoundPool
import android.util.Log
import android.view.MotionEvent

class Pong {
    private var deltaTime = 0
    private var paddleRect: Rect? = null
    private var pongRect: Rect? = null
    private var paddleWidth : Int = 0
    private var paddleHeight : Int = 0
    private var paddleCenter : Point? = null
    private var ballAngle : Float = 45F
    private var ballCenter: Point? = Point(540, 30)
    private var ballRadius = 30
    private var ballSpeed = 0F
    private var ballPosition : String = "default"
    private var currentDirection : String = "SW"
    private var currentNumberOfHits : Int = 0
    private var highScore : Int = 0
    private lateinit var soundPool : SoundPool
    private var hitSoundId : Int = 0
    private lateinit var pref : SharedPreferences
    private var gameFailed : Boolean = false
    private lateinit var context: Context
    private var gameFailedStatus = "TRY AGAIN"
    private var distanceAdjustmentTop = 0
    private var distanceAdjustmentSide = 0
    private var ballEdgeTop = 0
    private var ballEdgeSide = 0


    constructor(paddleRect : Rect?, context : Context, soundPoolParam : SoundPool) {
        if (paddleRect != null) {
            this.context = context
            paddleWidth = paddleRect.left - paddleRect.right
            paddleHeight = paddleRect.bottom - paddleRect.top
            setPaddleRect(paddleRect.left, paddleRect.top, paddleRect.right, paddleRect.bottom)
            this.paddleRect = paddleRect
            soundPool = soundPoolParam
            hitSoundId = soundPool.load(context, R.raw.pong_hit_new,1)
            pref = context.getSharedPreferences( context.packageName + "_preferences", Context.MODE_PRIVATE )
        }
    }

//    paddleRect = Rect( 600, 1850, 450, 1900 )

    fun reset() {
        setPaddleRect(600, 1850, 450, 1900)
        ballCenter = Point(540, 30)
        currentDirection = "SW"
        currentNumberOfHits = 0
    }

    fun setPaddleRect(left : Int, top : Int, right : Int, bottom : Int) {
        paddleRect = Rect(left, top, right, bottom)
    }

    fun setPongRect(newPongRect : Rect?) {
        if (newPongRect != null) {
            pongRect = newPongRect
        }
    }

    fun setBallSpeed(newBallSpeed: Float) {
        if (newBallSpeed > 0)
            ballSpeed = newBallSpeed
    }

    fun setDeltaTime(newDeltaTime: Int) {
        if (newDeltaTime > 0) {
            deltaTime = newDeltaTime
            distanceAdjustmentTop = deltaTime / 100
            distanceAdjustmentSide = (deltaTime - 100) / 100
        }
    }

    fun getBallCenter() : Point? {
        return ballCenter
    }

    fun getBallRadius() : Int {
        return ballRadius
    }

    fun getPaddleRect() : Rect? {
        return paddleRect
    }

    fun moveBall() {
        if (gameFailed) {
            return
        }
        ballEdgeTop = distanceAdjustmentTop * ballRadius
        ballEdgeSide = distanceAdjustmentSide * ballRadius
        if (ballCenter!!.y + ballEdgeSide > pongRect!!.bottom - 3*ballRadius) { // hitting the failure point
            currentDirection = "Fail"
            failGame()
        }
        if (ballCenter!!.x - ballEdgeSide < pongRect!!.left) { // left wall hit
            if (currentDirection == "SW") {
                currentDirection = "SE"
            } else {
                currentDirection = "NE"
            }
            soundPool.play(hitSoundId, 1.0f, 1.0f, 1, 0,1.0f)
        } else if(ballCenter!!.x + ballEdgeSide > pongRect!!.right) { // right wall hit
            if (currentDirection == "SE") {
                currentDirection = "SW"
            } else {
                currentDirection = "NW"
            }
            soundPool.play(hitSoundId, 1.0f, 1.0f, 1, 0,1.0f)
        } else if (ballCenter!!.y - ballEdgeTop < pongRect!!.top) { // ceiling hit
            if (currentDirection == "NW") {
                currentDirection = "SW"
            } else {
                currentDirection = "SE"
            }
            soundPool.play(hitSoundId, 1.0f, 1.0f, 1, 0,1.0f)
        } else if (successfulHit() && ballCenter!!.x + ballEdgeSide <= paddleRect!!.left) { // left side of paddle hit
            currentDirection = "SW"
            currentNumberOfHits += 1
            soundPool.play(hitSoundId, 1.0f, 1.0f, 1, 0,1.0f)
        } else if (successfulHit() && ballCenter!!.x - ballEdgeSide >= paddleRect!!.right) {// right side of paddle hit
            currentDirection = "SE"
            currentNumberOfHits += 1
            soundPool.play(hitSoundId, 1.0f, 1.0f, 1, 0,1.0f)
        } else if (successfulHit()) { // top of paddle hit
            if (currentDirection == "SW") {
                currentDirection = "NW"
            } else {
                currentDirection = "NE"
            }
            currentNumberOfHits += 1
            soundPool.play(hitSoundId, 1.0f, 1.0f, 1, 0,1.0f)
        }

        // change position of the ball
        if (currentDirection == "SW") {
            ballCenter!!.x -= (ballSpeed * Math.cos(ballAngle.toDouble()) * deltaTime).toInt()
            ballCenter!!.y += (ballSpeed * Math.sin(ballAngle.toDouble()) * deltaTime).toInt()
        } else if (currentDirection == "SE") {
            ballCenter!!.x += (ballSpeed * Math.cos(ballAngle.toDouble()) * deltaTime).toInt()
            ballCenter!!.y += (ballSpeed * Math.sin(ballAngle.toDouble()) * deltaTime).toInt()
        } else if (currentDirection == "NE") {
            ballCenter!!.x += (ballSpeed * Math.cos(ballAngle.toDouble()) * deltaTime).toInt()
            ballCenter!!.y -= (ballSpeed * Math.sin(ballAngle.toDouble()) * deltaTime).toInt()
        } else if (currentDirection == "NW") {
            ballCenter!!.x -= (ballSpeed * Math.cos(ballAngle.toDouble()) * deltaTime).toInt()
            ballCenter!!.y -= (ballSpeed * Math.sin(ballAngle.toDouble()) * deltaTime).toInt()
        }
    }

    fun successfulHit(): Boolean {
        return if (paddleRect!!.intersects(ballCenter!!.x - ballRadius, ballCenter!!.y - ballRadius,ballCenter!!.x + ballRadius, ballCenter!!.y + ballRadius)) {
            soundPool.play(hitSoundId, 1.0f, 1.0f, 1, 0,1.0f)
            true
        } else {
            false
        }
    }

    fun failGame() {
        gameFailed = true
        highScore = pref.getInt(PREFERENCE_HIGH_SCORE, 0 )
        var failSound = soundPool.load(context, R.raw.pong_normal_end,1)
        if (currentNumberOfHits > highScore) {
            failSound = soundPool.load(context, R.raw.new_high_score_end,1)
            highScore = currentNumberOfHits
            gameFailedStatus = "NEW HIGH SCORE"
            var editor : SharedPreferences.Editor = pref.edit()
            editor.putInt(PREFERENCE_HIGH_SCORE, highScore)
            editor.commit()
        } else if (currentNumberOfHits == highScore) {
            gameFailedStatus = "TIED HIGH SCORE"
        } else {
            gameFailedStatus = "TRY AGAIN!"
        }
        soundPool.play(failSound, 1.0f, 1.0f, 2, 0,1.0f)
    }

    fun getGameFailedStatus() : String {
        return gameFailedStatus
    }

    fun getCurrentScore() : Int {
        return currentNumberOfHits
    }

    fun getHighScore() : Int {
        return highScore
    }

    fun getFailed() : Boolean {
        return gameFailed
    }

    fun setFailed(boolean: Boolean) {
        gameFailed = false
    }

    fun updatePaddle(event: MotionEvent, pong: Pong) {
        var currentPaddle = pong.getPaddleRect()

        if (currentPaddle != null) {
            var left : Int = event.x.toInt()
            var right: Int = event.x.toInt() + 150
            if (left <= pongRect!!.left) { // Paddle hits left wall
                left = pongRect!!.left
                right = (left + paddleWidth).toInt()
            }
            if (right >= pongRect!!.right) { // Paddle hits right wall
                right = pongRect!!.right
                left = (right - paddleWidth).toInt()
            }

            pong.setPaddleRect(left, currentPaddle.top, right, currentPaddle.bottom)
        }
    }


    companion object {
        private const val PREFERENCE_HIGH_SCORE : String = "highScore"
    }
}

