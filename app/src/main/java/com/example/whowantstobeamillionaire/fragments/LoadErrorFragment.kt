package com.example.whowantstobeamillionaire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.whowantstobeamillionaire.R

class LoadErrorFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_load_error, container, false)

        view.findViewById<Button>(R.id.homeButton).setOnClickListener {
            it.findNavController().popBackStack()
        }

        return view
    }
}