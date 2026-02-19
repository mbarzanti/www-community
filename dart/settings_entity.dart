import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/master_key.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'settings_entity.freezed.dart';

/// Сущность настроек доступа к DKC API [SettingsEntity].
@freezed
class SettingsEntity with _$SettingsEntity {
  /// Конструктор.
  const factory SettingsEntity({
    required MasterKey masterKey,
    required AccessToken accessToken,
  }) = _SettingsEntity;

  const SettingsEntity._();

  /// Конструктор с пустыми настройками.
  factory SettingsEntity.empty() => const SettingsEntity(
        masterKey: MasterKey(value: ''),
        accessToken: AccessToken(value: ''),
      );
}
