@file:Suppress("PackageName")
package gcu.production.qr_check.Service.Repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import gcu.production.qrcheck.StructureApp.NetworkActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

typealias UnitFunction = () -> Unit
typealias PairRunnable = Pair<Runnable, Runnable>?

actual object NetworkConnection
{
    var executor: ScheduledExecutorService? = null

    @JvmStatic
    actual inline fun checkingAccessWithActions(
        crossinline actionSuccess: UnitFunction
        , crossinline actionFault: UnitFunction
        , actionsLoadingAfterAndBefore: PairRunnable
        , listenerForFailConnection: NetworkActions?)
    {
        GlobalScope.launch(Dispatchers.IO)
        {
            try
            {
                GlobalScope.launch(Dispatchers.Main) {
                    if (executor == null)
                        actionsLoadingAfterAndBefore?.first?.run()
                }

                val urlConnection = URL("https://www.google.com")
                    .openConnection() as? HttpURLConnection
                    ?: throw (Exception())

                urlConnection.setRequestProperty(
                    "User-Agent", "Test")
                urlConnection.setRequestProperty(
                    "Connection", "close")

                urlConnection.connectTimeout = 1500
                urlConnection.connect()
                delay(1600)

                if (urlConnection.responseCode == 200)
                    GlobalScope.launch(Dispatchers.Main)
                    {
                        executor?.shutdown()
                        executor = null

                        actionSuccess.invoke()
                        actionsLoadingAfterAndBefore?.second?.run()
                    }
                else
                    throw (Exception())
            }
            catch (e: Exception)
            {
                GlobalScope.launch(Dispatchers.Main)
                {
                    actionFault.invoke()

                    listenerForFailConnection?.let {
                        if (executor == null)
                        {
                            executor =
                                Executors.newSingleThreadScheduledExecutor()

                            executor!!.scheduleAtFixedRate(
                                { it.launchWithCheckNetworkConnection() }
                                , 0
                                , 2000
                                , TimeUnit.MILLISECONDS
                            )
                        }
                    } ?: actionsLoadingAfterAndBefore?.second?.run()
                }
            }
        }
    }
}

class NetworkChangeReceiver(
    private val actionReceiver: UnitFunction
) : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent?) =
        this.actionReceiver.invoke()
}
