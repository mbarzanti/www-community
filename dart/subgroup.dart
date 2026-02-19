import 'technique.dart';

class Subgroup {
  final String name;
  final List<Technique> techniques;

  Subgroup({
    required this.name,
    required this.techniques,
  });

  factory Subgroup.fromJson(Map<String, dynamic> json) {
    return Subgroup(
      name: (json['name'] ?? '').toString(),
      techniques: (json['techniques'] as List)
          .map((e) => Technique.fromJson(e))
          .toList(),
    );
  }

  bool get hasName => name.trim().isNotEmpty;
}

