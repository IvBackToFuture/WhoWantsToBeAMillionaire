package com.example.whowantstobeamillionaire.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.whowantstobeamillionaire.MainViewModel
import kotlin.random.Random

class PeoplesFragmentDialog : DialogFragment() {
    private val aList = listOf("A", "B", "C", "D")

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mNum = viewModel.questionFlow.value?.second?.answers?.indexOfFirst { it.isRight }

        val rand = Random(0)
        val list = List(4) { rand.nextInt(20) }
        val sum = list.filterIndexed { index, _ -> index != mNum }.sumOf { 20 - it }
        val message = list.mapIndexed { i, v ->
            val cur = if (i != mNum) v else 40 + sum
            "${aList[i]}) ${cur}%"
        }.joinToString()
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Зрители:")
                .setMessage(message)
                .setPositiveButton("Okay") { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}