import Foundation
import SwiftUI
import shared

struct singleItemListPoint: View
{
    var pointId: Int
    var pointDateCreate: String
    
    var body: some View
    {
        HStack
        {
            Text("\(pointId)")
                .padding(.horizontal, 12)
                
            Spacer()
                
            Text(pointDateCreate)
                .padding(.horizontal, 12)
        }
    }
}

class ListViewModel: ObservableObject
{
    @Published var listItems: [DataPointInputEntity] = []
    
    func addSingleItem(item: DataPointInputEntity)
    {
        self.listItems.append(item)
    }
}
