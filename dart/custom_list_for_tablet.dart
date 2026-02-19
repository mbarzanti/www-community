import 'package:flutter/material.dart';

import 'custom_grid_item.dart';

class CustomListForTablet extends StatelessWidget {
  const CustomListForTablet({super.key});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 176,
      child: ListView.builder(
        shrinkWrap: true,
        scrollDirection: Axis.horizontal,
        itemCount: 15,
        itemBuilder: (context, index) {
          return Container(
            margin: const EdgeInsets.symmetric(horizontal: 10),
            child: const AspectRatio(
              aspectRatio: 1,
              child: CustomGridItem(),
            ),
          );
        },
      ),
    );
  }
}
