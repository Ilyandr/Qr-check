@file:Suppress("PackageName")
package gcu.production.qr_check.Repository.Data

internal interface AdapterUpdate <in T> {
    fun updateAdapter(dataList: List<T>)
}