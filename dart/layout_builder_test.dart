import 'dart:developer';

import 'package:flutter/material.dart';

class LayoutBuilderTest extends StatelessWidget {
  const LayoutBuilderTest({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.purple,
      body: LayoutBuilder(
        builder: (context, constrains) {
          log(constrains.maxWidth.toString());
          if (constrains.maxWidth <= 450) {
            return const MobileLayout();
          } else {
            return const DesktopLayout();
          }
        },
      ),
    );
  }
}

class DesktopLayout extends StatefulWidget {
  const DesktopLayout({super.key});

  @override
  State<DesktopLayout> createState() => _DesktopLayoutState();
}

class _DesktopLayoutState extends State<DesktopLayout> {
  int number = 1;
  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: ListView.builder(
            itemCount: 10,
            itemBuilder: (context, index) {
              return GestureDetector(
                onTap: () {
                  number = ++index;
                  setState(() {});
                },
                child: ListTile(
                  title: Text('${index + 1}'),
                ),
              );
            },
          ),
        ),
        Expanded(
          child: Center(
            child: Text(number.toString()),
          ),
        ),
      ],
    );
  }
}

class MobileLayout extends StatelessWidget {
  const MobileLayout({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.purple,
      child: ListView.builder(
        itemCount: 10,
        itemBuilder: (context, index) {
          return ListTile(
            title: GestureDetector(
                onTap: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => TestOnTap(index: index + 1))),
                child: Text('${index + 1}')),
          );
        },
      ),
    );
  }
}

class TestOnTap extends StatelessWidget {
  const TestOnTap({super.key, required this.index});

  final int index;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: Center(
        child: Text('$index'),
      ),
    );
  }
}
