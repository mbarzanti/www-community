
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../utils/app_images.dart';
import '../utils/app_styles.dart';

class MyCardBody extends StatelessWidget {
  const MyCardBody({super.key});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(right: 15),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          ListTile(
            title: Text(
              'Name Card',
              style: AppStyles.styleRegular16(context).copyWith(
                color: Colors.white,
              ),
            ),
            subtitle: Text(
              'Syah Bandi',
              style: AppStyles.styleMedium20(context),
            ),
            trailing: SvgPicture.asset(Assets.imagesGallery),
          ),
          const Spacer(),
          Text(
            '0918 8124 0042 8129',
            style: AppStyles.styleSemiBold24(context).copyWith(
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '12/20 - 124',
            style: AppStyles.styleRegular16(context).copyWith(
              color: Colors.white,
            ),
          ),
          const Spacer(),
          // SizedBox(height: 30),
        ],
      ),
    );
  }
}

