import SwiftUI

@main
struct KmpTemplateApp: App {
    var body: some Scene {
        let environment = EnvironmentConfig.current
        let deviceConfig = IosDeviceConfig()

        WindowGroup {
            ContentView(environment: environment, deviceConfig: deviceConfig)
        }
    }
}
