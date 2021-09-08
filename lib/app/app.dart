import 'package:express_delivery/ui/components/splash_screen.dart';
import 'package:express_delivery/ui/screens/authentication/login.dart';
import 'package:express_delivery/ui/screens/authentication/registration.dart';
import 'package:flutter/material.dart';

class App extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      initialRoute: '/',
      home: SplashScreen(),
      routes: {
        '/': (context) => SplashScreen(),
        '/login': (context) => Login(),
        '/register': (context) => Registration(),
      },
    );
  }
}
