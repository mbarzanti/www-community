import 'package:dkc_cabinet_configurator/features/configurator/screens/cabinet_base_screen/cabinet_base_screen.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/cabinet_base_screen/cabinet_base_screen_model.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

CabinetBaseScreenWidgetModel _create(BuildContext context) {
  final model = CabinetBaseScreenModel();
  return CabinetBaseScreenWidgetModel(model);
}

/// Widget model для [CabinetBaseScreen].
class CabinetBaseScreenWidgetModel extends WidgetModel<CabinetBaseScreen, CabinetBaseScreenModel>
    implements ICabinetBaseScreenWidgetModel {
  /// Конструктор.
  CabinetBaseScreenWidgetModel(CabinetBaseScreenModel model) : super(model);

  /// Фабрика для [CabinetBaseScreenWidgetModel]
  factory CabinetBaseScreenWidgetModel.create(BuildContext context) => _create(context);
}

/// Интерфейс для [CabinetBaseScreenWidgetModel].
abstract class ICabinetBaseScreenWidgetModel extends IWidgetModel {
  ///
}
