package gcu.production.qrcheck.android.Main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gcu.production.qrcheck.StructureApp.GeneralStructure
import gcu.production.qrcheck.android.R

internal class GeneralAppFragment : Fragment(), GeneralStructure
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View?
    {

        return inflater.inflate(R.layout.fragment_general_app, container, false)
    }

    override fun objectsInit()
    {

    }

    override fun basicBehavior()
    {

    }
}