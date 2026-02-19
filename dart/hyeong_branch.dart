import 'figure.dart';
import 'step.dart';

class HyeongBranch {
  final String branch;
  final List<Figure>? figures;
  final List<Steps>? steps;

  HyeongBranch({required this.branch, this.figures, this.steps});

  factory HyeongBranch.fromJson(Map<String, dynamic> json) {
    return HyeongBranch(
      branch: json['branch'],
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
