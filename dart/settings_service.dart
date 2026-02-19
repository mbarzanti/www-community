import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/settings_entity.dart';
import 'package:elementary/elementary.dart';

/// Сервис для работы с настройками.
class SettingsService {
  /// Переменная проверки активности текущих настроект доступа к DKC API.
  /// Сделано для того, чтоб при загрузке проверять доступ к DKC API,
  /// т.к. у [AccessToken] переодически истекает срок действия и его надо обновлять.
  final isDkcApiAccessSettingsActive = StateNotifier<bool>();

  /// Текущие настройки, которые каждый раз обновляются при запуске приложения и при изменении.
  SettingsEntity currentSettings = SettingsEntity.empty();

  /// Конструктор.
  SettingsService() {
    isDkcApiAccessSettingsActive.accept(false);
  }
}
