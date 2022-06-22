import Foundation
import SwiftUI
import shared

struct singleItemListRecord: View
{
    var userName: String
    var timeScan: String
    
    var body: some View
    {
        HStack
        {
            Text(userName)
                .padding(.horizontal, 12)
                
            Text("")
                .padding(.horizontal, 12)
                
            Text(timeScan)
                .padding(.horizontal, 12)
        }
    }
}

class ListViewModelRecords: ObservableObject
{
    @Published var listItems: [UserInputEntity] = []
    
    func addSingleItem(item: UserInputEntity)
    {
        self.listItems.append(item)
    }
}
