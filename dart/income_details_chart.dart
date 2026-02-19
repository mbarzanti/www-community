import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/material.dart';

class IncomeDetailsChart extends StatefulWidget {
  const IncomeDetailsChart({super.key});

  @override
  State<IncomeDetailsChart> createState() => _IncomeDetailsChartState();
}

class _IncomeDetailsChartState extends State<IncomeDetailsChart> {
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
          currentIndex = pieTouchResponse?.touchedSection?.touchedSectionIndex ?? -1;
          setState(() {});
        },
      ),
      sectionsSpace: 0,
      sections: [
        PieChartSectionData(
          value: 40,
          color: const Color(0xff2088C7),
          showTitle: false,
          radius: currentIndex == 0 ? 30 : 24,
        ),
        PieChartSectionData(
          value: 25,
          color: const Color(0xff4DB7F2),
          radius: currentIndex == 1 ? 30 : 24,
          showTitle: false,
        ),
        PieChartSectionData(
          value: 20,
          color: const Color(0xff064060),
          radius: currentIndex == 2 ? 30 : 24,
          showTitle: false,
        ),
        PieChartSectionData(
          value: 22,
          color: const Color(0xffE2DECD),
          radius: currentIndex == 3 ? 30 : 24,
          showTitle: false,
        ),
      ],
    );
  }
}
