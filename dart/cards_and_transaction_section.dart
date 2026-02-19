import 'package:flutter/material.dart';
import 'cards_section.dart';
import 'transaction_section.dart';

class CardsAndTransactionsSection extends StatelessWidget {
  const CardsAndTransactionsSection({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(8),
      margin: const EdgeInsets.only(right: 10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: const Column(
        children: [
          CardsSection(),
          SizedBox(height: 8),
          TransactionSection(),
        ],
      ),
    );
  }
}
