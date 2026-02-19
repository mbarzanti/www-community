// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'specification_entity.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

/// @nodoc
mixin _$SpecificationEntity {
  List<SpecificationLineEntity> get lines => throw _privateConstructorUsedError;
  double get totalPrice => throw _privateConstructorUsedError;
  double get totalWeight => throw _privateConstructorUsedError;

  @JsonKey(ignore: true)
  $SpecificationEntityCopyWith<SpecificationEntity> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SpecificationEntityCopyWith<$Res> {
  factory $SpecificationEntityCopyWith(
          SpecificationEntity value, $Res Function(SpecificationEntity) then) =
      _$SpecificationEntityCopyWithImpl<$Res>;
  $Res call(
      {List<SpecificationLineEntity> lines,
      double totalPrice,
      double totalWeight});
}

/// @nodoc
class _$SpecificationEntityCopyWithImpl<$Res>
    implements $SpecificationEntityCopyWith<$Res> {
  _$SpecificationEntityCopyWithImpl(this._value, this._then);

  final SpecificationEntity _value;
  // ignore: unused_field
  final $Res Function(SpecificationEntity) _then;

  @override
  $Res call({
    Object? lines = freezed,
    Object? totalPrice = freezed,
    Object? totalWeight = freezed,
  }) {
    return _then(_value.copyWith(
      lines: lines == freezed
          ? _value.lines
          : lines // ignore: cast_nullable_to_non_nullable
              as List<SpecificationLineEntity>,
      totalPrice: totalPrice == freezed
          ? _value.totalPrice
          : totalPrice // ignore: cast_nullable_to_non_nullable
              as double,
      totalWeight: totalWeight == freezed
          ? _value.totalWeight
          : totalWeight // ignore: cast_nullable_to_non_nullable
              as double,
    ));
  }
}

/// @nodoc
abstract class _$$_SpecificationEntityCopyWith<$Res>
    implements $SpecificationEntityCopyWith<$Res> {
  factory _$$_SpecificationEntityCopyWith(_$_SpecificationEntity value,
          $Res Function(_$_SpecificationEntity) then) =
      __$$_SpecificationEntityCopyWithImpl<$Res>;
  @override
  $Res call(
      {List<SpecificationLineEntity> lines,
      double totalPrice,
      double totalWeight});
}

/// @nodoc
class __$$_SpecificationEntityCopyWithImpl<$Res>
    extends _$SpecificationEntityCopyWithImpl<$Res>
    implements _$$_SpecificationEntityCopyWith<$Res> {
  __$$_SpecificationEntityCopyWithImpl(_$_SpecificationEntity _value,
      $Res Function(_$_SpecificationEntity) _then)
      : super(_value, (v) => _then(v as _$_SpecificationEntity));

  @override
  _$_SpecificationEntity get _value => super._value as _$_SpecificationEntity;

  @override
  $Res call({
    Object? lines = freezed,
    Object? totalPrice = freezed,
    Object? totalWeight = freezed,
  }) {
    return _then(_$_SpecificationEntity(
      lines: lines == freezed
          ? _value._lines
          : lines // ignore: cast_nullable_to_non_nullable
              as List<SpecificationLineEntity>,
      totalPrice: totalPrice == freezed
          ? _value.totalPrice
          : totalPrice // ignore: cast_nullable_to_non_nullable
              as double,
      totalWeight: totalWeight == freezed
          ? _value.totalWeight
          : totalWeight // ignore: cast_nullable_to_non_nullable
              as double,
    ));
  }
}

/// @nodoc

class _$_SpecificationEntity implements _SpecificationEntity {
  const _$_SpecificationEntity(
      {required final List<SpecificationLineEntity> lines,
      required this.totalPrice,
      required this.totalWeight})
      : _lines = lines;

  final List<SpecificationLineEntity> _lines;
  @override
  List<SpecificationLineEntity> get lines {
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_lines);
  }

  @override
  final double totalPrice;
  @override
  final double totalWeight;

  @override
  String toString() {
    return 'SpecificationEntity(lines: $lines, totalPrice: $totalPrice, totalWeight: $totalWeight)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_SpecificationEntity &&
            const DeepCollectionEquality().equals(other._lines, _lines) &&
            const DeepCollectionEquality()
                .equals(other.totalPrice, totalPrice) &&
            const DeepCollectionEquality()
                .equals(other.totalWeight, totalWeight));
  }

  @override
  int get hashCode => Object.hash(
      runtimeType,
      const DeepCollectionEquality().hash(_lines),
      const DeepCollectionEquality().hash(totalPrice),
      const DeepCollectionEquality().hash(totalWeight));

  @JsonKey(ignore: true)
  @override
  _$$_SpecificationEntityCopyWith<_$_SpecificationEntity> get copyWith =>
      __$$_SpecificationEntityCopyWithImpl<_$_SpecificationEntity>(
          this, _$identity);
}

abstract class _SpecificationEntity implements SpecificationEntity {
  const factory _SpecificationEntity(
      {required final List<SpecificationLineEntity> lines,
      required final double totalPrice,
      required final double totalWeight}) = _$_SpecificationEntity;

  @override
  List<SpecificationLineEntity> get lines => throw _privateConstructorUsedError;
  @override
  double get totalPrice => throw _privateConstructorUsedError;
  @override
  double get totalWeight => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_SpecificationEntityCopyWith<_$_SpecificationEntity> get copyWith =>
      throw _privateConstructorUsedError;
}
