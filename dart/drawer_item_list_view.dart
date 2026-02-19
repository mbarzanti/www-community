import 'package:flutter/material.dart';

import '../models/drawer_item_model.dart';
import '../utils/app_images.dart';
import 'drawer_item.dart';

class DrewerItemListView extends StatefulWidget {
  const DrewerItemListView({super.key});



  
  @override
  State<DrewerItemListView> createState() => _DrewerItemListViewState();
}

class _DrewerItemListViewState extends State<DrewerItemListView> {

  final List<DrawerItemModel> items = const [
    DrawerItemModel(image: Assets.imagesDashboard, text: 'Dashboard'),
    DrawerItemModel(image: Assets.imagesMyTransctions, text: 'My Transactions'),
    DrawerItemModel(image: Assets.imagesStatistics, text: 'Statistics'),
    DrawerItemModel(image: Assets.imagesWalletAccount, text: 'Wallet Account'),
    DrawerItemModel(image: Assets.imagesMyInvestments, text: 'My Investments'),
  ];

  int activeItemIndex = 0;
  @override
  Widget build(BuildContext context) {
    return SliverList.builder(
      itemCount: items.length,
      itemBuilder: (context, index) {
        return GestureDetector(
          onTap: () {
            if(activeItemIndex != index){
              setState(() {
                activeItemIndex = index;
              });
            }
          },
          child: Padding(
            padding: const EdgeInsets.only(top: 20),
              child: DrawerItem(
                drawerItemModel: items[index],
                isActive: activeItemIndex == index,
              ),
          ),
        );
      },
    );
  }
}
