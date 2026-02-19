import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'access_token_dto_storage.freezed.dart';
part 'access_token_dto_storage.g.dart';

/// DTO для ключа доступа [AccessToken] при работе с локальным хранилищем.
@freezed
class AccessTokenDtoStorage with _$AccessTokenDtoStorage {
  /// Конструктор.
  const factory AccessTokenDtoStorage({
    required String value,
  }) = _AccessTokenDtoStorage;

  /// Конструктор из JSON.
  factory AccessTokenDtoStorage.fromJson(Map<String, dynamic> json) => _$AccessTokenDtoStorageFromJson(json);
}
