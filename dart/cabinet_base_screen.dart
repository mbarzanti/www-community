import 'package:dkc_cabinet_configurator/features/configurator/screens/cabinet_base_screen/cabinet_base_screen_wm.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

/// Экран конфигуратора.
class CabinetBaseScreen extends ElementaryWidget<CabinetBaseScreenWidgetModel> {
  /// Конструктор.
  const CabinetBaseScreen({
    Key? key,
  }) : super(CabinetBaseScreenWidgetModel.create, key: key);

  @override
  Widget build(ICabinetBaseScreenWidgetModel wm) {
    return Center(child: Text('CabinetBaseScreen'));
  }
}
