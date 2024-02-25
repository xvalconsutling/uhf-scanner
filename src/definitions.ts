export interface UHFScannerPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
