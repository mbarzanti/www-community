import 'package:flutter/material.dart';
import 'package:lqh_app/models/grade_menu_option.dart';

import 'package:lqh_app/screens/views/grades/gup1.dart';
import 'package:lqh_app/screens/views/grades/gup2.dart';
import 'package:lqh_app/screens/views/grades/gup3.dart';
import 'package:lqh_app/screens/views/grades/gup4.dart';
import 'package:lqh_app/screens/views/grades/gup5.dart';
import 'package:lqh_app/screens/views/grades/gup6.dart';
import 'package:lqh_app/screens/views/grades/gup7.dart';
import 'package:lqh_app/screens/views/grades/gup8.dart';
import 'package:lqh_app/screens/views/grades/gup9.dart';
import 'package:lqh_app/screens/views/grades/gup10.dart';
import 'package:lqh_app/screens/views/grades/dan1.dart';
import 'package:lqh_app/screens/views/grades/dan2.dart';
import 'package:lqh_app/screens/views/grades/dan3.dart';
import 'package:lqh_app/screens/views/grades/dan4.dart';
import 'package:lqh_app/screens/views/grades/dan5.dart';

final List<GradeMenuOption> gradeMenuOptions = [
  GradeMenuOption(
    grade: "Blanco",
    romanized: "(Hayan tti)",
    hangul: "하얀 띠",
    screen: Gup10(),
    primaryColor: Colors.white,
  ),
  GradeMenuOption(
    grade: "Amarillo",
    romanized: "(Noran tti)",
    hangul: "노란 띠",
    screen: Gup9(),
    primaryColor: Colors.yellow,
  ),
  GradeMenuOption(
    grade: "Naranja",
    romanized: "(Juhwang tti)",
    hangul: "주황 띠",
    screen: Gup8(),
    primaryColor: Colors.orange,
  ),
  GradeMenuOption(
    grade: "Verde",
    romanized: "(Chorok tti)",
    hangul: "초록 띠",
    screen: Gup7(),
    primaryColor: Colors.green,
  ),
  GradeMenuOption(
    grade: "Azul",
    romanized: "(Paran tti)",
    hangul: "파란 띠",
    screen: Gup6(),
    primaryColor: Colors.blue,
  ),
  GradeMenuOption(
    grade: "Púrpura",
    romanized: "(Bora tti)",
    hangul: "보라 띠",
    screen: Gup5(),
    primaryColor: Colors.deepPurple,
  ),
  GradeMenuOption(
    grade: "Rojo",
    romanized: "(Ppalgan tti)",
    hangul: "빨간 띠",
    screen: Gup4(),
    primaryColor: Colors.red,
  ),
  GradeMenuOption(
    grade: "Rojo Marrón",
    romanized: "(Ppalgan gal tti)",
    hangul: "빨간 갈 띠",
    screen: Gup3(),
    primaryColor: Colors.red,
    secondaryColor: Colors.brown,
  ),

  GradeMenuOption(
    grade: "Marrón",
    romanized: "(Gal tti)",
    hangul: "갈 띠",
    screen: Gup2(),
    primaryColor: Colors.brown,
  ),
  GradeMenuOption(
    grade: "Marrón Negro",
    romanized: "(Gal geomjeong tti)",
    hangul: "갈 검정 띠",
    screen: Gup1(),
    primaryColor: Colors.brown,
    secondaryColor: Colors.black,
  ),

  GradeMenuOption(
    grade: "Negro |",
    romanized: "(Geomjeong 1 dan)",
    hangul: "검정 1단",
    screen: Dan1(),
    primaryColor: Colors.black,
  ),
  GradeMenuOption(
    grade: "Negro ||",
    romanized: "(Geomjeong 2 dan)",
    hangul: "검정 2단",
    screen: Dan2(),
    primaryColor: Colors.black,
  ),
  GradeMenuOption(
    grade: "Negro |||",
    romanized: "(Geomjeong 3 dan)",
    hangul: "검정 3단",
    screen: Dan3(),
    primaryColor: Colors.black,
  ),
  GradeMenuOption(
    grade: "Negro ||||",
    romanized: "(Geomjeong 4 dan)",
    hangul: "검정 4단",
    screen: Dan4(),
    primaryColor: Colors.black,
  ),
  GradeMenuOption(
    grade: "Negro |||||",
    romanized: "(Geomjeong 5 dan)",
    hangul: "검정 5단",
    screen: Dan5(),
    primaryColor: Colors.black,
  ),
];
