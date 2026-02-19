// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'dkc_api.dart';

// **************************************************************************
// RetrofitGenerator
// **************************************************************************

// ignore_for_file: unnecessary_brace_in_string_interps

class _DkcApi implements DkcApi {
  _DkcApi(this._dio, {this.baseUrl}) {
    baseUrl ??= 'https://api.dkc.ru/v1';
  }

  final Dio _dio;

  String? baseUrl;

  @override
  Future<MaterialDtoApi> getMaterial(code, accept, accessToken) async {
    const _extra = <String, dynamic>{};
    final queryParameters = <String, dynamic>{r'code': code};
    final _headers = <String, dynamic>{
      r'accept': accept,
      r'AccessToken': accessToken
    };
    _headers.removeWhere((k, v) => v == null);
    final _data = <String, dynamic>{};
    final _result = await _dio.fetch<Map<String, dynamic>>(
        _setStreamType<MaterialDtoApi>(
            Options(method: 'GET', headers: _headers, extra: _extra)
                .compose(_dio.options, '/catalog/material?',
                    queryParameters: queryParameters, data: _data)
                .copyWith(baseUrl: baseUrl ?? _dio.options.baseUrl)));
    final value = MaterialDtoApi.fromJson(_result.data!);
    return value;
  }

  @override
  Future<AccessTokenDtoApi> getAccessToken(masterKey, accept) async {
    const _extra = <String, dynamic>{};
    final queryParameters = <String, dynamic>{};
    final _headers = <String, dynamic>{r'accept': accept};
    _headers.removeWhere((k, v) => v == null);
    final _data = <String, dynamic>{};
    final _result = await _dio.fetch<Map<String, dynamic>>(
        _setStreamType<AccessTokenDtoApi>(
            Options(method: 'GET', headers: _headers, extra: _extra)
                .compose(_dio.options, '/auth.access.token/${masterKey}',
                    queryParameters: queryParameters, data: _data)
                .copyWith(baseUrl: baseUrl ?? _dio.options.baseUrl)));
    final value = AccessTokenDtoApi.fromJson(_result.data!);
    return value;
  }

  RequestOptions _setStreamType<T>(RequestOptions requestOptions) {
    if (T != dynamic &&
        !(requestOptions.responseType == ResponseType.bytes ||
            requestOptions.responseType == ResponseType.stream)) {
      if (T == String) {
        requestOptions.responseType = ResponseType.plain;
      } else {
        requestOptions.responseType = ResponseType.json;
      }
    }
    return requestOptions;
  }
}
