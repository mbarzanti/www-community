import 'package:flutter/material.dart';

/// Стить текста для формы.
const textFormFieldStyle = TextStyle(color: Colors.black87, fontWeight: FontWeight.normal);

/// Обормление для формы.
const textFormFieldDecoration = InputDecoration(
  hintStyle: TextStyle(
    color: Colors.black38,
    fontWeight: FontWeight.normal,
    fontStyle: FontStyle.italic,
  ),
  helperStyle: TextStyle(
    color: Colors.black38,
    fontWeight: FontWeight.normal,
    fontStyle: FontStyle.italic,
  ),
  //contentPadding: EdgeInsets.symmetric(vertical: 10.0, horizontal: 20.0),
  border: UnderlineInputBorder(),
  enabledBorder: UnderlineInputBorder(
    borderSide: BorderSide(color: Colors.blueGrey),
  ),
  focusedBorder: UnderlineInputBorder(
    borderSide: BorderSide(color: Colors.blueGrey, width: 2.0),
  ),
  errorBorder: UnderlineInputBorder(
    borderSide: BorderSide(color: Colors.red),
  ),
  focusedErrorBorder: UnderlineInputBorder(
    borderSide: BorderSide(color: Colors.red, width: 2.0),
  ),
);
