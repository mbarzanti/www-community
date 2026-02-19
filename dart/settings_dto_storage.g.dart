// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'settings_dto_storage.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$_SettingsDtoStorage _$$_SettingsDtoStorageFromJson(
        Map<String, dynamic> json) =>
    _$_SettingsDtoStorage(
      masterKey: MasterKeyDtoStorage.fromJson(
          json['masterKey'] as Map<String, dynamic>),
      accessToken: AccessTokenDtoStorage.fromJson(
          json['accessToken'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$$_SettingsDtoStorageToJson(
        _$_SettingsDtoStorage instance) =>
    <String, dynamic>{
      'masterKey': instance.masterKey,
      'accessToken': instance.accessToken,
    };
