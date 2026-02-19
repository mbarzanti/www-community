// https://www.zellic.io/blog/proton-dart-flutter-csprng-prng/#2--proton-wallet-encryption-vulnerability

final serverProofs = await protonUsersApi.unlockPasswordChange(
  proofs: proofs,
);

/// check if the server proofs are valid
final check = clientProofs.expectedServerProof == serverProofs;
logger.i("EnableRecovery password server proofs: $check");
if (!check) {
  return Future.error('Invalid server proofs');
}

/// generate new entropy and mnemonic
final salt = WalletKeyHelper.getRandomValues(16);
final randomEntropy = WalletKeyHelper.getRandomValues(16);

final FrbMnemonic mnemonic = FrbMnemonic.newWith(entropy: randomEntropy);
final mnemonicWords = mnemonic.asWords();
final recoveryPassword = randomEntropy.base64encode();

final hashedPassword = await SrpClient.computeKeyPassword(
  password: recoveryPassword,
  salt: salt,
);
Future<ApiWalletData> createWallet(
    String walletName,
    String mnemonicStr,
    Network network,
    int walletType,
    String walletPassphrase,
  ) async {
    /// Generate a wallet secret key
    final SecretKey secretKey = WalletKeyHelper.generateSecretKey();
    final Uint8List entropy = Uint8List.fromList(await secretKey.extractBytes());

    /// get first user key (primary user key)
    final primaryUserKey = await userManager.getPrimaryKey();
    final String userPrivateKey = primaryUserKey.privateKey;
    final String userKeyID = primaryUserKey.keyID;
    final String passphrase = primaryUserKey.passphrase;

    /// encrypt mnemonic with wallet key
    final String encryptedMnemonic = await WalletKeyHelper.encrypt(
      secretKey,
      mnemonicStr,
    );

    /// encrypt wallet name with wallet key
    final String clearWalletName = walletName.isNotEmpty ? walletName : "My Wallet";
    final String encryptedWalletName = await WalletKeyHelper.encrypt(
      secretKey,
      clearWalletName,
    );
    // ...
  }