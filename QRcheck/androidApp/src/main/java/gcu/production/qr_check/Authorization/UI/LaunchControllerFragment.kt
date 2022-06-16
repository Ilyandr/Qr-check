package gcu.production.qrcheck.android.Authorization.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import gcu.production.qr_check.android.databinding.FragmentLaunchControllerBinding
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qr_check.Service.DataStorageService
import gcu.production.qr_check.android.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@DelicateCoroutinesApi
internal class LaunchControllerFragment : Fragment(), GeneralStructure
{
    private lateinit var bindingView: FragmentLaunchControllerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View
    {
        objectsInit()
        basicBehavior()
        return bindingView.root
    }

    override fun objectsInit()
    {
        this.bindingView =
            FragmentLaunchControllerBinding.inflate(layoutInflater)
    }

    override fun basicBehavior()
    {
        val dataStorageService =
            DataStorageService(requireContext())

        Executors
            .newSingleThreadScheduledExecutor()
            .schedule(
                {
                    GlobalScope.launch(Dispatchers.Main)
                    {
                        Navigation
                            .findNavController(bindingView.root)
                            .navigate(
                                if (!dataStorageService.actionWithAuth(
                                        DataStorageService.LOGIN_ID).isNullOrEmpty()
                                    && !dataStorageService.actionWithAuth(
                                        DataStorageService.PASSWORD_ID).isNullOrEmpty())
                                    (if (dataStorageService.actionWithAuth(
                                           DataStorageService.ROLE_ID)
                                       == requireContext().getString(R.string.roleID0))
                                       R.id.primaryLaunchGeneralFragmentUser
                                   else
                                       R.id.primaryLaunchGeneralFragmentAdmin)
                                else
                                    R.id.primaryLaunchAuth
                            )
                    }
                }, 50, TimeUnit.MILLISECONDS)
    }
}