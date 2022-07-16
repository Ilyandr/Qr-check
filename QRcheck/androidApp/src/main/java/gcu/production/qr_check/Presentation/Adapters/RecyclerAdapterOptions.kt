@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Adapters

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gcu.production.qr_check.Repository.Data.AdapterUpdate

internal infix fun RecyclerView.recyclerViewOptions(
    adapter: RecyclerView.Adapter<*>
) {
    val linearLayoutManager =
        LinearLayoutManager(this.context)
    linearLayoutManager.orientation =
        LinearLayoutManager.VERTICAL

    this.layoutManager = linearLayoutManager
    this.adapter = adapter

    if (adapter is AdapterUpdate<*>)
        adapter.updateAdapter(emptyList())
}