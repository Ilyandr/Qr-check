import SwiftUI
import shared


@main
struct iOSApp: App
{
    init() { projectInit() }
    
    var body: some Scene
    {
		WindowGroup
        {
			ContentView()
		}
	}
}


public func projectInit()
{
    EngineSDK().doInit(configuration: Configuration(platformType: PlatformType.IOS(versionPlatform: "1.0", buildNumber: "1")))
}
