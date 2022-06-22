import Foundation
import SwiftUI
import shared

struct singleItemListPoint: View
{
    var pointId: Int64
    var pointDateCreate: String
    
    var body: some View
    {
        NavigationLink(
            destination: ListAllRecordsController(pointId: self.pointId)
            , label: {
                HStack
                {
                    Text("\(pointId)")
                        .padding(.horizontal, 12)
                        
                    Text("")
                        .padding(.horizontal, 12)
                        
                    Text(pointDateCreate)
                        .padding(.horizontal, 12)
                }
            }
        )
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
