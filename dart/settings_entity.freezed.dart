// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target

part of 'settings_entity.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
    'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#custom-getters-and-methods');

/// @nodoc
mixin _$SettingsEntity {
  MasterKey get masterKey => throw _privateConstructorUsedError;
  AccessToken get accessToken => throw _privateConstructorUsedError;

  @JsonKey(ignore: true)
  $SettingsEntityCopyWith<SettingsEntity> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SettingsEntityCopyWith<$Res> {
  factory $SettingsEntityCopyWith(
          SettingsEntity value, $Res Function(SettingsEntity) then) =
      _$SettingsEntityCopyWithImpl<$Res>;
  $Res call({MasterKey masterKey, AccessToken accessToken});

  $MasterKeyCopyWith<$Res> get masterKey;
  $AccessTokenCopyWith<$Res> get accessToken;
}

/// @nodoc
class _$SettingsEntityCopyWithImpl<$Res>
    implements $SettingsEntityCopyWith<$Res> {
  _$SettingsEntityCopyWithImpl(this._value, this._then);

  final SettingsEntity _value;
  // ignore: unused_field
  final $Res Function(SettingsEntity) _then;

  @override
  $Res call({
    Object? masterKey = freezed,
    Object? accessToken = freezed,
  }) {
    return _then(_value.copyWith(
      masterKey: masterKey == freezed
          ? _value.masterKey
          : masterKey // ignore: cast_nullable_to_non_nullable
              as MasterKey,
      accessToken: accessToken == freezed
          ? _value.accessToken
          : accessToken // ignore: cast_nullable_to_non_nullable
              as AccessToken,
    ));
  }

  @override
  $MasterKeyCopyWith<$Res> get masterKey {
    return $MasterKeyCopyWith<$Res>(_value.masterKey, (value) {
      return _then(_value.copyWith(masterKey: value));
    });
  }

  @override
  $AccessTokenCopyWith<$Res> get accessToken {
    return $AccessTokenCopyWith<$Res>(_value.accessToken, (value) {
      return _then(_value.copyWith(accessToken: value));
    });
  }
}

/// @nodoc
abstract class _$$_SettingsEntityCopyWith<$Res>
    implements $SettingsEntityCopyWith<$Res> {
  factory _$$_SettingsEntityCopyWith(
          _$_SettingsEntity value, $Res Function(_$_SettingsEntity) then) =
      __$$_SettingsEntityCopyWithImpl<$Res>;
  @override
  $Res call({MasterKey masterKey, AccessToken accessToken});

  @override
  $MasterKeyCopyWith<$Res> get masterKey;
  @override
  $AccessTokenCopyWith<$Res> get accessToken;
}

/// @nodoc
class __$$_SettingsEntityCopyWithImpl<$Res>
    extends _$SettingsEntityCopyWithImpl<$Res>
    implements _$$_SettingsEntityCopyWith<$Res> {
  __$$_SettingsEntityCopyWithImpl(
      _$_SettingsEntity _value, $Res Function(_$_SettingsEntity) _then)
      : super(_value, (v) => _then(v as _$_SettingsEntity));

  @override
  _$_SettingsEntity get _value => super._value as _$_SettingsEntity;

  @override
  $Res call({
    Object? masterKey = freezed,
    Object? accessToken = freezed,
  }) {
    return _then(_$_SettingsEntity(
      masterKey: masterKey == freezed
          ? _value.masterKey
          : masterKey // ignore: cast_nullable_to_non_nullable
              as MasterKey,
      accessToken: accessToken == freezed
          ? _value.accessToken
          : accessToken // ignore: cast_nullable_to_non_nullable
              as AccessToken,
    ));
  }
}

/// @nodoc

class _$_SettingsEntity extends _SettingsEntity {
  const _$_SettingsEntity({required this.masterKey, required this.accessToken})
      : super._();

  @override
  final MasterKey masterKey;
  @override
  final AccessToken accessToken;

  @override
  String toString() {
    return 'SettingsEntity(masterKey: $masterKey, accessToken: $accessToken)';
  }

  @override
  bool operator ==(dynamic other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$_SettingsEntity &&
            const DeepCollectionEquality().equals(other.masterKey, masterKey) &&
            const DeepCollectionEquality()
                .equals(other.accessToken, accessToken));
  }

  @override
  int get hashCode => Object.hash(
      runtimeType,
      const DeepCollectionEquality().hash(masterKey),
      const DeepCollectionEquality().hash(accessToken));

  @JsonKey(ignore: true)
  @override
  _$$_SettingsEntityCopyWith<_$_SettingsEntity> get copyWith =>
      __$$_SettingsEntityCopyWithImpl<_$_SettingsEntity>(this, _$identity);
}

abstract class _SettingsEntity extends SettingsEntity {
  const factory _SettingsEntity(
      {required final MasterKey masterKey,
      required final AccessToken accessToken}) = _$_SettingsEntity;
  const _SettingsEntity._() : super._();

  @override
  MasterKey get masterKey => throw _privateConstructorUsedError;
  @override
  AccessToken get accessToken => throw _privateConstructorUsedError;
  @override
  @JsonKey(ignore: true)
  _$$_SettingsEntityCopyWith<_$_SettingsEntity> get copyWith =>
      throw _privateConstructorUsedError;
}
