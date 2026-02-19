import 'package:flutter/material.dart';
import 'package:lqh_app/models/techniques/technique_models.dart';
import 'package:lqh_app/services/techniques_service.dart';
import 'package:lqh_app/screens/views/techniques/technique_group_view.dart';
import 'package:lqh_app/widgets/card_sub_menu.dart';

class SelfDefenseTechniques extends StatelessWidget {
  const SelfDefenseTechniques({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        iconTheme: const IconThemeData(color: Colors.yellow, weight: 300),
        backgroundColor: Colors.black,
        title: const Text(
          'Técnicas (Ho Sin Sul)',
          style: TextStyle(color: Colors.yellow),
        ),
      ),
      body: FutureBuilder<List<TechniqueGroup>>(
        future: TechniquesService.loadGroups(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Text(
                  'ERROR:\n${snapshot.error}',
                  textAlign: TextAlign.center,
                  style: const TextStyle(color: Colors.red),
                ),
              ),
            );
          }

          final groups = snapshot.data;

          if (groups == null || groups.isEmpty) {
            return const Center(
              child: Text(
                'No hay técnicas disponibles',
                style: TextStyle(color: Colors.white54),
              ),
            );
          }

          return GridView.builder(
            padding: const EdgeInsets.all(12),
            gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
              crossAxisCount: 2,
              mainAxisSpacing: 12,
              crossAxisSpacing: 12,
              childAspectRatio: 1.2,
            ),
            itemCount: groups.length,
            itemBuilder: (context, index) {
              final group = groups[index];

              return CardSubMenu(
                title: group.name,
                onTap: () {
                  Navigator.push(
                    context,
                    PageRouteBuilder(
                      transitionDuration: const Duration(milliseconds: 200),
                      pageBuilder: (_, _, _) =>
                          TechniqueGroupView(group: group),
                      transitionsBuilder: (_, animation, _, child) {
                        return FadeTransition(opacity: animation, child: child);
                      },
                    ),
                  );
                },
              );
            },
          );
        },
      ),
    );
  }
}
