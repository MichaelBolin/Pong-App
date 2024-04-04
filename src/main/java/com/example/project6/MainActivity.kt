// Michael Bolin
package com.example.project6

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.SoundPool
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer


class MainActivity : AppCompatActivity() {
    private lateinit var pongView : GameView
    private lateinit var pong : Pong
    private lateinit var detector : GestureDetector
    private lateinit var gameTimerTask : GameTimerTask
    private lateinit var soundPool : SoundPool
    private lateinit var canvas : Canvas
    private lateinit var paint : Paint
    private var gameStatus = 0
    private var screenMidX = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // game sounds
        var poolBuilder : SoundPool.Builder = SoundPool.Builder()
        soundPool = poolBuilder.setMaxStreams(5).build()

        var mediaPlayer = MediaPlayer.create(this, R.raw.pong_background_music)
        mediaPlayer.setOnCompletionListener(OnCompletionListener { p1 -> p1.release() })
        mediaPlayer.start()
        mediaPlayer.isLooping = true

        var width : Int = Resources.getSystem().displayMetrics.widthPixels
        var height : Int = Resources.getSystem().displayMetrics.heightPixels

        pongView = GameView( this, this, width, height, soundPool )
        pong = pongView.getGame()
        setContentView( pongView )

        // Set up Event Handling
        var handler : TouchHandler = TouchHandler( )
        detector = GestureDetector( this, handler )
        detector.setOnDoubleTapListener( handler )


        // set schedule
        var gameTimer : Timer = Timer( )
        gameTimerTask = GameTimerTask( this )
        gameTimer.schedule( gameTimerTask, GameView.DELTA_TIME.toLong(), GameView.DELTA_TIME.toLong() )


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if( event != null )
            detector.onTouchEvent( event )
        return super.onTouchEvent(event)
    }

    fun updateView( ) {
        pongView.invalidate()
    }

    fun updateModel() {
        pong.moveBall()
    }

    fun getGameStatus() : Int {
        return gameStatus
    }

    fun setGameStatus(status: Int) {
        gameStatus = status
    }

    inner class TouchHandler : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (gameStatus == 0) {
                gameStatus = 1
                gameTimerTask.startGame()
            } else if (gameStatus == 2) {
                pongView.setGameReset(true)
                pong.setFailed(false)
                pong.reset()
                gameStatus = 1
            }
            return super.onSingleTapConfirmed(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            pong.updatePaddle(e2, pong) // Moves Paddle
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
}