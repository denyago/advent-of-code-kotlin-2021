@file:Suppress("unused")

import Day04.asGame
import Day04.play

fun main() {
  val noWinningBoardsYet: (game: Game) -> Boolean = { game -> game.winningBoards.isEmpty() }
  val game1 = readInput("Day04_p1").asGame()
  println(
    "Part 1: " +
      play(
        game1,
        shouldPlayAsLongAs = noWinningBoardsYet
      ).winningBoard().score

  ) // 4662

  val notAllBoardsHaveWon: (game: Game) -> Boolean = { game -> game.boards.count(Board::isWinning) < game.boards.size }
  val game2 = readInput("Day04_p1").asGame()
  println(
    "Part 2: " +
      play(
        game2,
        shouldPlayAsLongAs = notAllBoardsHaveWon
      ).winningBoard().score
  ) // 12080
}

object Day04 {
  private const val EXTRA_LINES = 1

  fun List<String>.asGame(): Game {
    val numberLine = this.first()
    val rawBoards = drop(EXTRA_LINES)
      .chunked(Board.SIZE + EXTRA_LINES)
      .map { it.drop(EXTRA_LINES) }
    return Game(
      numberLine.split(",").map(String::toInt),
      rawBoards.map(Board.Companion::fromText)
    )
  }

  fun play(game: Game, shouldPlayAsLongAs: (game: Game) -> Boolean = { true }): Game =
    if (game.canPlay() && shouldPlayAsLongAs(game)) {
      play(game.takeTurn(), shouldPlayAsLongAs)
    } else {
      game
    }
}

data class Game(
  val numbers: List<Int>,
  val boards: List<Board>,
  val winningBoards: List<Board> = emptyList()
) {
  fun inspect() =
    numbers.joinToString(", ") +
      "\n\n" +
      boards.mapIndexed { index, board ->
        board.inspect { "Board $index" }
      }.joinToString("\n\n")

  fun takeTurn() =
    if (numbers.isEmpty()) {
      this
    } else {
      val theNumber = numbers.first()
      val updatedBoards = boards.map { it.maybeMark(theNumber) }.groupBy { it.isWinning }
      val restOfNumbers = numbers.drop(1)
      copy(
        numbers = restOfNumbers,
        boards = updatedBoards.getOrDefault(false, emptyList()),
        winningBoards = winningBoards + updatedBoards.getOrDefault(true, emptyList())
      )
    }

  fun canPlay() = numbers.isNotEmpty()

  fun winningBoard(): Board =
    winningBoards.lastOrNull() ?: Board()
}

class Board {
  var score = 0
  var isWinning = false

  private val numbers = emptyMap<Key, BingoNumber>().toMutableMap()

  fun maybeMark(number: Int): Board {
    numbers.forEach { (key, value) ->
      if (value.number == number) {
        numbers[key] = BingoNumber(number, true)
        isWinning = maybeWinning(key)
        if (isWinning) {
          score = number * numbers.values.filterNot(BingoNumber::isMarked).sumOf(BingoNumber::number)
        }
      }
    }
    return this
  }

  fun inspect(name: (board: Board) -> String = { board -> "Board #${board.hashCode()}" }) =
    name(this) + if (isWinning) " (had won with score $score)" else "" + "\n" +
      (0 until SIZE).joinToString("\n") { rowIndex ->
        (0 until SIZE).joinToString("") { columnIndex ->
          numbers.getOrDefault(buildKey(columnIndex, rowIndex), BingoNumber(-1)).inspect()
        }
      }

  companion object {
    const val SIZE = 5

    fun fromText(lines: List<String>) =
      lines.foldIndexed(Board()) { rowIndex, board, line ->
        line.trim().split("""\s+""".toRegex())
          .map(BingoNumber::fromString)
          .foldIndexed(board) { columnIndex: Int, _, bingoNumber: BingoNumber ->
            board.numbers[buildKey(columnIndex, rowIndex)] = bingoNumber
            board
          }
      }

    private data class Key(val columnIndex: Int, val rowIndex: Int)
    private fun buildKey(columnIndex: Int, rowIndex: Int) =
      Key(columnIndex, rowIndex)
  }

  private fun maybeWinning(key: Key): Boolean {
    val rawWinning =
      (0 until SIZE).map { buildKey(it, key.rowIndex) }
        .count { numbers.getOrDefault(it, BingoNumber(-1)).isMarked } == SIZE
    val columnWinning =
      (0 until SIZE).map { buildKey(key.columnIndex, it) }
        .count { numbers.getOrDefault(it, BingoNumber(-1)).isMarked } == SIZE
    return rawWinning || columnWinning
  }
}

data class BingoNumber(val number: Int, val isMarked: Boolean = false) {
  fun inspect() =
    if (isMarked) {
      " (%2d) ".format(number)
    } else {
      "  %2d  ".format(number)
    }

  companion object {
    fun fromString(string: String) = BingoNumber(string.toInt())
  }
}