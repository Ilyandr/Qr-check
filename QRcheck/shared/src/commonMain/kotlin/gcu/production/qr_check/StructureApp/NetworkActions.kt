package gcu.production.qrcheck.StructureApp

import kotlinx.coroutines.Job

interface NetworkActions
{
    fun networkFaultConnection()
    fun launchWithCheckNetworkConnection(): Job?
}