import 'package:flutter/material.dart';

import '../models/all_expenses_item_model.dart';
import '../utils/app_images.dart';
import 'all_expenses_items.dart';

class AllExpensesBody extends StatefulWidget {
  const AllExpensesBody({
    super.key,
  });

  @override
  State<AllExpensesBody> createState() => _AllExpensesBodyState();
}

class _AllExpensesBodyState extends State<AllExpensesBody> {
  List<AllExpensesItemModel> expenseItems = [
    AllExpensesItemModel(
      image: Assets.imagesBalance,
      text: 'Balance',
    ),
    AllExpensesItemModel(
      image: Assets.imagesIncome,
      text: 'Income',
    ),
    AllExpensesItemModel(
      image: Assets.imagesExpenses,
      text: 'Expenses',
    )
  ];

  int selectedIndex = 0;

  @override
  Widget build(BuildContext context) {

    // return Row(
    //   children: expenseItems
    //       .map((e) => Expanded(child: AllExpensesItem(allExpensesItemModel: e)))
    //       .toList(),
    // );

    return Row(
      children: expenseItems.asMap().entries.map((e) {
        return e.key == 0 
            ? Expanded(
                child: GestureDetector(
                  onTap: () {
                    selectedIndex = e.key;
                    setState(() {});
                  },
                  child: AllExpensesItems(
                    allExpensesItemModel: e.value,
                    isSelected: selectedIndex == e.key,
                  ),
                ),
              )
            : Expanded(
                child: GestureDetector(
                  onTap: () {
                    selectedIndex = e.key;
                    setState(() {});
                  },
                  child: AllExpensesItems(
                    allExpensesItemModel: e.value,
                    isSelected: selectedIndex == e.key,
                  ),
                ),
              );
      }).toList(),
    );
  }
}
