import SwiftUI
import shared

struct ListAllRecordsController: View
{
    @State private var pointId: Int64
    @State private var showToastMsg = false
    @State private var showLoadingDialog = false
    @State private var startShowQRCode = false
    
    @ObservedObject var viewModel = ListViewModelRecords()
    private let dataStorageService = DataStorageService()

    
    init(pointId: Int64) {
        self.pointId = pointId
    }
    
    func networkFaultConnection() {
       showToastMsg = false
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
        
    func objectsInit()
    {
        NetworkConnection
            .shared
            .checkingAccessWithActions(
                actionSuccess: {
                    EngineSDK()
                        .restAPI
                        .restRecordRepository
                        .getAllRecord(
                            userAuthKey: authAction()
                            , pointId: self.pointId
                            , completionHandler: {
                                response, error in
                                response?.forEach(
                                    { singleItem in
                                        viewModel.addSingleItem(
                                            item: singleItem as! UserInputEntity)
                                    }
                                )
                            })
                }
                , actionFault: {
                    self.showLoadingDialog = false
                }
                , actionsLoadingAfterAndBefore: KotlinPair(
                    first: KotlinRunnable(
                        actionInit: { self.showLoadingDialog = true}
                    )
                    , second: KotlinRunnable(
                        actionInit: { self.showLoadingDialog = false})
                )
                , listenerForFailConnection: nil
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
                       List()
                       {
                           ForEach(viewModel.listItems, id: \.self)
                           { item in
                               singleItemListRecord(
                                userName: item.name!
                                , timeScan: item.time!
                             )
                           }
                       }.padding(.top, 6.0)
                       
                       NavigationLink(
                           destination: ShowQRController(
                            selectPointId: self.pointId)
                           , isActive: $startShowQRCode) {}
                       
                       TabView
                       {
                           Button(
                            action: { self.startShowQRCode = true }
                            , label: {
                                VStack
                                {
                                    Image("qrCode")
                                        .resizable()
                                        .frame(minWidth: 24, idealWidth: 24, maxWidth: 24, minHeight: 24, idealHeight: 24, maxHeight: 24, alignment: .center)
                                        .scaledToFit()
                                    
                                    Text("Данные")
                                        .font(.system(size: 10))
                                        .padding(.bottom, 8)
                                }
                            })
                           .scaleEffect(0.85)
                               .padding(.top, 42)
                               .navigationBarHidden(true)
                   }.frame(height: 40)
                }
               }
               if showLoadingDialog {
                   CustomLoadingDialog(
                       show: $showLoadingDialog
                       , placeHolder: "Загрузка данных"
                   )
               }
           }
           .onAppear { objectsInit() }
           .navigationBarHidden(true)
           .toast(
           message: "Ошибка - отсутствует интернет-соеденение"
           , isShowing: $showToastMsg
           , duration: Toast.short)
       }
}

struct ListAllRecordsController_Previews: PreviewProvider
{
    static var previews: some View
    {
        ListAllRecordsController(pointId: 0).body
    }
}
