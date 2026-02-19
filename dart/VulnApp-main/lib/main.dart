import 'package:flutter/material.dart';
import 'screens/login_screen.dart';

void main() {
  runApp(VulnApp());
}

class VulnApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'VulnApp',
      theme: ThemeData(primarySwatch: Colors.red),
      home: LoginScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}
