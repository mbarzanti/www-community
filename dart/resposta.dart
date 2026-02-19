import 'package:flutter/material.dart';

class Resposta extends StatelessWidget {

  //Componente que formata e mostra as respostas

  final String texto;
  final void Function() onSelect;

  const Resposta(this.texto, this.onSelect);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(10),
      width: double.infinity,
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.blue,
          foregroundColor: Colors.white,
        ),
        onPressed: onSelect,
        child: Text(texto),
      ),
    );
  }
}
