package com.example.whowantstobeamillionaire.model.managers

import com.example.whowantstobeamillionaire.model.data.IMoneyFormatter
import com.example.whowantstobeamillionaire.model.data.Money

abstract class MoneyManager(
    moneyList: List<Long>,
    private val moneyFormatter: IMoneyFormatter,
) {
    private val list = moneyList.map { Money(it) }
    fun getMoneyStringById(id: Int) = list.getOrNull(id)?.let { moneyFormatter.toFormat(it) }
    fun getMoneyStrings() = list.map { moneyFormatter.toFormat(it) }
    fun getMoneyStringByEnd(idQuestion: Int, isWin: Boolean, isEnd: Boolean): String {
        val res = if (isEnd) {
            list.last()
        } else if (isWin) {
            list.getOrElse(idQuestion - 1) { Money.default }
        } else {
            val saveDot = list.size / 3
            val numQuestion = idQuestion / saveDot * saveDot
            if (numQuestion != 0) {
                list[numQuestion - 1]
            } else {
                Money(0)
            }
        }
        return moneyFormatter.toFormat(res)
    }
}

class MoneyManagerImpl(
    moneyList: List<Long>,
    moneyFormatter: IMoneyFormatter
) : MoneyManager(moneyList, moneyFormatter)

