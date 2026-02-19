import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'master_key.freezed.dart';

/// Объект-значение мастер ключа [MasterKey].
/// Мастер ключ выдается сотрудником DKC, с помощью него создается [AccessToken].
@freezed
class MasterKey with _$MasterKey {
  /// Конструктор.
  const factory MasterKey({
    required String value,
  }) = _MasterKey;
}
