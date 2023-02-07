package com.example.insectinvasion

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity()
{
  private var handler: Handler? = null

  override fun onCreate(savedInstanceState: Bundle?)
  {
    super.onCreate(savedInstanceState)
    instance = this
    setContentView(R.layout.activity_main)

    // Initialize and set the event handlers
    handler = Handler()
    val angleSeekBar = findViewById<SeekBar>(R.id.angleSeekBar)
    val velocitySeekBar = findViewById<SeekBar>(R.id.velocitySeekBar)
    val fireButton = findViewById<Button>(R.id.fireButton)
    angleSeekBar.setOnSeekBarChangeListener(handler)
    velocitySeekBar.setOnSeekBarChangeListener(handler)
    fireButton.setOnClickListener(handler)
  }

  fun endGame()
  {
    val builder = AlertDialog.Builder(this)
    builder.setTitle("You did it!")
    builder.setMessage("The threat has been eliminated.\nWait! It appears this is not over!\nPress 'OK' to face your next challenge.")
    builder.setPositiveButton("Ok", handler)

    // Create the AlertDialog
    val alertDialog: AlertDialog = builder.create()
    alertDialog.setCancelable(false) // disables ability to cancel dialog using the back button
    alertDialog.show()
  }

  companion object
  {
    private var instance: MainActivity? = null
    fun getInstance() : MainActivity
    {
      return instance!!
    }
  }
}

