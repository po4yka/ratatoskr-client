import SwiftUI
import Shared

struct SubmitURLView: View {
    // TODO: Integrate with SubmitURLViewModel
    @State private var url: String = ""
    
    var body: some View {
        VStack {
            TextField("URL", text: $url)
                .textFieldStyle(RoundedBorderTextFieldStyle())
                .padding()
            Button("Submit") {
                // Submit logic
            }
        }
        .navigationTitle("Submit URL")
    }
}