import 'package:flutter/material.dart';
import 'package:lqh_app/models/hyeong/hyeong_models.dart';
import 'package:lqh_app/services/hyeong_service.dart';
import 'package:lqh_app/screens/views/unarmed/hyeong/hyeong_branch_view.dart';
import 'package:lqh_app/widgets/card_sub_menu.dart';

class Hyeong extends StatelessWidget {
  const Hyeong({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        iconTheme: const IconThemeData(color: Colors.yellow, weight: 300),
        backgroundColor: Colors.black,
        title: const Text(
          'Figuras (Hyeong)',
          style: TextStyle(color: Colors.yellow),
        ),
      ),
      body: FutureBuilder<List<HyeongBranch>>(
        future: HyeongService.loadGroups(),
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

          final branches = snapshot.data;

          if (branches == null || branches.isEmpty) {
            return const Center(
              child: Text(
                'No hay Figuras disponibles',
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
            itemCount: branches.length,
            itemBuilder: (context, index) {
              final figure = branches[index];

              return CardSubMenu(
                title: figure.branch,
                onTap: () {
                  Navigator.push(
                    context,
                    PageRouteBuilder(
                      transitionDuration: const Duration(milliseconds: 200),
                      pageBuilder: (_, _, _) =>
                          HyeongBranchView(hyeongBranch: figure),
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
