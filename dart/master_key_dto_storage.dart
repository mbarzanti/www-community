import 'package:dkc_cabinet_configurator/features/settings/domain/entity/master_key.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'master_key_dto_storage.freezed.dart';
part 'master_key_dto_storage.g.dart';

/// DTO для мастер ключа [MasterKey] при работе с локальным хранилищем.
@freezed
class MasterKeyDtoStorage with _$MasterKeyDtoStorage {
  /// Конструктор.
  const factory MasterKeyDtoStorage({
    required String value,
  }) = _MasterKeyDtoStorage;

  /// Конструктор из JSON.
  factory MasterKeyDtoStorage.fromJson(Map<String, dynamic> json) => _$MasterKeyDtoStorageFromJson(json);
}
