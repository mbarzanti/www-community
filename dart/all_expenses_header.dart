

import 'package:flutter/material.dart';

import '../utils/app_styles.dart';

class AllExpensesHeader extends StatelessWidget {
  const AllExpensesHeader({super.key});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          'All Expenses',
          style: AppStyles.styleSemiBold20(context),
        ),
        Container(
          padding: const EdgeInsets.all(8),
          decoration: ShapeDecoration(
            shape: RoundedRectangleBorder(
              side: const BorderSide(
                width: 1,
                color: Color(0xffF1F1F1),
              ),
              borderRadius: BorderRadius.circular(12),
            ),
            color: Colors.white,
            // borderRadius: BorderRadius.circular(12),
          ),
          child: Row(
            children: [
              Text(
                'Monthly',
                style: AppStyles.styleMedium16(context),
              ),
              const SizedBox(width: 8),
              const Icon(
                Icons.keyboard_arrow_down_sharp,
                color: Color(0xff064061),
                size: 32,
              ),
            ],
          ),
        ),
      ],
    );
  }
}

