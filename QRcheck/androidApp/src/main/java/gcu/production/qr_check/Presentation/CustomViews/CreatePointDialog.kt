@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.CustomViews

import android.app.AlertDialog
import android.text.InputType
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.FragmentActivity
import gcu.production.qr_check.android.R

internal object CreatePointDialog
{
    private var radius: Int = 0
    private var timeActive: Long? = null

    infix fun ((additionalData: Pair<Int, Long?>) -> Any).buildDialogCreatePoint(
        contextCall: FragmentActivity
    ): AlertDialog
    {
        val userInputDataRadius =
            AppCompatEditText(contextCall)
        val userInputDataTime =
            AppCompatEditText(contextCall)
        val layoutContainer =
            LinearLayout(contextCall)

        userInputDataRadius.hint =
            contextCall.getString(
                R.string.infoEditTextQRCreator
            )
        userInputDataTime.hint =
            contextCall.getString(
                R.string.infoEditTextQRCreator1
            )

        userInputDataRadius.inputType =
            InputType.TYPE_CLASS_NUMBER
        userInputDataTime.inputType =
            InputType.TYPE_CLASS_DATETIME

        userInputDataTime.gravity = Gravity.CENTER
        userInputDataRadius.gravity = Gravity.CENTER
        layoutContainer.gravity = Gravity.CENTER
        layoutContainer.orientation = LinearLayout.VERTICAL

        layoutContainer.addView(userInputDataRadius)
        layoutContainer.addView(userInputDataTime)

        return AlertDialog.Builder(contextCall)
            .setTitle(R.string.infoCreateQRTitle)
            .setMessage(R.string.infoMessageQRCreator)
            .setView(layoutContainer)
            .setPositiveButton("Создать") { it, _ ->
                if (!userInputDataRadius.text.isNullOrEmpty()
                    && !userInputDataTime.text.isNullOrEmpty())
                    try
                    {
                        radius =
                            userInputDataRadius.text?.toString()?.toInt()
                                ?: return@setPositiveButton
                        timeActive =
                            userInputDataTime.text?.toString()?.toLong()
                    }
                    catch (ex: ClassCastException)
                    {
                        Toast.makeText(
                            contextCall
                            , R.string.errorInputRadius
                            , Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                this.invoke(Pair(radius, timeActive))
                it.cancel()
            }
            .setNegativeButton("Отмена") {
                    it, _ ->
                it.cancel()
            }.create()
    }
}