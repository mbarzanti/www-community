import 'package:dkc_cabinet_configurator/assets/themes/form_style.dart';
import 'package:dkc_cabinet_configurator/features/settings/screens/settings_screen_wm.dart';
import 'package:elementary/elementary.dart';
import 'package:flutter/material.dart';

/// Экран конфигуратора.
class SettingsScreen extends ElementaryWidget<SettingsScreenWidgetModel> {
  /// Конструктор.
  const SettingsScreen({
    Key? key,
  }) : super(SettingsScreenWidgetModel.create, key: key);

  @override
  Widget build(ISettingsScreenWidgetModel wm) {
    return ScaffoldMessenger(
      key: wm.scaffoldMessengerKey,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Настройки приложения'),
          centerTitle: true,
        ),
        body: Center(
          child: SizedBox(
            width: 400,
            height: 500,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Text(
                  'Настройки доступа к API DKC',
                  style: TextStyle(
                    fontSize: 18,
                  ),
                ),
                Form(
                  key: wm.masterKeyFormKey,
                  child: Column(
                    children: [
                      Row(
                        children: [
                          SizedBox(
                            width: 400,
                            height: 60,
                            child: TextFormField(
                              autovalidateMode: AutovalidateMode.onUserInteraction,
                              validator: wm.fieldValidator,
                              controller: wm.masterKeyController,
                              style: textFormFieldStyle,
                              decoration: textFormFieldDecoration.copyWith(
                                hintText: 'Поле ввода мастер ключа',
                                helperText: 'Ключ можно получить у сотрудников компании DKC',
                                prefixIcon: const Icon(Icons.key, color: Colors.blueGrey),
                              ),
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 10),
                      ElevatedButton(
                        onPressed: wm.submitMasterKeyForm,
                        child: const Text('Обновить ключ доступа к API и сохранить настройки'),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
