import 'package:dkc_cabinet_configurator/features/configurator/screens/accessories_screen/accessories_screen_wm.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

/// Экран конфигуратора.
class AccessoriesScreen extends ElementaryWidget<AccessoriesScreenWidgetModel> {
  /// Конструктор.
  const AccessoriesScreen({
    Key? key,
  }) : super(AccessoriesScreenWidgetModel.create, key: key);

  @override
  Widget build(IAccessoriesScreenWidgetModel wm) {
    return Center(child: Text('AccessoriesScreen'));
  }
}
