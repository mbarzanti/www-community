import 'package:flutter/material.dart';
import 'package:projeto_perguntas/questao.dart';
import 'package:projeto_perguntas/resposta.dart';

class Questionario extends StatelessWidget {
  final int perguntaSelecionada;
  final List<Map<String, Object>> perguntas;
  final void Function(int) quandoResponder;

  Questionario({
    required this.perguntaSelecionada,
    required this.perguntas,
    required this.quandoResponder,
  });

  //Verifica se ainda tem alguma pergunta para mostrar
  bool get temPerguntaSelecionada {
    return perguntaSelecionada < perguntas.length;
  }

  @override
  Widget build(BuildContext context) {
    //Lista que armazena todas as respostas da pergunta atual
    List<Map<String, Object>> respostas = temPerguntaSelecionada
        ? perguntas[perguntaSelecionada].cast()['respostas']
            as List<Map<String, Object>>
        : [];

    return Column(
      children: [
        //Mostrando a questão
        Questao(perguntas[perguntaSelecionada]['texto'].toString()),
        //Os ... mostram todos os elementos da lista
        //Mostrando as respostas
        ...respostas.map((resp) {
          return Resposta(resp['texto'] as String,
              () => quandoResponder(int.parse(resp['pontuacao'].toString())));
        }),
      ],
    );
  }
}
