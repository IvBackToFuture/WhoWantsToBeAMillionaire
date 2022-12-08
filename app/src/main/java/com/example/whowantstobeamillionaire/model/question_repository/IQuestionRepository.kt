package com.example.whowantstobeamillionaire.model.question_repository

import com.example.whowantstobeamillionaire.model.data.QuestionDifficult
import com.example.whowantstobeamillionaire.model.data.Question

interface IQuestionRepository {
    suspend fun getQuestions(qType: QuestionDifficult, count: Int): List<Question>
}