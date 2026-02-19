import 'package:flutter/material.dart';

class FlexibleTest extends StatelessWidget {
  const FlexibleTest({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          const Flexible(
            child: FittedBox(
              child: Icon(
                Icons.ac_unit_outlined,
                size: 300,
              ),
            ),
          ),
          Expanded(
            child: Container(
              color: Colors.pink,
              child: const FittedBox(
                fit: BoxFit.scaleDown,
                child: Icon(Icons.favorite),
              ),
            ),
          ),
          Container(
            height: 150,
            color: Colors.purple,
          ),
          Container(
            height: 150,
            color: Colors.orange,
          ),
        ],
      ),
    );
  }
}
