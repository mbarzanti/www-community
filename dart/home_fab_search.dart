import 'package:flutter/material.dart';
import 'search_modal.dart';

class HomeFABSearch extends StatelessWidget {
  const HomeFABSearch({super.key});

  @override
  Widget build(BuildContext context) {
    return Positioned(
      top: 12,
      right: 12,
      child: FloatingActionButton(
        heroTag: 'search_fab',
        mini: true,
        backgroundColor: const Color(0xFF1E1E1E),
        onPressed: () => SearchModal.open(context),
        child: const Icon(Icons.search, color: Colors.white),
      ),
    );
  }
}
