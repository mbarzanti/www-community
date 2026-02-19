import 'package:freezed_annotation/freezed_annotation.dart';

part 'material.freezed.dart';

/// Объект-значение для материала [Material].
@freezed
class Material with _$Material {
  /// Конструктор.
  const factory Material({
    required String code,
    required String name,
    required String unit,
    required double price,
    required double weight,
  }) = _Material;
}
