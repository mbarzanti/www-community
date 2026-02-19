// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'material.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

/// @nodoc
mixin _$Material {
  String get code => throw _privateConstructorUsedError;
  String get name => throw _privateConstructorUsedError;
  String get unit => throw _privateConstructorUsedError;
  double get price => throw _privateConstructorUsedError;
  double get weight => throw _privateConstructorUsedError;

  @JsonKey(ignore: true)
  $MaterialCopyWith<Material> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $MaterialCopyWith<$Res> {
  factory $MaterialCopyWith(Material value, $Res Function(Material) then) =
      _$MaterialCopyWithImpl<$Res>;
  $Res call(
      {String code, String name, String unit, double price, double weight});
}

/// @nodoc
class _$MaterialCopyWithImpl<$Res> implements $MaterialCopyWith<$Res> {
  _$MaterialCopyWithImpl(this._value, this._then);

  final Material _value;
  // ignore: unused_field
  final $Res Function(Material) _then;

  @override
  $Res call({
    Object? code = freezed,
    Object? name = freezed,
    Object? unit = freezed,
    Object? price = freezed,
    Object? weight = freezed,
  }) {
    return _then(_value.copyWith(
      code: code == freezed
          ? _value.code
          : code // ignore: cast_nullable_to_non_nullable
              as String,
      name: name == freezed
          ? _value.name
          : name // ignore: cast_nullable_to_non_nullable
              as String,
      unit: unit == freezed
          ? _value.unit
          : unit // ignore: cast_nullable_to_non_nullable
              as String,
      price: price == freezed
          ? _value.price
          : price // ignore: cast_nullable_to_non_nullable
              as double,
      weight: weight == freezed
          ? _value.weight
          : weight // ignore: cast_nullable_to_non_nullable
              as double,
    ));
  }
}

/// @nodoc
abstract class _$$_MaterialCopyWith<$Res> implements $MaterialCopyWith<$Res> {
  factory _$$_MaterialCopyWith(
          _$_Material value, $Res Function(_$_Material) then) =
      __$$_MaterialCopyWithImpl<$Res>;
  @override
  $Res call(
      {String code, String name, String unit, double price, double weight});
}

/// @nodoc
class __$$_MaterialCopyWithImpl<$Res> extends _$MaterialCopyWithImpl<$Res>
    implements _$$_MaterialCopyWith<$Res> {
  __$$_MaterialCopyWithImpl(
      _$_Material _value, $Res Function(_$_Material) _then)
      : super(_value, (v) => _then(v as _$_Material));

  @override
  _$_Material get _value => super._value as _$_Material;

  @override
  $Res call({
    Object? code = freezed,
    Object? name = freezed,
    Object? unit = freezed,
    Object? price = freezed,
    Object? weight = freezed,
  }) {
    return _then(_$_Material(
      code: code == freezed
          ? _value.code
          : code // ignore: cast_nullable_to_non_nullable
              as String,
      name: name == freezed
          ? _value.name
          : name // ignore: cast_nullable_to_non_nullable
              as String,
      unit: unit == freezed
          ? _value.unit
          : unit // ignore: cast_nullable_to_non_nullable
              as String,
      price: price == freezed
          ? _value.price
          : price // ignore: cast_nullable_to_non_nullable
              as double,
      weight: weight == freezed
          ? _value.weight
          : weight // ignore: cast_nullable_to_non_nullable
              as double,
    ));
  }
}

/// @nodoc

class _$_Material implements _Material {
  const _$_Material(
      {required this.code,
      required this.name,
      required this.unit,
      required this.price,
      required this.weight});

  @override
  final String code;
  @override
  final String name;
  @override
  final String unit;
  @override
  final double price;
  @override
  final double weight;

  @override
  String toString() {
    return 'Material(code: $code, name: $name, unit: $unit, price: $price, weight: $weight)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_Material &&
            const DeepCollectionEquality().equals(other.code, code) &&
            const DeepCollectionEquality().equals(other.name, name) &&
            const DeepCollectionEquality().equals(other.unit, unit) &&
            const DeepCollectionEquality().equals(other.price, price) &&
            const DeepCollectionEquality().equals(other.weight, weight));
  }

  @override
  int get hashCode => Object.hash(
      runtimeType,
      const DeepCollectionEquality().hash(code),
      const DeepCollectionEquality().hash(name),
      const DeepCollectionEquality().hash(unit),
      const DeepCollectionEquality().hash(price),
      const DeepCollectionEquality().hash(weight));

  @JsonKey(ignore: true)
  @override
  _$$_MaterialCopyWith<_$_Material> get copyWith =>
      __$$_MaterialCopyWithImpl<_$_Material>(this, _$identity);
}

abstract class _Material implements Material {
  const factory _Material(
      {required final String code,
      required final String name,
      required final String unit,
      required final double price,
      required final double weight}) = _$_Material;

  @override
  String get code => throw _privateConstructorUsedError;
  @override
  String get name => throw _privateConstructorUsedError;
  @override
  String get unit => throw _privateConstructorUsedError;
  @override
  double get price => throw _privateConstructorUsedError;
  @override
  double get weight => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_MaterialCopyWith<_$_Material> get copyWith =>
      throw _privateConstructorUsedError;
}
