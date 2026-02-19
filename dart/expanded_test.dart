import 'package:flutter/material.dart';

class ExpandedTest extends StatelessWidget {
  const ExpandedTest({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          Expanded(
            flex: 5,
            child: Container(
              color: Colors.pink,
            ),
          ),
          Expanded(
            flex: 2,
            child: Container(
              color: Colors.purple,
            ),
          ),
          Expanded(
            flex: 1,
            child: Container(
              color: Colors.orange,
            ),
          ),
        ],
      ),
    );
  }
}
