import Foundation

@objc public class UHFScanner: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
