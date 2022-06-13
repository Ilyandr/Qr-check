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
                .frame(width: 200, height: 200)
                .rotationEffect(.init(degrees: animate ? 360 : 0))
            
            Text(placeHolder)
                .fontWeight(.bold)
        }
        .padding(.vertical, 25)
        .padding(.horizontal, 35)
        .cornerRadius(20)
        .background(BlurView())
        .frame(maxWidth: .infinity, maxHeight: .infinity)
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
