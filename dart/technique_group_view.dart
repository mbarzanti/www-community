import 'package:flutter/material.dart';
import '../../../models/techniques/technique_models.dart';

class TechniqueGroupView extends StatelessWidget {
  final TechniqueGroup group;

  const TechniqueGroupView({super.key, required this.group});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        iconTheme: const IconThemeData(color: Colors.yellow, weight: 300),
        backgroundColor: Colors.black,
        title: Text(group.name, style: const TextStyle(color: Colors.yellow)),
      ),
      body: Padding(padding: const EdgeInsets.all(12), child: _buildContent()),
    );
  }

  /// Decide qué renderizar según el tipo de grupo
  Widget _buildContent() {
    if (group.hasSubgroups) {
      return _buildSubgroups();
    }

    if (group.hasTechniques) {
      return _buildTechniques(group.techniques!);
    }

    return const Center(
      child: Text(
        'No hay técnicas disponibles',
        style: TextStyle(color: Colors.white54),
      ),
    );
  }

  /// Render para grupos CON subgrupos
  Widget _buildSubgroups() {
    return ListView.builder(
      itemCount: group.subgroups!.length,
      itemBuilder: (context, index) {
        final subgroup = group.subgroups![index];

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (subgroup.hasName)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Text(
                  subgroup.name,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Colors.blueGrey,
                  ),
                ),
              ),

            _buildTechniques(subgroup.techniques),

            const SizedBox(height: 16),
          ],
        );
      },
    );
  }

  /// Render reutilizable para listas de técnicas
  Widget _buildTechniques(List<Technique> techniques) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: techniques
          .map(
            (tech) => Padding(
              padding: const EdgeInsets.symmetric(vertical: 4),
              child: RichText(
                text: TextSpan(
                  children: [
                    TextSpan(
                      text: '${tech.number}. ',
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.white70,
                      ),
                    ),
                    TextSpan(
                      text: tech.description,
                      style: const TextStyle(color: Colors.grey),
                    ),
                  ],
                ),
              ),
            ),
          )
          .toList(),
    );
  }
}
