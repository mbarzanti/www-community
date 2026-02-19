import 'package:dkc_cabinet_configurator/features/settings/domain/entity/settings_entity.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/mapper/settings_mapper.dart';
import 'package:dkc_cabinet_configurator/persistence/storage/settings_storage/settings_storage.dart';

/// Репозиторий [SettingsRepository] для работы с локальным хранилищем.
class SettingsRepository {
  final ISettingsStorage _settings;

  /// Конструктор.
  SettingsRepository(this._settings);

  /// Сохранить настройки.
  Future<void> saveSettings(SettingsEntity settingsEntity) async {
    await _settings.setSettings(settingsEntity);
  }

  /// Получить настройки.
  /// Если настройки еще ни разу не создавались, то записываются и возвращаются пустые настройки.
  Future<SettingsEntity> readSettings() async {
    final settingsDtoStorage = await _settings.getSettings();
    if (settingsDtoStorage == null) {
      final emptySettings = SettingsEntity.empty();
      await saveSettings(emptySettings);
      return emptySettings;
    }
    return mapSettings(settingsDtoStorage);
  }
}
