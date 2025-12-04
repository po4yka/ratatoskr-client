import SwiftUI
import Shared

struct SearchView: View {
    @State private var query: String = ""
    
    var body: some View {
        VStack {
            TextField("Search", text: $query)
                .padding()
            List {
                Text("Result 1")
            }
        }
        .navigationTitle("Search")
    }
}