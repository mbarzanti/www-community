import 'package:flutter/material.dart';

import '../utils/app_images.dart';
import 'my_card_body.dart';

class MyCard extends StatelessWidget {
  const MyCard({super.key});

  @override
  Widget build(BuildContext context) {
    return AspectRatio(
      aspectRatio: 420 / 215,
      child: Container(
        decoration: BoxDecoration(
          image: const DecorationImage(
            fit: BoxFit.fill,
            image: AssetImage(
              Assets.imagesCardBackground,
            ),
          ),
          borderRadius: BorderRadius.circular(12),
          color: const Color(0xff4EB7F2),
        ),
        child: const MyCardBody(),
      ),
    );
  }
}
