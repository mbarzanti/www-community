import 'package:dkc_cabinet_configurator/features/configurator/screens/configurator_screen.dart';
import 'package:dkc_cabinet_configurator/features/settings/service/settings_service.dart';
import 'package:elementary/elementary.dart';

/// Модель для [ConfiguratorScreen].
class ConfiguratorScreenModel extends ElementaryModel {
  final SettingsService _settingsService;

  /// Состояние настроек доступа к DKC API.
  StateNotifier<bool> get isDkcApiAccessSettingsActive => _settingsService.isDkcApiAccessSettingsActive;

  /// Конструктор.
  ConfiguratorScreenModel(this._settingsService);
}
