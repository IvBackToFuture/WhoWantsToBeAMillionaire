package com.example.whowantstobeamillionaire.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.whowantstobeamillionaire.MainViewModel
import com.example.whowantstobeamillionaire.R


class GeniusFragmentDialog : DialogFragment() {
    private val list = listOf("A", "B", "C", "D")

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mNum = viewModel.questionFlow.value?.second?.answers?.indexOfFirst { it.isRight }
        val message = mNum?.let {
            "${getString(R.string.i_think_right_ans_str)}: ${list[mNum]}"
        } ?: getString(R.string.i_dont_know_answer)
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("${getString(R.string.Genius)}:")
                .setMessage(message)
                .setPositiveButton(getString(R.string.okay_for_genius)) { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException(getString(R.string.activity_cannot_be_null))
    }
}