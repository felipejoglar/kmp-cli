import SwiftUI
import KmpTemplateKit
import UIKit

struct ComposeView: UIViewControllerRepresentable {
    let environment: KmpTemplateKit.Environment
    let deviceConfig: KmpTemplateKit.DeviceConfig

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(environment: environment, deviceConfig: deviceConfig)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    let environment: KmpTemplateKit.Environment
    let deviceConfig: KmpTemplateKit.DeviceConfig

    var body: some View {
        ComposeView(environment: environment, deviceConfig: deviceConfig)
            .ignoresSafeArea()
            .ignoresSafeArea(.keyboard)  // Compose has own keyboard handler
    }
}
