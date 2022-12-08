package com.example.whowantstobeamillionaire.model.game_difficult

import com.example.whowantstobeamillionaire.R
import com.example.whowantstobeamillionaire.model.data.Question
import com.example.whowantstobeamillionaire.model.question_repository.IQuestionRepository
import com.example.whowantstobeamillionaire.model.data.QuestionDifficult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlin.math.ceil

abstract class QuestionsOnDifficult(protected val questionRepository: IQuestionRepository) {
    abstract val name: Int
    abstract suspend fun getQuestions(count: Int): List<Question>
}

class EasyQuestions(questionRepository: IQuestionRepository) : QuestionsOnDifficult(questionRepository) {
    override val name: Int = R.string.easy

    override suspend fun getQuestions(count: Int): List<Question> = questionRepository.getQuestions(
        QuestionDifficult.EASY, count)
}

class NormalQuestions(questionRepository: IQuestionRepository) : QuestionsOnDifficult(questionRepository) {
    override val name: Int = R.string.middle

    override suspend fun getQuestions(count: Int): List<Question> = questionRepository.getQuestions(
        QuestionDifficult.NORMAL, count)
}

class HardQuestions(questionRepository: IQuestionRepository) : QuestionsOnDifficult(questionRepository) {
    override val name: Int = R.string.hard

    override suspend fun getQuestions(count: Int): List<Question> = questionRepository.getQuestions(
        QuestionDifficult.HARD, count)
}

class ChildQuestions(questionRepository: IQuestionRepository) : QuestionsOnDifficult(questionRepository) {
    override val name: Int = R.string.child

    override suspend fun getQuestions(count: Int): List<Question> = questionRepository.getQuestions(
        QuestionDifficult.CHILD, count)
}

class ClassicQuestions(questionRepository: IQuestionRepository, private val difficultList: List<QuestionsOnDifficult>) : QuestionsOnDifficult(questionRepository) {
    override val name: Int = R.string.classic

    override suspend fun getQuestions(count: Int): List<Question> {
        val onOneCount = ceil(count.toDouble() / difficultList.size).toInt()
        return withContext(Dispatchers.IO) {
            List(difficultList.size) { async { difficultList[it].getQuestions(onOneCount) } }.awaitAll().flatten().take(count)
        }
    }
}



