import SwiftUI
import shared

struct ConfirmController: View
{
    @State var loginData: String
    @State var password: String = ""
    @State var showToastMsg = false
    @State var showLoadingDialog = false
    
    @State var startAdminController = false
    @State var startUserController = false
    
    var body: some View
    {
        ZStack {
            NavigationView {
            VStack {
                Text("Подтверждение")
                    .font(.title2)
                    .foregroundColor(Color.black)
                    .multilineTextAlignment(.center)
                    .padding(.top, 32.0)
                
                
                Text("Требуются дополнительные данные для входа в аккаунт")
                    .font(.footnote)
                    .foregroundColor(Color.gray)
                    .multilineTextAlignment(.center)
                    .padding([.leading, .bottom, .trailing], 32.0)
                    .padding(.top, 1.0)
                
                
                TextField("Введите пароль", text: $password)
                    .padding(.leading, 12)
                    .font(.system(size: 18))
                    .frame(width: 290, height: 50, alignment: .center)
                    .background(
                        RoundedRectangle(
                            cornerRadius: 10).stroke()
                            .background(Color.init("fff")))
                .cornerRadius(10.0)
                .padding(.bottom, 20)
                .padding(.top, -25)
                
                NavigationLink(
                    "", destination: GeneralAdminController()
                    , isActive: $startAdminController)
                             
                NavigationLink("",
                    destination: GeneralUserController()
                    , isActive: $startUserController)
                
                Button(action: {
                    NetworkConnection
                        .shared
                        .checkingAccessWithActions(
                        actionSuccess: { authInitAction() }
                        , actionFault: { showToastMsg = true }
                        , actionsLoadingAfterAndBefore: KotlinPair(
                            first:KotlinRunnable(
                                actionInit: { showLoadingDialog = true }
                            )
                            , second: KotlinRunnable(
                                actionInit: {showLoadingDialog = false}
                            )
                        ), listenerForFailConnection: nil
                    )
                }
                        , label: {
                    Text("Войти")
                        .font(.callout)
                        .frame(width: 250 , height: 18, alignment: .center)
                })
                .padding()
                .background(Color.blue)
                .foregroundColor(Color.white)
                .cornerRadius(10)
                .frame(maxHeight: .infinity, alignment: .bottom)
                .padding()
            }
            .navigationBarHidden(true)
        } .navigationBarHidden(true)
            .toast(
                message: "Ошибка - введены неверные данные или отсутствует интернет-соеденение"
                , isShowing: $showToastMsg
                , duration: Toast.short)
            
            if showLoadingDialog {
                CustomLoadingDialog(
                    show: $showLoadingDialog
                    , placeHolder: "Загрузка данных"
                )
            }
        }
  }
    
    private func authInitAction()
    {
        let dataCheckData = KotlinArray<NSString>(
            size: 1,
            init: { index in self.password as NSString }
        )
        
        DataCorrectness.shared.checkInputUserData(
            selectedAction: DataCorrectness().PASSWORD_ACTION
            , inputUserData: dataCheckData
            , additionalData: nil
            , actionForSuccess: { correctPassword in
                self.showLoadingDialog = true
                
                EngineSDK
                    .shared
                    .restAPI
                    .restAuthRepository
                    .login(userLoginKey: Base64Factory()
                        .createEncoder()
                        .encodeToString(
                            src: encodeString(
                                inputData: "\(self.loginData):\(correctPassword[0])"
                            )
                        )
                        , completionHandler: {
                            responseData, error in
                               authFinallyAction(inputUserData: responseData)
                    }
                )
            }
            , actionForFault: { self.showToastMsg = true }
        )
    }
    
    private func authFinallyAction(inputUserData: UserInputEntity?)
    {
        self.showLoadingDialog = false
        
        if inputUserData == nil
        {
            self.showToastMsg = true
            return
        }
        
        let dataStorageManager = DataStorageService()
        
        dataStorageManager.actionWithAuth(
            dataID: DataStorageService.companion.LOGIN_ID
            , newValue: inputUserData!.phone
        )
        
        dataStorageManager.actionWithAuth(
            dataID: DataStorageService.companion.PASSWORD_ID
            , newValue: self.password
        )
        
        dataStorageManager.actionWithAuth(
            dataID: DataStorageService.companion.ROLE_ID
            , newValue: inputUserData!.roles![0]
        )
        
        self.showLoadingDialog = false
        
        if inputUserData!.roles![0] == "USER" {
            self.startUserController = true
        }
        else {
            self.startAdminController = true
        }
    }
}

struct ConfirmController_Previews: PreviewProvider
{
    static var previews: some View
    {
        ConfirmController(loginData: "")
    }
}
