package gcu.production.qrcheck.android.Main.Admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import gcu.production.qrcheck.AppEngine.EngineSDK
import gcu.production.qrcheck.RestAPI.Features.RestInteraction.restAPI
import gcu.production.qrcheck.RestAPI.Models.User.UserInputEntity
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.android.Authorization.Base64Encoder.encodeAuthDataToBase64Key
import gcu.production.qrcheck.android.GeneralAppUI.CustomLoadingDialog
import gcu.production.qrcheck.android.Main.Admin.GeneralAppFragmentAdmin.Companion.DATA_SELECT_KEY
import gcu.production.qrcheck.android.R
import gcu.production.qrcheck.android.Service.Adapters.CustomListViewAdapterRecord
import gcu.production.qrcheck.android.Service.SharedPreferencesAuth
import gcu.production.qrcheck.android.databinding.FragmentListAllRecordsBinding
import kotlinx.coroutines.*

@DelicateCoroutinesApi
internal class ListAllRecordsFragment : Fragment(), GeneralStructure
{
    private lateinit var viewBinding: FragmentListAllRecordsBinding
    private lateinit var loadingDialog: CustomLoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        objectsInit()
        basicBehavior()
        return this.viewBinding.root
    }

    override fun objectsInit()
    {
        this.viewBinding =
            FragmentListAllRecordsBinding.inflate(layoutInflater)
        this.loadingDialog =
            CustomLoadingDialog(requireActivity())
    }

    override fun basicBehavior()
    {
        inflateListView()

        this.viewBinding.btnShowQR.setOnClickListener {
            it.startAnimation(
                AnimationUtils.loadAnimation(
                    requireContext()
                    , R.anim.select_object)
            )

            val bundleSendData = Bundle()

            bundleSendData.putLong(
                DATA_SELECT_KEY
                , requireArguments().getLong(DATA_SELECT_KEY)
            )

            Navigation
                .findNavController(
                    this.viewBinding.root)
                .navigate(
                    R.id.actionLaunchFragmentShowQR
                    , bundleSendData)
        }
    }

    private fun inflateListView()
    {
        this.loadingDialog.startLoadingDialog()

        val dataInflateForListRecord: Deferred<List<UserInputEntity>> =
            GlobalScope.async(Dispatchers.IO)
            {
                EngineSDK.restAPI.
                restRecordRepository
                    .getAllRecord(
                        SharedPreferencesAuth(
                            requireContext()).encodeAuthDataToBase64Key()
                        , requireArguments().getLong(DATA_SELECT_KEY))
            }

        GlobalScope.launch(Dispatchers.Main)
        {
            this@ListAllRecordsFragment
                .viewBinding
                .listAllRecord
                .adapter = CustomListViewAdapterRecord(
                requireContext()
                , dataInflateForListRecord.await())

            loadingDialog.stopLoadingDialog()
        }
    }
}