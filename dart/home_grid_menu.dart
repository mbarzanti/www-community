import 'package:flutter/material.dart';
import 'package:lqh_app/data/principal_menu_options.dart';
import 'package:lqh_app/widgets/principal_menu_button.dart';

class HomeGridMenu extends StatelessWidget {
  const HomeGridMenu({super.key});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: GridView.count(
        crossAxisCount: 2,
        crossAxisSpacing: 10,
        mainAxisSpacing: 10,
        padding: const EdgeInsets.all(12),
        children: principalMenuOption.map((option) {
          return PrincipalMenuButton(
            icon: option.icon,
            romanizedText: option.romanized,
            spanishText: option.spanish,
            hangulText: option.hangul,
            screen: option.screen,
          );
        }).toList(),
      ),
    );
  }
}
