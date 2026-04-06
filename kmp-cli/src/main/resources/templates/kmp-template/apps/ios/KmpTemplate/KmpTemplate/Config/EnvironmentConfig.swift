import Foundation
import KmpTemplateKit

enum EnvironmentConfig {
    static var current: Environment {
        #if DEV
        return .dev
        #elseif BETA
        return .beta
        #elseif PROD
        return .prod
        #else
        return .dev
        #endif
    }
}
