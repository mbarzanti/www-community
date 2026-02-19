import 'package:dkc_cabinet_configurator/features/specification/domain/entity/specification_line_entity.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'specification_entity.freezed.dart';

/// Сущность спецификации [SpecificationEntity].
@freezed
class SpecificationEntity with _$SpecificationEntity {
  /// Конструктор.
  const factory SpecificationEntity({
    required List<SpecificationLineEntity> lines,
    required double totalPrice,
    required double totalWeight,
  }) = _SpecificationEntity;
}
