import 'package:flutter/material.dart';
import '../utils/app_styles.dart';
import 'latest_transaction_list_view.dart';

class LatestTrasactionSection extends StatelessWidget {
  const LatestTrasactionSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Align(
          alignment: Alignment.centerLeft,
          child: Text(
            'Latest Transaction',
            style: AppStyles.styleMedium16(context),
          ),
        ),
        const SizedBox(height: 12),
        const LatestTransactionListView(),
      ],
    );
  }
}
