import 'package:freezed_annotation/freezed_annotation.dart';

part 'access_token.freezed.dart';

/// Объект-значение ключа доступа [AccessToken].
/// Ключ доступа необходим для работы с DKC API.
@freezed
class AccessToken with _$AccessToken {
  /// Конструктор.
  const factory AccessToken({
    required String value,
  }) = _AccessToken;
}
