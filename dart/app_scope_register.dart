import 'package:dkc_cabinet_configurator/features/app/di/app_scope.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Регистрация и настройка сторонних зависимостей для общих зависимостей приложения.
class AppScopeRegister {
  /// Создаем зависимости.
  Future<AppScope> createScope() async {
    final sharedPreferences = await _createSharedPreferences();
    return AppScope(sharedPreferences);
  }

  Future<SharedPreferences> _createSharedPreferences() => SharedPreferences.getInstance();
}
