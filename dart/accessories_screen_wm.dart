import 'package:dkc_cabinet_configurator/features/configurator/screens/accessories_screen/accessories_screen.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/accessories_screen/accessories_screen_model.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

AccessoriesScreenWidgetModel _create(BuildContext context) {
  final model = AccessoriesScreenModel();
  return AccessoriesScreenWidgetModel(model);
}

/// Widget model для [AccessoriesScreen].
class AccessoriesScreenWidgetModel extends WidgetModel<AccessoriesScreen, AccessoriesScreenModel>
    implements IAccessoriesScreenWidgetModel {
  /// Конструктор.
  AccessoriesScreenWidgetModel(AccessoriesScreenModel model) : super(model);

  /// Фабрика для [AccessoriesScreenWidgetModel]
  factory AccessoriesScreenWidgetModel.create(BuildContext context) => _create(context);
}

/// Интерфейс для [AccessoriesScreenWidgetModel].
abstract class IAccessoriesScreenWidgetModel extends IWidgetModel {
  ///
}
