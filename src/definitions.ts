import type {PluginListenerHandle} from "@capacitor/core";

import type {UHFScannerOptions} from "./web";

export interface UHFScannerPlugin {
  addListener(eventName: 'BroadcastReceiverEvent', listenerFunc: (barcode: { result: any }) => void): PluginListenerHandle;
  addListener(eventName: 'buttonClicked', listenerFunc: (keyCode: { result: string }) => void): PluginListenerHandle;
  scanInit(options:UHFScannerOptions):Promise<void>;
  scanDestroy():Promise<void>;
  beginScan():Promise<void>;
  stopScan():Promise<void>;
}
