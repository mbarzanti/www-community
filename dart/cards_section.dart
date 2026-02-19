import 'package:flutter/material.dart';

import '../utils/app_styles.dart';
import 'dots_indictor.dart';
import 'my_cards_page_view.dart';

class CardsSection extends StatefulWidget {
  const CardsSection({super.key});

  @override
  State<CardsSection> createState() => _CardsSectionState();
}

class _CardsSectionState extends State<CardsSection> {
  late PageController pageController;
  int currentPageIndex = 0;
  @override
  void initState() {
    super.initState();
    pageController = PageController();
    pageController.addListener(() {
      currentPageIndex = pageController.page!.round();
      setState(() {});
    });
  }

  @override
  void dispose() {
    super.dispose();
    pageController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'My Card',
          style: AppStyles.styleSemiBold20(context),
        ),
        const SizedBox(height: 8),
        MyCardsPageView(pageController: pageController),
        const SizedBox(height: 12),
        DotsIndicator(currentPageIndex: currentPageIndex),
      ],
    );
  }
}
