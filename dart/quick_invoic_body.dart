import 'package:flutter/material.dart';
import 'latest_transaction_section.dart';
import 'quick_invoice_form.dart';
import 'quick_invoice_header.dart';

class QuickInvoiceBody extends StatelessWidget {
  const QuickInvoiceBody({super.key});

  @override
  Widget build(BuildContext context) {
    return const Column(
      children: [
        QuickInvoiceHeader(),
        LatestTrasactionSection(),
        SizedBox(
          height: 16,
        ),
        Divider(
          height: 18,
        ),
        SizedBox(
          height: 16,
        ),
        QuickInvoiceForm(),
      ],
    );
  }
}
