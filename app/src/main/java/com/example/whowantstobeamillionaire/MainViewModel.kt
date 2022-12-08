package com.example.whowantstobeamillionaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whowantstobeamillionaire.model.data.*
import com.example.whowantstobeamillionaire.model.game_difficult.*
import com.example.whowantstobeamillionaire.model.managers.MainManager
import com.example.whowantstobeamillionaire.model.managers.MoneyManagerImpl
import com.example.whowantstobeamillionaire.model.question_repository.IQuestionRepository
import com.example.whowantstobeamillionaire.model.question_repository.LifeIsPornQuestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository: IQuestionRepository
    private val questionDifficulties: List<QuestionsOnDifficult>
    private val mainManager: MainManager
    private val defaultDifficulties: QuestionsOnDifficult

    private val mutableQuestionFlow = MutableStateFlow<Pair<Int, Question>?>(null)
    val questionFlow = mutableQuestionFlow.asStateFlow()
    fun resetQuestion() {
        mainManager.resetQuestion()
    }

    private val mutableGameStatusFlow = MutableStateFlow(GameStatus.NOT_RUN)
    val gameStatusFlow = mutableGameStatusFlow.asStateFlow()

    fun getListDifficultiesName() = questionDifficulties.map { it.name }

    fun setCurrentDifficultiesById(id: Int) =
        mainManager.setQuestionsOnDifficult(
            questionDifficulties.getOrElse(id) { defaultDifficulties }
        )

    fun getNextQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val isFirst = mainManager.isFirstQuestion()
                if (isFirst) {
                    mutableGameStatusFlow.emit(GameStatus.LOADING)
                    allowedSpecButtonsList = MutableList(3) { true }
                }

                val (id, quiz) = mainManager.getNextQuestionWithId()
                if (quiz == null) {
                    if (mainManager.isFinishQuestion()) mutableGameStatusFlow.emit(GameStatus.WIN)
                    else mutableGameStatusFlow.emit(GameStatus.ERROR)
                } else {
                    mutableGameStatusFlow.emit(
                        if (isFirst) GameStatus.LOADED
                        else GameStatus.RUN
                    )
                    mutableQuestionFlow.emit(id to quiz)
                }
            } catch (e: retrofit2.HttpException) {
                mutableGameStatusFlow.emit(GameStatus.ERROR)
            } catch (e: Exception) {
                mutableGameStatusFlow.emit(GameStatus.ERROR)
            }
        }
    }

    fun getMoneyStrings() = mainManager.getMoneyStrings()
    fun getMoneyByEnd(questionId: Int, isWin: Boolean, isLast: Boolean) = mainManager.getMoneyByEnd(questionId, isWin, isLast)
    fun getCurrentMoneyString() = mainManager.getCurrentMoneyString()
    fun getQuestionCount() = MainManager.questionsCount
    fun checkAnswer(answer: Answer) {
        if (answer.isRight) {
            getNextQuestion()
        } else {
            viewModelScope.launch {
                mainManager.resetQuestion()
                mutableGameStatusFlow.emit(GameStatus.LOSE)
            }
        }
    }

    var allowedSpecButtonsList = MutableList(3) { true }

    init {
        repository = LifeIsPornQuestionRepository()
        val difficulties = listOf(
            ChildQuestions(repository),
            EasyQuestions(repository),
            NormalQuestions(repository),
            HardQuestions(repository),
        )
        questionDifficulties = listOf(ClassicQuestions(repository, difficulties)) + difficulties
        defaultDifficulties = questionDifficulties[0]

        val moneyList = listOf<Long>(
            500, 1_000, 2_000, 3_000, 5_000, 10_000, 15_000, 25_000, 50_000,
            100_000, 200_000, 400_000, 800_000, 1_500_000, 3_000_000,
        )

        val moneyManager = MoneyManagerImpl(moneyList, MoneyFormatter())

        mainManager = MainManager(moneyManager, questionDifficulties[0])
    }

    companion object {
        const val HALF_BUTTON = 0
        const val GENIUS_BUTTON = 1
        const val PEOPLE_BUTTON = 2
    }
}