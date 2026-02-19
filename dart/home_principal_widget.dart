import 'package:flutter/material.dart';
import 'package:health_center_core/health_center_core.dart';

class HomePrincipalWidget extends StatelessWidget {
  const HomePrincipalWidget({
    super.key,
    required this.passwordLabel,
    required this.password,
    required this.deskNumber,
    required this.labelColor,
    required this.buttonColor,
  });

  final String passwordLabel;
  final String password;
  final String deskNumber;

  final Color labelColor;
  final Color buttonColor;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Container(
        margin: const EdgeInsets.all(48),
        padding: const EdgeInsets.all(32),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: HealthCenterTheme.orangeColor),
        ),
        child: Column(
          children: [
            Text(
              passwordLabel,
              style: HealthCenterTheme.titleStyle.copyWith(color: labelColor),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: buttonColor,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Text(
                password,
                style: HealthCenterTheme.titleStyle
                    .copyWith(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
            const SizedBox(height: 24),
            Text(
              'Ticket officer',
              style: HealthCenterTheme.titleStyle.copyWith(color: labelColor),
            ),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
              alignment: Alignment.center,
              decoration: BoxDecoration(
                color: buttonColor,
                borderRadius: BorderRadius.circular(16),
              ),
              child: Text(
                deskNumber,
                style: HealthCenterTheme.titleStyle
                    .copyWith(color: Colors.white, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
