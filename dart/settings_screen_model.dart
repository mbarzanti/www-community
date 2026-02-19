import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/master_key.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/access_token_repository.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/data/results.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/settings_repository.dart';
import 'package:dkc_cabinet_configurator/features/settings/screens/settings_screen.dart';
import 'package:dkc_cabinet_configurator/features/settings/service/settings_service.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

/// Модель для [SettingsScreen].
class SettingsScreenModel extends ElementaryModel {
  final SettingsService _settingsService;
  final SettingsRepository _settingsRepository;
  final AccessTokenRepository _accessTokenRepository;

  /// Получить текущий мастер ключ для отображения в поле настроек.
  String get masterKey => _settingsService.currentSettings.masterKey.value;

  /// Конструктор.
  SettingsScreenModel(this._settingsService, this._settingsRepository, this._accessTokenRepository);

  /// Получение ключа доступа [AccessToken] по мастер ключу [MasterKey].
  Future<AccessTokenResult> getAccessToken(String masterKeyString) async {
    final accessTokenResult = await _accessTokenRepository.getAccessToken(masterKeyString);
    return accessTokenResult;
  }

  /// Сохранение обновленных настроек доступа к DKC API в локальном хранилище.
  void saveSettings(String masterKeyString, AccessToken accessToken) {
    final masterKey = MasterKey(value: masterKeyString);
    final newSettings = _settingsService.currentSettings.copyWith(masterKey: masterKey, accessToken: accessToken);
    _settingsService.currentSettings = newSettings;
    _settingsRepository.saveSettings(newSettings);
    debugPrint('Обновленные настройки ${_settingsService.currentSettings.toString()}');
  }

  /// Установить состояние активных настроек доступа к DKC API.
  void setDkcApiAccessSettingsIsActive() {
    _settingsService.isDkcApiAccessSettingsActive.accept(true);
    debugPrint('Настройки доступа активны ${_settingsService.isDkcApiAccessSettingsActive.value}');
  }
}
