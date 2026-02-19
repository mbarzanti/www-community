import 'package:flutter/material.dart';

import '../utils/app_styles.dart';

class CustomElevatedButton extends StatelessWidget {
  const CustomElevatedButton({super.key, required this.text});

  final String text;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 45,
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(
          elevation: 0,
          backgroundColor:
              text == 'Send Money' ? const Color(0xff4DB7F2) : Colors.white,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10),
          ),
        ),
        onPressed: () {},
        child: FittedBox(
          fit: BoxFit.scaleDown,
          child: Text(
            text,
            style: AppStyles.styleSemiBold18(context).copyWith(
              color: text == 'Add more details'
                  ? const Color(0xff4DB7F2)
                  : Colors.white,
            ),
          ),
        ),
      ),
    );
  }
}
