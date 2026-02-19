import 'package:dkc_cabinet_configurator/api/data/material_dto_api.dart';
import 'package:dkc_cabinet_configurator/features/configurator/domain/entity/material.dart';

/// Маппер для получения [Material] из [MaterialDtoApi]
Material mapMaterial(MaterialDtoApi materialDtoApi) {
  return Material(
    code: materialDtoApi.code,
    name: materialDtoApi.name,
    unit: materialDtoApi.unit,
    price: materialDtoApi.price,
    weight: materialDtoApi.weight,
  );
}
