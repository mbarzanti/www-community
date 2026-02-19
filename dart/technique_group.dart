import 'subgroup.dart';
import 'technique.dart';

class TechniqueGroup {
  final int group;
  final String name;
  final List<Subgroup>? subgroups;
  final List<Technique>? techniques;

  TechniqueGroup({
    required this.group,
    required this.name,
    this.subgroups,
    this.techniques,
  });

  factory TechniqueGroup.fromJson(Map<String, dynamic> json) {
    return TechniqueGroup(
      group: json['group'] ?? json['grupo'],
      name: json['name'],
      subgroups: json['subgroups'] != null
          ? (json['subgroups'] as List)
                .map((e) => Subgroup.fromJson(e))
                .toList()
          : null,
      techniques: json['techniques'] != null
          ? (json['techniques'] as List)
                .map((e) => Technique.fromJson(e))
                .toList()
          : null,
    );
  }

  bool get hasSubgroups => subgroups != null && subgroups!.isNotEmpty;
  bool get hasTechniques => techniques != null && techniques!.isNotEmpty;
}
