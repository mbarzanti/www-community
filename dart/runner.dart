import 'dart:async';
import 'dart:io';

import 'package:bitsdojo_window/bitsdojo_window.dart';
import 'package:dkc_cabinet_configurator/features/app/app.dart';
import 'package:dkc_cabinet_configurator/features/app/di/app_scope_register.dart';
import 'package:dkc_cabinet_configurator/util/crashlytics_strategy.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:surf_logger/surf_logger.dart';

/// App launch.
Future<void> run() async {
  /// It must be called so that the orientation does not fall.
  WidgetsFlutterBinding.ensureInitialized();

  /// Fix orientation.
  // TODO(init-project): change as needed or remove.
  await SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);

  _initLogger();
  _runApp();
}

void _runApp() {
  runZonedGuarded<Future<void>>(
    () async {
      final appScopeRegister = AppScopeRegister();
      final appScope = await appScopeRegister.createScope();

      runApp(App(
        appScope: appScope,
        appScopeRegister: appScopeRegister,
      ));

      if (Platform.isWindows) {
        doWhenWindowReady(() {
          const initialSize = Size(800, 600);
          appWindow.minSize = initialSize;
          appWindow.size = initialSize; //default size
          appWindow.title = 'Конфигуратор шкафов DKC';
          appWindow.show();
        });
      }
    },
    (exception, stack) {
      // TODO(init-project): Инициализировать Crashlytics.
      // FirebaseCrashlytics.instance.recordError(exception, stack);
    },
  );
}

void _initLogger() {
  RemoteLogger.addStrategy(CrashlyticsRemoteLogStrategy());
  Logger.addStrategy(DebugLogStrategy());
  Logger.addStrategy(RemoteLogStrategy());
}
