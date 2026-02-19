import 'dart:convert';
import 'package:http/http.dart' as http;

class InsecureNetwork {
  // This is a dummy public insecure API for demonstration.
  static const String endpoint = "http://httpbin.org/get"; // 👈 HTTP, not HTTPS

  static Future<String> fetchData() async {
    try {
      final response = await http.get(Uri.parse(endpoint));

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        print("🔓 Insecure response: $data"); // Not safe to log in real apps
        return "Data received: ${data['origin']}";
      } else {
        return "Error: ${response.statusCode}";
      }
    } catch (e) {
      return "Exception: $e";
    }
  }
}
