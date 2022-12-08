package com.example.whowantstobeamillionaire.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whowantstobeamillionaire.MainViewModel
import com.example.whowantstobeamillionaire.MainViewModel.Companion.GENIUS_BUTTON
import com.example.whowantstobeamillionaire.MainViewModel.Companion.HALF_BUTTON
import com.example.whowantstobeamillionaire.MainViewModel.Companion.PEOPLE_BUTTON
import com.example.whowantstobeamillionaire.R
import com.example.whowantstobeamillionaire.model.data.GameStatus
import com.example.whowantstobeamillionaire.services.AnswersAudioService
import kotlinx.coroutines.*
import java.util.*

class AnswerAdapter(private val viewModel: MainViewModel, private val activity: FragmentActivity?) :
    RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.button)
    }

    private var answersList = viewModel.questionFlow.value?.second?.answers ?: emptyList()

    private val printList = listOf("a", "b", "c", "d")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.answer_button, parent, false)
        return ViewHolder(view)
    }

    private var trueAnimation: AnimationDrawable? = null
        set(value) {
            value?.isOneShot = true
            field = value
        }

    private var statusList = List(4) { true }
    private var visibleList = MutableList(4) { 0 }

    fun hideTwoButtons() {
        List(4) { it }
            .shuffled()
            .map { it to answersList[it].isRight }
            .filter { !it.second }
            .take(2)
            .forEach {
                visibleList[it.first] = if (visibleList[it.first] == 0) 4 else 0
                notifyItemChanged(it.first)
            }
    }
    fun resetVisible() {
        visibleList = MutableList(4) { 0 }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val answer = answersList[position]
        holder.button.visibility = visibleList[position]
        holder.button.text = "${printList[position]}) ${answer.text}"
        holder.button.backgroundTintMode = PorterDuff.Mode.DST
        holder.button.setBackgroundResource(if (answer.isRight) R.drawable.button_anim_true else R.drawable.button_anim_false)

        if (answer.isRight) trueAnimation = holder.button.background as AnimationDrawable
        holder.button.setOnClickListener {
            if (statusList[position]) {
                statusList = statusList.map { it.not() }
                val soundIntent = Intent(activity, AnswersAudioService::class.java)
                    .putExtra(AnswersAudioService.STATUS, answer.isRight)
                activity?.startService(soundIntent)

                val animationDrawable = it.background as AnimationDrawable
                animationDrawable.isOneShot = true
                animationDrawable.start()
                if (!answer.isRight) trueAnimation?.start()

                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        viewModel.checkAnswer(answer)
                        statusList = statusList.map { it.not() }
                    }
                }, 1200)
            }
        }
    }


    override fun getItemCount(): Int {
        answersList = viewModel.questionFlow.value?.second?.answers ?: emptyList()
        return answersList.size
    }
}

class QuestionFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_question, container, false)
        val navigationController = this.findNavController()

        val homeButton = view.findViewById<Button>(R.id.homeButton)
        val moneyButton = view.findViewById<Button>(R.id.moneyButton)

        val currentBidView = view.findViewById<TextView>(R.id.currentBidView)
        val currentQuestionNumberView = view.findViewById<TextView>(R.id.currentQuestionNumberView)

        val questionView = view.findViewById<TextView>(R.id.questionView)

        val geniusButton = view.findViewById<Button>(R.id.geniusButton)
        val halfButton = view.findViewById<Button>(R.id.halfButton)
        val peopleButton = view.findViewById<Button>(R.id.peopleButton)

        val adapter = AnswerAdapter(viewModel, activity)

        val buttonsRecycler = view.findViewById<RecyclerView>(R.id.buttonsRecyclerView)
        buttonsRecycler.adapter = adapter
        buttonsRecycler.layoutManager = LinearLayoutManager(context)

        setVisibility(halfButton, HALF_BUTTON)
        setVisibility(geniusButton, GENIUS_BUTTON)
        setVisibility(peopleButton, PEOPLE_BUTTON)

        halfButton.setOnClickListener {
            viewModel.allowedSpecButtonsList[HALF_BUTTON] = false
            setVisibility(it, HALF_BUTTON)
            adapter.hideTwoButtons()
        }
        geniusButton.setOnClickListener {
            viewModel.allowedSpecButtonsList[GENIUS_BUTTON] = false
            setVisibility(it, GENIUS_BUTTON)
            activity?.supportFragmentManager?.let { manager ->
                GeniusFragmentDialog().show(manager, GENIUS)
            }
        }
        peopleButton.setOnClickListener {
            viewModel.allowedSpecButtonsList[PEOPLE_BUTTON] = false
            setVisibility(it, PEOPLE_BUTTON)
            activity?.supportFragmentManager?.let { manager ->
                PeoplesFragmentDialog().show(manager, PEOPLE)
            }
        }

        lifecycleScope.launch {
            viewModel.questionFlow.collect {
                adapter.resetVisible()
                adapter.notifyDataSetChanged()
                if (it != null) {
                    it.second.let {
                        questionView.text = it.question
                    }
                    it.first.let {
                        currentBidView.text = "${viewModel.getCurrentMoneyString()} $"
                        currentQuestionNumberView.text = "${it + 1}/${viewModel.getQuestionCount()}"
                    }
                }
            }
        }

        job = lifecycleScope.launch {
            viewModel.gameStatusFlow.collect {
                if (it == GameStatus.WIN || it == GameStatus.LOSE) {
                    withContext(Dispatchers.Main) {
                        navigationController.navigate(R.id.action_questionFragment_to_gameResultFragment)
                    }
                }
            }
        }

        homeButton?.setOnClickListener {
            navigationController.navigate(R.id.action_questionFragment_to_gameResultFragment)
        }

        moneyButton?.setOnClickListener {
            navigationController.navigate(R.id.action_questionFragment_to_moneyFragment)
        }
        return view
    }

    private fun setVisibility(view: View, constVal: Int) {
        view.visibility = if (viewModel.allowedSpecButtonsList[constVal]) View.VISIBLE else View.INVISIBLE
    }

    private lateinit var job: Job

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }

    companion object {
        const val GENIUS = "GENIUS"
        const val PEOPLE = "PEOPLE"
    }
}