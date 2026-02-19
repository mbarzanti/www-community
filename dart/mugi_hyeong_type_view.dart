import 'package:flutter/material.dart';
import '../../../../models/mugi_hyeong/mugi_hyeong_models.dart';

class MugiHyeongTypeView extends StatelessWidget {
  final MugiHyeongType mugiHyeongType;

  const MugiHyeongTypeView({super.key, required this.mugiHyeongType});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      appBar: AppBar(
        iconTheme: const IconThemeData(color: Colors.yellow, weight: 300),
        backgroundColor: Colors.black,
        title: Text(
          mugiHyeongType.type,
          style: const TextStyle(color: Colors.yellow),
        ),
      ),
      body: Padding(padding: const EdgeInsets.all(12), child: _buildContent()),
    );
  }

  Widget _buildContent() {
    if (mugiHyeongType.hasFigures) {
      return _buildFigures();
    }

    return const Center(
      child: Text(
        'No hay figuras disponibles.',
        style: TextStyle(color: Colors.white54),
      ),
    );
  }

  Widget _buildFigures() {
    return ListView.builder(
      itemCount: mugiHyeongType.figures!.length,
      itemBuilder: (context, index) {
        final figure = mugiHyeongType.figures![index];

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (figure.hasName)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 8),
                child: Text(
                  figure.name,
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Colors.blueGrey,
                  ),
                ),
              ),
            _buildSteps(figure.steps),
            const SizedBox(height: 16),
          ],
        );
      },
    );
  }

  Widget _buildSteps(List<Steps> steps) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: steps.map(
        (stps) => Padding(
          padding: const EdgeInsets.symmetric(vertical: 4),
          child: RichText(
            text: TextSpan(
              children: [
                TextSpan(
                  text: '${stps.step}. ',
                  style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    color: Colors.white70,
                  ),
                ),
                TextSpan(
                  text: stps.description,
                  style: const TextStyle(color: Colors.grey),
                ),
              ],
            ),
          ),
        ),
      ).toList()
    );
  }
}
