package com.example.whowantstobeamillionaire.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.whowantstobeamillionaire.MainViewModel


class GeniusFragmentDialog : DialogFragment() {
    private val list = listOf("A", "B", "C", "D")

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mNum = viewModel.questionFlow.value?.second?.answers?.indexOfFirst { it.isRight }
        val message = mNum?.let {
            "Не простой вопрос... Думаю правильный ответ: ${list[mNum]}"
        } ?: "Прошу прощения, но я не знаю ответа"
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Гений:")
                .setMessage(message)
                .setPositiveButton("Okay, thank you") { dialog, _ ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}