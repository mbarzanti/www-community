import 'package:flutter/material.dart';
import 'cards_and_transaction_section.dart';
import 'income_section.dart';

class LastSection extends StatelessWidget {
  const LastSection({super.key});

  @override
  Widget build(BuildContext context) {
    return const Column(
      children: [
        Expanded(
          flex: 4,
          child: CardsAndTransactionsSection()),
        SizedBox(height: 8),
        Expanded(
          flex: 2,
          child: IncomeSection(),
        ),
      ],
    );
  }
}
