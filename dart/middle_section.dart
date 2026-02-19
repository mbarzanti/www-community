import 'package:flutter/material.dart';

import 'all_expenses.dart';
import 'quick_invoice.dart';

class MiddleSection extends StatelessWidget {
  const MiddleSection({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 15),
      child: const Column(
        children: [
          AllExpenses(),
          SizedBox(height: 4),
            Expanded(child: QuickInvoice()),
          // Expanded(
          // ),
        ],
      ),
    );
  }
}
