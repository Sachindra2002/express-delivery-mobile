import 'dart:js';

import 'package:express_delivery/authentication/bloc/authentication_bloc.dart';
import 'package:express_delivery/authentication_repository.dart';
import 'package:express_delivery/ui/components/splash_screen.dart';
import 'package:express_delivery/ui/screens/authentication/login.dart';
import 'package:express_delivery/ui/screens/authentication/registration.dart';
import 'package:express_delivery/ui/screens/customer/homescreen_customer.dart';
import 'package:express_delivery/user_repository.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

class App extends StatelessWidget {
  const App({
    Key? key,
    required this.authenticationRepository,
    required this.userRepository,
  }) : super(key: key);

  final AuthenticationRepository authenticationRepository;
  final UserRepository userRepository;

  @override
  Widget build(BuildContext context) {
    return RepositoryProvider.value(
      value: authenticationRepository,
      child: BlocProvider(
        create: (_) => AuthenticationBloc(
          authenticationRepository: authenticationRepository,
          userRepository: userRepository,
        ),
        child: AppView(),
      ),
    );
  }
}

class AppView extends StatefulWidget {
  @override
  _AppViewState createState() => _AppViewState();
}

class _AppViewState extends State<AppView> {
  final _navigatorKey = GlobalKey<NavigatorState>();

  NavigatorState get _navigator => _navigatorKey.currentState!;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      navigatorKey: _navigatorKey,
      builder: (context, child) {
          return BlocListener<AuthenticationBloc, AuthenticationState>(
            listener: (context, state) {
              switch (state.status) {
                case AuthenticationStatus.authenticated :
                _navigator.pushAndRemoveUntil<void> (
                  HomePageCustomer.route(),
                  (route) => false,
                );
                break;
              }
            },
          );
        }
    );
  }
  
}
