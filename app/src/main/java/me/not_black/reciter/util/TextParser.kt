package me.not_black.reciter.util

import kotlin.math.max

open class TextParser(
    val text: String,
    val delimiters: List<Char> = listOf('，', '。', '；', '！', '？', '：', ' ', '\n')
) : Iterable<String> {
    val split: MutableList<String> = mutableListOf()
    val punctuations : MutableList<String> = mutableListOf()
    val maxLength: Int

    init {
        var tmpMaxLength: Int = -1
        assert(text.isNotEmpty())
        val sentenceBuilder = StringBuilder()
        val punctuationBuilder = StringBuilder()

        // true -> sentence, false -> punctuation
        var state = true
        for (char in text) {
            if (char in delimiters) {
                if (state) {
                    state = false
                    val str = sentenceBuilder.toString()
                    sentenceBuilder.clear()
                    split.add(str)
                    punctuationBuilder.append(char)
                    tmpMaxLength = max(tmpMaxLength, str.length)
                }
                else punctuationBuilder.append(char)
            }
            else if (state) sentenceBuilder.append(char)
            else {
                state =  true
                val p = punctuationBuilder.toString()
                punctuationBuilder.clear()
                punctuations.add(p)
                sentenceBuilder.append(char)
            }
        }
        if (sentenceBuilder.isNotEmpty()) {
            val str = sentenceBuilder.toString()
            split.add(str)
            punctuations.add(" ")
            tmpMaxLength = max(tmpMaxLength, str.filterNot { it in setOf('\n', ' ') }.length)
        }
        if (punctuationBuilder.isNotEmpty()) {
            val p = punctuationBuilder.toString()
            if (split.size == punctuations.size)
                split.add(" ")
            punctuations.add(p)
        }
        maxLength = tmpMaxLength
        assert(split.size == punctuations.size)
    }

    operator fun get(index: Int) = split[index] + punctuations[index]
    override fun iterator(): Iterator<String> = object : Iterator<String> {
        var index = 0
        override fun hasNext(): Boolean = index < split.size

        override fun next(): String {
            index++
            return this@TextParser[index]
        }
    }
}