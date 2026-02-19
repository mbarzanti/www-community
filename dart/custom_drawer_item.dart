import 'package:flutter/material.dart';

import '../../models/for_test/item_model.dart';



class CustomDrawerItem extends StatelessWidget {
  const CustomDrawerItem({
    super.key,
    required this.itemModel,
  });

  final ItemModel itemModel;
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(left: 5, bottom: 40),
      child: Row(
        children: [
          Icon(
            itemModel.icon,
            size: 20,
            color: Colors.black.withOpacity(.6),
          ),
          const SizedBox(width: 30),
          Expanded(
            child: FittedBox(
              fit: BoxFit.scaleDown,
              alignment: Alignment.centerLeft,
              child: Text(
                itemModel.text.toUpperCase(),
                style: const TextStyle(
                  color: Colors.grey,
                  fontSize: 17,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
