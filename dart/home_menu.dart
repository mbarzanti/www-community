import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'home_header.dart';
import 'home_grid_menu.dart';
import 'home_fab_search.dart';

class HomeMenu extends StatefulWidget {
  const HomeMenu({super.key});

  @override
  State<HomeMenu> createState() => _HomeMenuState();
}

class _HomeMenuState extends State<HomeMenu> {
  @override
  Widget build(BuildContext context) {
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
        statusBarBrightness: Brightness.dark,
      ),
    );

    return SafeArea(
      child: Scaffold(
        backgroundColor: Colors.black,
        body: Stack(
          children: const [
            Column(children: [HomeHeader(), HomeGridMenu()]),
            HomeFABSearch(),
          ],
        ),
      ),
    );
  }
}
