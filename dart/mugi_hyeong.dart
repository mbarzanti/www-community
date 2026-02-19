import 'package:flutter/material.dart';
import 'package:lqh_app/models/mugi_hyeong/mugi_hyeong_models.dart';
import 'package:lqh_app/services/mugi_hyeong_service.dart';
import 'package:lqh_app/screens/views/armed/hyeong/mugi_hyeong_type_view.dart';
import 'package:lqh_app/widgets/card_sub_menu.dart';

class MugiHyeong extends StatelessWidget {
  const MugiHyeong({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        iconTheme: const IconThemeData(color: Colors.yellow, weight: 300),
        backgroundColor: Colors.black,
        title: const Text(
          'Figuras con armas (Mugi Hyeong)',
          style: TextStyle(color: Colors.yellow),
        ),
      ),
      body: FutureBuilder<List<MugiHyeongType>>(
        future: MugiHyeongService.loadGroups(),

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

          final types = snapshot.data;

          if (types == null || types.isEmpty) {
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
            itemCount: types.length,
            itemBuilder: (context, index) {
              final type = types[index];

              return CardSubMenu(
                title: type.type, // ← NO figure.figure
                onTap: () {
                  Navigator.push(
                    context,
                    PageRouteBuilder(
                      transitionDuration: const Duration(milliseconds: 200),
                      pageBuilder: (_, _, _) =>
                          MugiHyeongTypeView(mugiHyeongType: type),
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
