import 'package:flutter/material.dart';

import '../models/transaction_item_model.dart';
import 'transaction_item.dart';

class TransactionListView extends StatelessWidget {
  const TransactionListView({super.key});

  static const List<TransactionItemModel> items = [
    TransactionItemModel(
      title: 'Cash Withdrawal',
      date: '13 Apr, 2022',
      amount: '\$20,129',
      isWithdrawal: true,
    ),
    TransactionItemModel(
      title: 'Landing Page Project',
      date: '13 Apr, 2022',
      amount: '\$20,129',
      isWithdrawal: false,
    ),
    TransactionItemModel(
      title: 'Juni Mobile App Project',
      date: '13 Apr, 2022',
      amount: '\$20,129',
      isWithdrawal: false,
    ),
  ];

  @override
  Widget build(BuildContext context) {
    return Column(children: items.map((e) => TransactionItem(transactionItemModel: e)).toList());

    // return ListView.builder(
    //   // shrinkWrap: true,
    //   physics: NeverScrollableScrollPhysics(),
    //   itemCount: items.length,
    //   itemBuilder: (context, index) {
    //     return Padding(
    //       padding: const EdgeInsets.only(top: 6),
    //       child: TransactionItem(
    //         transactionItemModel: items[index],
    //       ),
    //     );
    //   },
    // );
  }
}
