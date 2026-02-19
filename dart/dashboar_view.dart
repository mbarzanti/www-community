import 'package:flutter/material.dart';
import 'package:responsive_and_adaptive/utils/size_config.dart';
import 'package:responsive_and_adaptive/widgets/custom_drawer.dart';
import '../utils/size_config.dart';
import '../widgets/adaptive_layout_widget.dart';
import '../widgets/all_expenses.dart';
import '../widgets/cards_and_transaction_section.dart';
import '../widgets/custom_drawer.dart';
import '../widgets/dashboard_desktop_layout.dart';
import '../widgets/income_details_chart.dart';
import '../widgets/income_details_list_view.dart';
import '../widgets/income_header.dart';
import '../widgets/quick_invoice.dart';

class DashBoardView extends StatelessWidget {
  const DashBoardView({super.key});

  @override
  Widget build(BuildContext context) {
    SizeConfig.init(context);
    return Scaffold(
      drawer: SizedBox(
        width: MediaQuery.sizeOf(context).width * 0.7,
        child: const CustomDrawer(),
      ),
      appBar: MediaQuery.sizeOf(context).width < 600 ? AppBar() : null,
      backgroundColor: const Color(0xffF7F9FA),
      body: AdaptiveLayoutWidget(
        mobileLayout: (context) => const DashBoardMobileLayout(),
        tabletLayout: (context) => const DashBoardTabletLayout(),
        desktopLayout: (context) => const DashBoardDesktopLayout(),
      ),
    );
  }
}

class DashBoardTabletLayout extends StatelessWidget {
  const DashBoardTabletLayout({super.key});

  @override
  Widget build(BuildContext context) {
    return const Row(
      children: [
        Expanded(
          child: CustomDrawer(),
        ),
        Expanded(
          flex: 3,
          child: DashBoardMobileLayout(),
        ),
      ],
    );
  }
}

class DashBoardMobileLayout extends StatelessWidget {
  const DashBoardMobileLayout({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return const SingleChildScrollView(
      child: Column(
        children: [
          AllExpenses(),
          SizedBox(height: 16),
          QuickInvoice(),
          SizedBox(height: 16),
          CardsAndTransactionsSection(),
          SizedBox(height: 16),
          IncomeHeader(),
          SizedBox(
            height: 280,
            child: Row(
              children: [
                Expanded(
                  flex: 2,
                  child: IncomeDetailsChart(),
                ),
                Expanded(
                  flex: 3,
                  child: IncomeDetailsListView(),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
