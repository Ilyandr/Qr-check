package gcu.production.qr_check.Service

import gcu.production.qrcheck.StructureApp.NetworkActions
import kotlinx.coroutines.Runnable

actual object NetworkConnection
{
    actual inline fun checkingAccessWithActions(
        crossinline actionSuccess: () -> Unit,
        crossinline actionFault: () -> Unit,
        actionsLoadingAfterAndBefore: Pair<Runnable, Runnable>?,
        listenerForFailConnection: NetworkActions?)
    {

    }
}