import 'package:web_socket_channel/web_socket_channel.dart';

abstract interface class PanelCheckinRepository {
  ({WebSocketChannel channel, Function dispose}) openChannelSocket();
  Stream getTodayPanelCheckin(WebSocketChannel channel);
}
