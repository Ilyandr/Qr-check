@file:Suppress("PackageName")
package gcu.production.qr_check.Presentation.CustomViews

import android.view.Menu
import android.view.View
import android.view.animation.Animation
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.PopupMenu
import gcu.production.qr_check.android.R

internal class PopupMenuCreator
{
    internal var selectRoleData: String = "USER"

    internal fun createMenuForSelectUserRole(
        viewWithShow: View
        , outputSelectedData: AppCompatTextView? = null
        , animationSelected: Animation? = null
    ) {
        val rolesMenu = PopupMenu(
            viewWithShow.context
            , viewWithShow
        )

        rolesMenu.menu.add(
            Menu.NONE, 0, 0, viewWithShow.context.getString(R.string.infoPopupMenuRole0))
        rolesMenu.menu.add(
            Menu.NONE, 1, 1, viewWithShow.context.getString(R.string.infoPopupMenuRole1))

        rolesMenu.setOnMenuItemClickListener { menuItem ->
            selectRoleData =
                when(menuItem.itemId)
                {
                    0 -> "USER"
                    1 -> "ADMIN"
                    else -> "USER"
                }

            outputSelectedData?.text =
                menuItem.title.toString()
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