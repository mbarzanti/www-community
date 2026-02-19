import 'package:dio/dio.dart';
import 'package:dkc_cabinet_configurator/api/data/access_token_dto_api.dart';
import 'package:dkc_cabinet_configurator/api/data/material_dto_api.dart';
import 'package:dkc_cabinet_configurator/api/service/dkc_api/dkc_api_constants.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/master_key.dart';
import 'package:retrofit/retrofit.dart';

part 'dkc_api.g.dart';

/// Интерфейс API для работы с DKC API.
@RestApi(baseUrl: DkcApiUrls.baseUrl)
abstract class DkcApi {
  /// Создание API клиента [DkcApi].
  factory DkcApi(Dio dio, {String baseUrl}) = _DkcApi;

  /// Получение DTO материала [MaterialDtoApi] по артикулу [code] и с указанием ключа доступа [AccessToken].
  // TODO(martynov): Что лучше укаывать в документации: имя Entity [AccessToken] или имя переменной [accessToken]
  @GET(DkcApiUrls.getMaterialUrl)
  Future<MaterialDtoApi> getMaterial(
    @Query('code') String code,
    @Header('accept') String accept,
    @Header('AccessToken') String accessToken,
  );

  /// Получение DTO ключа доступа [AccessTokenDtoApi] по мастрер ключу [MasterKey].
  @GET(DkcApiUrls.getAccessToken)
  Future<AccessTokenDtoApi> getAccessToken(
    @Path() String masterKey,
    @Header('accept') String accept,
  );
}
