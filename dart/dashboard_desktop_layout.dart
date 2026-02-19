import 'package:flutter/material.dart';
import 'package:responsive_and_adaptive/widgets/custom_drawer.dart';

import 'last_section.dart';
import 'middle_section.dart';

class DashBoardDesktopLayout extends StatelessWidget {
  const DashBoardDesktopLayout({super.key});

  @override
  Widget build(BuildContext context) {
    return const Padding(
      padding: EdgeInsets.symmetric(vertical: 10),
      child: Row(
        children: [
          Expanded(
            flex: 3,
            child: CustomDrawer(),
          ),
          Expanded(
            flex: 14,
            child: CustomScrollView(
              slivers: [
                SliverFillRemaining(
                  hasScrollBody: false,
                  child: Row(
                    children: [
                      Expanded(
                        flex: 9,
                        child: MiddleSection(),
                      ),
                      Expanded(
                        flex: 5,
                        child: LastSection(),
                      ),
                    ],
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
