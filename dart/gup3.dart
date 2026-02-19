import 'package:flutter/material.dart';

class Gup3 extends StatelessWidget {
  const Gup3({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        iconTheme: IconThemeData(color: Colors.yellow, weight: 300),
        backgroundColor: Colors.black,
        title: const Text('Naranja', style: TextStyle(color: Colors.yellow)),
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // TODO: Hacer esto de forma programática no quemado manual.
          Text("4 meses", style: TextStyle(color: Colors.white38)),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text("Llaves", style: TextStyle(color: Colors.blueGrey)),
              Text(
                "- 15 Técnicas de defensa personal contra agarre lineal de muñeca.",
                style: TextStyle(color: Colors.blueGrey),
              ),
              Text(
                "- 5 Técnicas de defensa personal contra agarre cruzado de muñeca.",
                style: TextStyle(color: Colors.blueGrey),
              ),
            ],
          ),
          Container(),
          Container(),
          Container(),
          Container(),
        ],
      ),
    );
  }
}
