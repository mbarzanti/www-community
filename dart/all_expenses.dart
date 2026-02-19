import 'package:flutter/material.dart';
import 'all_expenses_body.dart';
import 'all_expenses_header.dart';
import 'cutsom_background_container.dart';

class AllExpenses extends StatelessWidget {
  const AllExpenses({super.key});

  @override
  Widget build(BuildContext context) {
    return const CustomBackgroundContainer(
      padding: 8,
      child: Column(
        children: [
          AllExpensesHeader(),
          SizedBox(height: 16),
          AllExpensesBody(),
        ],
      ),
    );
  }
}
