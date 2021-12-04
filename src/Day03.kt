object Day03 {
  fun readAsCounts(name: String) =
    readInput(name).fold(emptyList<Count>()) { acc, line ->
      line.mapIndexed { index, c ->
        val currentCount = acc.getOrElse(index) { Count() }
        when (c) {
          '0' -> currentCount.withMoreZeros()
          '1' -> currentCount.withMoreOnes()
          else -> Count()
        }
      }
    }

  fun listAndCounts(list: List<String>) =
    Pair(
      list,
      list.fold(emptyList<Count>()) { acc, line ->
        line.mapIndexed { index, c ->
          val currentCount = acc.getOrElse(index) { Count() }
          when (c) {
            '0' -> currentCount.withMoreZeros()
            '1' -> currentCount.withMoreOnes()
            else -> Count()
          }
        }
      }
    )

  fun reduceList(list: List<String>, condition: (count: Count) -> Int, iteration: Int = 0, ): List<String> =
    if (list.size == 1) {
      list
    } else {
      val count = listAndCounts(list).second.get(iteration)
      reduceList(
        list.filter { line -> line[iteration].toString().toInt() == condition(count) },
        condition,
        iteration + 1
      )
    }
}

fun main() {
  println(
    "Part 1: " +
            Day03.listAndCounts(readInput("Day03_p1")).let { (_, counts) ->
              val epsilonBinary = counts.map(Count::mostCommon).joinToString("")
              val gammaBinary = counts.map(Count::leastCommon).joinToString("")
              val epsilonRate = Integer.parseInt(epsilonBinary, 2)
              val gammaRate = Integer.parseInt(gammaBinary, 2)
              epsilonRate * gammaRate
            }

  ) // 4139586

  println(
    "Part 2: " +
            Day03.listAndCounts(readInput("Day03_p2")).let { (full_list, _) ->
              val oxygenRatingBin = Day03.reduceList(full_list, { c -> c.mostCommon()}).joinToString("")
              val co2RatingBin = Day03.reduceList(full_list, { c -> c.leastCommon()}).joinToString("")

              val oxygenRate = Integer.parseInt(oxygenRatingBin, 2)
              val co2Rate = Integer.parseInt(co2RatingBin, 2)
              oxygenRate * co2Rate
            }
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

