import 'package:flutter/material.dart';
import 'package:lqh_app/data/grade_menu_options.dart';
import 'package:lqh_app/widgets/grade_menu_button.dart';

class Programa extends StatelessWidget {
  const Programa({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        iconTheme: IconThemeData(color: Colors.yellow, weight: 300),
        backgroundColor: Colors.black,
        title: const Text('Programa', style: TextStyle(color: Colors.yellow)),
      ),
      body: GridView.count(
        crossAxisCount: 2,
        padding: const EdgeInsets.all(5),
        crossAxisSpacing: 10,
        mainAxisSpacing: 10,
        children: gradeMenuOptions.map((option) {
          return GradeMenuButton(
            grade: option.grade,
            romanizedText: option.romanized,
            hangulText: option.hangul,
            primaryColor: option.primaryColor,
            secondaryColor: option.secondaryColor,
            screen: option.screen,
          );
        }).toList(),
      ),
    );
  }
}
