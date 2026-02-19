import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

import '../models/user_info_model.dart';
import '../utils/app_styles.dart';

class UserInfoListTile extends StatelessWidget {
  const UserInfoListTile({
    super.key,
    required this.userinfoModel,
  });

  final UserInfoModel userinfoModel;

  @override
  Widget build(BuildContext context) {
    return Card(
      color: const Color(0xffFAFAFA),
      elevation: 0,
      child: ListTile(
        leading: SvgPicture.asset(userinfoModel.image),
        title: Text(
          userinfoModel.title,
          style: AppStyles.styleSemiBold16(context),
        ),
        subtitle: Text(
          userinfoModel.subTitle,
          style: AppStyles.styleRegular12(context),
        ),
      ),
    );
  }
}
