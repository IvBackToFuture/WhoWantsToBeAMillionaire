package com.example.whowantstobeamillionaire.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.whowantstobeamillionaire.MainViewModel
import com.example.whowantstobeamillionaire.R
import com.example.whowantstobeamillionaire.model.data.GameStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameResultFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_game_result, container, false)

        val resultTextView = view.findViewById<TextView>(R.id.resultTextView)
        val moneyResultView = view.findViewById<TextView>(R.id.moneyResultView)
        val homeButton = view.findViewById<Button>(R.id.homeButton)
        homeButton.setOnClickListener {
            it.findNavController().popBackStack()
        }

        lifecycleScope.launch {
            viewModel.gameStatusFlow.collect {
                withContext(Dispatchers.Main) {
                    val question = viewModel.questionFlow.value
                    val isLose = it == GameStatus.LOSE
                    val isWin = it == GameStatus.WIN
                    val resultText = if (isWin) {
                        "${getString(R.string.you_answer_string)} ${getString(R.string.all_string)} ${
                            getString(R.string.questions)
                        }"
                    } else {
                        "${getString(R.string.you_answer_string)} ${question?.first}/${viewModel.getQuestionCount()} ${
                            getString(R.string.questions)
                        }"
                    }
                    resultTextView.text = resultText
                    moneyResultView.text =
                        "${viewModel.getMoneyByEnd(question?.first ?: 0, !isLose, isWin)}$"
                }
            }
        }
        return view
    }
}