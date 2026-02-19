import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../models/drawer_item_model.dart';
import '../utils/app_styles.dart';

class DrawerItem extends StatelessWidget {
  const DrawerItem({
    super.key,
    required this.drawerItemModel,
    required this.isActive,
  });

  final DrawerItemModel drawerItemModel;
  final bool isActive;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: SvgPicture.asset(drawerItemModel.image),
      title: !isActive? FittedBox(
        fit: BoxFit.scaleDown,
        alignment: Alignment.centerLeft,
        child: Text(
          
          drawerItemModel.text,
          style: AppStyles.styleMedium16(context),
        ),
      ):FittedBox(
        fit: BoxFit.scaleDown,
        alignment: Alignment.centerLeft,
        child: Text(
          drawerItemModel.text,
          style: AppStyles.styleBold16(context),
        ),
      ),
      trailing: isActive?Container(
        height: 45,
        width: 3,
        color: const Color(0xff4EB7F2),
      ):const SizedBox(),
    );
  }
}
