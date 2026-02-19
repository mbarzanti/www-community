import 'package:flutter/material.dart';

import '../utils/app_styles.dart';

class CustomTextField extends StatelessWidget {
  const CustomTextField({
    super.key,
    required this.hint,
    required this.title,
  });

  final String title;
  final String hint;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: AppStyles.styleMedium16(context),
        ),
        const SizedBox(height: 8),
        TextField(
          enabled: false,
          decoration: InputDecoration(
            hintText: hint,
            hintStyle: AppStyles.styleRegular16(context),
            fillColor: const Color(0xffFAFAFA),
            filled: true,
            border: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: const BorderSide(
                color: Color(0xffFAFAFA),
              ),
            ),
            enabledBorder: OutlineInputBorder(
              borderRadius: BorderRadius.circular(12),
              borderSide: const BorderSide(
                color: Color(0xffFAFAFA),
              ),
            ),
          ),
        ),
      ],
    );
  }
}