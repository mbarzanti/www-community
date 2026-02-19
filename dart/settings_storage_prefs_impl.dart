import 'dart:convert';

import 'package:dkc_cabinet_configurator/features/settings/domain/entity/settings_entity.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/access_token_dto_storage.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/master_key_dto_storage.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/settings_dto_storage.dart';
import 'package:dkc_cabinet_configurator/persistence/storage/settings_storage/settings_storage.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Реализация интерфейса хранения настроек приложения [ISettingsStorage] через пакет shared_preferences.
class SettingsStoragePrefsImpl implements ISettingsStorage {
  static const String _settings = 'settings';

  final SharedPreferences _prefs;

  /// Конструктор.
  SettingsStoragePrefsImpl(this._prefs);

  @override
  Future<bool> setSettings(SettingsEntity settingsEntity) {
    final settingsDtoStorage = SettingsDtoStorage(
      masterKey: MasterKeyDtoStorage(value: settingsEntity.masterKey.value),
      accessToken: AccessTokenDtoStorage(value: settingsEntity.accessToken.value),
    );
    return _prefs.setString(_settings, json.encode(settingsDtoStorage.toJson()));
  }

  @override
  Future<SettingsDtoStorage?> getSettings() async {
    final settingsJson = _prefs.getString(_settings);
    if (settingsJson == null) {
      return null;
    }
    final settingsDtoStorage = SettingsDtoStorage.fromJson(json.decode(settingsJson) as Map<String, dynamic>);
    return settingsDtoStorage;
  }

  @override
  Future<bool> removeSettings() {
    return _prefs.remove(_settings);
  }
}
