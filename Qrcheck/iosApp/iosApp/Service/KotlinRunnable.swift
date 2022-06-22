import Foundation
import shared


class KotlinRunnable: Kotlinx_coroutines_coreRunnable
{
    let action: () -> ()
    
    init (actionInit: @escaping () -> ()) {
         action = actionInit
    }
    
    func run() {
        action()
    }
}
