// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'access_token_dto_storage.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

AccessTokenDtoStorage _$AccessTokenDtoStorageFromJson(
    Map<String, dynamic> json) {
  return _AccessTokenDtoStorage.fromJson(json);
}

/// @nodoc
mixin _$AccessTokenDtoStorage {
  String get value => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $AccessTokenDtoStorageCopyWith<AccessTokenDtoStorage> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $AccessTokenDtoStorageCopyWith<$Res> {
  factory $AccessTokenDtoStorageCopyWith(AccessTokenDtoStorage value,
          $Res Function(AccessTokenDtoStorage) then) =
      _$AccessTokenDtoStorageCopyWithImpl<$Res>;
  $Res call({String value});
}

/// @nodoc
class _$AccessTokenDtoStorageCopyWithImpl<$Res>
    implements $AccessTokenDtoStorageCopyWith<$Res> {
  _$AccessTokenDtoStorageCopyWithImpl(this._value, this._then);

  final AccessTokenDtoStorage _value;
  // ignore: unused_field
  final $Res Function(AccessTokenDtoStorage) _then;

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
abstract class _$$_AccessTokenDtoStorageCopyWith<$Res>
    implements $AccessTokenDtoStorageCopyWith<$Res> {
  factory _$$_AccessTokenDtoStorageCopyWith(_$_AccessTokenDtoStorage value,
          $Res Function(_$_AccessTokenDtoStorage) then) =
      __$$_AccessTokenDtoStorageCopyWithImpl<$Res>;
  @override
  $Res call({String value});
}

/// @nodoc
class __$$_AccessTokenDtoStorageCopyWithImpl<$Res>
    extends _$AccessTokenDtoStorageCopyWithImpl<$Res>
    implements _$$_AccessTokenDtoStorageCopyWith<$Res> {
  __$$_AccessTokenDtoStorageCopyWithImpl(_$_AccessTokenDtoStorage _value,
      $Res Function(_$_AccessTokenDtoStorage) _then)
      : super(_value, (v) => _then(v as _$_AccessTokenDtoStorage));

  @override
  _$_AccessTokenDtoStorage get _value =>
      super._value as _$_AccessTokenDtoStorage;

  @override
  $Res call({
    Object? value = freezed,
  }) {
    return _then(_$_AccessTokenDtoStorage(
      value: value == freezed
          ? _value.value
          : value // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_AccessTokenDtoStorage implements _AccessTokenDtoStorage {
  const _$_AccessTokenDtoStorage({required this.value});

  factory _$_AccessTokenDtoStorage.fromJson(Map<String, dynamic> json) =>
      _$$_AccessTokenDtoStorageFromJson(json);

  @override
  final String value;

  @override
  String toString() {
    return 'AccessTokenDtoStorage(value: $value)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_AccessTokenDtoStorage &&
            const DeepCollectionEquality().equals(other.value, value));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode =>
      Object.hash(runtimeType, const DeepCollectionEquality().hash(value));

  @JsonKey(ignore: true)
  @override
  _$$_AccessTokenDtoStorageCopyWith<_$_AccessTokenDtoStorage> get copyWith =>
      __$$_AccessTokenDtoStorageCopyWithImpl<_$_AccessTokenDtoStorage>(
          this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_AccessTokenDtoStorageToJson(this);
  }
}

abstract class _AccessTokenDtoStorage implements AccessTokenDtoStorage {
  const factory _AccessTokenDtoStorage({required final String value}) =
      _$_AccessTokenDtoStorage;

  factory _AccessTokenDtoStorage.fromJson(Map<String, dynamic> json) =
      _$_AccessTokenDtoStorage.fromJson;

  @override
  String get value => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_AccessTokenDtoStorageCopyWith<_$_AccessTokenDtoStorage> get copyWith =>
      throw _privateConstructorUsedError;
}
