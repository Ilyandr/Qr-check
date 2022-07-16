@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import gcu.production.qr_check.Presentation.Main.Common.BasicActivity
import gcu.production.qr_check.android.databinding.SingleItemCustomListBinding
import gcu.production.qrcheck.RestAPI.Models.Point.DataPointInputEntity
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private typealias DelegateFunction =
        ((dataBundle: Bundle) -> Unit)?

internal class PointsAdapter <T> (
    private val basicInputList: List<T>,
    var actionClick: DelegateFunction = null
) : RecyclerView.Adapter<PointsAdapter<T>.MyViewHolder>()
{
    private lateinit var binding: SingleItemCustomListBinding

    inner class MyViewHolder(
        itemView: View,
    ) : RecyclerView.ViewHolder(itemView)
    {
        fun drawItem(singleItem: T)
        {
            when (singleItem)
            {
                is DataPointInputEntity ->
                {
                    binding.pointID.text =
                        singleItem.id.toString()
                    binding.pointName.text =
                        singleItem.createTime.toString()
                }

                is UserInputEntity ->
                {
                    binding.pointID.text =
                        singleItem.name
                    binding.pointName.text =
                        singleItem.time
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup
        , viewType: Int
    ): MyViewHolder
    {
        this.binding =
            SingleItemCustomListBinding.inflate(
                LayoutInflater.from(parent.context)
                , parent
                , false
            )
        return MyViewHolder(this.binding.root)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder
        , position: Int) =
        this.basicInputList[position].let { selectedData ->
            holder.drawItem(selectedData)

            holder.itemView.setOnClickListener {
                MainScope().launch(Dispatchers.Main)
                {
                    when (selectedData)
                    {
                        is DataPointInputEntity ->
                        {
                            selectedData.id?.let { args ->
                                val bundleSendData = Bundle()

                                bundleSendData.putLong(
                                    BasicActivity.DATA_SELECT_KEY,
                                    args
                                )
                                this@PointsAdapter
                                    .actionClick
                                    ?.invoke(bundleSendData)
                            }
                        }
                    }
                }
            }
        }

    override fun getItemCount() =
        this.basicInputList.size
}