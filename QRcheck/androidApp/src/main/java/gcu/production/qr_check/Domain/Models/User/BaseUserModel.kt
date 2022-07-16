@file:Suppress("PackageName")
package gcu.production.qr_check.Domain.Models.User

import android.view.SurfaceView

internal sealed class BaseUserModel
{
     internal object DefaultState: BaseUserModel()

     internal object FaultDataState: BaseUserModel()

     internal data class ActiveDetectorState(
      val surface: SurfaceView
      ): BaseUserModel()

     internal object LoadingState: BaseUserModel()

     internal object SuccessDetectorState: BaseUserModel()

     internal object FaultDetectorState: BaseUserModel()
}