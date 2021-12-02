enum class Direction {
  FORWARD, UP, DOWN
}

data class Command(val direction: Direction, val units: Int)

data class Position(val depth: Int = 0, val distance: Int = 0) {
  fun applyCommand(command: Command) =
    when (command.direction) {
      Direction.FORWARD -> copy(distance = distance + command.units)
      Direction.UP -> copy(depth = depth - command.units)
      Direction.DOWN -> copy(depth = depth + command.units)
    }
}

data class PositionWithAim(val depth: Int = 0, val distance: Int = 0, val aim: Int = 0) {
  fun applyCommand(command: Command) =
    when (command.direction) {
      Direction.FORWARD -> copy(distance = distance + command.units, depth = depth + command.units * aim)
      Direction.UP -> copy(aim = aim - command.units)
      Direction.DOWN -> copy(aim = aim + command.units)
    }
}

object Day02 {
  fun readCommands(name: String) =
    readInput(name).asCommands()

  private fun List<String>.asCommands(): List<Command> =
    this.map { line ->
      val (textCommand, units) = line.split(" ")
      Command(
        when (textCommand) {
          "up" -> Direction.UP
          "down" -> Direction.DOWN
          "forward" -> Direction.FORWARD
          else -> throw RuntimeException("Command $textCommand is not supported")
        },
        units.toInt()
      )
    }
}

fun main() {
  println(
    "Part 1: " +
            Day02.readCommands("Day02_p1").fold(Position()) { position, command ->
              position.applyCommand(command)
            }.let { position -> position.depth * position.distance }

  ) // 1524750

  println(
    "Part 2: " +
            Day02.readCommands("Day02_p2").fold(PositionWithAim()) { position, command ->
              position.applyCommand(command)
            }.let { position -> position.depth * position.distance }
  ) // 1592426537
}

