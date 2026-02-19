
import 'package:flutter/material.dart';

class CustomTwoContainers extends StatelessWidget {
  const CustomTwoContainers({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Expanded(
          flex: 2,
          child: Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8),
              color: const Color(0xffB4B4B4),
            ),
          ),
        ),
        const SizedBox(height: 16),
        Expanded(
          flex: 1,
          child: Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8),
              color: const Color(0xffECECEC),
            ),
          ),
        ),
        const SizedBox(height: 18),
      ],
    );
  }
}

