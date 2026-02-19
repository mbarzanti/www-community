import 'package:flutter/material.dart';

class AspectRatioExample extends StatelessWidget {
  const AspectRatioExample({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Align(
        alignment: Alignment.center,
        child: AspectRatio(
          aspectRatio: 4,
          child: Container(
            color: Colors.purple,
            width: MediaQuery.of(context).size.width,
            // height: 100,
          ),
        ),
      ),
    );
  }
}
