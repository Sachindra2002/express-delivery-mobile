import 'package:express_delivery/app/app.dart';
import 'package:express_delivery/authentication_repository.dart';
import 'package:express_delivery/user_repository.dart';
import 'package:flutter/material.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(App(
    authenticationRepository: AuthenticationRepository(),
    userRepository: UserRepository(),
  ));
}
