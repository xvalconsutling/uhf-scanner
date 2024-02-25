import { WebPlugin } from '@capacitor/core';

import type { UHFScannerPlugin } from './definitions';

export class UHFScannerWeb extends WebPlugin implements UHFScannerPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
