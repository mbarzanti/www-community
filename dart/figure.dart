import 'step.dart';

class Figure {
  final String name;
  final List<Steps> steps;

  Figure({required this.name, required this.steps});

  factory Figure.fromJson(Map<String, dynamic> json) {
    return Figure(
      name: (json['name'] ?? '').toString(),
      steps: (json['steps'] as List).map((e) => Steps.fromJson(e)).toList(),
    );
  }

  bool get hasName => name.trim().isNotEmpty;
}
