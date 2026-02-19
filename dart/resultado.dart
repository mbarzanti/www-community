import 'package:flutter/material.dart';

class Resultado extends StatelessWidget {
  //Componente para mostrar o resultado ao final das perguntas

  final int pontuacao;

  final void Function() quandoReiniciarQuestionario;

  Resultado(this.pontuacao, this.quandoReiniciarQuestionario);

  //Função para mostrar a frase de resultado
  //Pode ser modificada, mudando parâmetros / texto
  String get fraseResultado {
    if (pontuacao < 8) {
      return 'Parabéns!';
    } else if (pontuacao < 12) {
      return 'Você é bom!';
    } else if (pontuacao < 16) {
      return 'Impressionante!';
    } else {
      return 'Nível Jedi!';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Center(
          child: Text(
            fraseResultado,
            style: const TextStyle(fontSize: 28),
          ),
        ),
        ElevatedButton(
          onPressed: quandoReiniciarQuestionario,
          child: const Text(
            'Reiniciar',
            style: TextStyle(color: Colors.blue, fontSize: 18),
          ),
        )
      ],
    );
  }
}
