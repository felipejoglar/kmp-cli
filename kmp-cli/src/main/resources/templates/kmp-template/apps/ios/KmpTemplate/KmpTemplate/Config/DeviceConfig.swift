import Foundation
import KmpTemplateKit
import UIKit

class IosDeviceConfig: DeviceConfig {
    var appVersion: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? "Unknown"
    }

    var osVersion: String {
        UIDevice.current.systemVersion
    }

    var platform: String {
        UIDevice.current.systemName
    }

    var device: String {
        UIDevice.current.name
    }

    var isDebug: Bool {
        #if DEBUG
        return true
        #else
        return false
        #endif
    }
}
