// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'master_key_dto_storage.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

MasterKeyDtoStorage _$MasterKeyDtoStorageFromJson(Map<String, dynamic> json) {
  return _MasterKeyDtoStorage.fromJson(json);
}

/// @nodoc
mixin _$MasterKeyDtoStorage {
  String get value => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $MasterKeyDtoStorageCopyWith<MasterKeyDtoStorage> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $MasterKeyDtoStorageCopyWith<$Res> {
  factory $MasterKeyDtoStorageCopyWith(
          MasterKeyDtoStorage value, $Res Function(MasterKeyDtoStorage) then) =
      _$MasterKeyDtoStorageCopyWithImpl<$Res>;
  $Res call({String value});
}

/// @nodoc
class _$MasterKeyDtoStorageCopyWithImpl<$Res>
    implements $MasterKeyDtoStorageCopyWith<$Res> {
  _$MasterKeyDtoStorageCopyWithImpl(this._value, this._then);

  final MasterKeyDtoStorage _value;
  // ignore: unused_field
  final $Res Function(MasterKeyDtoStorage) _then;

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
abstract class _$$_MasterKeyDtoStorageCopyWith<$Res>
    implements $MasterKeyDtoStorageCopyWith<$Res> {
  factory _$$_MasterKeyDtoStorageCopyWith(_$_MasterKeyDtoStorage value,
          $Res Function(_$_MasterKeyDtoStorage) then) =
      __$$_MasterKeyDtoStorageCopyWithImpl<$Res>;
  @override
  $Res call({String value});
}

/// @nodoc
class __$$_MasterKeyDtoStorageCopyWithImpl<$Res>
    extends _$MasterKeyDtoStorageCopyWithImpl<$Res>
    implements _$$_MasterKeyDtoStorageCopyWith<$Res> {
  __$$_MasterKeyDtoStorageCopyWithImpl(_$_MasterKeyDtoStorage _value,
      $Res Function(_$_MasterKeyDtoStorage) _then)
      : super(_value, (v) => _then(v as _$_MasterKeyDtoStorage));

  @override
  _$_MasterKeyDtoStorage get _value => super._value as _$_MasterKeyDtoStorage;

  @override
  $Res call({
    Object? value = freezed,
  }) {
    return _then(_$_MasterKeyDtoStorage(
      value: value == freezed
          ? _value.value
          : value // ignore: cast_nullable_to_non_nullable
              as String,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_MasterKeyDtoStorage implements _MasterKeyDtoStorage {
  const _$_MasterKeyDtoStorage({required this.value});

  factory _$_MasterKeyDtoStorage.fromJson(Map<String, dynamic> json) =>
      _$$_MasterKeyDtoStorageFromJson(json);

  @override
  final String value;

  @override
  String toString() {
    return 'MasterKeyDtoStorage(value: $value)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_MasterKeyDtoStorage &&
            const DeepCollectionEquality().equals(other.value, value));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode =>
      Object.hash(runtimeType, const DeepCollectionEquality().hash(value));

  @JsonKey(ignore: true)
  @override
  _$$_MasterKeyDtoStorageCopyWith<_$_MasterKeyDtoStorage> get copyWith =>
      __$$_MasterKeyDtoStorageCopyWithImpl<_$_MasterKeyDtoStorage>(
          this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_MasterKeyDtoStorageToJson(this);
  }
}

abstract class _MasterKeyDtoStorage implements MasterKeyDtoStorage {
  const factory _MasterKeyDtoStorage({required final String value}) =
      _$_MasterKeyDtoStorage;

  factory _MasterKeyDtoStorage.fromJson(Map<String, dynamic> json) =
      _$_MasterKeyDtoStorage.fromJson;

  @override
  String get value => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_MasterKeyDtoStorageCopyWith<_$_MasterKeyDtoStorage> get copyWith =>
      throw _privateConstructorUsedError;
}
