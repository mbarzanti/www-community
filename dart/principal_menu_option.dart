import 'package:flutter/material.dart';

class PrincipalMenuOption {
  final String icon;
  final String romanized;
  final String spanish;
  final String hangul;
  final Widget screen;

  PrincipalMenuOption({
    required this.icon,
    required this.romanized,
    required this.spanish,
    required this.hangul,
    required this.screen,
  });
}
