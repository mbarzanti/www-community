import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:lqh_app/models/hyeong/hyeong_models.dart';

class HyeongService {
  static Future<List<HyeongBranch>> loadGroups() async {
    final jsonString = await rootBundle.loadString('assets/json/hyeong.json');

    final List<dynamic> jsonData = json.decode(jsonString);

    return jsonData.map((e) => HyeongBranch.fromJson(e)).toList();
  }
}
