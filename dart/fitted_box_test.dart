import 'package:flutter/material.dart';

class FittedBoxTest extends StatelessWidget {
  const FittedBoxTest({super.key});

  @override
  Widget build(BuildContext context) {
    List<String> images = [
      'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxjwXg_N8cjvZz7wvkbBW676zxkU5gYLpq0A&usqp=CAU',
      'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxjwXg_N8cjvZz7wvkbBW676zxkU5gYLpq0A&usqp=CAU',
      'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxjwXg_N8cjvZz7wvkbBW676zxkU5gYLpq0A&usqp=CAU',
      'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQxjwXg_N8cjvZz7wvkbBW676zxkU5gYLpq0A&usqp=CAU',
    ];
    return Scaffold(
      appBar: AppBar(),
      body: GridView.builder(
        itemCount: images.length,
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          mainAxisSpacing: 4,
          crossAxisSpacing: 4,
        ),
        itemBuilder: (context, index) {
          return FittedBox(child: Image.network(images[index]));
        },
      ),
    );
  }
}
