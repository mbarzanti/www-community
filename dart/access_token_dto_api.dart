import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/master_key.dart';
import 'package:json_annotation/json_annotation.dart';

part 'access_token_dto_api.g.dart';

/// DTO для ключа доступа [AccessToken] при работе с DKC API.
/// Получение ключа доступа от DKC API происходит при запросе по мастер ключу [MasterKey].
@JsonSerializable()
class AccessTokenDtoApi {
  /// Ключ доступа по API.
  final String access_token;

  /// Конструктор.
  AccessTokenDtoApi({
    required this.access_token,
  });

  /// Конструктор из JSON.
  factory AccessTokenDtoApi.fromJson(Map<String, dynamic> json) => _$AccessTokenDtoApiFromJson(json);
}
