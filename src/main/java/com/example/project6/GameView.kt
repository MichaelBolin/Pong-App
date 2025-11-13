// Michael Bolin
package com.example.project6

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.media.MediaPlayer
import android.media.SoundPool
import android.view.View

class GameView : View {
    private lateinit var canvas: Canvas
    private lateinit var paint : Paint
    private lateinit var pong : Pong
    private lateinit var activity : MainActivity
    private var width : Int = 0
    private var height : Int = 0
    private lateinit var paddleRect : Rect
    private var screenCleared = false
    private var gameReset = false

    constructor(context : Context, mainActivity : MainActivity, width : Int, height : Int, soundPool: SoundPool) : super( context ) {
        activity = mainActivity
        this.width = width
        this.height = height

        paint = Paint( )
        paint.style = Paint.Style.FILL;
        paint.color = Color.BLACK;
        paint.textSize = 100F;
        paint.textAlign = Paint.Align.CENTER

        pong = Pong(Rect( 600, 1850, 450, 1900 ), context, soundPool)
        pong.setPongRect(Rect(0, 0, width, height))
        pong.setBallSpeed( width * .0003f )
        pong.setDeltaTime( DELTA_TIME )

    }

    override fun onDraw(canvas: Canvas) {
        this.canvas = canvas
        super.onDraw(canvas)
        var currentPaddle = pong.getPaddleRect()
        var xVal = (canvas.width / 2).toFloat()

        if (activity.getGameStatus() == 0) {
            canvas.drawText("CLICK TO START GAME", xVal, 400F, paint);
        } else if (!screenCleared){
            canvas.drawColor(Color.WHITE)
            screenCleared = true
        } else if (gameReset) {
            canvas.drawColor(Color.WHITE)
            gameReset = false
        }

        if (currentPaddle != null) {
            var ballCoordinates = pong.getBallCenter()

            if (ballCoordinates != null) {
                canvas.drawCircle(
                ballCoordinates.x.toFloat(),
                ballCoordinates.y.toFloat(), pong.getBallRadius().toFloat(), paint
                )
            }
            paddleRect = Rect( currentPaddle.left, currentPaddle.top, currentPaddle.right, currentPaddle.bottom )
            canvas.drawRect(paddleRect, paint)
        }
        if (pong.getFailed()) {
            var currScore = pong.getCurrentScore()
            var highScore = pong.getHighScore()
            var gameFailedStatus : String = pong.getGameFailedStatus()

            canvas.drawText("$gameFailedStatus", xVal, 400F, paint);
            canvas.drawText("TOTAL HITS: $currScore", xVal, 500F, paint)
            canvas.drawText("HIGH SCORE: $highScore", xVal, 600F, paint)
            canvas.drawText("CLICK TO RESTART", xVal, 700F, paint);
            activity.setGameStatus(2)
        }
    }

    fun setGameReset(boolean: Boolean) {
        gameReset = boolean
    }

    fun getGame( ) : Pong {
        return pong
    }

    fun getCanvas() : Canvas {
        return canvas
    }

    fun getPaint() : Paint {
        return paint
    }

    companion object {
        const val DELTA_TIME : Int  = 300
    }
}