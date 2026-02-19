
import 'package:flutter/material.dart';

import '../utils/app_styles.dart';
import 'transaction_header.dart';
import 'transaction_list_view.dart';

class TransactionSection extends StatelessWidget {
  const TransactionSection({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const TransactionHeader(),
        const SizedBox(height: 8),
        Text(
          '13 April 2022',
          style: AppStyles.styleMedium16(context),
        ),
        const SizedBox(height: 8),
        const TransactionListView(),
      ],
    );
  }
}


