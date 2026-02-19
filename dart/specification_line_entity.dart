import 'package:dkc_cabinet_configurator/features/configurator/domain/entity/material.dart';
import 'package:dkc_cabinet_configurator/features/specification/domain/entity/specification_entity.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'specification_line_entity.freezed.dart';

/// Сущность [SpecificationLineEntity] является строкой в спецификации [SpecificationEntity].
@freezed
class SpecificationLineEntity with _$SpecificationLineEntity {
  /// Конструктор.
  const factory SpecificationLineEntity({
    required Material material,
    required int quantity,
    // required String materialType,
  }) = _SpecificationLineEntity;
}
