import Foundation
import SwiftUI


func generateBarcode(str: String) -> Data
{
    let filter = CIFilter(name: "CIQRCodeGenerator")
    let data = str.data(using: .ascii, allowLossyConversion: false)
    filter?.setValue(data, forKey: "inputMessage")
    let transform = CGAffineTransform(scaleX: 5, y: 5)

    let image = filter?.outputImage?.transformed(by: transform)
    let uiImage = UIImage(ciImage: image!)

    return uiImage.pngData()!
}
