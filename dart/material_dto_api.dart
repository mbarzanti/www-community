import 'package:dkc_cabinet_configurator/features/configurator/domain/entity/material.dart';
import 'package:dkc_cabinet_configurator/features/settings/domain/entity/access_token.dart';
import 'package:json_annotation/json_annotation.dart';

part 'material_dto_api.g.dart';

/// DTO для материала [Material] при работе с DKC API.
/// Получение материала от DKC API происходит при запросе по артикулу [code] с указанием ключа доступа [AccessToken].
@JsonSerializable()
class MaterialDtoApi {
  /// Уникальный идентификатор элемента в БД.
  final int id;

  /// Уникальный идентификатор ветки каталога в БД.
  final int node_id;

  /// Наименование товара.
  final String name;

  /// ID класса ETIM.
  final String etim_class_id;

  /// Тип товара.
  final String type;

  /// Серия товара.
  final String series;

  /// Страна происхождения товара.
  final String country;

  /// Единица измерения.
  final String unit;

  /// Объем за 1 минимальную единицу измерения, м³.
  final double volume;

  /// Вес за 1 минимальную единицу измерения, кг.
  final double weight;

  /// Артикул товара.
  final String code;

  /// Ссылка на карточку материала на сайте
  final String url;

  /// Цена по запросу ДА(true)/НЕТ(false)
  final bool no_price;

  /// Цена товара без учета скидок без НДС.
  final double price;

  /// Массив штрих-кодов.
  final List<int> barcode;

  /// URL картинки товара.
  final String thumbnail_url;

  /// Массив URL дополнительных изображений товара.
  final List<String> additional_images;

  /// Набор значений key-value, отображающих свойства товара.
  final Map<String, String> attributes;

  /// Набор значений key-value, отображающих свойства товара в формате ETIM.
  /// Ключ - технический код признака ETIM. Значение - технический код значения ETIM.
  final Map<String, String> etim_attributes;

  /// Набор значений key-value, отображающих упаковки товара.
  /// Ключ - наименование упаковки. Значение - кол-во товара в упаковке.
  final Map<String, int> packing;

  /// Набор значений key-value, отображающих средний срок поставки товара на склад.
  /// Ключ - ID склада. Значение - кол-во дней.
  final Map<String, int> avg_delivery;

  /// Массив аксессуаров к данному материалу. Указаны id материалов.
  final List<String> accessories;

  /// Массив аксессуаров к данному материалу. Указаны code материалов.
  final List<String> accessories_codes;

  /// Конструктор.
  MaterialDtoApi({
    required this.id,
    required this.node_id,
    required this.name,
    required this.etim_class_id,
    required this.type,
    required this.series,
    required this.country,
    required this.unit,
    required this.volume,
    required this.weight,
    required this.code,
    required this.url,
    required this.no_price,
    required this.price,
    required this.barcode,
    required this.thumbnail_url,
    required this.additional_images,
    required this.attributes,
    required this.etim_attributes,
    required this.packing,
    required this.avg_delivery,
    required this.accessories,
    required this.accessories_codes,
  });

  /// Конструктор из JSON.
  factory MaterialDtoApi.fromJson(Map<String, dynamic> json) =>
      _$MaterialDtoApiFromJson(json['material'] as Map<String, dynamic>);
}
