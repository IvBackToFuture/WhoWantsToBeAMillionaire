package com.example.whowantstobeamillionaire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.whowantstobeamillionaire.MainViewModel
import com.example.whowantstobeamillionaire.R
import com.example.whowantstobeamillionaire.model.data.GameStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadingFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_loading, container, false)
        val navController = this.findNavController()

        job = lifecycleScope.launch {
            viewModel.gameStatusFlow.collect {
                if (it == GameStatus.LOADED) {
                    withContext(Dispatchers.Main) {
                        navController.navigate(R.id.action_loadingFragment_to_questionFragment)
                    }
                } else if (it == GameStatus.ERROR) {
                    withContext(Dispatchers.Main) {
                        navController.navigate(R.id.action_loadingFragment_to_loadErrorFragment)
                    }
                }
            }
        }

        return view
    }

    override fun onDestroyView() {
        job.cancel()
        super.onDestroyView()
    }
}