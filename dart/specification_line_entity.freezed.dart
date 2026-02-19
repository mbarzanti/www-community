// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'specification_line_entity.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

/// @nodoc
mixin _$SpecificationLineEntity {
  Material get material => throw _privateConstructorUsedError;
  int get quantity => throw _privateConstructorUsedError;

  @JsonKey(ignore: true)
  $SpecificationLineEntityCopyWith<SpecificationLineEntity> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SpecificationLineEntityCopyWith<$Res> {
  factory $SpecificationLineEntityCopyWith(SpecificationLineEntity value,
          $Res Function(SpecificationLineEntity) then) =
      _$SpecificationLineEntityCopyWithImpl<$Res>;
  $Res call({Material material, int quantity});

  $MaterialCopyWith<$Res> get material;
}

/// @nodoc
class _$SpecificationLineEntityCopyWithImpl<$Res>
    implements $SpecificationLineEntityCopyWith<$Res> {
  _$SpecificationLineEntityCopyWithImpl(this._value, this._then);

  final SpecificationLineEntity _value;
  // ignore: unused_field
  final $Res Function(SpecificationLineEntity) _then;

  @override
  $Res call({
    Object? material = freezed,
    Object? quantity = freezed,
  }) {
    return _then(_value.copyWith(
      material: material == freezed
          ? _value.material
          : material // ignore: cast_nullable_to_non_nullable
              as Material,
      quantity: quantity == freezed
          ? _value.quantity
          : quantity // ignore: cast_nullable_to_non_nullable
              as int,
    ));
  }

  @override
  $MaterialCopyWith<$Res> get material {
    return $MaterialCopyWith<$Res>(_value.material, (value) {
      return _then(_value.copyWith(material: value));
    });
  }
}

/// @nodoc
abstract class _$$_SpecificationLineEntityCopyWith<$Res>
    implements $SpecificationLineEntityCopyWith<$Res> {
  factory _$$_SpecificationLineEntityCopyWith(_$_SpecificationLineEntity value,
          $Res Function(_$_SpecificationLineEntity) then) =
      __$$_SpecificationLineEntityCopyWithImpl<$Res>;
  @override
  $Res call({Material material, int quantity});

  @override
  $MaterialCopyWith<$Res> get material;
}

/// @nodoc
class __$$_SpecificationLineEntityCopyWithImpl<$Res>
    extends _$SpecificationLineEntityCopyWithImpl<$Res>
    implements _$$_SpecificationLineEntityCopyWith<$Res> {
  __$$_SpecificationLineEntityCopyWithImpl(_$_SpecificationLineEntity _value,
      $Res Function(_$_SpecificationLineEntity) _then)
      : super(_value, (v) => _then(v as _$_SpecificationLineEntity));

  @override
  _$_SpecificationLineEntity get _value =>
      super._value as _$_SpecificationLineEntity;

  @override
  $Res call({
    Object? material = freezed,
    Object? quantity = freezed,
  }) {
    return _then(_$_SpecificationLineEntity(
      material: material == freezed
          ? _value.material
          : material // ignore: cast_nullable_to_non_nullable
              as Material,
      quantity: quantity == freezed
          ? _value.quantity
          : quantity // ignore: cast_nullable_to_non_nullable
              as int,
    ));
  }
}

/// @nodoc

class _$_SpecificationLineEntity implements _SpecificationLineEntity {
  const _$_SpecificationLineEntity(
      {required this.material, required this.quantity});

  @override
  final Material material;
  @override
  final int quantity;

  @override
  String toString() {
    return 'SpecificationLineEntity(material: $material, quantity: $quantity)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_SpecificationLineEntity &&
            const DeepCollectionEquality().equals(other.material, material) &&
            const DeepCollectionEquality().equals(other.quantity, quantity));
  }

  @override
  int get hashCode => Object.hash(
      runtimeType,
      const DeepCollectionEquality().hash(material),
      const DeepCollectionEquality().hash(quantity));

  @JsonKey(ignore: true)
  @override
  _$$_SpecificationLineEntityCopyWith<_$_SpecificationLineEntity>
      get copyWith =>
          __$$_SpecificationLineEntityCopyWithImpl<_$_SpecificationLineEntity>(
              this, _$identity);
}

abstract class _SpecificationLineEntity implements SpecificationLineEntity {
  const factory _SpecificationLineEntity(
      {required final Material material,
      required final int quantity}) = _$_SpecificationLineEntity;

  @override
  Material get material => throw _privateConstructorUsedError;
  @override
  int get quantity => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_SpecificationLineEntityCopyWith<_$_SpecificationLineEntity>
      get copyWith => throw _privateConstructorUsedError;
}
