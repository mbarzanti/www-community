import 'basic.dart';
import '../hyeong/step.dart';

class GibonTypes {
  final String type;
  final List<Basic>? basics;
  final List<Steps>? steps;

  GibonTypes({
    required this.type,
    this.basics,
    this.steps,
  });

  factory GibonTypes.fromJson(Map<String, dynamic> json) {
    return GibonTypes(
      type: json['type'],
      basics: json['basics'] != null
          ? (json['basics'] as List)
              .map((e) => Basic.fromJson(e))
              .toList()
          : null,
      steps: json['steps'] != null
          ? (json['steps'] as List)
              .map((e) => Steps.fromJson(e))
              .toList()
          : null,
    );
  }

  bool get hasBasics => basics != null && basics!.isNotEmpty;
  bool get hasSteps => steps != null && steps!.isNotEmpty;
}
