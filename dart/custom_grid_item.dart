import 'package:flutter/material.dart';

class CustomGridItem extends StatelessWidget {
  const CustomGridItem({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10),
        color: const Color(0xffB4B4B4),
      ),
    );
  }
}
