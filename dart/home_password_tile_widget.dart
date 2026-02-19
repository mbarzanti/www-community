import 'package:flutter/material.dart';
import 'package:health_center_core/health_center_core.dart';

class HomePasswordTileWidget extends StatelessWidget {
  const HomePasswordTileWidget(
      {super.key, required this.password, required this.ticketOffice});

  final String password;
  final String ticketOffice;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: HealthCenterTheme.orangeColor),
      ),
      child: Column(
        children: [
          Text(
            password,
            style: HealthCenterTheme.titleStyle
                .copyWith(fontWeight: FontWeight.bold),
          ),
          Text(
            ticketOffice,
            style: HealthCenterTheme.titleStyle.copyWith(
                fontWeight: FontWeight.bold,
                color: HealthCenterTheme.orangeColor),
          ),
        ],
      ),
    );
  }
}
