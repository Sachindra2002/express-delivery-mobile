import 'package:equatable/equatable.dart';

class User extends Equatable {
  const User(this.email, this.firstName, this.lastName, this.phoneNumber,
      this.location, this.password);

  final String email;
  final String firstName;
  final String lastName;
  final String phoneNumber;
  final String location;
  final String password;

  @override
  List<Object> get props =>
      [email, firstName, lastName, phoneNumber, location, password];

  static const empty = User('-', '-', '-', '-', '-', '-');
}
