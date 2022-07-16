package gcu.production.qr_check.Service.Repository

import gcu.production.qrcheck.StructureApp.NetworkActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

actual object NetworkConnection
{
    actual inline fun checkingAccessWithActions(
        crossinline actionSuccess: () -> Unit,
        crossinline actionFault: () -> Unit,
        actionsLoadingAfterAndBefore: Pair<Runnable, Runnable>?,
        listenerForFailConnection: NetworkActions?)
    {
         launchAction(
            actionFirst = {}
            , actionRunnable = actionsLoadingAfterAndBefore?.first
        )

        val request =
            NSMutableURLRequest(
                NSURL(string = "https://google.com/")
            )

        request.setHTTPMethod(
            HTTPMethod = "HEAD")
        request.setCachePolicy(
            cachePolicy = NSURLRequestReloadIgnoringLocalAndRemoteCacheData)
        request.setTimeoutInterval(5.0)

        NSURLConnection.sendAsynchronousRequest(
            request = request
            , queue = NSOperationQueue.currentQueue() ?: return
        )
        { response, _, _ ->
            (response as? NSHTTPURLResponse)?.let { status ->
                if (status.statusCode == 200L)
                    launchAction(
                        actionFirst = actionSuccess
                        , actionRunnable = actionsLoadingAfterAndBefore?.second
                    )
                else
                    launchAction(
                        actionFirst = actionFault
                        , actionRunnable = actionsLoadingAfterAndBefore?.second
                    )
            } ?:
            launchAction(
                actionFirst = actionFault
                , actionRunnable = actionsLoadingAfterAndBefore?.second
            )
        }
    }

    inline fun launchAction(
        actionFirst: () -> Unit
        , actionRunnable: Runnable? = null)
    {
        actionFirst.let {
            GlobalScope.launch(Dispatchers.Main) {
                it.invoke()
            }
        }
        actionRunnable?.let {
            GlobalScope.launch(Dispatchers.Main) {
                it.run()
            }
        }
    }
}