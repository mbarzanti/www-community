

import 'package:flutter/material.dart';

import '../models/user_info_model.dart';
import '../utils/app_images.dart';
import 'user_info_list_tile.dart';

class LatestTransactionListView extends StatelessWidget {
  const LatestTransactionListView({super.key});

  @override
  Widget build(BuildContext context) {
    List<UserInfoModel> items = [
      UserInfoModel(
        title: 'Madrani Andi',
        subTitle: 'MadraniAndi20@gmail.com',
        image: Assets.imagesAvatar1,
      ),
      UserInfoModel(
        title: 'Madrani Andi',
        subTitle: 'MadraniAndi20@gmail.com',
        image: Assets.imagesAvatar1,
      ),
      UserInfoModel(
        title: 'Madrani Andi',
        subTitle: 'MadraniAndi20@gmail.com',
        image: Assets.imagesAvatar1,
      ),
      UserInfoModel(
        title: 'Madrani Andi',
        subTitle: 'MadraniAndi20@gmail.com',
        image: Assets.imagesAvatar1,
      ),
      UserInfoModel(
        title: 'Madrani Andi',
        subTitle: 'MadraniAndi20@gmail.com',
        image: Assets.imagesAvatar1,
      ),
      UserInfoModel(
        title: 'Madrani Andi',
        subTitle: 'MadraniAndi20@gmail.com',
        image: Assets.imagesAvatar1,
      ),
    ];

    return SingleChildScrollView(
      scrollDirection: Axis.horizontal,
      child: Row(
        children: items
            .map((e) => IntrinsicWidth(child: UserInfoListTile(userinfoModel: e)))
            .toList(),
      ),
    );


    // return SizedBox(
    //   height: 80,
    //   child: ListView.builder(
    //     itemCount: items.length,
    //     scrollDirection: Axis.horizontal,
    //     itemBuilder: (context, index) {
    //       return IntrinsicWidth(
    //         child: UserInfoListTile(
    //           userinfoModel: items[index],
    //         ),
    //       );
    //     },
    //   ),
    // );


  }
}

