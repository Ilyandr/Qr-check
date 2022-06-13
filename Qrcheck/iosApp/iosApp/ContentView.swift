import SwiftUI
import shared

struct ContentView: View
{
    @State var username: String = ""
    @State var showLoadingDialog = false
    
    @State var startNavigateRegister = false
    @State var startNavigateConfirm = false
    @State var showToastMsg = false
    
    var body: some View
    {
        ZStack {
            NavigationView {
            VStack {
                Text("Авторизация")
                    .font(.title2)
                    .foregroundColor(Color.black)
                    .multilineTextAlignment(.center)
                    .padding(.top, -90.0)
                
                
                Text("Введённый вами номер телефона будет использован для дальнейшнего входа")
                    .font(.footnote)
                    .foregroundColor(Color.gray)
                    .multilineTextAlignment(.center)
                    .padding([.leading, .bottom, .trailing], 32.0)
                    .padding(.top, -75.0)
                
                HStack
                {
                    Button(action: {}, label:
                    {
                        Text("+7")
                            .font(.system(size: 18))
                            .foregroundColor(Color.gray)
                            .padding(.horizontal, 12.0)
                            .frame(width: 50, height: 30, alignment: .leading)
                    })
                    
                
                    Divider()
                        .frame(width: 0, height: 30, alignment: .center)
                        .background(Color.gray)
                    
                    
                    TextField("Номер телефона", text: $username)
                        .padding(.leading, 7)
                        .font(.system(size: 18))
                        .frame(width: 220, height: 50, alignment: .center)
                }
                .background(
                    RoundedRectangle(
                        cornerRadius: 10).stroke()
                        .background(Color.init("fff")))
                .cornerRadius(10.0)
                .padding(.bottom, 20)
                .padding(.top, -25)

                
                NavigationLink(destination: ConfirmController(), isActive: $startNavigateConfirm) {}
                
                NavigationLink(destination: RegistrationController(), isActive: $startNavigateRegister) {}
                
                
                Button(action: { launchAuth(inputLogin: username)}
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
            }
            .toast(
                message: "Ошибка - введены неверные данные или отсутствует интернет-соеденение"
                , isShowing: $showToastMsg
                , duration: Toast.short)
            
            if showLoadingDialog
            {
            CustomLoadingDialog(show: $showLoadingDialog, placeHolder: "Загрузка данных")
            }
    }.edgesIgnoringSafeArea(.all)
    }
    
    private func launchAuth(inputLogin: String)
    {
        let dataCheckData = KotlinArray<NSString>(
            size: 1,
            init: { index in inputLogin as NSString })
                                   
        DataCorrectness().checkInputUserData(
            selectedAction: DataCorrectness().LOGIN_ACTION
            , inputUserData: dataCheckData
            , additionalData: "8"
            , actionForSuccess: { data in
                showLoadingDialog = true
                
                EngineSDK
                    .shared
                    .restAPI
                    .restAuthRepository
                    .existUser(
                        userLoginData: inputLogin
                        , completionHandler: { response, error  in
                    if ((response) != nil)
                    {
                        if response!.boolValue {
                            startNavigateConfirm = true
                        }
                        else {
                            startNavigateRegister = true
                        }
                    }
                    else {
                        startNavigateRegister = true
                    }
                    showLoadingDialog = false
                })            }
            , actionForFault: { showToastMsg = true }
        )
    }
}


struct ContentView_Previews: PreviewProvider
{
	static var previews: some View
    {
		ContentView()
	}
}
