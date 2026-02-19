// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'master_key.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

/// @nodoc
mixin _$MasterKey {
  String get value => throw _privateConstructorUsedError;

  @JsonKey(ignore: true)
  $MasterKeyCopyWith<MasterKey> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $MasterKeyCopyWith<$Res> {
  factory $MasterKeyCopyWith(MasterKey value, $Res Function(MasterKey) then) =
      _$MasterKeyCopyWithImpl<$Res>;
  $Res call({String value});
}

/// @nodoc
class _$MasterKeyCopyWithImpl<$Res> implements $MasterKeyCopyWith<$Res> {
  _$MasterKeyCopyWithImpl(this._value, this._then);

  final MasterKey _value;
  // ignore: unused_field
  final $Res Function(MasterKey) _then;

  @override
  $Res call({
    Object? value = freezed,
  }) {
    return _then(_value.copyWith(
      value: value == freezed
          ? _value.value
          : value // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc
abstract class _$$_MasterKeyCopyWith<$Res> implements $MasterKeyCopyWith<$Res> {
  factory _$$_MasterKeyCopyWith(
          _$_MasterKey value, $Res Function(_$_MasterKey) then) =
      __$$_MasterKeyCopyWithImpl<$Res>;
  @override
  $Res call({String value});
}

/// @nodoc
class __$$_MasterKeyCopyWithImpl<$Res> extends _$MasterKeyCopyWithImpl<$Res>
    implements _$$_MasterKeyCopyWith<$Res> {
  __$$_MasterKeyCopyWithImpl(
      _$_MasterKey _value, $Res Function(_$_MasterKey) _then)
      : super(_value, (v) => _then(v as _$_MasterKey));

  @override
  _$_MasterKey get _value => super._value as _$_MasterKey;

  @override
  $Res call({
    Object? value = freezed,
  }) {
    return _then(_$_MasterKey(
      value: value == freezed
          ? _value.value
          : value // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc

class _$_MasterKey implements _MasterKey {
  const _$_MasterKey({required this.value});

  @override
  final String value;

  @override
  String toString() {
    return 'MasterKey(value: $value)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_MasterKey &&
            const DeepCollectionEquality().equals(other.value, value));
  }

  @override
  int get hashCode =>
      Object.hash(runtimeType, const DeepCollectionEquality().hash(value));

  @JsonKey(ignore: true)
  @override
  _$$_MasterKeyCopyWith<_$_MasterKey> get copyWith =>
      __$$_MasterKeyCopyWithImpl<_$_MasterKey>(this, _$identity);
}

abstract class _MasterKey implements MasterKey {
  const factory _MasterKey({required final String value}) = _$_MasterKey;

  @override
  String get value => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_MasterKeyCopyWith<_$_MasterKey> get copyWith =>
      throw _privateConstructorUsedError;
}
