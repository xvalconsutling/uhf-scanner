import { registerPlugin } from '@capacitor/core';

import type { UHFScannerPlugin } from './definitions';

const UHFScanner = registerPlugin<UHFScannerPlugin>('UHFScanner', {
  web: () => import('./web').then(m => new m.UHFScannerWeb()),
});

export * from './definitions';
export { UHFScanner };
