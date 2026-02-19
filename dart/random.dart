// TODO: Make this actually random
  static int _initialSeed() => 0xCAFEBABEDEADBEEF;
  static int _initialSeed() {
    final low = (_jsMath.random() * 4294967295.0).toInt();
    final high = (_jsMath.random() * 4294967295.0).toInt();
    return ((high << 32) | low);
  }