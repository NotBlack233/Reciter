package me.not_black.reciter.wrapper

import me.not_black.reciter.util.TextParser

class ReciteWrapper(val text: String, var placeholder: String = "__")  {
    var phase: Int = 0
        set(value) {
            if (value in 0..maxPhase) {
                field = value
                update()
            }
            else throw IllegalArgumentException("value must be in 0 to $maxPhase")
        }
    private val parser = TextParser(text)
    val maxPhase: Int = parser.maxLength
    private val current: MutableList<String> = parser.split.toMutableList()
    val maxSentence = parser.split.size

    private fun update() {
        if (phase == 0) return
        current.clear()
        for (str in parser.split) {
            if (str.length <= phase)
                current.add(str[0] + placeholder.repeat(str.length - 1))
            else {
                val str1 = str.substring(0 until str.length - phase)
                current.add(str1 + placeholder.repeat(phase))
            }
        }
    }

    fun untilSentence(sentence: Int): String {
        if (sentence !in 0 .. parser.split.size)
            throw IllegalArgumentException("sentence out of bound: must be in 0 to ${parser.split.size}")
        val stringBuilder = StringBuilder()
        for (i in 0 until sentence)
            stringBuilder.append(parser[i])
        for (i in sentence until parser.split.size)
            stringBuilder.append(current[i]).append(parser.punctuations[i])
        return stringBuilder.toString()
    }

    override fun toString(): String = untilSentence(0)
}