import 'package:dio/dio.dart';
import 'package:dkc_cabinet_configurator/api/service/dkc_api/dkc_api.dart';
import 'package:dkc_cabinet_configurator/api/service/dkc_api/dkc_api_constants.dart';
import 'package:dkc_cabinet_configurator/features/configurator/domain/mappers/material_mapper.dart';
import 'package:dkc_cabinet_configurator/features/configurator/domain/repository/data/failures.dart';
import 'package:dkc_cabinet_configurator/features/configurator/domain/repository/data/results.dart';
import 'package:flutter/material.dart';

/// [MaterialRepository] работает с [DkcApi].
class MaterialRepository {
  final DkcApi _client;

  /// Конструктор.
  MaterialRepository(this._client);

  /// Получение [Material] по артикулу [code] и с указанием [accessToken].
  Future<MaterialResult> getMaterial(String code, String accessToken) async {
    try {
      final material = await _client.getMaterial(code, DkcApiHeaders.acceptHeader, accessToken).then(mapMaterial);
      return MaterialResult.success(material: material);
    } on DioError catch (dioError) {
      final res = dioError.response;
      debugPrint('Статус код: ${res?.statusCode.toString()}');

      if (res!.statusCode == 403) {
        return const MaterialResult.failure(
          MaterialRequestFailure.wrongRequest('Ключ доступа указан некорректно или истек срок жизни ключа'),
        );
      }

      return const MaterialResult.failure(MaterialRequestFailure.unknownError('Неизвестная ошибка'));
    }
  }
}
