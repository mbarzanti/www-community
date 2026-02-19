import 'package:flutter/material.dart';

class AllExpensesItemModel {
  final String image;
  final String text;
  final Color color;
  final String date;
  final String price;

  AllExpensesItemModel({
    required this.image,
    required this.text,
    this.color =const Color(0xffFAFAFA),
    this.date = 'April 2022',
    this.price = '\$20,129',
  });


}
