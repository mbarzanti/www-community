import 'package:dkc_cabinet_configurator/features/settings/domain/entity/settings_entity.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/mapper/access_token_mapper.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/mapper/master_key_mapper.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/settings_dto_storage.dart';

/// Маппер для получения [SettingsEntity] из [SettingsDtoStorage]
SettingsEntity mapSettings(SettingsDtoStorage settingsDtoStorage) {
  final masterKey = mapMasterKeyStorage(settingsDtoStorage.masterKey);
  final accessToken = mapAccessTokenStorage(settingsDtoStorage.accessToken);

  return SettingsEntity(
    masterKey: masterKey,
    accessToken: accessToken,
  );
}
