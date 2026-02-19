import 'package:dkc_cabinet_configurator/features/configurator/domain/entity/material.dart';
import 'package:dkc_cabinet_configurator/features/configurator/domain/repository/material_repository.dart';
import 'package:freezed_annotation/freezed_annotation.dart';

part 'failures.freezed.dart';

/// Типы ошибок для [Material] при работе с [MaterialRepository].
@freezed
class MaterialRequestFailure with _$MaterialRequestFailure {
  /// Код 403 "Ключ доступа указан некорректно или истек срок жизни ключа".
  const factory MaterialRequestFailure.wrongRequest(String message) = _WrongRequest;

  /// Неизвестная ошибка.
  const factory MaterialRequestFailure.unknownError(String message) = _UnknownError;
}
