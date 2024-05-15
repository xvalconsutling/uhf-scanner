import { WebPlugin } from '@capacitor/core';

import type { UHFScannerPlugin } from './definitions';

export interface UHFScannerOptions {
  readPower?: number;
  prefix?: string;
  suffix?: string;
  start?:number;
  end?:number;
}
export class UHFScannerWeb extends WebPlugin implements UHFScannerPlugin {
  async beginScan(): Promise<void> {
    console.log("beginScan");
    return Promise.resolve();
  }

  async scanDestroy(): Promise<void> {
    console.log("scanDestroy");
    return Promise.resolve();
  }

  async scanInit(options:UHFScannerOptions): Promise<void> {
      console.log("scanInit",options);
      return Promise.resolve();
  }
  async stopScan():Promise<void>{
      console.log("stopScan");
      return Promise.resolve();
  }
}
