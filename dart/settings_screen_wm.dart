import 'package:dkc_cabinet_configurator/api/service/dkc_api/dkc_api.dart';
import 'package:dkc_cabinet_configurator/features/app/di/app_scope.dart';
import 'package:dkc_cabinet_configurator/features/navigation/service/router.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/access_token_repository.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/settings_repository.dart';
import 'package:dkc_cabinet_configurator/features/settings/screens/settings_screen.dart';
import 'package:dkc_cabinet_configurator/features/settings/screens/settings_screen_model.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

SettingsScreenWidgetModel _create(BuildContext context) {
  final appDependencies = context.read<IAppScope>();
  final settingsRepository = SettingsRepository(appDependencies.settingsStorage);
  final accessTokenRepository = AccessTokenRepository(DkcApi(appDependencies.dio));
  final router = appDependencies.router;
  final settingsService = appDependencies.settingsService;
  final formKey = GlobalKey<FormState>();
  final scaffoldMessengerKey = GlobalKey<ScaffoldMessengerState>();
  final model = SettingsScreenModel(settingsService, settingsRepository, accessTokenRepository);
  return SettingsScreenWidgetModel(model, router, formKey, scaffoldMessengerKey);
}

/// Widget model для [SettingsScreen].
class SettingsScreenWidgetModel extends WidgetModel<SettingsScreen, SettingsScreenModel>
    implements ISettingsScreenWidgetModel {
  ///
  final AppRouter router;
  final GlobalKey<FormState> _masterKeyFormKey;
  final GlobalKey<ScaffoldMessengerState> _scaffoldMessengerKey;
  final TextEditingController _masterKeyController = TextEditingController();

  @override
  GlobalKey<FormState> get masterKeyFormKey => _masterKeyFormKey;

  @override
  GlobalKey<ScaffoldMessengerState> get scaffoldMessengerKey => _scaffoldMessengerKey;

  @override
  TextEditingController get masterKeyController => _masterKeyController;

  /// Конструктор.
  SettingsScreenWidgetModel(SettingsScreenModel model, this.router, this._masterKeyFormKey, this._scaffoldMessengerKey)
      : super(model);

  /// Фабрика для [SettingsScreenWidgetModel]
  factory SettingsScreenWidgetModel.create(BuildContext context) => _create(context);

  @override
  Future<void> submitMasterKeyForm() async {
    if (masterKeyFormKey.currentState!.validate()) {
      final masterKeyFormString = masterKeyController.value.text;
      final accessTokenResult = await model.getAccessToken(masterKeyFormString);
      accessTokenResult.when(
        success: (accessToken) {
          _saveSettings(masterKeyFormString, accessToken);
          model.setDkcApiAccessSettingsIsActive();
        },
        failure: (accessTokenRequestFailure) {
          accessTokenRequestFailure.when(
            wrongRequest: _showSnackBar,
            serverError: _showSnackBar,
            unknownError: _showSnackBar,
          );
        },
      );
    }
  }

  @override
  void returnToConfigurator() {
    router.pop();
  }

  @override
  String? fieldValidator(String? value) {
    if (value == null || value.isEmpty) {
      return 'Поле не может быть пустым';
    }
    return null;
  }

  @override
  void initWidgetModel() {
    super.initWidgetModel();
    _masterKeyController.text = model.masterKey;
  }

  void _saveSettings(String masterKeyFormString, AccessToken accessToken) {
    model.saveSettings(masterKeyFormString, accessToken);
    _showSnackBar('Настройки доступа получены и успешно сохранены');
  }

  void _showSnackBar(String message) {
    scaffoldMessengerKey.currentState!.showSnackBar(
      SnackBar(
        content: Text(message),
      ),
    );
  }
}

/// Интерфейс для [SettingsScreenWidgetModel].
abstract class ISettingsScreenWidgetModel extends IWidgetModel {
  ///
  GlobalKey<FormState> get masterKeyFormKey;

  ///
  GlobalKey<ScaffoldMessengerState> get scaffoldMessengerKey;

  ///
  TextEditingController get masterKeyController;

  ///
  void returnToConfigurator();

  ///
  String? fieldValidator(String? value);

  ///
  void submitMasterKeyForm();
}
