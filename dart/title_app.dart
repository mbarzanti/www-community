import 'package:flutter/material.dart';

class TitleApp extends StatelessWidget {
  const TitleApp({super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const Text(
          'LQH',
          style: TextStyle(
            color: Colors.yellow,
            fontSize: 30,
            fontWeight: FontWeight.bold,
          ),
        ),
        const Text(
          'App',
          style: TextStyle(
            backgroundColor: Colors.yellow,
            color: Colors.black,
            fontWeight: FontWeight.w300,
            fontSize: 30,
            height: 1,
          ),
        ),
      ],
    );
  }
}
