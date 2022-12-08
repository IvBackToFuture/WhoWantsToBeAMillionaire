package com.example.whowantstobeamillionaire.model.managers

import com.example.whowantstobeamillionaire.model.data.MoneyFormatter
import com.example.whowantstobeamillionaire.model.game_difficult.QuestionsOnDifficult

class MainManager(
    moneyManagerItem: MoneyManager,
    questionsOnDifficult: QuestionsOnDifficult
) {
    private var questionManager: QuestionManager
    private val moneyManager: MoneyManager

    fun getCurrentMoneyString() = moneyManager.getMoneyStringById(questionManager.getQuestionId())
    fun getMoneyStrings() = moneyManager.getMoneyStrings()
    fun getMoneyByEnd(questionId: Int, isWin: Boolean, isLast: Boolean) =
        moneyManager.getMoneyStringByEnd(questionId, isWin, isLast)

    suspend fun getNextQuestionWithId() = questionManager.getNextQuestionWithId()
    fun resetQuestion() = questionManager.resetQuestion()
    fun setQuestionsOnDifficult(questionsOnDifficult: QuestionsOnDifficult) {
        questionManager = QuestionManagerImpl(questionsOnDifficult, questionsCount)
    }

    fun isFirstQuestion() = questionManager.isFirst()
    fun isFinishQuestion() = questionManager.isFinish()

    init {
        questionManager = QuestionManagerImpl(questionsOnDifficult, questionsCount)
        moneyManager = if (moneyManagerItem.getMoneyStrings().size != questionsCount)
            MoneyManagerImpl(
                generateSequence(500L) { it * 2 }.take(questionsCount).toList(),
                MoneyFormatter()
            )
        else moneyManagerItem
    }

    companion object {
        const val questionsCount = 15
    }
}