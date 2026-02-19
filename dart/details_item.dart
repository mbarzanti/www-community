import 'package:flutter/material.dart';

import '../models/details_item_model.dart';
import '../utils/app_styles.dart';

class DetailsItem extends StatelessWidget {
  const DetailsItem({super.key, required this.detailsItemModel});

  final DetailsItemModel detailsItemModel;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: Container(
        width: 12,
        height: 12,
        decoration: ShapeDecoration(
          color: detailsItemModel.color,
          shape: const OvalBorder(),
        ),
      ),
      title: Text(
        detailsItemModel.title,
        style: AppStyles.styleRegular16(context),
      ),
      trailing: Text(
        detailsItemModel.value,
        style: AppStyles.styleMedium16(
          context,
        ),
      ),
    );
  }
}
