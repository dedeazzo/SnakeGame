package com.example.snakegame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import java.util.*

class MainActivity : AppCompatActivity() {

    // maintain a 2:5 ratio for screen dimensions where 2 is this screen width and 5 is dp in main_activity @+id/game_container for cellSize 50
    val screenWidth = 300
    val screenHeight = 200
    val cellSize = 50
    private lateinit var foodCounter: TextView
    private lateinit var highestScore: TextView
    private var foodCounterCount = 0
    private var highestScoreCount = 0

    private val snake = Snake(screenWidth, screenHeight, cellSize)
    private val random = Random()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var gameFrame: FrameLayout
    private lateinit var startButton: Button

    private var currentDirection = Direction.RIGHT
    private var foodX: Int = 0
    private var foodY: Int = 0

    private val lock = Any()
    private var gameRunnable: Runnable? = null
    private var isGameRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameFrame = findViewById(R.id.game_frame)
        startButton = findViewById(R.id.start_button)
        foodCounter = findViewById(R.id.food_counter)
        highestScore = findViewById(R.id.highest_score)

        startButton.setOnClickListener {
            if (!isGameRunning) {
                // use the following line to leave the previous game visible until next game
                gameFrame.removeAllViews()
                startGame()
            }
        }

        // Register key event handling
        gameFrame.isFocusable = true
        gameFrame.isFocusableInTouchMode = true
        gameFrame.requestFocus()
        gameFrame.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                onKeyDown(keyCode, event)
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun startGame() {
        synchronized(lock) {
            gameRunnable?.let {
                handler.removeCallbacks(it)
            }

            isGameRunning = true
            startButton.isVisible = false

            snake.reset()
            spawnFood()
            currentDirection = Direction.RIGHT

            gameRunnable = object : Runnable {
                override fun run() {
                    snake.changeDirection(currentDirection)
                    updateGame()
                    handler.postDelayed(this, 100)
                }
            }
            gameRunnable?.let {
                handler.postDelayed(it, 100)
            }
        }
    }

    private fun updateGame() {
        snake.move()

        if (snake.checkCollision()) {
            endGame()
            return
        }

        if (snake.eatFood(foodX, foodY)) {
            foodCounterCount++
            if (foodCounterCount > highestScoreCount) {
                highestScoreCount = foodCounterCount
            }
            updateScoreViews()
            spawnFood()
        }

        gameFrame.removeAllViews()
        drawSnake()
        drawFood()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> currentDirection = Direction.UP
            KeyEvent.KEYCODE_DPAD_DOWN -> currentDirection = Direction.DOWN
            KeyEvent.KEYCODE_DPAD_LEFT -> currentDirection = Direction.LEFT
            KeyEvent.KEYCODE_DPAD_RIGHT -> currentDirection = Direction.RIGHT
        }
        return super.onKeyDown(keyCode, event)
    }

    /*
    // square drawables
    private fun drawSnake() {
        for (segment in snake.getSegments()) {
            val segmentView = View(this)
            segmentView.setBackgroundColor(ContextCompat.getColor(this, R.color.snake_color))
            val params = FrameLayout.LayoutParams(cellSize, cellSize)
            params.leftMargin = segment.x * cellSize
            params.topMargin = segment.y * cellSize
            gameFrame.addView(segmentView, params)
        }
    }

    private fun drawFood() {
        val foodView = View(this)
        foodView.setBackgroundColor(ContextCompat.getColor(this, R.color.food_color))
        val params = FrameLayout.LayoutParams(cellSize, cellSize)
        params.leftMargin = foodX * cellSize
        params.topMargin = foodY * cellSize
        gameFrame.addView(foodView, params)
    }*/

    // icon drawables
    private fun drawSnake() {
        for ((index, segment) in snake.getSegments().withIndex()) {
            val segmentView = ImageView(this)
            val drawableResource = if (index == 0 && currentDirection==Direction.LEFT) R.drawable.truck_24_l else if (index == 0) R.drawable.truck_24 else R.drawable.trailer_24
            segmentView.setImageResource(drawableResource)

            val params = FrameLayout.LayoutParams(cellSize+10, cellSize+10)
            params.leftMargin = segment.x * cellSize
            params.topMargin = segment.y * cellSize
            gameFrame.addView(segmentView, params)
        }
    }

    private fun drawFood() {
        val foodView = ImageView(this)
        foodView.setImageResource(R.drawable.package_24) // Set food icon
        val params = FrameLayout.LayoutParams(cellSize+20, cellSize+20)
        params.leftMargin = foodX * cellSize
        params.topMargin = foodY * cellSize
        gameFrame.addView(foodView, params)
    }

    private fun spawnFood() {
        foodX = random.nextInt(screenWidth / (cellSize / 5))
        foodY = random.nextInt(screenHeight / (cellSize / 5))
    }

    private fun updateScoreViews() {
        // Update the TextViews with the current scores
        foodCounter.text = "$foodCounterCount"
        highestScore.text = "$highestScoreCount"
    }

    private fun endGame() {
        isGameRunning = false
        Thread.sleep(1000)
        startButton.isVisible = true
        // uncomment the next line to remove the game played as soon as it ends
        //gameFrame.removeAllViews()

        // Reset the food eaten counter
        foodCounterCount = 0
        updateScoreViews()
    }
}
