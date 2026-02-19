import 'package:dkc_cabinet_configurator/features/configurator/screens/specification_screen/specification_screen_wm.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

/// Экран конфигуратора.
class SpecificationScreen extends ElementaryWidget<SpecificationScreenWidgetModel> {
  /// Конструктор.
  const SpecificationScreen({
    Key? key,
  }) : super(SpecificationScreenWidgetModel.create, key: key);

  @override
  Widget build(ISpecificationScreenWidgetModel wm) {
    return Center(child: Text('SpecificationScreen'));
  }
}
