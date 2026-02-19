import 'package:dkc_cabinet_configurator/features/settings/domain/entity/settings_entity.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/settings_dto_storage.dart';

/// Интерфейс для хранения настроек приложения.
abstract class ISettingsStorage {
  /// Сохранить настройки.
  Future<bool> setSettings(SettingsEntity settingsEntity);

  /// Получить настройки.
  Future<SettingsDtoStorage?> getSettings();

  /// Удалить настройки.
  Future<bool> removeSettings();
}
