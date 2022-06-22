import SwiftUI

struct CustomLoadingDialog: View
{
    @Binding var show: Bool
    @State var animate = false
    var placeHolder: String
    
    var body: some View
    {
        VStack(spacing: 28)
        {
            Circle()
                .stroke(
                    AngularGradient(
                        gradient: .init(
                            colors: [Color.primary, Color.primary.opacity(0)])
                        , center: .center))
                .frame(width: 125, height: 125)
                .rotationEffect(.init(degrees: animate ? 360 : 0))
                .padding(.all, 10)
            
            Text(placeHolder)
                .fontWeight(.bold)
                .padding(.all, 15)
        }
        .background(BlurView())
        .cornerRadius(12)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(.vertical, 25)
        .padding(.horizontal, 35)
        .background(
            Color.primary.opacity(0.35)
                .onTapGesture {
                    withAnimation {
                        show.toggle()
                    }
            }
        )
        .onAppear
        {
            withAnimation(
                Animation.linear(duration: 1.5)
                    .repeatForever(autoreverses: false)) {
                        animate.toggle()
            }
        }
    }
}

struct BlurView: UIViewRepresentable
{
    func makeUIView(context: Context) -> UIVisualEffectView
    {
        return UIVisualEffectView(
            effect: UIBlurEffect(
                style: .systemThinMaterial))
    }
    
    func updateUIView(
        _ uiView: UIVisualEffectView
        , context: Context) {}
}
