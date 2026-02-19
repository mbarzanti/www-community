import 'package:dio/dio.dart';
import 'package:dkc_cabinet_configurator/api/service/dkc_api/dkc_api.dart';
import 'package:dkc_cabinet_configurator/api/service/dkc_api/dkc_api_constants.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/mapper/access_token_mapper.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/data/failures.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/repository/data/results.dart';
import 'package:flutter/material.dart';

/// Репозиторий [AccessTokenRepository] для работы с DKC API.
class AccessTokenRepository {
  final DkcApi _client;

  /// Конструктор.
  AccessTokenRepository(this._client);

  /// Получение [AccessToken] по мастер ключу [masterKeyString].
  Future<AccessTokenResult> getAccessToken(String masterKeyString) async {
    try {
      final accessToken =
          await _client.getAccessToken(masterKeyString, DkcApiHeaders.acceptHeader).then(mapAccessTokenApi);
      return AccessTokenResult.success(accessToken: accessToken);
    } on DioError catch (dioError) {
      final res = dioError.response;
      debugPrint('Статус код: ${res?.statusCode.toString()}');

      if (res!.statusCode == 400 || res.statusCode == 403 || res.statusCode == 405) {
        return const AccessTokenResult.failure(
          AccessTokenRequestFailure.wrongRequest('Мастер ключ неверный или просрочен'),
        );
      }

      if (res.statusCode == 500) {
        return const AccessTokenResult.failure(
          AccessTokenRequestFailure.serverError('Ошибка сервера'),
        );
      }

      return const AccessTokenResult.failure(AccessTokenRequestFailure.unknownError('Неизвестная ошибка'));
    }
  }
}
