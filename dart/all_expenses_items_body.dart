import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../models/all_expenses_item_model.dart';
import '../utils/app_styles.dart';

class AllExpensesItemBody extends StatelessWidget {
  const AllExpensesItemBody({
    super.key,
    required this.allExpensesItemModel,
    required this.isActive,
  });

  final AllExpensesItemModel allExpensesItemModel;
  final bool isActive;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 24),
      decoration: ShapeDecoration(
        color: isActive ? const Color(0xff4DB7F2) : allExpensesItemModel.color,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
          side: const BorderSide(
            width: 1,
            color: Color(
              0xffF1F1F1,
            ),
          ),
        ),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                child: ConstrainedBox(
                  constraints: const BoxConstraints(maxWidth: 60),
                  child: AspectRatio(
                    aspectRatio: 1,
                    child: FittedBox(
                      child: Container(
                        decoration: ShapeDecoration(
                          color: isActive
                              ? Colors.white.withOpacity(0.100000000149011612)
                              : const Color(0xffFAFAFA),
                          shape: const OvalBorder(),
                        ),
                        child: Center(
                          child: SvgPicture.asset(
                            allExpensesItemModel.image,
                            colorFilter: ColorFilter.mode(
                              isActive ? Colors.white : const Color(0xff4Eb7F2),
                              BlendMode.srcIn,
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ),
              Icon(
                Icons.keyboard_arrow_right,
                // ignore: prefer_const_constructors
                color: isActive ? Colors.white : Color(0xff064016),
                size: 32,
              ),
            ],
          ),
          Align(
            alignment: Alignment.centerLeft,
            child: Column(
              children: [
                const SizedBox(height: 32),
                FittedBox(
                  fit: BoxFit.scaleDown,
                  child: Text(
                    allExpensesItemModel.text,
                    style: isActive
                        ? AppStyles.styleMedium16(context).copyWith(
                            color: Colors.white,
                          )
                        : AppStyles.styleMedium16(context),
                  ),
                ),
                const SizedBox(height: 8),
                FittedBox(
                  fit: BoxFit.scaleDown,
                  child: Text(
                    allExpensesItemModel.date,
                    style: isActive
                        ? AppStyles.styleRegular14(context).copyWith(
                            color: const Color(0xffFAFAFA),
                          )
                        : AppStyles.styleRegular14(context),
                  ),
                ),
                const SizedBox(height: 16),
                FittedBox(
                  fit: BoxFit.scaleDown,
                  child: Text(
                    allExpensesItemModel.price,
                    style: isActive
                        ? AppStyles.styleSemiBold24(context).copyWith(
                            color: Colors.white,
                          )
                        : AppStyles.styleSemiBold24(context),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
