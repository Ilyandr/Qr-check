package gcu.production.qr_check.Service.Repository

import gcu.production.qrcheck.StructureApp.NetworkActions
import kotlinx.coroutines.Runnable

expect object NetworkConnection
{
    inline fun checkingAccessWithActions(
        crossinline actionSuccess: () -> Unit
        , crossinline actionFault: () -> Unit
        , actionsLoadingAfterAndBefore: Pair<Runnable, Runnable>? = null
        , listenerForFailConnection: NetworkActions? = null)
}