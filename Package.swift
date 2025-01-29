// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CapacitorMlkitDocScannerGEV1",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "CapacitorMlkitDocScannerGEV1",
            targets: ["CapacitorMlkitDocScannerPluginGEV1"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "CapacitorMlkitDocScannerPluginGEV1",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CapacitorMlkitDocScannerPluginGEV1"),
        .testTarget(
            name: "CapacitorMlkitDocScannerPluginGEV1Tests",
            dependencies: ["CapacitorMlkitDocScannerPluginGEV1"],
            path: "ios/Tests/CapacitorMlkitDocScannerPluginGEV1Tests")
    ]
)