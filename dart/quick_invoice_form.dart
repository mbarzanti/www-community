import 'package:flutter/material.dart';

import 'custom_elevated_button.dart';
import 'custom_text_field.dart';

class QuickInvoiceForm extends StatelessWidget {
  const QuickInvoiceForm({super.key});

  @override
  Widget build(BuildContext context) {
    return const Column(
      children: [
        Row(
          children: [
            Expanded(
              child: CustomTextField(
                hint: 'Type Customer Name',
                title: 'Customer Name',
              ),
            ),
            SizedBox(width: 8),
            Expanded(
              child: CustomTextField(
                hint: 'Type Customer Email',
                title: 'Customer Email',
              ),
            ),
          ],
        ),
        SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: CustomTextField(
                hint: 'Type Customer Name',
                title: 'Customer Name',
              ),
            ),
            SizedBox(width: 8),
            Expanded(
              child: CustomTextField(
                hint: 'USD',
                title: 'Customer Email',
              ),
            ),
          ],
        ),
        SizedBox(height: 18),
        Row(
          children: [
            Expanded(child: CustomElevatedButton(text: 'Add more details')),
            SizedBox(width: 8),
            Expanded(child: CustomElevatedButton(text: 'Send Money')),
          ],
        ),
      ],
    );
  }
}
