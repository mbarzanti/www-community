import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';
import 'package:responsive_and_adaptive/utils/app_styles.dart';
import 'income_details_chart.dart';
import 'income_details_list_view.dart';

class IncomeDetails extends StatelessWidget {
  const IncomeDetails({super.key});

  @override
  Widget build(BuildContext context) {
    return MediaQuery.sizeOf(context).width > 1350
        ? const Row(
            children: [
              Expanded(
                child: IncomeDetailsChart(),
              ),
              Expanded(
                flex: 2,
                child: IncomeDetailsListView(),
              )
            ],
          )
        : SizedBox(
            width: MediaQuery.sizeOf(context).width * 0.2,
            child: const DetailedIncomeChart(),
          );
  }
}

class DetailedIncomeChart extends StatefulWidget {
  const DetailedIncomeChart({super.key});

  @override
  State<DetailedIncomeChart> createState() => _DetailedIncomeChartState();
}

class _DetailedIncomeChartState extends State<DetailedIncomeChart> {
  int currentIndex = -1;

  @override
  Widget build(BuildContext context) {
    return PieChart(
      getPieChartData(),
    );
  }

  PieChartData getPieChartData() {
    return PieChartData(
      pieTouchData: PieTouchData(
        enabled: true,
        touchCallback: (p0, pieTouchResponse) {
          currentIndex =
              pieTouchResponse?.touchedSection?.touchedSectionIndex ?? -1;
          setState(() {});
        },
      ),
      sectionsSpace: 0,
      sections: [
        PieChartSectionData(
          titlePositionPercentageOffset: currentIndex == 0 ? 1.5 : null,
          title: currentIndex == 0 ? 'Design Service' : '40%',
          titleStyle: AppStyles.styleMedium16(context).copyWith(
            color: currentIndex == 0 ? Colors.black : Colors.white,
            fontSize: 11,
          ),
          value: 40,
          color: const Color(0xff2088C7),
          radius: currentIndex == 0 ? 30 : 24,
        ),
        PieChartSectionData(
          titlePositionPercentageOffset: currentIndex == 1 ? 2.6 : null,
          title: currentIndex == 1 ? 'Design product' : '25%',
          titleStyle: AppStyles.styleMedium16(context).copyWith(
            color: currentIndex == 1 ? Colors.black : Colors.white,
            fontSize: 11,
          ),
          value: 25,
          color: const Color(0xff4DB7F2),
          radius: currentIndex == 1 ? 30 : 24,
        ),
        PieChartSectionData(
          titlePositionPercentageOffset: currentIndex == 2 ? 1.5 : null,
          title: currentIndex == 2 ? 'Product royalti' : '20%',
          titleStyle: AppStyles.styleMedium16(context).copyWith(
            color: currentIndex == 2 ? Colors.black : Colors.white,
            fontSize: 11,
          ),
          value: 20,
          color: const Color(0xff064060),
          radius: currentIndex == 2 ? 30 : 24,
        ),
        PieChartSectionData(
          titlePositionPercentageOffset: currentIndex == 3 ? 1.5 : null,
          title: currentIndex == 3 ? 'Other' : '22%',
          titleStyle: AppStyles.styleMedium16(context).copyWith(
            color: Colors.black,
            fontSize: 11,
          ),
          value: 22,
          color: const Color(0xffE2DECD),
          radius: currentIndex == 3 ? 30 : 24,
        ),
      ],
    );
  }
}
