import 'dart:convert';
import 'package:flutter/services.dart';

import '../models/techniques/technique_group.dart';

class TechniquesService {
  static Future<List<TechniqueGroup>> loadGroups() async {
    final jsonString =
        await rootBundle.loadString('assets/json/all_techniques.json');

    final List<dynamic> jsonData = json.decode(jsonString);

    return jsonData
        .map((e) => TechniqueGroup.fromJson(e))
        .toList();
  }
}
