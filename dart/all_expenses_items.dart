
import 'package:flutter/material.dart';
import '../models/all_expenses_item_model.dart';
import 'all_expenses_items_body.dart';

class AllExpensesItems extends StatelessWidget {
  const AllExpensesItems({
    super.key,
    required this.allExpensesItemModel,
    required this.isSelected,
  });

  final AllExpensesItemModel allExpensesItemModel;
  final bool isSelected;

  @override
  Widget build(BuildContext context) {
    return AllExpensesItemBody(
      allExpensesItemModel: allExpensesItemModel,
      isActive: isSelected,
    );
  }
}

