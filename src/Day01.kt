import Day01.incrementsCount

object Day01 {
  fun readInputsInts(name: String) =
    readInput(name).map(String::toInt)

  fun List<Int>.incrementsCount(): Int =
    this.windowed(2).fold(0) { increments, measures ->
      increments + if (measures.last() > measures.first()) {
        1
      } else {
        0
      }
    }
}

fun main() {
  println(
    "Part 1: " +
            Day01.readInputsInts("Day01_p1").incrementsCount()

  )

  println(
    "Part 2: " +
            readInput("Day01_p2").map(String::toInt).windowed(3, transform = { it.sum() }).incrementsCount()
  )
}
