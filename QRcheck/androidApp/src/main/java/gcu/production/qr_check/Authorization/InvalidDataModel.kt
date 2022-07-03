package gcu.production.qr_check.Authorization

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import gcu.production.qr_check.BasicActivity
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.android.R

internal interface InvalidDataModel
{
   infix fun isDataInvalid(activity: Activity)
   {
       activity.let {
           DataStorageService(it).removeAllData()
           it.finish()

           it.startActivity(
               Intent(
                   it, BasicActivity::class.java
               )
           )

           Toast.makeText(
               it
               , R.string.infoRemoveAccount
               , Toast.LENGTH_LONG
           ).show()
       }
   }
}