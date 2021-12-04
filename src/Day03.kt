import Day03.asCounts
import Day03.multiply

object Day03 {
  fun List<String>.asCounts() =
      this.fold(emptyList<Count>()) { acc, line ->
        line.mapIndexed { index, c ->
          val currentCount = acc.getOrElse(index) { Count() }
          when (c) {
            '0' -> currentCount.withMoreZeros()
            '1' -> currentCount.withMoreOnes()
            else -> Count()
          }
        }
      }

  fun reduceList(list: List<String>, condition: (count: Count) -> Int, iteration: Int = 0): List<String> =
    if (list.size == 1) {
      list
    } else {
      val count = list.asCounts()[iteration]
      reduceList(
        list.filter { line -> line[iteration].toString().toInt() == condition(count) },
        condition,
        iteration + 1
      )
    }

  fun multiply(first: String, second: String) =
    Integer.parseInt(first, 2) * Integer.parseInt(second, 2)

}

fun main() {
  println(
    "Part 1: " +
            readInput("Day03_p1").let { list ->
              val counts = list.asCounts()
              val epsilonBinary = counts.map(Count::mostCommon).joinToString("")
              val gammaBinary = counts.map(Count::leastCommon).joinToString("")
              multiply(epsilonBinary, gammaBinary)
            }

  ) // 4139586

  println(
    "Part 2: " +
            readInput("Day03_p2").let { list ->
              val oxygenRatingBin = Day03.reduceList(list, Count::mostCommon).joinToString("")
              val co2RatingBin = Day03.reduceList(list, Count::leastCommon).joinToString("")
              multiply(oxygenRatingBin, co2RatingBin)
            }
  ) // 1800151
}

data class Count(val zeros: Int = 0, val ones: Int = 0) {
  fun withMoreZeros(): Count =
    copy(zeros = zeros + 1)

  fun withMoreOnes(): Count =
    copy(ones = ones + 1)

  fun mostCommon(): Int =
    if (zeros > ones) {
      0
    } else {
      1
    }

  fun leastCommon(): Int =
    if (zeros > ones) {
      1
    } else {
      0
    }
}

