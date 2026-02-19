import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:lqh_app/models/mugi_hyeong/mugi_hyeong_models.dart';

class MugiHyeongService {
  static Future<List<MugiHyeongType>> loadGroups() async {
    final jsonString = await rootBundle.loadString('assets/json/mugi_hyeong.json');

    final List<dynamic> jsonData = json.decode(jsonString);

    return jsonData.map((e) => MugiHyeongType.fromJson(e)).toList();
  }
}
