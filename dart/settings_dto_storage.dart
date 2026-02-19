import 'package:dkc_cabinet_configurator/features/settings/domain/entity/settings_entity.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/access_token_dto_storage.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/master_key_dto_storage.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'settings_dto_storage.freezed.dart';
part 'settings_dto_storage.g.dart';

/// DTO для настроек [SettingsEntity] при работе с локальным хранилищем.
@freezed
class SettingsDtoStorage with _$SettingsDtoStorage {
  /// Конструктор.
  const factory SettingsDtoStorage({
    required MasterKeyDtoStorage masterKey,
    required AccessTokenDtoStorage accessToken,
  }) = _SettingsDtoStorage;

  /// Конструктор из JSON.
  factory SettingsDtoStorage.fromJson(Map<String, dynamic> json) => _$SettingsDtoStorageFromJson(json);
}
