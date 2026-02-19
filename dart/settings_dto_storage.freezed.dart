// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'settings_dto_storage.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

SettingsDtoStorage _$SettingsDtoStorageFromJson(Map<String, dynamic> json) {
  return _SettingsDtoStorage.fromJson(json);
}

/// @nodoc
mixin _$SettingsDtoStorage {
  MasterKeyDtoStorage get masterKey => throw _privateConstructorUsedError;
  AccessTokenDtoStorage get accessToken => throw _privateConstructorUsedError;

  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;
  @JsonKey(ignore: true)
  $SettingsDtoStorageCopyWith<SettingsDtoStorage> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SettingsDtoStorageCopyWith<$Res> {
  factory $SettingsDtoStorageCopyWith(
          SettingsDtoStorage value, $Res Function(SettingsDtoStorage) then) =
      _$SettingsDtoStorageCopyWithImpl<$Res>;
  $Res call({MasterKeyDtoStorage masterKey, AccessTokenDtoStorage accessToken});

  $MasterKeyDtoStorageCopyWith<$Res> get masterKey;
  $AccessTokenDtoStorageCopyWith<$Res> get accessToken;
}

/// @nodoc
class _$SettingsDtoStorageCopyWithImpl<$Res>
    implements $SettingsDtoStorageCopyWith<$Res> {
  _$SettingsDtoStorageCopyWithImpl(this._value, this._then);

  final SettingsDtoStorage _value;
  // ignore: unused_field
  final $Res Function(SettingsDtoStorage) _then;

  @override
  $Res call({
    Object? masterKey = freezed,
    Object? accessToken = freezed,
  }) {
    return _then(_value.copyWith(
      masterKey: masterKey == freezed
          ? _value.masterKey
          : masterKey // ignore: cast_nullable_to_non_nullable
              as MasterKeyDtoStorage,
      accessToken: accessToken == freezed
          ? _value.accessToken
          : accessToken // ignore: cast_nullable_to_non_nullable
              as AccessTokenDtoStorage,
    ));
  }

  @override
  $MasterKeyDtoStorageCopyWith<$Res> get masterKey {
    return $MasterKeyDtoStorageCopyWith<$Res>(_value.masterKey, (value) {
      return _then(_value.copyWith(masterKey: value));
    });
  }

  @override
  $AccessTokenDtoStorageCopyWith<$Res> get accessToken {
    return $AccessTokenDtoStorageCopyWith<$Res>(_value.accessToken, (value) {
      return _then(_value.copyWith(accessToken: value));
    });
  }
}

/// @nodoc
abstract class _$$_SettingsDtoStorageCopyWith<$Res>
    implements $SettingsDtoStorageCopyWith<$Res> {
  factory _$$_SettingsDtoStorageCopyWith(_$_SettingsDtoStorage value,
          $Res Function(_$_SettingsDtoStorage) then) =
      __$$_SettingsDtoStorageCopyWithImpl<$Res>;
  @override
  $Res call({MasterKeyDtoStorage masterKey, AccessTokenDtoStorage accessToken});

  @override
  $MasterKeyDtoStorageCopyWith<$Res> get masterKey;
  @override
  $AccessTokenDtoStorageCopyWith<$Res> get accessToken;
}

/// @nodoc
class __$$_SettingsDtoStorageCopyWithImpl<$Res>
    extends _$SettingsDtoStorageCopyWithImpl<$Res>
    implements _$$_SettingsDtoStorageCopyWith<$Res> {
  __$$_SettingsDtoStorageCopyWithImpl(
      _$_SettingsDtoStorage _value, $Res Function(_$_SettingsDtoStorage) _then)
      : super(_value, (v) => _then(v as _$_SettingsDtoStorage));

  @override
  _$_SettingsDtoStorage get _value => super._value as _$_SettingsDtoStorage;

  @override
  $Res call({
    Object? masterKey = freezed,
    Object? accessToken = freezed,
  }) {
    return _then(_$_SettingsDtoStorage(
      masterKey: masterKey == freezed
          ? _value.masterKey
          : masterKey // ignore: cast_nullable_to_non_nullable
              as MasterKeyDtoStorage,
      accessToken: accessToken == freezed
          ? _value.accessToken
          : accessToken // ignore: cast_nullable_to_non_nullable
              as AccessTokenDtoStorage,
    ));
  }
}

/// @nodoc
@JsonSerializable()
class _$_SettingsDtoStorage implements _SettingsDtoStorage {
  const _$_SettingsDtoStorage(
      {required this.masterKey, required this.accessToken});

  factory _$_SettingsDtoStorage.fromJson(Map<String, dynamic> json) =>
      _$$_SettingsDtoStorageFromJson(json);

  @override
  final MasterKeyDtoStorage masterKey;
  @override
  final AccessTokenDtoStorage accessToken;

  @override
  String toString() {
    return 'SettingsDtoStorage(masterKey: $masterKey, accessToken: $accessToken)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_SettingsDtoStorage &&
            const DeepCollectionEquality().equals(other.masterKey, masterKey) &&
            const DeepCollectionEquality()
                .equals(other.accessToken, accessToken));
  }

  @JsonKey(ignore: true)
  @override
  int get hashCode => Object.hash(
      runtimeType,
      const DeepCollectionEquality().hash(masterKey),
      const DeepCollectionEquality().hash(accessToken));

  @JsonKey(ignore: true)
  @override
  _$$_SettingsDtoStorageCopyWith<_$_SettingsDtoStorage> get copyWith =>
      __$$_SettingsDtoStorageCopyWithImpl<_$_SettingsDtoStorage>(
          this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$_SettingsDtoStorageToJson(this);
  }
}

abstract class _SettingsDtoStorage implements SettingsDtoStorage {
  const factory _SettingsDtoStorage(
          {required final MasterKeyDtoStorage masterKey,
          required final AccessTokenDtoStorage accessToken}) =
      _$_SettingsDtoStorage;

  factory _SettingsDtoStorage.fromJson(Map<String, dynamic> json) =
      _$_SettingsDtoStorage.fromJson;

  @override
  MasterKeyDtoStorage get masterKey => throw _privateConstructorUsedError;
  @override
  AccessTokenDtoStorage get accessToken => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_SettingsDtoStorageCopyWith<_$_SettingsDtoStorage> get copyWith =>
      throw _privateConstructorUsedError;
}
