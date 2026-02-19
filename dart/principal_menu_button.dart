import 'package:flutter/material.dart';

class PrincipalMenuButton extends StatelessWidget {
  final String icon;
  final String romanizedText;
  final String spanishText;
  final String hangulText;
  final Widget screen;

  const PrincipalMenuButton({
    super.key,
    required this.icon,
    required this.romanizedText,
    required this.spanishText,
    required this.hangulText,
    required this.screen,
  });

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
          color: const Color(0xFF171F2A),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(icon, style: const TextStyle(fontSize: 50)),
              Text(
                romanizedText,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                spanishText,
                style: const TextStyle(
                  color: Colors.white70,
                  fontSize: 15,
                  fontWeight: FontWeight.w100,
                  letterSpacing: 1,
                ),
              ),
              Text(
                hangulText,
                style: const TextStyle(
                  color: Colors.white54,
                  fontSize: 18,
                  fontWeight: FontWeight.w100,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
