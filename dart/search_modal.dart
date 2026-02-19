import 'package:flutter/material.dart';
import 'package:lqh_app/data/principal_menu_options.dart';
import 'package:lqh_app/widgets/dark_search_bar.dart';

class SearchModal {
  static void open(BuildContext context) {
    final TextEditingController searchController = TextEditingController();

    showDialog(
      context: context,
      barrierDismissible: true,
      barrierColor: Colors.black.withValues(alpha: 0.7),
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setState) {
            final query = searchController.text.toLowerCase();
            final results = principalMenuOption.where((option) {
              return option.spanish.toLowerCase().contains(query) ||
                  option.romanized.toLowerCase().contains(query) ||
                  option.hangul.toLowerCase().contains(query);
            }).toList();

            return Dialog(
              backgroundColor: const Color(0xFF121212),
              insetPadding: const EdgeInsets.all(16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(16),
              ),
              child: SizedBox(
                height: MediaQuery.of(context).size.height * 0.7,
                child: Column(
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(12),
                      child: DarkSearchBar(
                        controller: searchController,
                        onChanged: (_) => setState(() {}),
                      ),
                    ),
                    const Divider(color: Colors.grey),
                    Expanded(
                      child: results.isEmpty
                          ? const Center(
                              child: Text(
                                'Sin resultados',
                                style: TextStyle(color: Colors.grey),
                              ),
                            )
                          : ListView.builder(
                              itemCount: results.length,
                              itemBuilder: (_, index) {
                                final item = results[index];
                                return ListTile(
                                  title: Text(
                                    item.spanish,
                                    style: const TextStyle(color: Colors.white),
                                  ),
                                  subtitle: Text(
                                    '${item.romanized} · ${item.hangul}',
                                    style: TextStyle(
                                      color: Colors.grey.shade400,
                                    ),
                                  ),
                                  onTap: () {
                                    Navigator.pop(context);
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                        builder: (_) => item.screen,
                                      ),
                                    );
                                  },
                                );
                              },
                            ),
                    ),
                  ],
                ),
              ),
            );
          },
        );
      },
    );
  }
}
