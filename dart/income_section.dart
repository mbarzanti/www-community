import 'package:flutter/material.dart';
import 'income_details.dart';
import 'income_header.dart';

class IncomeSection extends StatelessWidget {
  const IncomeSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(32),
      margin: const EdgeInsets.only(bottom: 20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: const Column(
        children: [
          IncomeHeader(),
          Expanded(
            flex: 2,
            child: IncomeDetails(),
          ),
        ],
      ),
    );
  }
}
