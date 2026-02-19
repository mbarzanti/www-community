import 'package:flutter/material.dart';
import 'package:lqh_app/widgets/title_app.dart';

class HomeHeader extends StatelessWidget {
  const HomeHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const SizedBox(height: 12),
        ClipOval(
          child: Image.asset(
            'assets/images/logoJDK_optimized.png',
            width: 180,
            height: 180,
            fit: BoxFit.cover,
          ),
        ),
        const SizedBox(height: 8),
        const TitleApp(),
      ],
    );
  }
}
