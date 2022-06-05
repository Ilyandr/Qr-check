package gcu.production.qrcheck.android.GeneralAppUI

import android.content.Context
import android.view.Menu
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.PopupMenu
import gcu.production.qrcheck.android.R

internal object PopupMenuCreator
{
    internal fun createMenuForSelectUserRole(
        viewWithShow: View
      , animationSelected: Animation? = null
      , actionForChange: (setNewValue: String) -> Unit)
    {
        val rolesMenu = PopupMenu(
            viewWithShow.context, viewWithShow)

        rolesMenu.menu.add(
            Menu.NONE, 0, 0, viewWithShow.context.getString(R.string.infoPopupMenuRole0))
        rolesMenu.menu.add(
            Menu.NONE, 1, 1, viewWithShow.context.getString(R.string.infoPopupMenuRole1))

        rolesMenu.setOnMenuItemClickListener { menuItem ->
            actionForChange(when(menuItem.itemId)
            {
                0 -> "USER"
                1 -> "ADMIN"
                else -> "USER"
            })
            false
        }

        viewWithShow.setOnClickListener {
            animationSelected?.let { anim ->
                it.startAnimation(anim)
            }
            rolesMenu.show()
        }
    }
}