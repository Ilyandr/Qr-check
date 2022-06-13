import SwiftUI

struct RegistrationController: View
{
    @State var userName: String = ""
    @State var userJob: String = ""
    @State var organization: String = ""
    @State var password: String = ""
    
    var body: some View
    {
        NavigationView
        {
        VStack
        {
            Text("Регистрация")
                .font(.title2)
                .foregroundColor(Color.black)
                .multilineTextAlignment(.center)
                .padding(.top, -90.0)
            
            
            Text("Для продолжения необходимо заполнить все данные")
                .font(.footnote)
                .foregroundColor(Color.gray)
                .multilineTextAlignment(.center)
                .padding([.leading, .bottom, .trailing], 32.0)
                .padding(.top, -75.0)
            
            
            HStack
            {
                Text("Тип аккаунта")
                    .multilineTextAlignment(.leading)
                    .foregroundColor(Color.gray)
                    .font(.system(size: 18))
                    .frame(width: 200, height: 50, alignment: .center)
                
                
                Divider()
                    .frame(width: 0, height: 30, alignment: .center)
                    .background(Color.gray)

                
                Button(action: {
                    
                }, label:
                {
                    Image("arrow-bottom-svgrepo-com")
                        .padding(.trailing)
                })
                .padding(.horizontal)
            }
            .background(
                RoundedRectangle(
                    cornerRadius: 8).stroke()
                    .background(Color.init("fff")))
            .cornerRadius(8.0)
            .padding(.bottom, 20)
            .frame(width: 300 , height: 30, alignment: .center)
 
            
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
            
            
            NavigationLink(destination: RegistrationController())
            {
                 Text("Войти")
                     .font(.callout)
                     .frame(width: 250 , height: 18, alignment: .center)
             }
            .padding()
            .background(Color.blue)
            .foregroundColor(Color.white)
            .cornerRadius(10)
            .frame(maxHeight: .infinity, alignment: .bottom)
            .padding()
        }
        }
    }
}

struct RegistrationController_Previews: PreviewProvider
{
    static var previews: some View
    {
        RegistrationController()
    }
}
