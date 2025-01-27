import Foundation
import Capacitor
import UIKit

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CapacitorMlkitDocScannerPluginGE)
public class CapacitorMlkitDocScannerPluginGE: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CapacitorMlkitDocScannerPluginGE"
    public let jsName = "CapacitorMlkitDocScannerGE"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise)
    ]
    private let implementation = CapacitorMlkitDocScannerGE()
    
    
//will be inplemented once google release MLKIT vision for iOS
//https://developers.google.com/ml-kit/vision/doc-scanner/iOS

    @objc func hello(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
}
