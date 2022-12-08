package com.example.whowantstobeamillionaire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whowantstobeamillionaire.MainViewModel
import com.example.whowantstobeamillionaire.R

class DifficultAdapter(private val viewModel: MainViewModel) :
    RecyclerView.Adapter<DifficultAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.difficultButton)
    }

    private val list = viewModel.getListDifficultiesName()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.difficult_button_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.button.text = list[position]
        holder.button.setOnClickListener {
            viewModel.resetQuestion()
            viewModel.setCurrentDifficultiesById(position)
            viewModel.getNextQuestion()
            it.findNavController().navigate(R.id.action_selectDifficultFragment_to_loadingFragment)
//            TODO("navigation to loading fragment")//            navController.navigate(R.id.action_selectDifficultFragment_to_loadingFragment)
        }
    }
    override fun getItemCount(): Int = list.size
}

class SelectDifficultFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_select_difficult, container, false)
    }

    override fun onStart() {
        super.onStart()
        val recycler = view?.findViewById<RecyclerView>(R.id.difficultRecyclerView)
        recycler?.layoutManager = LinearLayoutManager(this.context)
        recycler?.adapter = DifficultAdapter(viewModel)
    }
}