import 'package:flutter/material.dart';
import '../utils/insecure_network.dart';
import '../utils/insecure_storage.dart';
import 'login_screen.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  String _result = '';

  void _testInsecureApi() async {
    final result = await InsecureNetwork.fetchData();
    setState(() => _result = result);
  }

  Future<void> _logout() async {
    await InsecureStorage.clearToken();
    if (!mounted) return;
    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (_) => LoginScreen()),
          (route) => false,
    );
  }

  @override
  Widget build(BuildContext context) {
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: Text("👤 Profile"),
        centerTitle: true,
        actions: [
          IconButton(
            onPressed: _logout,
            icon: Icon(Icons.logout),
            tooltip: "Logout",
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text(
              "Welcome!",
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 12),
            Text(
              "You're now logged in (insecurely).",
              style: TextStyle(color: Colors.grey[600]),
            ),
            SizedBox(height: 30),
            ElevatedButton.icon(
              onPressed: _testInsecureApi,
              icon: Icon(Icons.cloud_off),
              label: Text("🔓 Call Insecure API"),
              style: ElevatedButton.styleFrom(
                padding: EdgeInsets.symmetric(vertical: 14),
                backgroundColor: Colors.redAccent,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
              ),
            ),
            SizedBox(height: 24),
            if (_result.isNotEmpty)
              Card(
                color: isDark ? Colors.grey[850] : Colors.red.shade50,
                elevation: 4,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Text(
                    _result,
                    style: TextStyle(
                      fontSize: 16,
                      color: Colors.red[700],
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ),
              ),
          ],
        ),
      ),
    );
  }
}
