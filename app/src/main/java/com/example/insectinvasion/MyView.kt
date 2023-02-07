package com.example.insectinvasion

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class MyView : View
{
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private var cannonball: Drawable? = null
  private var cannonballCoords = Rect(0, 0, 0, 0)
  private var cannonBase: Drawable? = null
  private var viewWidth: Int? = null
  private var viewHeight: Int? = null
  private var bugs: ArrayList<ArrayList<Drawable>>? = null
  private val destroyedBug = Rect(-1, -1, -1, -1)
  private var barrelX1 = 1.0f
  private var barrelY1 = 1.0f
  private var barrelX2 = 1.0f
  private var barrelY2 = 1.0f
  private var barrelLength = 1.0f
  private var barrelWidth = 1.0f
  private val path = Path()
  private var ballX1 = -1
  private var ballY1 = -1
  private var ballX2 = -1
  private var ballY2 = -1
  private var ballRadius = 0
  private var isBallFired = false
  private var shotCount = 0

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  {
    instance = this
    killCount = 0
  }

  override fun onDraw(canvas: Canvas?)
  {
    super.onDraw(canvas)

    // Draw the barrel
    paint.setStyle(Paint.Style.STROKE)
    paint.setStrokeWidth(barrelWidth)
    paint.setColor(Color.DKGRAY)
    path.reset() // needed to erase previous drawn path
    path.moveTo(barrelX1, barrelY1)
    path.lineTo(barrelX2, barrelY2)
    path.close()
    canvas?.drawPath(path, paint)

    // Draw the cannon base
    cannonBase?.draw(canvas!!)

    // Draw the cannonball
    if (isBallFired)
    {
      //cannonball?.setBounds(ballX1, ballY1, ballX2, ballY2)
      cannonball?.draw(canvas!!)
    }

    // Logic to hit and remove bugs.
    // TODO: should check for isFired first
    for (row in bugs!!)
    {
      for (bug in row)
      {
        // If bug is destroyed, skip bug.
        if (bug.bounds == destroyedBug)
        {
          continue
        }
        // If top of ball is below bug or bottom of ball is above bug, skip the row.
        else if (cannonball?.bounds?.top!! > bug.bounds.bottom ||
          cannonball?.bounds?.bottom!! < bug.bounds.top)
        {
          break
        }
        // Hit case. Checking upper right corner of cannonball.
        else if ((cannonball?.bounds?.top!! <= bug.bounds.bottom &&
                  cannonball?.bounds?.top!! >= bug.bounds.top) &&
          (cannonball?.bounds?.right!! >= bug.bounds.left &&
                  cannonball?.bounds?.right!! <= bug.bounds.right))
        {
          bug.bounds = destroyedBug
          killCount++
          continue
        }
        // Hit case. Checking upper left corner of cannonball.
        else if ((cannonball?.bounds?.top!! <= bug.bounds.bottom &&
                  cannonball?.bounds?.top!! >= bug.bounds.top) &&
                  (cannonball?.bounds?.left!! >= bug.bounds.left &&
                  cannonball?.bounds?.left!! <= bug.bounds.right))
        {
          bug.bounds = destroyedBug
          killCount++
          continue
        }
        // Hit case. Checking lower right corner of cannonball.
        else if ((cannonball?.bounds?.bottom!! >= bug.bounds.top &&
                  cannonball?.bounds?.bottom!! <= bug.bounds.bottom) &&
          (cannonball?.bounds?.right!! >= bug.bounds.left &&
                  cannonball?.bounds?.right!! <= bug.bounds.right))
        {
          bug.bounds = destroyedBug
          killCount++
          continue
        }
        // Hit case. Checking lower left corner of cannonball.
        else if ((cannonball?.bounds?.bottom!! >= bug.bounds.top &&
                  cannonball?.bounds?.bottom!! <= bug.bounds.bottom) &&
          (cannonball?.bounds?.left!! >= bug.bounds.left &&
                  cannonball?.bounds?.left!! <= bug.bounds.right))
        {
          bug.bounds = destroyedBug
          killCount++
          continue
        }
      }
    }

    // Draw the bugs
    for (row in bugs!!)
    {
      for (bug in row)
        if (bug.bounds != destroyedBug)
        {
          bug.draw(canvas!!)
        }
    }
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
  {
    super.onSizeChanged(w, h, oldw, oldh)
    viewWidth = w
    viewHeight = h

    // Initialize cannon base. Calculate/set position.
    val baseX1 = 0
    val baseY1 = (0.83 * viewHeight!!).toInt()
    val baseY2 = (0.9 * viewHeight!!).toInt()
    val baseX2 = 2 * (baseY2 - baseY1)
    val imageViewBase = ImageView(MainActivity.getInstance())
    imageViewBase.setImageResource(R.drawable.cannon_base)
    cannonBase = imageViewBase.drawable
    cannonBase?.setBounds(baseX1, baseY1, baseX2, baseY2)

    // Initialize the cannon barrel at 0 degrees
    // The base of the barrel is set at half the width of the cannon base and 3/4 the height of the base
    barrelX1 = (baseX2 / 2.0).toFloat()
    barrelY1 = (baseY2 - 3 * (baseY2 - baseY1) / 4.0).toFloat()
    barrelLength = (1.1 * barrelX1).toFloat()
    barrelWidth = (0.4 * (baseY2 - baseY1)).toFloat()
    barrelX2 = barrelX1 + barrelLength // start at 0 degrees
    barrelY2 = barrelY1 // start at 0 degrees

    // Initialize the bugs
    initializeBugs()

    // Initialize the cannonball
    val ballImageView = ImageView(MainActivity.getInstance())
    ballImageView.setImageResource(R.drawable.cannon_ball)
    cannonball = ballImageView.drawable
  }

  private fun initializeBugs()
  {
    bugs = ArrayList<ArrayList<Drawable>>()
    val initialBugX = (0.5 * viewWidth!!).toInt()
    var initialBugY = (0.1 * viewHeight!!).toInt()
    val bugSideLength = (0.1 * viewHeight!!).toInt()
    for (row in 0..6)
    {
      val toAdd = ArrayList<Drawable>()
      for (col in 0..6)
      {
        val x1 = initialBugX + bugSideLength * col
        val y1 = initialBugY
        val x2 = x1 + bugSideLength
        val y2 = y1 + bugSideLength
        val imgView = ImageView(MainActivity.getInstance())
        imgView.setImageResource(R.drawable.untouched)
        val drawable = imgView.drawable
        drawable.setBounds(x1, y1, x2, y2)
        toAdd.add(drawable)
      }
      // Add the row(ArrayList<Drawable>) to bugs
      bugs?.add(toAdd)

      // Increment starting y coordinate for next row
      initialBugY += bugSideLength
    }
  }

  // Sets the endpoint of the barrel for the chosen launch angle
  fun setBarrel(angle: Int)
  {
    val radians = (angle * kotlin.math.PI / 180).toFloat()
    barrelX2 = barrelX1 + barrelLength * (kotlin.math.cos(radians))
    barrelY2 = barrelY1 - barrelLength * (kotlin.math.sin(radians))
  }

  // Sets the cannonballs diameter to the width of the barrel and sets the initial position
  // to be halfway out of the barrel
  fun setBallInitial()
  {
    ballRadius = (barrelWidth / 2).toInt()
    ballX1 = barrelX2.toInt() - ballRadius
    ballY1 = barrelY2.toInt() - ballRadius
    ballX2 = ballX1 + 2 * ballRadius
    ballY2 = ballY1 + 2 * ballRadius
    isBallFired = true
    cannonballCoords.set(ballX1, ballY1, ballX2, ballY2)
    cannonball?.setBounds(cannonballCoords)
  }

  fun getBallRadius(): Int
  {
    return ballRadius
  }

  fun getBallCoords(): Rect
  {
    return cannonballCoords
  }

  fun setBallCoords(coords: Rect)
  {
    cannonballCoords.set(coords)
  }

  fun getIsBallFired(): Boolean
  {
    return isBallFired
  }

  fun setIsBallFired(bool: Boolean)
  {
    isBallFired = bool
  }

  fun setCannonBallBounds(bounds: Rect)
  {
    cannonball?.setBounds(bounds)
  }

  fun setShotCount(count: Int)
  {
    shotCount = count
  }

  fun getShotCount(): Int
  {
    return shotCount
  }

  fun getViewWidth(): Int
  {
    return viewWidth!!
  }

  fun getViewHeight(): Int
  {
    return viewHeight!!
  }

  fun getKillCount(): Int
  {
    return killCount
  }

  fun resetGame()
  {
    initializeBugs()
    setShotCount(0)
    killCount = 0
    invalidate()
  }

  companion object
  {
    private var instance: MyView? = null
    var killCount = 0

    fun getInstance(): MyView
    {
      return instance!!
    }
  }

}