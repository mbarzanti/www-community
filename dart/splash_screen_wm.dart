import 'package:dkc_cabinet_configurator/api/service/dkc_api/dkc_api.dart';
import 'package:dkc_cabinet_configurator/features/app/di/app_scope.dart';
import 'package:dkc_cabinet_configurator/features/configurator/domain/repository/material_repository.dart';
import 'package:dkc_cabinet_configurator/features/navigation/domain/entity/app_route_names.dart';
import 'package:dkc_cabinet_configurator/features/navigation/service/router.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/settings_repository.dart';
import 'package:dkc_cabinet_configurator/features/settings/service/settings_service.dart';
import 'package:dkc_cabinet_configurator/features/splash/screens/splash_screen.dart';
import 'package:dkc_cabinet_configurator/features/splash/screens/splash_screen_model.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

SplashScreenWidgetModel _create(BuildContext context) {
  final appDependencies = context.read<IAppScope>();
  final settingsRepository = SettingsRepository(appDependencies.settingsStorage);
  final materialRepository = MaterialRepository(DkcApi(appDependencies.dio));
  final router = appDependencies.router;
  final settingsService = appDependencies.settingsService;
  final model = SplashScreenModel(settingsRepository, materialRepository);
  return SplashScreenWidgetModel(model, router, settingsService);
}

/// Widget model для [SplashScreen].
class SplashScreenWidgetModel extends WidgetModel<SplashScreen, SplashScreenModel> implements ISplashScreenWidgetModel {
  ///
  final AppRouter router;
  final SettingsService _settingsService;

  /// Конструктор.
  SplashScreenWidgetModel(SplashScreenModel model, this.router, this._settingsService) : super(model);

  /// Фабрика для [SplashScreenWidgetModel]
  factory SplashScreenWidgetModel.create(BuildContext context) => _create(context);

  @override
  Future<void> initWidgetModel() async {
    super.initWidgetModel();
    await _loadSettings();
    await _setIsDkcApiAccessSettingsActive();
    router.goNamed(AppRouteNames.configurator);
  }

  /// Загрузка настроек из локального хранилища.
  Future<void> _loadSettings() async {
    final settings = await model.readSettings();
    _settingsService.currentSettings = settings;
    await Future<void>.delayed(const Duration(seconds: 2));
    debugPrint('Загруженные настройки ${_settingsService.currentSettings.toString()}');
  }

  Future<void> _setIsDkcApiAccessSettingsActive() async {
    final settings = _settingsService.currentSettings;
    if (settings.accessToken.value == '') {
      _settingsService.isDkcApiAccessSettingsActive.accept(false);
      debugPrint('В настройках нет записи о ключе доступа.');
    } else {
      final materialResult = await model.getTestMaterial(settings.accessToken.value);
      materialResult.when(
        success: (_) {
          debugPrint('Текущие настройки доступа к DKC API активны.');
          return _settingsService.isDkcApiAccessSettingsActive.accept(true);
        },
        failure: (_) {
          debugPrint('Текущие настройки доступа к DKC API не активны.');
          return _settingsService.isDkcApiAccessSettingsActive.accept(false);
        },
      );
    }
  }
}

/// Интерфейс для [SplashScreenWidgetModel].
abstract class ISplashScreenWidgetModel extends IWidgetModel {}
