import SwiftUI
import Shared

struct SummaryListView: View {
    // TODO: Integrate with SummaryListViewModel from Shared via SKIE
    
    var body: some View {
        NavigationView {
            List {
                Text("Summary 1")
                Text("Summary 2")
            }
            .navigationTitle("Summaries")
        }
    }
}