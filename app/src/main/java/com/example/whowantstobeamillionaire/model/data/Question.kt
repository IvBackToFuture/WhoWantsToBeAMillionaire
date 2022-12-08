package com.example.whowantstobeamillionaire.model.data

data class Question(
    val difficult: QuestionDifficult,
    val question: String,
    val answers: List<Answer>
)