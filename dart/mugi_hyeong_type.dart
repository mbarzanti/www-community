import '../hyeong/figure.dart';
import '../hyeong/step.dart';

class MugiHyeongType {
  final String type;
  final List<Figure>? figures;
  final List<Steps>? steps;

  MugiHyeongType({required this.type, this.figures, this.steps});

  factory MugiHyeongType.fromJson(Map<String, dynamic> json) {
    return MugiHyeongType(
      type: json['type'],
      figures: json['figures'] != null
          ? (json['figures'] as List).map((e) => Figure.fromJson(e)).toList()
          : null,
      steps: json['steps'] != null
          ? (json['steps'] as List).map((e) => Steps.fromJson(e)).toList()
          : null,
    );
  }

  bool get hasFigures => figures != null && figures!.isNotEmpty;
  bool get hasSteps => steps != null && steps!.isNotEmpty;
}
