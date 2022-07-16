@file:Suppress("PackageName")
package gcu.production.qr_check.Repository.Data.MVVM

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import gcu.production.qr_check.Presentation.Main.Common.BasicActivity
import gcu.production.qr_check.Service.Repository.DataStorageService
import gcu.production.qr_check.android.R

internal interface InvalidDataModel
{
   infix fun isDataInvalid(activity: Activity)
   {
       activity.let {
          DataStorageService().init(it).removeAllData()
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