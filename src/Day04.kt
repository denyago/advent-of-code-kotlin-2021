import Day04.asGame
import Day04.play

object Day04 {
  fun List<String>.asGame(): Game {
    val numberLine = this.first()
    val rawBoards = drop(1).chunked(6).map { it.drop(1) }
    return Game(
      numberLine.split(",").map(String::toInt),
      rawBoards.map { Board.fromText(it) }
    )
  }

  fun play(game: Game, extraCondition: (game: Game) -> Boolean = { true }): Game =
    if (game.shouldPlay()) {
      if (extraCondition(game)) {
        play(game.takeTurn(), extraCondition)
      } else {
        game
      }
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
              "Board $index (winning = ${board.isWinning}, score = ${board.score})\n${board.inspect()}"
            }.joinToString("\n\n")

  fun takeTurn() =
    if (numbers.isEmpty()) {
      this
    } else {
      val newBoards = boards.map { it.maybeMark(numbers.first()) }
      copy(
        numbers = numbers.drop(1),
        boards = newBoards.filterNot(Board::isWinning),
        winningBoards = winningBoards + newBoards.filter(Board::isWinning)
      )
    }

  fun shouldPlay(): Boolean {
    return numbers.isNotEmpty()
  }

  fun winningBoard(): Board =
    winningBoards.lastOrNull() ?: Board()
}

fun main() {
  println(
    "Part 1: " +
            play(readInput("Day04_p1").asGame()) { game -> game.winningBoards.isEmpty() }.winningBoard().score

  ) // 4662

  println(
    "Part 2: " +
            play(readInput("Day04_p2").asGame()) { game -> game.boards.count(Board::isWinning) < game.boards.size }.winningBoard().score
  ) // 12080
}

class Board {
  var score: Int = 0
  private val numbers = emptyMap<Key, BingoNumber>().toMutableMap()

  data class Key(val columnIndex: Int, val rowIndex: Int)

  var isWinning = false

  fun inspect() =
    (0..4).joinToString("\n") { rowIndex ->
      (0..4).joinToString("") { columnIndex ->
        numbers.getOrDefault(key(columnIndex, rowIndex), BingoNumber(-1)).inspect()
      }
    }

  companion object {
    fun fromText(lines: List<String>) =
      lines.foldIndexed(Board()) { rowIndex, board, line ->
        line.trim().split("""\s+""".toRegex())
          .map(BingoNumber::fromString)
          .foldIndexed(board) { columnIndex: Int, _, bingoNumber: BingoNumber ->
            board.updateAt(columnIndex, rowIndex, bingoNumber)
          }
      }
  }

  private fun updateAt(columnIndex: Int, rowIndex: Int, bingoNumber: BingoNumber) =
    numbers.put(key(columnIndex, rowIndex), bingoNumber).let { this }

  private fun key(columnIndex: Int, rowIndex: Int) =
    Key(columnIndex, rowIndex)

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

  private fun maybeWinning(key: Key): Boolean {
    val rawWinning =
      (0..4).map { Key(it, key.rowIndex) }.count { numbers.getOrDefault(it, BingoNumber(-1)).isMarked } == 5
    val columnWinning =
      (0..4).map { Key(key.columnIndex, it) }.count { numbers.getOrDefault(it, BingoNumber(-1)).isMarked } == 5
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
