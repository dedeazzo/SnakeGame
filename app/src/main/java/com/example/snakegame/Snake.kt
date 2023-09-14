package com.example.snakegame

class Snake(private var screenWidth: Int, private var screenHeight:Int, private var cellSize:Int) {
    private val initialSize = 3
    private val segments = mutableListOf<Segment>()
    private var direction = Direction.RIGHT

    init {
        // Initialize the snake with segments
        for (i in 0 until initialSize) {
            segments.add(Segment(initialSize - 1 - i, 0))
        }
    }

    fun reset() {
        segments.clear()
        direction = Direction.RIGHT
        for (i in 0 until initialSize) {
            segments.add(Segment(initialSize - 1 - i, 0))
        }
    }

    fun move() {
        val head = getHead()
        val newHead = Segment(head.x, head.y)

        when (direction) {
            Direction.UP -> newHead.y--
            Direction.DOWN -> newHead.y++
            Direction.LEFT -> newHead.x--
            Direction.RIGHT -> newHead.x++
        }

        // Add the new head to the front of the snake
        segments.add(0, newHead)

        // Remove the tail segment to keep the snake the same length
        if (segments.size > initialSize) {
            segments.removeAt(segments.size - 1)
        }
    }

    fun changeDirection(newDirection: Direction) {
        // Prevent the snake from reversing direction
        if (newDirection != direction.opposite()) {
            direction = newDirection
        }
    }

    fun checkCollision(): Boolean {
        val head = getHead()

        // Check for collision with walls
        val collisionCellSize = (cellSize / 5)
        if (head.x < 0 || head.x >= screenWidth / collisionCellSize || head.y < 0 || head.y >= screenHeight / collisionCellSize) {
            return true
        }

        // Check for collision with itself
        for (i in 1 until segments.size) {
            if (head == segments[i]) {
                return true
            }
        }

        return false
    }

    fun eatFood(foodX: Int, foodY: Int): Boolean {
        val head = getHead()
        if (head.x == foodX && head.y == foodY) {
            // Snake ate the food, so add a new segment
            segments.add(Segment(foodX, foodY))
            return true
        }
        return false
    }

    fun getSegments(): List<Segment> {
        return segments.toList()
    }

    private fun getHead(): Segment {
        return segments[0]
    }
}

data class Segment(var x: Int, var y: Int)

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}
