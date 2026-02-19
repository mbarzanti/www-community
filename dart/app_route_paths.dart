import 'package:dkc_cabinet_configurator/features/configurator/screens/configurator_screen.dart';
import 'package:dkc_cabinet_configurator/features/settings/screens/settings_screen.dart';
import 'package:dkc_cabinet_configurator/features/splash/screens/splash_screen.dart';

/// Список всех путей, которые используются для навигации.
abstract class AppRoutePaths {
  /// Путь к [SplashScreen].
  static const String splash = '/splash';

  /// Путь к [ConfiguratorScreen].
  static const String configurator = '/configurator';

  /// Путь к [SettingsScreen].
  static const String settings = '/settings';
}
