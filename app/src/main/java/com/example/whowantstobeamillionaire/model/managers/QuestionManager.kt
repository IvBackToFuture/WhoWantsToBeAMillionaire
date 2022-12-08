package com.example.whowantstobeamillionaire.model.managers

import com.example.whowantstobeamillionaire.model.data.Question
import com.example.whowantstobeamillionaire.model.game_difficult.QuestionsOnDifficult

abstract class QuestionManager(
    private val questionsOnDifficult: QuestionsOnDifficult,
    private val questionsCount: Int
) {
    private suspend fun getNextQuestion(): Question? {
        currentQuestionId = nextQuestionId
        if (currentQuestionId == startIndex) questions =
            questionsOnDifficult.getQuestions(questionsCount)
        return if (++nextQuestionId <= questionsCount)
            questions[currentQuestionId] else null
    }

    fun getQuestionId() = currentQuestionId
    fun resetQuestion() {
        nextQuestionId = startIndex
    }

    suspend fun getNextQuestionWithId() = nextQuestionId to getNextQuestion()

    fun isFirst() = nextQuestionId == startIndex
    fun isFinish() = nextQuestionId > questionsCount

    private var questions: List<Question> = emptyList()

    private var nextQuestionId = startIndex
    private var currentQuestionId = startIndex

    companion object {
        const val startIndex = 0
    }
}

class QuestionManagerImpl(
    questionsOnDifficult: QuestionsOnDifficult,
    questionsCount: Int
) : QuestionManager(questionsOnDifficult, questionsCount)