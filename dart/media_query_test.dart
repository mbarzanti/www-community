import 'dart:developer';

import 'package:flutter/material.dart';

class MediaQueryTest extends StatelessWidget {
  const MediaQueryTest({super.key});

  @override
  Widget build(BuildContext context) {
    double height = MediaQuery.of(context).size.height / 3.5;
    log(MediaQuery.of(context).size.height.toString());
    return Scaffold(  
        body: Column(
          children: [
            Container(
              color: Colors.pink,
              height: height,
              width: 200,
            ),
            Container(
              color: Colors.purple,
              height: height,
              width: 200,
            ),
            Container(
              color: Colors.orange,
              height: height,
              width: 200,
            ),
          ],
        ),
      );
  }
}