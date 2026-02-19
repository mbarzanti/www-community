// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'material_dto_api.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

MaterialDtoApi _$MaterialDtoApiFromJson(Map<String, dynamic> json) =>
    MaterialDtoApi(
      id: json['id'] as int,
      node_id: json['node_id'] as int,
      name: json['name'] as String,
      etim_class_id: json['etim_class_id'] as String,
      type: json['type'] as String,
      series: json['series'] as String,
      country: json['country'] as String,
      unit: json['unit'] as String,
      volume: (json['volume'] as num).toDouble(),
      weight: (json['weight'] as num).toDouble(),
      code: json['code'] as String,
      url: json['url'] as String,
      no_price: json['no_price'] as bool,
      price: (json['price'] as num).toDouble(),
      barcode: (json['barcode'] as List<dynamic>).map((e) => e as int).toList(),
      thumbnail_url: json['thumbnail_url'] as String,
      additional_images: (json['additional_images'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      attributes: Map<String, String>.from(json['attributes'] as Map),
      etim_attributes: Map<String, String>.from(json['etim_attributes'] as Map),
      packing: Map<String, int>.from(json['packing'] as Map),
      avg_delivery: Map<String, int>.from(json['avg_delivery'] as Map),
      accessories: (json['accessories'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      accessories_codes: (json['accessories_codes'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
    );

Map<String, dynamic> _$MaterialDtoApiToJson(MaterialDtoApi instance) =>
    <String, dynamic>{
      'id': instance.id,
      'node_id': instance.node_id,
      'name': instance.name,
      'etim_class_id': instance.etim_class_id,
      'type': instance.type,
      'series': instance.series,
      'country': instance.country,
      'unit': instance.unit,
      'volume': instance.volume,
      'weight': instance.weight,
      'code': instance.code,
      'url': instance.url,
      'no_price': instance.no_price,
      'price': instance.price,
      'barcode': instance.barcode,
      'thumbnail_url': instance.thumbnail_url,
      'additional_images': instance.additional_images,
      'attributes': instance.attributes,
      'etim_attributes': instance.etim_attributes,
      'packing': instance.packing,
      'avg_delivery': instance.avg_delivery,
      'accessories': instance.accessories,
      'accessories_codes': instance.accessories_codes,
    };
