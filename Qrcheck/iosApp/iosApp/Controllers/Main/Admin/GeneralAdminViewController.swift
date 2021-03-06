import SwiftUI
import shared


struct GeneralAdminController: View
{
    @State private var showToastMsg = false
    @State private var showLoadingDialog = false
    @ObservedObject var viewModel = ListViewModel()
    private let dataStorageService = DataStorageService()

    
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
                        .restPointRepository
                        .getAllPoint(
                            userLoginKey: authAction()
                            , completionHandler: {
                                response, error in
                                response?.forEach(
                                    { singleItem in
                                        viewModel.addSingleItem(
                                            item: singleItem as! DataPointInputEntity)
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
                               singleItemListPoint(
                                   pointId: Int64(truncating: item.id!)
                                   , pointDateCreate: item.createTime!
                               )
                           }
                       }.padding(.top, 6.0)
                       
                       TabView
                       {
                           HStack
                           {
                               Button(
                                action: { objectsInit() }
                                , label: {
                                    VStack
                                    {
                                        Image("list")
                                            .resizable()
                                            .frame(minWidth: 24, idealWidth: 24, maxWidth: 24, minHeight: 24, idealHeight: 24, maxHeight: 24, alignment: .center)
                                            .scaledToFit()
                                        
                                        Text("????????????")
                                            .font(.system(size: 10))
                                            .padding(.bottom, 8)
                                    }
                                }).scaleEffect(0.85)
                                   .padding(.top, 42)
                           
                               Divider().padding(.horizontal, 30)

                               Button(
                                action: {}
                                , label: {
                                    VStack
                                    {
                                        Image("add")
                                            .resizable()
                                            .frame(minWidth: 24, idealWidth: 24, maxWidth: 24, minHeight: 24, idealHeight: 24, maxHeight: 24, alignment: .center)
                                            .scaledToFit()
                                        
                                        Text("??????????????")
                                            .font(.system(size: 10))
                                            .padding(.bottom, 8)
                                    }
                                }).scaleEffect(0.85)
                                   .padding(.top, 42)
                               
                               Divider().padding(.horizontal, 30)
                               
                           Button(
                            action: {}
                            , label: {
                                VStack
                                {
                                    Image("settings")
                                        .resizable()
                                        .frame(minWidth: 24, idealWidth: 24, maxWidth: 24, minHeight: 24, idealHeight: 24, maxHeight: 24, alignment: .center)
                                        .scaledToFit()
                                    
                                    Text("??????????")
                                        .font(.system(size: 10))
                                        .padding(.bottom, 8)
                                }
                            }).scaleEffect(0.85)
                               .padding(.top, 42)
                           }.navigationBarHidden(true)
                   }.frame(height: 40)
                }
               }
               if showLoadingDialog {
                   CustomLoadingDialog(
                       show: $showLoadingDialog
                       , placeHolder: "???????????????? ????????????"
                   )
               }
           }
           .onAppear { objectsInit() }
           .navigationBarHidden(true)
           .toast(
           message: "???????????? - ?????????????????????? ????????????????-????????????????????"
           , isShowing: $showToastMsg
           , duration: Toast.short)
       }
}

    struct GeneralAdminViewController_Previews: PreviewProvider
    {
        static var previews: some View
        {
            GeneralAdminController().body
        }
    }


