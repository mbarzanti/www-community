

import 'package:flutter/material.dart';

class TestOnText extends StatelessWidget {
  const TestOnText({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Text(
            'Title Text',
            style: TextStyle(
              fontSize: getResponsiveFontSize(context, fontSize: 20),
            ),
          ),
          Text(
            'Body Text, it should be responsive',
            style: TextStyle(
              fontSize: getResponsiveFontSize(context, fontSize: 16),
            ),
          ),
        ],
      ),
    );
  }
//? Determine ScaleFactor
//? responsive FontSize = base FontSize * scaleFactor
//? (min, max) FontSize

  double getResponsiveFontSize(BuildContext context,
      {required double fontSize}) {
    double scaleFactor = getScaleFactor(context);
    double responsiveFontSize = fontSize * scaleFactor;
    return responsiveFontSize.clamp(fontSize * 0.8, fontSize * 1.2);
  }
}

double getScaleFactor(BuildContext context) {
  double width = MediaQuery.of(context).size.width;
  if (width < 600) {
    return width / 400;
  } else if (width < 900) {
    return width / 700;
  } else {
    return width / 1000;
  }
}


