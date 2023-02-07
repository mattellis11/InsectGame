package com.example.insectinvasion

import android.content.DialogInterface
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class Handler : OnSeekBarChangeListener, OnClickListener, DialogInterface.OnClickListener
{
  private val angleSeekBar = MainActivity.getInstance().findViewById<SeekBar>(R.id.angleSeekBar)
  private val velocitySeekBar = MainActivity.getInstance().findViewById<SeekBar>(R.id.velocitySeekBar)
  private val angleTextView = MainActivity.getInstance().findViewById<TextView>(R.id.angleTextView)
  private val velocityTextView = MainActivity.getInstance().findViewById<TextView>(R.id.velocityTextView)
  private val shots = MainActivity.getInstance().findViewById<TextView>(R.id.shots)
  private var fireButton = MainActivity.getInstance().findViewById<Button>(R.id.fireButton)
  private var view = MainActivity.getInstance().findViewById<MyView>(R.id.myView)
  private val t0 = 0.025
  private var score = MainActivity.getInstance().findViewById<TextView>(R.id.score)

  companion object
  {
    private var timer: Timer? = null
    private var t = 0.0
  }

  // updates textviews for sliders
  override fun onProgressChanged(seekBar: SeekBar?, position: Int, p2: Boolean)
  {
    if (seekBar == angleSeekBar) {
      angleTextView.text = position.toString()
      view.setBarrel(position)
      view.invalidate()
    }
    else if (seekBar == velocitySeekBar)
    {
      velocityTextView.text = position.toString()
    }
  }

  override fun onStartTrackingTouch(p0: SeekBar?) {}

  override fun onStopTrackingTouch(p0: SeekBar?) {}

  override fun onClick(p0: View?)
  {
    when ((p0 as Button).text.toString())
    {
      "FIRE" ->
      {
        timer = Timer() // needed to create new timer each time, old timer wouldn't work after cancel had been called
        val timerTask = TimerObject() // create a thread
        view.setBallInitial()
        fireButton.isEnabled = false
        t = t0 // reset t for projectile motion equations
        timer?.schedule(timerTask, 0, 25) // starts animation
      }
    }
  }

  fun fire()
  {
    var angle = angleTextView.text.toString().toInt() * Math.PI / 180
    var velocity = velocityTextView.text.toString().toInt()
    var ballCoords = view.getBallCoords()
    var x0 = ballCoords.left
    var y0 = ballCoords.top

    // Projectile motion equations
    var x1 = ((velocity * cos(angle) * t) + x0).toInt()
    var y1 = ((-velocity * (sin(angle) * t) + 0.5 * 9.8 * t.pow(2)) + y0).toInt()

    // Set cannonball
    ballCoords.set(x1, y1, x1 + view.getBallRadius() * 2, y1 + view.getBallRadius() * 2)
    view.setCannonBallBounds(ballCoords)

    // Increment time
    t += 0.025

    // Refresh display
    view.invalidate()

    // Off screen condition
    if (ballCoords.left > view.getViewWidth() || ballCoords.top > view.getViewHeight())
    {
      timer?.cancel() // ends animation
      view.setIsBallFired(false) // when false, onDraw won't consider cannonball
      fireButton.isEnabled = true // enable fire button
      view.setShotCount(view.getShotCount() + 1) // increment shot counter
      shots.text = view.getShotCount().toString() // update on-screen shot total
      score.text = view.getKillCount().toString() // update on-screen score

      // Check for end game
      if (view.getKillCount() == 49) // all bugs eliminated
      {
        MainActivity.getInstance().endGame()
      }
    }
  }

  override fun onClick(p0: DialogInterface?, p1: Int)
  {
    // Only triggers at end game
    view.resetGame()
    shots.text = "0"
    score.text = "0"
  }
}

class TimerObject : TimerTask() // subclass of thread used for typing purposes
{
  override fun run()
  {
    val helper = HelperThread()
    MainActivity.getInstance().runOnUiThread(helper)
  }
}

class HelperThread : Runnable
{
  override fun run()
  {
    val handler = Handler()
    handler.fire()
  }
}