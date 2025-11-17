import SwiftUI
import Shared

/// Submit URL view with validation and progress tracking
struct SubmitURLView: View {
    @ObservedObject var viewModel: SubmitURLViewModelWrapper
    let onBack: () -> Void
    let onSuccess: (Int32) -> Void

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                if viewModel.state.requestStatus == nil {
                    // URL Input Form
                    URLInputForm(viewModel: viewModel)
                } else {
                    // Processing Status
                    ProcessingStatusView(
                        requestStatus: viewModel.state.requestStatus,
                        onCancel: { viewModel.cancelRequest() },
                        onViewSummary: {
                            if let summaryId = viewModel.state.summaryId {
                                onSuccess(summaryId)
                            }
                        },
                        onSubmitAnother: { viewModel.reset() }
                    )
                }
            }
            .padding()
            .navigationTitle("Submit URL")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: onBack) {
                        Image(systemName: "xmark")
                    }
                }
            }
        }
    }
}

/// URL input form
struct URLInputForm: View {
    @ObservedObject var viewModel: SubmitURLViewModelWrapper

    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Text("Enter the URL of an article you want to summarize")
                .font(.body)

            TextField("Article URL", text: Binding(
                get: { viewModel.state.url },
                set: { viewModel.onUrlChange($0) }
            ))
            .textFieldStyle(.roundedBorder)
            .autocapitalization(.none)
            .keyboardType(.URL)
            .disabled(viewModel.state.isSubmitting)

            if let validationError = viewModel.state.validationError {
                Text(validationError)
                    .font(.caption)
                    .foregroundColor(.red)
            }

            Button(action: { viewModel.submitUrl() }) {
                if viewModel.state.isSubmitting {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                } else {
                    Text("Submit URL")
                }
            }
            .frame(maxWidth: .infinity)
            .padding()
            .background(viewModel.state.url.isEmpty || viewModel.state.isSubmitting ? Color.gray : Color.accentColor)
            .foregroundColor(.white)
            .cornerRadius(10)
            .disabled(viewModel.state.url.isEmpty || viewModel.state.isSubmitting)

            // How it works
            VStack(alignment: .leading, spacing: 12) {
                Text("How it works")
                    .font(.headline)

                VStack(alignment: .leading, spacing: 8) {
                    InfoRow(number: 1, text: "Enter a URL to any web article")
                    InfoRow(number: 2, text: "Our AI will download and analyze the content")
                    InfoRow(number: 3, text: "Get a concise summary with key ideas")
                    InfoRow(number: 4, text: "Processing typically takes 30-60 seconds")
                }
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(10)

            Spacer()
        }
    }
}

struct InfoRow: View {
    let number: Int
    let text: String

    var body: some View {
        HStack(alignment: .top) {
            Text("\(number).")
                .fontWeight(.bold)
            Text(text)
                .font(.caption)
        }
    }
}

/// Processing status view
struct ProcessingStatusView: View {
    let requestStatus: RequestStatus?
    let onCancel: () -> Void
    let onViewSummary: () -> Void
    let onSubmitAnother: () -> Void

    var body: some View {
        VStack(spacing: 24) {
            if requestStatus == .completed {
                Text("Summary Ready!")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.green)
            } else if requestStatus == .failed || requestStatus == .cancelled {
                Text(requestStatus == .cancelled ? "Processing Cancelled" : "Processing Failed")
                    .font(.title)
                    .fontWeight(.bold)
                    .foregroundColor(.red)
            } else {
                Text("Processing Your Article")
                    .font(.title)
                    .fontWeight(.bold)
            }

            // Progress stages
            if let status = requestStatus {
                ProgressStagesView(status: status)
            }

            Spacer()

            // Action buttons
            if requestStatus == .completed {
                Button(action: onViewSummary) {
                    Text("View Summary")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)

                Button(action: onSubmitAnother) {
                    Text("Submit Another URL")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            } else if requestStatus == .failed || requestStatus == .cancelled {
                Button(action: onSubmitAnother) {
                    Text("Try Again")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
            } else {
                Button(action: onCancel) {
                    Text("Cancel")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
            }
        }
    }
}

/// Progress stages indicator
struct ProgressStagesView: View {
    let status: RequestStatus

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            StageRow(title: "Submitted", isCompleted: true, isCurrent: status == .pending)
            StageRow(title: "Downloading Content", isCompleted: status.rawValue.intValue >= RequestStatus.processing.rawValue.intValue, isCurrent: status == .downloading)
            StageRow(title: "Processing Summary", isCompleted: status == .completed, isCurrent: status == .processing)
            StageRow(title: "Ready", isCompleted: status == .completed, isCurrent: false)
        }
        .padding()
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

struct StageRow: View {
    let title: String
    let isCompleted: Bool
    let isCurrent: Bool

    var body: some View {
        HStack(spacing: 12) {
            if isCompleted {
                Image(systemName: "checkmark.circle.fill")
                    .foregroundColor(.green)
            } else if isCurrent {
                ProgressView()
                    .scaleEffect(0.8)
            } else {
                Circle()
                    .fill(Color(.systemGray4))
                    .frame(width: 20, height: 20)
            }

            Text(title)
                .foregroundColor(isCompleted || isCurrent ? .primary : .secondary)
        }
    }
}
