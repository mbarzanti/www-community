import '../hyeong/step.dart';

class Basic {
  final String name;
  final List<Steps> steps;

  Basic({required this.name, required this.steps});

  factory Basic.fromJson(Map<String, dynamic> json) {
    return Basic(
      name: (json['name'] ?? '').toString(),
      steps: (json['steps'] as List).map((e) => Steps.fromJson(e)).toList(),
    );
  }

  bool get hasName => name.trim().isNotEmpty;
}
