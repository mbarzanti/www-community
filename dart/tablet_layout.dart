
import 'package:flutter/material.dart';

import 'custom_list_for_tablet.dart';
import 'custom_sliver_list_view.dart';

class TabletLayout extends StatelessWidget {
  const TabletLayout({super.key});

  @override
  Widget build(BuildContext context) {
    return const CustomScrollView(
      slivers: [
        SliverToBoxAdapter(child: CustomListForTablet()),
        SliverToBoxAdapter(child: SizedBox(height: 7)),
        CustomSliverListView(),
      ],
    );
  }
}

