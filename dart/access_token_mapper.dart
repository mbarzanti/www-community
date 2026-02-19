import 'package:dkc_cabinet_configurator/api/data/access_token_dto_api.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:dkc_cabinet_configurator/persistence/data/settings/access_token_dto_storage.dart';

/// Маппер для получения [AccessToken] из [AccessTokenDtoApi]
AccessToken mapAccessTokenApi(AccessTokenDtoApi accessTokenDtoApi) {
  return AccessToken(
    value: accessTokenDtoApi.access_token,
  );
}

/// Маппер для получения [AccessToken] из [AccessTokenDtoStorage]
AccessToken mapAccessTokenStorage(AccessTokenDtoStorage accessTokenDtoStorage) {
  return AccessToken(
    value: accessTokenDtoStorage.value,
  );
}
