import SwiftUI

struct ConfirmController: View
{
    @State var password: String = ""
    
    var body: some View
    {
        VStack
        {
            Text("Подтверждение")
                .font(.title2)
                .foregroundColor(Color.black)
                .multilineTextAlignment(.center)
                .padding(.top, 16.0)
            
            
            Text("Требуются дополнительные данные для входа в аккаунт")
                .font(.footnote)
                .foregroundColor(Color.gray)
                .multilineTextAlignment(.center)
                .padding([.leading, .bottom, .trailing], 10.0)
                .padding(.top, -8.0)
            
 
            TextField("Введите пароль", text: $password)
                .padding(.leading, 12)
                .font(.system(size: 18))
                .frame(width: 290, height: 50, alignment: .center)
                .background(
                    RoundedRectangle(
                        cornerRadius: 10).stroke()
                        .background(Color.init("fff")))
                .padding(.top, -2)
            
            
            Button(action:{
                 
             },label:{
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
}

struct ConfirmController_Previews: PreviewProvider
{
    static var previews: some View
    {
        ConfirmController()
    }
}
