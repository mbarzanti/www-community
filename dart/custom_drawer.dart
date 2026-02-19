import 'package:flutter/material.dart';
import 'package:responsive_and_adaptive/models/user_info_model.dart';
import '../utils/app_images.dart';
import 'drawer_item_list_view.dart';
import 'last_two_drawer_items.dart';
import 'user_info_list_tile.dart';

class CustomDrawer extends StatelessWidget {
  const CustomDrawer({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.white,
      child: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: UserInfoListTile(
              userinfoModel: UserInfoModel(
                title: 'Lekan Okeowo',
                subTitle: 'demo@gmial.com',
                image: Assets.imagesAvatar3,
              ),
            ),
          ),
          const DrewerItemListView(),
          const SliverFillRemaining(
            hasScrollBody: false,
            child: Column(
              children: [
                Spacer(),
                LastTwoDrawerItems(),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
