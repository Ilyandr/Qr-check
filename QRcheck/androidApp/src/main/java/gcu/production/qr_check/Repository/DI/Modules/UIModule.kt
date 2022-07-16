@file:Suppress("PackageName")
package gcu.production.qr_check.Repository.DI.Modules

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import dagger.Module
import dagger.Provides
import dagger.Reusable
import gcu.production.qr_check.Presentation.CustomViews.PopupMenuCreator
import gcu.production.qr_check.android.R
import javax.inject.Singleton

@Module
internal class UIModule
{
    @Provides
    @Singleton
    fun provideAnimationSelected(context: Context): Animation =
        AnimationUtils.loadAnimation(
            context
            , R.anim.select_object
        )

    @Provides
    @Reusable
    fun providePopupMenuCreator() = PopupMenuCreator()
}