package com.example.whowantstobeamillionaire.fragments

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whowantstobeamillionaire.MainViewModel
import com.example.whowantstobeamillionaire.R
import com.example.whowantstobeamillionaire.model.data.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoneyAdapter(
    private val listOfMoneyString: List<String>,
    private val currentQuestion: StateFlow<Pair<Int, Question>?>
) :
    RecyclerView.Adapter<MoneyAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moneyView: TextView = itemView.findViewById(R.id.moneyView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.money_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.moneyView.text = listOfMoneyString[position]
        if (position == currentQuestion.value?.first) {
            holder.moneyView.setBackgroundResource(R.drawable.money_view_current_style)
        } else if ((position + 1) % 5 == 0) {
            holder.moneyView.setBackgroundResource(R.drawable.money_view_save_style)
        } else {
            holder.moneyView.setBackgroundResource(R.drawable.money_view_style)
        }
    }

    override fun getItemCount(): Int = listOfMoneyString.size
}

class MoneyFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_money, container, false)
        val backButton = view.findViewById<Button>(R.id.backButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.moneyRecyclerView)

        val adapter = MoneyAdapter(viewModel.getMoneyStrings(), viewModel.questionFlow)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        backButton.setOnClickListener { it.findNavController().popBackStack() }

        lifecycleScope.launch {
            viewModel.questionFlow.collect {
                it?.first?.let {
                    withContext(Dispatchers.Main) {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        return view
    }
}