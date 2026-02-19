import 'package:flutter/material.dart';

class Gibon extends StatelessWidget {
  const Gibon({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.red,
      appBar: AppBar(title: Text("Pantalla Roja")),
      body: Center(
        child: Text(
          "Hola desde la pantalla roja",
          style: TextStyle(color: Colors.white, fontSize: 22),
        ),
      ),
    );
  }
}
