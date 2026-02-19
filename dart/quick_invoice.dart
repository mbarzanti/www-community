import 'package:flutter/material.dart';
import 'package:responsive_and_adaptive/widgets/cutsom_background_container.dart';

import 'quick_invoic_body.dart';

class QuickInvoice extends StatelessWidget {
  const QuickInvoice({super.key});

  @override
  Widget build(BuildContext context) {
    return const CustomBackgroundContainer(
      padding: 12,
      child: QuickInvoiceBody(),
    );
  }
}
