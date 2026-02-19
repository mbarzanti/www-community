import 'package:flutter/material.dart';

import 'custom_grid_item.dart';

class CustomSliverGridView extends StatelessWidget {
  const CustomSliverGridView({super.key});

  @override
  Widget build(BuildContext context) {
    return SliverGrid.builder(
      itemCount: 4,
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 2,
        crossAxisSpacing: 16,
        mainAxisSpacing: 16,
      ),
      itemBuilder: (context, index) {
        return const CustomGridItem();
      },
    );
  }
}
