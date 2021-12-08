@file:Suppress("MagicNumber")

fun main() {
  val fish1 = readInput("Day06_p1").asInts()
  println(
    "Part 1: " +
      SeaSimulation.rewind(fish1, 80)
  ) // 371379

  println(
    "Part 2: " +
      SeaSimulation.rewind(fish1, 256)
  ) // -> 1674303997472
}

private fun List<String>.asInts() =
  (this.firstOrNull() ?: "").split(",").map(String::toInt)

// Kudos to https://github.com/feedm3/advent-of-code-2021/blob/main/src/main/java/me/dietenberger/Day6.java
// I could make something reasonable for the Part 1 only.
object SeaSimulation {
  fun rewind(fish: List<Int>, days: Int, debug: Boolean = false): Long {
    var fishByCycle = fish.fold(List(9) { 0L }.toMutableList()) { acc, cycle ->
      acc[cycle] += 1L
      acc
    }
    repeat(days) {
      fishByCycle = arrayListOf(
        fishByCycle[1], // 0
        fishByCycle[2],
        fishByCycle[3],
        fishByCycle[4],
        fishByCycle[5],
        fishByCycle[6],
        fishByCycle[7] + fishByCycle[0], // 6
        fishByCycle[8],
        fishByCycle[0] // 8
      )
      if (debug && days < 81) {
        println(
          "After ${it + 1} day:" +
            fishByCycle
              .mapIndexed { index, i ->
                if (i == 0L) {
                  null
                } else {
                  if (i > Int.MAX_VALUE) {
                    "Can't make a List $i-long"
                  } else {
                    List(i.toInt()) { index }.joinToString(",")
                  }
                }
              }
              .filterNotNull()
              .joinToString(",")
        )
      }
    }

    return fishByCycle.sum()
  }
}