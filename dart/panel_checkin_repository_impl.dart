import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter_experience_health_center_web_panel/src/core/env.dart';
import 'package:flutter_experience_health_center_web_panel/src/models/panel_checkin_model.dart';
import 'package:health_center_core/health_center_core.dart';
import 'package:intl/intl.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

import './panel_checkin_repository.dart';

class PanelCheckinRepositoryImpl implements PanelCheckinRepository {
  PanelCheckinRepositoryImpl({required this.restCLient});

  final RestClient restCLient;

  @override
  ({WebSocketChannel channel, Function dispose}) openChannelSocket() {
    final channel = WebSocketChannel.connect(
      Uri.parse('${Env.webSocketBaseUrl}?tables=painelCheckin'),
    );

    return (
      channel: channel,
      dispose: () {
        channel.sink.close();
      },
    );
  }

  @override
  Stream getTodayPanelCheckin(WebSocketChannel channel) async* {
    yield await requestData();
    yield* channel.stream.asyncMap((_) async => requestData());
  }

  Future<List<PanelCheckinModel>> requestData() async {
    final dateFormat = DateFormat('y-MM-dd');
    final Response(:List data) =
        await restCLient.auth.get('/painelCheckin', queryParameters: {
      'time_called': dateFormat.format(DateTime.now()),
    });

    return data.reversed
        .take(7)
        .map((e) => PanelCheckinModel.fromJson(e))
        .toList();
  }
}
