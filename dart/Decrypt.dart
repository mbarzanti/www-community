import 'dart:math';

Random _rnd = Random();

typedef StringGeneratorFunction = String Function();

class StringGenerators {
  static const String letters = 'abcdefghijklmnopqrstuvwxyz';
  static const String numbers = '1234567890';
  static const String symbols = '_';

  static String getRandomString(
    final int length, {
    final hasLowercaseLetters = false,
    final hasUppercaseLetters = false,
    final hasNumbers = false,
    final hasSymbols = false,
    final isStrict = false,
  }) {
    String chars = '';

    if (hasLowercaseLetters) {chars += letters;}
    if (hasUppercaseLetters) {chars += letters.toUpperCase();}
    if (hasNumbers) {chars += numbers;}
    if (hasSymbols) {chars += symbols;}

    assert(chars.isNotEmpty, 'chart empty');

    if (!isStrict) {return genString(length, chars);}

    String res = '';
    int loose = length;
    // (...) for brewity, code that guarantees that it contains 
    // e.g. upper case has been removed
    res += genString(loose, chars);

    final List<String> shuffledlist = res.split('')..shuffle();
    return shuffledlist.join();
  }

  static String genString(final int length, final String chars) =>
      String.fromCharCodes(
        Iterable.generate(
          length,
          (final _) => chars.codeUnitAt(
            _rnd.nextInt(chars.length),
          ),
        ),
      );

  static StringGeneratorFunction userPassword = () => getRandomString(
        8,
        hasLowercaseLetters: true,
        hasUppercaseLetters: true,
        hasNumbers: true,
        isStrict: true,
      );