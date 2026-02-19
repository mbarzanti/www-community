import 'package:dkc_cabinet_configurator/features/app/di/app_scope.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/accessories_screen/accessories_screen.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/cabinet_base_screen/cabinet_base_screen.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/configurator_screen.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/configurator_screen_model.dart';
import 'package:dkc_cabinet_configurator/features/configurator/screens/specification_screen/specification_screen.dart';
import 'package:dkc_cabinet_configurator/features/navigation/domain/entity/app_route_names.dart';
import 'package:dkc_cabinet_configurator/features/navigation/service/router.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

ConfiguratorScreenWidgetModel _create(BuildContext context) {
  final appDependencies = context.read<IAppScope>();
  final router = appDependencies.router;
  final settingsService = appDependencies.settingsService;
  final model = ConfiguratorScreenModel(settingsService);
  return ConfiguratorScreenWidgetModel(model, router);
}

/// Widget model для [ConfiguratorScreen].
class ConfiguratorScreenWidgetModel extends WidgetModel<ConfiguratorScreen, ConfiguratorScreenModel>
    implements IConfiguratorScreenWidgetModel {
  ///
  final AppRouter router;
  final _selectedIndex = StateNotifier<int>(initValue: 0);

  final List<Widget> _destinationsList = [
    const CabinetBaseScreen(),
    const AccessoriesScreen(),
    const SpecificationScreen(),
  ];

  @override
  StateNotifier<int> get selectedIndex => _selectedIndex;

  @override
  StateNotifier<bool> get isDkcApiAccessSettingsActive => model.isDkcApiAccessSettingsActive;

  @override
  Widget get destinationWidget => _destinationsList[_selectedIndex.value ?? 0];

  /// Конструктор.
  ConfiguratorScreenWidgetModel(ConfiguratorScreenModel model, this.router) : super(model);

  /// Фабрика для [ConfiguratorScreenWidgetModel]
  factory ConfiguratorScreenWidgetModel.create(BuildContext context) => _create(context);

  @override
  void openSettingsScreen() {
    router.pushNamed(AppRouteNames.settings);
  }
}

/// Интерфейс для [ConfiguratorScreenWidgetModel].
abstract class IConfiguratorScreenWidgetModel extends IWidgetModel {
  /// Текущее состояние переключателя шкагов в навигации.
  StateNotifier<int> get selectedIndex;

  /// Состояние настроек доступа к DKC API.
  StateNotifier<bool> get isDkcApiAccessSettingsActive;

  /// Виджет текщего шага.
  Widget get destinationWidget;

  /// Открыть окно настроек.
  void openSettingsScreen();
}
