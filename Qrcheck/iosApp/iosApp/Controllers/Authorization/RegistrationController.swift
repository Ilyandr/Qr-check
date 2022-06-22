import SwiftUI
import shared

struct RegistrationController: View
{
    var loginData: String
    @State var userRole: String = "USER"

    @State var showToastMsg = false
    @State var showLoadingDialog = false
    
    @State var startAdminController = false
    @State var startUserController = false
    
    @State var userName: String = ""
    @State var userJob: String = ""
    @State var organization: String = ""
    @State var password: String = ""
    
    init(loginData: String) {
        self.loginData = loginData
    }
    
    var body: some View
    {
        ZStack {
            NavigationView {
            VStack {
                Text("Регистрация")
                    .font(.title2)
                    .foregroundColor(Color.black)
                    .multilineTextAlignment(.center)
                    .padding(.top, 32.0)
                
                
                Text("Для продолжения необходимо заполнить все данные")
                    .font(.footnote)
                    .foregroundColor(Color.gray)
                    .multilineTextAlignment(.center)
                    .padding([.leading, .bottom, .trailing], 32.0)
                    .padding(.top, 1.0)
                
                VStack
                {
                    HStack
                    {
                        Text("Тип аккаунта")
                            .padding(.leading, 12)
                            .font(.system(size: 18))
                        
                        Spacer()
                        
                        Divider()
                            .frame(width: 0, height: 30, alignment: .center)
                            .background(Color.gray)
                        
                        Menu(content: {
                            Button(
                                "Сотрудник"
                                , action: { self.userRole = "USER" }
                            )
                            Button(
                                "Работодатель"
                                , action: { self.userRole = "ADMIN" }
                            )
                        }, label: {
                            Image("arrow-bottom-svgrepo-com")
                                .font(.system(size: 18))
                                .foregroundColor(Color.gray)
                                .padding(.horizontal, 12.0)
                                .frame(width: 50, height: 30, alignment: .leading)
                        })
                    }
                    .frame(width: 290, height: 50, alignment: .center)
                    .background(
                        RoundedRectangle(
                            cornerRadius: 10).stroke()
                            .background(Color.init("fff")))
                    .cornerRadius(10.0)
                    .padding(.top, -25)

                    
                    TextField("Ф.И.О", text: $userName)
                                  .padding(.leading, 12)
                                  .font(.system(size: 18))
                                  .frame(width: 290, height: 50, alignment: .center)
                                  .background(
                                      RoundedRectangle(
                                          cornerRadius: 10).stroke()
                                          .background(Color.init("fff")))
                                  .padding(.top, 2)
                              
                              TextField("Должность", text: $userJob)
                                  .padding(.leading, 12)
                                  .font(.system(size: 18))
                                  .frame(width: 290, height: 50, alignment: .center)
                                  .background(
                                      RoundedRectangle(
                                          cornerRadius: 10).stroke()
                                          .background(Color.init("fff")))
                                  .padding(.top, 2)
                              
                              
                              TextField("Организация", text: $organization)
                                  .padding(.leading, 12)
                                  .font(.system(size: 18))
                                  .frame(width: 290, height: 50, alignment: .center)
                                  .background(
                                      RoundedRectangle(
                                          cornerRadius: 10).stroke()
                                          .background(Color.init("fff")))
                                  .padding(.top, 2)
                              
                              
                              TextField("Пароль", text: $password)
                                  .padding(.leading, 12)
                                  .font(.system(size: 18))
                                  .frame(width: 290, height: 50, alignment: .center)
                                  .background(
                                      RoundedRectangle(
                                          cornerRadius: 10).stroke()
                                          .background(Color.init("fff")))
                                  .padding(.top, 2)
                }
                 .padding(.top, 24)
                 .frame(maxHeight: .infinity, alignment: .center)

                
                NavigationLink(
                    destination: GeneralAdminController()
                    , isActive: $startAdminController) {}
                             
                NavigationLink(
                    destination: GeneralUserController()
                    , isActive: $startUserController) {}
                
                
                Button(action: {
                    NetworkConnection().checkingAccessWithActions(
                        actionSuccess: registrationFirstAction
                        , actionFault: { showToastMsg = true }
                        , actionsLoadingAfterAndBefore: KotlinPair(
                            first:KotlinRunnable(
                                actionInit: { showLoadingDialog = true }
                            )
                            , second: KotlinRunnable(
                                actionInit: { showLoadingDialog = false }
                            )
                        ), listenerForFailConnection: nil
                    )
                }, label: {
                    Text("Далее")
                        .font(.callout)
                        .frame(width: 250 , height: 18, alignment: .center)
                })
                .padding()
                .background(Color.blue)
                .foregroundColor(Color.white)
                .cornerRadius(10)
                .frame(maxHeight: .infinity, alignment: .bottom)
            }
            .navigationBarHidden(true)
        }
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
        }.navigationBarHidden(true)
}
    
    
    private func registrationFirstAction()
    {
        let dataCheckData = KotlinArray<NSString>(
            size: 5,
            init: { index in password as NSString }
        )
        
        DataCorrectness().checkInputUserData(
            selectedAction: DataCorrectness().PASSWORD_ACTION
            , inputUserData: dataCheckData
            , additionalData: nil
            , actionForSuccess: self.registrationSecondAction
            , actionForFault: { showToastMsg = true }
        )
    }
    
    
    private func registrationSecondAction(inputPasswordData: [String])
    {
        self.showLoadingDialog = true

        let dataCheckData = KotlinArray<NSString>(
            size: 5,
            init: { index in inputPasswordData[0] as NSString }
        )
        
        dataCheckData.set(index: 1, value: userName as NSString)
        dataCheckData.set(index: 2, value: userJob as NSString)
        dataCheckData.set(index: 3, value: organization as NSString)
        dataCheckData.set(index: 4, value: userRole as NSString)
                
        DataCorrectness().checkInputUserData(
            selectedAction: DataCorrectness().COMMON_ACTION_REGISTER
            , inputUserData: dataCheckData
            , additionalData: nil
            , actionForSuccess: { inputDataList in
                EngineSDK
                    .shared
                    .restAPI
                    .restAuthRepository
                    .registration(
                        userOutputEntity: UserOutputEntity(
                            name: inputDataList[1]
                            , password: inputPasswordData[0]
                            , phone: self.loginData
                            , jobTitle: inputDataList[2]
                            , organization: inputDataList[3]
                            , roles: [inputDataList[4]]
                        )
                        , completionHandler: {
                            checkedData, error in
                            registrationFinallyAction(inputUserData: checkedData)
                        }
                    )
            }
            , actionForFault: {
                showToastMsg = true
                showLoadingDialog = false
            }
        )
    }
    
    private func registrationFinallyAction(inputUserData: UserInputEntity?)
    {
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
