import 'package:flutter/material.dart';

class GradeMenuOption {
  final String grade;
  final String romanized;
  final String hangul;
  final Widget screen;

  final Color primaryColor;
  final Color? secondaryColor;

  const GradeMenuOption({
    required this.grade,
    required this.romanized,
    required this.hangul,
    required this.screen,
    required this.primaryColor,
    this.secondaryColor,
  });

  bool get isDualColor => secondaryColor != null;
  bool get isBlackOnly => primaryColor == Colors.black && secondaryColor == null;
}
