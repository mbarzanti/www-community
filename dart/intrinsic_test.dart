import 'package:flutter/material.dart';

class InterinsicTest extends StatelessWidget {
  const InterinsicTest({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SingleChildScrollView(
        child: Column(
          children: [
            const SizedBox(height: 100),
            IntrinsicHeight(
              child: Row(
                children: [
                  Expanded(
                    child: Container(
                      height: 50,
                      color: Colors.red,
                    ),
                  ),
                  const SizedBox(width: 30),
                  Expanded(
                    child: Column(
                      children: [
                        Expanded(
                          child: Container(
                            color: Colors.green,
                          ),
                        ),
                        const SizedBox(height: 15),
                        Expanded(
                          child: Container(
                            color: Colors.blue,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
