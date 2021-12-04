object Day03 {
  fun readLines(name: String) =
    readInput(name)
}

fun main() {
  println(
    "Part 1: " +
            Day03.readLines("Day03_p1").fold(emptyList<Count>()) { acc, line ->
              line.mapIndexed { index, c ->
                val currentCount = acc.getOrElse(index) { Count() }
                when (c) {
                  '0' -> currentCount.withMoreZeros()
                  '1' -> currentCount.withMoreOnes()
                  else -> Count()
                }
              }
            }.let { counts ->
              val epsilonBinary = counts.map(Count::mostCommon).joinToString("")
              val gammaBinary = counts.map(Count::leastCommon).joinToString("")
              val epsilonRate = Integer.parseInt(epsilonBinary, 2)
              val gammaRate = Integer.parseInt(gammaBinary, 2)
              epsilonRate * gammaRate
            }

  ) // 4139586

  println(
    "Part 2: " +
            Day03.readLines("Day03_p2")
  )
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

