import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../utils/app_images.dart';
import '../utils/app_styles.dart';

class LastTwoDrawerItems extends StatelessWidget {
  const LastTwoDrawerItems({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 20, right: 20, bottom: 48),
      child: Column(
        children: [
          ListTile(
            leading: SvgPicture.asset(Assets.imagesSettings),
            title: Text(
              'Settings system',
              style: AppStyles.styleMedium16(context),
            ),
          ),
          const SizedBox(height: 20),
          ListTile(
            leading: SvgPicture.asset(Assets.imagesLogout),
            title: Text(
              'Logout account',
              style: AppStyles.styleMedium16(context),
            ),
          ),
        ],
      ),
    );
  }
}
