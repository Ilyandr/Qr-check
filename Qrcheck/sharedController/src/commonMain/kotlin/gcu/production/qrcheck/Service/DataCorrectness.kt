package gcu.production.qrcheck.Service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.jvm.JvmStatic

object DataCorrectness
{
    const val LOGIN_ACTION: Short = 107
    const val PASSWORD_ACTION: Short = 117
    const val COMMON_ACTION_REGISTER: Short = 137

    private const val REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"
    private const val REGEX_PHONE_RU = "8(?:\\d{3}){2}(?:\\d{2}){2}"

    @JvmStatic
    inline fun checkInputUserData(
        selectedAction: Short
        , vararg inputUserData: String?
        , additionalData: String? = null
        , crossinline actionForSuccess: (correctData: List<String>) -> Unit
        , crossinline actionForFault: () -> Unit) =
        GlobalScope.launch(Dispatchers.Main)
        {
            when(selectedAction)
            {
                LOGIN_ACTION ->
                    if (loginAction(inputUserData[0], additionalData))
                        actionForSuccess(listOf(inputUserData[0]!!))
                    else
                        actionForFault()

                PASSWORD_ACTION ->
                    if (passwordAction(inputUserData[0]))
                        actionForSuccess(listOf(inputUserData[0]!!))
                else
                    actionForFault()

                COMMON_ACTION_REGISTER ->
                    if (commonAction(*inputUserData))
                        actionForSuccess(
                            listOf(
                                inputUserData[0]!!
                                , inputUserData[1]!!
                                , inputUserData[2]!!
                                , inputUserData[3]!!
                                , inputUserData[4]!!))
                    else
                        actionForFault()
            }
        }

     fun loginAction(
         inputUserData: String?
         , additionalData: String? = null): Boolean =
         if (inputUserData.isNullOrEmpty())
             false
         else
             REGEX_PHONE_RU
                 .toRegex()
                 .matches("${additionalData ?: ""}$inputUserData")

    fun passwordAction(inputUserData: String?): Boolean
    {
        if (inputUserData.isNullOrEmpty()
            || inputUserData.length < 8)
            return false
        return true
    }

    fun commonAction(vararg inputUserData: String?): Boolean
    {
        var completeAction = true

        inputUserData.forEach { singleElement ->
            if (singleElement.isNullOrEmpty())
            {
                completeAction = !completeAction
                return@forEach
            }
        }
        return completeAction
    }
}