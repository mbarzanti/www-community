import 'package:flutter/material.dart';

import 'custom_drawer.dart';
import 'custom_two_containers.dart';
import 'tablet_layout.dart';

class DesktopLayout extends StatelessWidget {
  const DesktopLayout({super.key});

  @override
  Widget build(BuildContext context) {
    return const Row(
      children: [
        Expanded(
          flex: 1,
          child: CustomDrawer(),
        ),
        Expanded(
          flex: 3,
          child: TabletLayout(),
        ),
        SizedBox(width: 13),
        Expanded(
          flex: 1,
          child: CustomTwoContainers(),
        ),
      ],
    );
  }
}
