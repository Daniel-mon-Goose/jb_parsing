import java.io.File


fun locateLiterals(line: String, index: Int,
                   storage: MutableMap<String, MutableList<Int>>,
                   wrappers: CharArray) {
    var start = 0
    var finish = -1
    val slashRemovalRegex = "(${wrappers.joinToString(separator = "|") {
        "\\\\" + it
    }})".toRegex()

    while (true) {
        start = line.indexOfAny(wrappers, finish + 1)
        if (start < 0) break

        finish = start
        do {
            finish = line.indexOf(line[start], finish + 1)

            var slashesCounter = 0
            var currentIndex = finish - 1
            while (currentIndex >= 0 && line[currentIndex] == '\\') {
                slashesCounter++
                currentIndex--
            }
        } while (finish >= 0 && slashesCounter % 2 == 1)
        if (finish < 0) break

        val word = line.substring(start + 1, finish)
            .replace(slashRemovalRegex) { it.value[1].toString() }
            .replace("\\\\", "\\")

        storage.computeIfAbsent(word) { mutableListOf() }.add(index)
    }
}


fun printRepeatingLiterals(storage: MutableMap<String, MutableList<Int>>) {
    storage.forEach { (literal, indList) ->
        if (indList.size > 1) {
            println("Lines with '$literal': ${indList.distinct().joinToString(separator = ", ")}")
        }
    }
}


fun main(args: Array<String>) {
    if (args.size != 1) {
        throw IllegalArgumentException("A path to a file required")
    }

    val fileName = args[0]
    val literals = mutableMapOf<String, MutableList<Int>>()
    val stringWrappers = "\"'".toCharArray()

    File(fileName).bufferedReader().useLines {
        it.forEachIndexed { index, line -> locateLiterals(line, index, literals, stringWrappers) }
    }

    printRepeatingLiterals(literals)
}