import 'package:flutter/material.dart';

import '../../models/for_test/item_model.dart';
import 'custom_drawer_item.dart';

class CustomDrawerListView extends StatelessWidget {
  const CustomDrawerListView({
    super.key,
    required this.items,
  });

  final List<ItemModel> items;

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      itemCount: items.length,
      itemBuilder: (context, index) {
        return CustomDrawerItem(
          itemModel: items[index],
        );
      },
    );
  }
}
