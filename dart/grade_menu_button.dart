import 'package:flutter/material.dart';

class GradeMenuButton extends StatelessWidget {
  final String grade;
  final String romanizedText;
  final String hangulText;
  final Color primaryColor;
  final Color? secondaryColor;
  final Widget screen;

  const GradeMenuButton({
    super.key,
    required this.grade,
    required this.romanizedText,
    required this.hangulText,
    required this.primaryColor,
    this.secondaryColor,
    required this.screen,
  });

  bool get isDualColor => secondaryColor != null;
  bool get isBlackOnly =>
      primaryColor == Colors.black && secondaryColor == null;
  bool get isBrownBlack =>
      primaryColor == Colors.brown && secondaryColor == Colors.black;

  Color get textColor {
    if (isBlackOnly || isBrownBlack) {
      return Colors.yellow;
    }
    return Colors.black;
  }

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        Navigator.push(
          context,
          PageRouteBuilder(
            transitionDuration: const Duration(milliseconds: 200),
            pageBuilder: (_, _, _) => screen,
            transitionsBuilder: (_, animation, _, child) {
              return FadeTransition(opacity: animation, child: child);
            },
          ),
        );
      },
      child: Container(
        decoration: BoxDecoration(
          border: Border.all(color: Colors.blueGrey),
          borderRadius: BorderRadius.circular(12),
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(12),
          child: Stack(
            children: [
              Column(
                children: [
                  Expanded(child: Container(color: primaryColor)),
                  Expanded(
                    child: Container(
                      color: isDualColor ? secondaryColor : primaryColor,
                    ),
                  ),
                ],
              ),

              Center(
                child: Padding(
                  padding: const EdgeInsets.all(12),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        grade,
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          color: textColor,
                          fontSize: 30,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      Text(
                        romanizedText,
                        style: TextStyle(
                          color: textColor.withValues(alpha: 0.9),
                          fontSize: 16,
                        ),
                      ),
                      Text(
                        hangulText,
                        style: TextStyle(
                          color: textColor.withValues(alpha: 0.8),
                          fontSize: 18,
                          fontWeight: FontWeight.w300,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
