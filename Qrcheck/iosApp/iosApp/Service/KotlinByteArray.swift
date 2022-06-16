import Foundation
import shared

public func encodeString(inputData: String) -> KotlinByteArray
{
    let swiftByteArray = [UInt8](inputData.utf8)
    
    let intArray : [Int8] = swiftByteArray.map {
        Int8(bitPattern: $0)
    }
    
    let kotlinByteArray: KotlinByteArray =
      KotlinByteArray.init(
        size: Int32(
            swiftByteArray.count))
    
    for (index, element) in intArray.enumerated() {
        kotlinByteArray.set(
            index: Int32(index)
            , value: element)
    }
    
    return kotlinByteArray
}
