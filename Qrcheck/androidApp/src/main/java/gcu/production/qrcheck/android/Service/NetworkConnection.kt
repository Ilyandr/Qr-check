package gcu.production.qrcheck.android.Service

import gcu.production.qrcheck.StructureApp.NetworkActions
import kotlinx.coroutines.*
import java.lang.Runnable
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@DelicateCoroutinesApi
internal object NetworkConnection
{
    private var executor: ScheduledExecutorService? = null
    internal var isActiveConnectionListener = (executor != null)

    @JvmStatic
    internal inline fun checkingAccessWithActions(
        crossinline actionSuccess: () -> Unit
        , crossinline actionFault: () -> Unit
        , actionsLoadingAfterAndBefore: Pair<Runnable, Runnable>? = null
        , listenerForFailConnection: NetworkActions? = null) =
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
                else throw (Exception())
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