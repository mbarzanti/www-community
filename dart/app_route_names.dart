import 'package:dkc_cabinet_configurator/features/configurator/screens/configurator_screen.dart';
import 'package:dkc_cabinet_configurator/features/settings/screens/settings_screen.dart';
import 'package:dkc_cabinet_configurator/features/splash/screens/splash_screen.dart';

/// Список всех имен для путей, которые используются для навигации.
abstract class AppRouteNames {
  /// Имя для [SplashScreen].
  static const String splash = 'splash';

  /// Имя для [ConfiguratorScreen].
  static const String configurator = 'configurator';

  /// Имя для [SettingsScreen].
  static const String settings = 'settings';
}
