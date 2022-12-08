package com.example.whowantstobeamillionaire.model.question_repository

import com.example.whowantstobeamillionaire.model.data.Answer
import com.example.whowantstobeamillionaire.model.data.EmptyDataException
import com.example.whowantstobeamillionaire.model.data.Question
import com.example.whowantstobeamillionaire.model.data.QuestionDifficult
import com.example.whowantstobeamillionaire.model.question_repository.LifeIsPornQuestionRepository.Companion.API_KEY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.math.ceil

data class QuestionLifeIsPornDTO(
    val question: String,
    val answers: List<String>
)

data class QueryLifeIsPornDTO(
    val ok: Boolean,
    val data: List<QuestionLifeIsPornDTO>
)

interface QuestionLifeIsPornService {
    @GET("millionaire.php?apikey=$API_KEY")
    suspend fun getQuestions(
        @Query("qType")
        qType: Int,
        @Query("count")
        count: Int
    ): QueryLifeIsPornDTO
}

class LifeIsPornQuestionRepository : IQuestionRepository {
    private val retrofit = Retrofit
        .Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

    private val service = retrofit.create<QuestionLifeIsPornService>()

    override suspend fun getQuestions(qType: QuestionDifficult, count: Int): List<Question> {
        val queriesCount = ceil(count.toDouble() / MAX_COUNT_QUESTION).toInt()
        val result = withContext(Dispatchers.IO) {
            List(queriesCount) {
                async {
                    val res = service.getQuestions(qType.value.toInt(), MAX_COUNT_QUESTION)
                    if (res.ok) res.data else throw EmptyDataException()
                }
            }.awaitAll().flatten().take(count)
        }

        return result.map {
            Question(
                qType, it.question,
                it.answers.mapIndexed { i, v -> Answer(v, i == 0) }.shuffled()
            )
        }
    }

    companion object {
        const val BASE_URL = "https://engine.lifeis.porn/api/"
        const val API_KEY = "8e253c1d1843e1a9b55bd20cb"
        const val MIN_COUNT_QUESTION = 1
        const val MAX_COUNT_QUESTION = 5
        const val MIN_QUESTION_TYPE = 1
        const val MAX_QUESTION_TYPE = 4
    }
}