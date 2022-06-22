import SwiftUI
import shared

struct ShowQRController: View
{
    @State private var barcodeData: Data =
    generateBarcode(str: "")
    @State private var showLoadingDialog = false
    @State private var showToastMsg = false
    
    private let selectPointId: Int64
    private let dataStorageService = DataStorageService()
    

    init(selectPointId: Int64) {
        self.selectPointId = selectPointId
    }
    
    private func objectsInit()
    {
        Timer.scheduledTimer(
            withTimeInterval: 10.0
            , repeats: true
            , block: {
                timer in
                NetworkConnection
                    .shared
                    .checkingAccessWithActions(
                        actionSuccess: {
                            EngineSDK()
                                .restAPI
                                .restPointRepository
                                .generateToken(
                                    userLoginKey: authAction()
                                    , pointId: self.selectPointId
                                    , completionHandler:
                                        { complete, error in
                                            self.barcodeData =
                                            generateBarcode(str: complete!)
                                        }
                                )
                        }
                        , actionFault: { self.showToastMsg = true }
                        , actionsLoadingAfterAndBefore: KotlinPair(
                            first: KotlinRunnable(
                                actionInit: { self.showLoadingDialog = true }
                            )
                            , second: KotlinRunnable(
                                actionInit: { self.showLoadingDialog = false })
                        )
                        , listenerForFailConnection: nil
                    )
            }
        )
    }
    
    func authAction() -> String
    {
      return Base64Factory()
            .createEncoder()
            .encodeToString(
                src: encodeString(
                    inputData: self.dataStorageService
                        .actionWithAuth(
                            dataID: DataStorageService.companion.LOGIN_ID
                            , newValue: nil
                        )! +
                    ":"
                    + self.dataStorageService
                        .actionWithAuth(
                            dataID: DataStorageService.companion.PASSWORD_ID
                            , newValue: nil
                        )!
                )
            )
    }
    
    var body: some View
    {
        ZStack
        {
            NavigationView
            {
                VStack
                {
                    Image(
                        uiImage: UIImage(
                            data: self.barcodeData)!)
                    .resizable()
                    .frame(minWidth: 100, idealWidth: 100, maxWidth: 100, minHeight: 100, idealHeight: 100, maxHeight: 100, alignment: .center)
                    .scaledToFit().navigationBarHidden(true)
                }
            }
        }.onAppear {
            objectsInit() }
    }
}

struct ShowQRController_Previews: PreviewProvider {
    static var previews: some View {
        ShowQRController(selectPointId: 0)
    }
}
