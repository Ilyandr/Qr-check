package gcu.production.qrcheck.android.Service.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatTextView
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.android.R

internal class CustomListViewAdapterRecord(
    private val contextCall: Context
    , private val basicInputList: List<UserInputEntity>)
    : BaseAdapter(), GeneralStructure
{
    private lateinit var singleView: View
    private lateinit var pointIdTextView: AppCompatTextView
    private lateinit var pointNameTextView: AppCompatTextView
    private lateinit var singleItemData: UserInputEntity

    override fun getCount() =
        this.basicInputList.size

    override fun getItem(position: Int) =
        this.basicInputList[position]

    override fun getItemId(position: Int) =
        position.toLong()

    override fun getView(
        position: Int
        , convertView: View?
        , parent: ViewGroup?): View
    {
        this.singleView = convertView
            ?: LayoutInflater
                .from(this.contextCall)
                .inflate(
                    R.layout.single_item_custom_list
                    , parent
                    , false)
        this.singleItemData = getItem(position)

        this.singleView.startAnimation(
            AnimationUtils.loadAnimation(
                this.contextCall
                , R.anim.general_listview_animation))

        objectsInit()
        basicBehavior()
        return this.singleView
    }

    override fun objectsInit()
    {
        this.pointIdTextView =
            this.singleView.findViewById(R.id.pointID)
        this.pointNameTextView =
            this.singleView.findViewById(R.id.pointName)
    }

    @SuppressLint("SetTextI18n")
    override fun basicBehavior()
    {
        this.pointIdTextView.text =
            this.singleItemData.name
        this.pointNameTextView.text =
            this.singleItemData.time
    }
}