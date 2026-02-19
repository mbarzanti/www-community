import 'dart:ui';

import 'package:dio/adapter.dart';
import 'package:dio/dio.dart';
import 'package:dkc_cabinet_configurator/config/app_config.dart';
import 'package:dkc_cabinet_configurator/config/environment/environment.dart';
import 'package:dkc_cabinet_configurator/features/navigation/domain/entity/app_route_paths.dart';
import 'package:dkc_cabinet_configurator/features/navigation/domain/entity/app_routes.dart';
import 'package:dkc_cabinet_configurator/features/navigation/service/router.dart';
import 'package:dkc_cabinet_configurator/features/settings/service/settings_service.dart';
import 'package:dkc_cabinet_configurator/persistence/storage/settings_storage/settings_storage.dart';
import 'package:dkc_cabinet_configurator/persistence/storage/settings_storage/settings_storage_prefs_impl.dart';
import 'package:dkc_cabinet_configurator/util/default_error_handler.dart';
import 'package:elementary/elementary.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Scope of dependencies which need through all app's life.
class AppScope implements IAppScope {
  late final Dio _dio;
  late final ErrorHandler _errorHandler;
  late final AppRouter _router;
  final SharedPreferences _sharedPreferences;
  late final SettingsService _settingsService;
  late final ISettingsStorage _settingsStorage;

  @override
  late VoidCallback applicationRebuilder;

  @override
  Dio get dio => _dio;

  @override
  ErrorHandler get errorHandler => _errorHandler;

  @override
  AppRouter get router => _router;

  @override
  SharedPreferences get sharedPreferences => _sharedPreferences;

  @override
  SettingsService get settingsService => _settingsService;

  @override
  ISettingsStorage get settingsStorage => _settingsStorage;

  /// Create an instance [AppScope].
  AppScope(
    this._sharedPreferences,
  ) {
    /// List interceptor. Fill in as needed.
    final additionalInterceptors = <Interceptor>[];

    _dio = _initDio(additionalInterceptors);
    _errorHandler = DefaultErrorHandler();
    _router = AppRouter(
      delegate: AppRoutes(),
      initialLocation: AppRoutePaths.splash,
    );
    _settingsService = SettingsService();
    _settingsStorage = SettingsStoragePrefsImpl(_sharedPreferences);
  }

  Dio _initDio(Iterable<Interceptor> additionalInterceptors) {
    const timeout = Duration(seconds: 30);

    final dio = Dio();

    dio.options
      ..baseUrl = Environment<AppConfig>.instance().config.url
      ..connectTimeout = timeout.inMilliseconds
      ..receiveTimeout = timeout.inMilliseconds
      ..sendTimeout = timeout.inMilliseconds;

    (dio.httpClientAdapter as DefaultHttpClientAdapter).onHttpClientCreate = (client) {
      final proxyUrl = Environment<AppConfig>.instance().config.proxyUrl;
      if (proxyUrl != null && proxyUrl.isNotEmpty) {
        client
          ..findProxy = (uri) {
            return 'PROXY $proxyUrl';
          }
          ..badCertificateCallback = (_, __, ___) {
            return true;
          };
      }

      return client;
    };

    dio.interceptors.addAll(additionalInterceptors);

    if (Environment<AppConfig>.instance().isDebug) {
      dio.interceptors.add(LogInterceptor(requestBody: true, responseBody: true));
    }

    return dio;
  }
}

/// App dependencies.
abstract class IAppScope {
  /// Http client.
  Dio get dio;

  /// Interface for handle error in business logic.
  ErrorHandler get errorHandler;

  /// Callback to rebuild the whole application.
  VoidCallback get applicationRebuilder;

  /// Class that coordinates navigation for the whole app.
  AppRouter get router;

  ///
  SettingsService get settingsService;

  ///
  SharedPreferences get sharedPreferences;

  ///
  ISettingsStorage get settingsStorage;
}
