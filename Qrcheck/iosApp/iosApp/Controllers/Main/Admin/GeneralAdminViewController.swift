import SwiftUI
import shared


struct GeneralAdminController: View
{
    @State private var showToastMsg = false
    @ObservedObject var viewModel = ListViewModel()

    
    func networkFaultConnection()
    {
       showToastMsg = false
    }
    
    init()
    {
       objectsInit()
    }
    
    func authAction() -> String
    {
      return Base64Factory()
            .createEncoder()
            .encodeToString(
                src: encodeString(
                    inputData:"9187712840:12345678"
                )
            )
    }
    
    func basicBehavior() {}
    
    func objectsInit()
    {
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
                                pointId: Int(truncating: item.id!)
                                , pointDateCreate: item.createTime!
                            )
                        }
                    }.padding(.top, 48)
                }
                .edgesIgnoringSafeArea(.all)
            }
            .navigationBarBackButtonHidden(true)
        }
        .toast(
        message: "Ошибка - введены неверные данные или отсутствует интернет-соеденение"
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

