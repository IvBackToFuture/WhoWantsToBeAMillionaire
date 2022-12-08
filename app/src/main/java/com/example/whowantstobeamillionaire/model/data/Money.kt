package com.example.whowantstobeamillionaire.model.data

data class Money(val value: Long) {
    companion object {
        val default = Money(0)
    }
}

interface IMoneyFormatter {
    fun toFormat(money: Money): String
}

class MoneyFormatter: IMoneyFormatter {
    override fun toFormat(money: Money) = money.value.toString().reversed().chunked(3).reversed().joinToString(separator = " ") { it.reversed() }
}