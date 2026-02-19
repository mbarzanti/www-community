import 'package:flutter/material.dart';
import 'package:responsive_and_adaptive/widgets/for_test/desktop_layout.dart';
import 'package:responsive_and_adaptive/widgets/for_test/mobile_layout.dart';


import 'adaptive_layout.dart';
import 'tablet_layout.dart';

class ViewHomeBody extends StatelessWidget {
  const ViewHomeBody({super.key});

  @override
  Widget build(BuildContext context) {
    return  Padding(
      padding: const EdgeInsets.only(left: 16, right: 16, top: 16),
      child: AdaptiveLayout(
        mobileLayout: (context) => const MobileLayout(),
        tabletLayout:(context) => const TabletLayout(),
        desktopLayout:(context) => const DesktopLayout(),
      ),
    );
  }
}
