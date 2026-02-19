import 'package:dkc_cabinet_configurator/features/configurator/screens/specification_screen/specification_screen.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/specification_screen/specification_screen_model.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

SpecificationScreenWidgetModel _create(BuildContext context) {
  final model = SpecificationScreenModel();
  return SpecificationScreenWidgetModel(model);
}

/// Widget model для [SpecificationScreen].
class SpecificationScreenWidgetModel extends WidgetModel<SpecificationScreen, SpecificationScreenModel>
    implements ISpecificationScreenWidgetModel {
  /// Конструктор.
  SpecificationScreenWidgetModel(SpecificationScreenModel model) : super(model);

  /// Фабрика для [SpecificationScreenWidgetModel]
  factory SpecificationScreenWidgetModel.create(BuildContext context) => _create(context);
}

/// Интерфейс для [SpecificationScreenWidgetModel].
abstract class ISpecificationScreenWidgetModel extends IWidgetModel {
  ///
}
