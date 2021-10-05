// import 'dart:convert';
// import 'dart:io';
// import 'package:email_validator/email_validator.dart';
// import 'package:express_delivery/models/user.dart';
// import 'package:express_delivery/ui/screens/customer/homescreen_customer.dart';
// import 'package:express_delivery/ui/screens/authentication/login.dart';
// //import 'package:express_delivery/screens/login.dart';
// import 'package:flutter/material.dart';
// import 'package:http/http.dart' as http;

// class Registration extends StatefulWidget {
//   const Registration({Key? key}) : super(key: key);

//   @override
//   _RegistrationState createState() => _RegistrationState();
// }

// class _RegistrationState extends State<Registration> {
//   String? email;
//   String? firstName;
//   String? lastname;
//   String location = "Colombo";
//   String? phoneNumber;
//   String? password;
//   String? password2;

//   User user = User("", "", "", "", "", "");
//   String url = "http://10.0.2.2:8080/api/register";
//   Future register() async {
//     var response = await http.post(Uri.parse(url),
//         headers: {'Content-Type': 'application/json'},
//         body: json.encode({
//           'email': user.email,
//           'firstName': user.firstName,
//           'lastName': user.lastName,
//           'location': user.location,
//           'phoneNumber': user.phoneNumber,
//           'password': user.password,
//         }));
//     print(response.body);
//     if (response.statusCode == HttpStatus.badRequest) {
//       print("Failed");
//       showDialog(
//           context: context,
//           builder: (BuildContext context) {
//             return AlertDialog(
//               title: new Text("Warning!"),
//               content: new Text("Email already in use!"),
//               actions: <Widget>[
//                 // usually buttons at the bottom of the dialog
//                 new TextButton(
//                   child: new Text("Close"),
//                   onPressed: () {
//                     Navigator.of(context).pop();
//                   },
//                 ),
//               ],
//             );
//           });
//     } else if (response.statusCode == HttpStatus.created) {
//       print("Success");
//       // Navigator.of(context)
//       //     .pushReplacement(MaterialPageRoute(builder: (_) => Login()));
//       showDialog(
//           context: context,
//           builder: (BuildContext context) {
//             return AlertDialog(
//               title: new Text("Success!"),
//               content: new Text("Account created successfully"),
//               actions: <Widget>[
//                 // usually buttons at the bottom of the dialog
//                 new TextButton(
//                   child: new Text("Sign In"),
//                   onPressed: () {
//                     Navigator.of(context).pushReplacement(
//                         MaterialPageRoute(builder: (_) => Login()));
//                   },
//                 ),
//               ],
//             );
//           });
//     }
//   }

//   final GlobalKey<FormState> formKey = GlobalKey<FormState>();

//   final TextEditingController _pass = TextEditingController();

//   RegExp regExp = new RegExp(r'(^(?:[+0]9)?[0-9]{10,12}$)');

//   Color companyYellow = Color(0xFFFEBC17);

//   bool _isObscure = true;

//   Widget buildEmail() {
//     return TextFormField(
//       controller: TextEditingController(text: user.email),
//       decoration: InputDecoration(
//           labelText: 'Email',
//           border: OutlineInputBorder(),
//           hintText: 'name@Example.com'),
//       validator: (value) {
//         if (value == null || value.isEmpty) {
//           return 'Email is required';
//         } else if (!EmailValidator.validate(value)) {
//           return 'Please enter valid email';
//         }
//         return null;
//       },
//       onSaved: (String? value) {
//         email = value;
//       },
//       onChanged: (val) {
//         user.email = val;
//       },
//     );
//   }

//   Widget buildFirstName() {
//     return TextFormField(
//       controller: TextEditingController(text: user.firstName),
//       decoration: InputDecoration(
//           labelText: 'First Name',
//           border: OutlineInputBorder(),
//           hintText: 'Enter your first name'),
//       validator: (value) {
//         if (value == null || value.isEmpty) {
//           return 'First Name is required';
//         }
//         return null;
//       },
//       onSaved: (String? value) {
//         firstName = value;
//       },
//       onChanged: (val) {
//         user.firstName = val;
//       },
//     );
//   }

//   Widget buildLastName() {
//     return TextFormField(
//       controller: TextEditingController(text: user.lastName),
//       decoration: InputDecoration(
//           labelText: 'Last Name',
//           border: OutlineInputBorder(),
//           hintText: 'Enter your last name'),
//       validator: (value) {
//         if (value == null || value.isEmpty) {
//           return 'Last name is required';
//         }
//         return null;
//       },
//       onSaved: (String? value) {
//         lastname = value;
//       },
//       onChanged: (val) {
//         user.lastName = val;
//       },
//     );
//   }

//   Widget buildLocationDropDown() {
//     return DropdownButtonFormField<String>(
//       decoration: InputDecoration(
//           labelText: 'Location',
//           border: OutlineInputBorder(),
//           hintText: 'Enter a location'),
//       value: location,
//       onChanged: (String? newValue) {
//         setState(() {
//           user.location = newValue!;
//         });
//       },
//       items: <String>['Colombo', 'Negombo', 'Galle', 'Kandy']
//           .map<DropdownMenuItem<String>>((String value) {
//         return DropdownMenuItem<String>(
//           value: value,
//           child: Text(value),
//         );
//       }).toList(),
//     );
//   }

//   Widget buildPhoneNumber() {
//     return TextFormField(
//       controller: TextEditingController(text: user.phoneNumber),
//       decoration: InputDecoration(
//           labelText: 'Phone number',
//           border: OutlineInputBorder(),
//           hintText: 'Enter your phone number'),
//       validator: (value) {
//         if (value == null || value.isEmpty) {
//           return 'Phone Number is required';
//         } else if (!regExp.hasMatch(value)) {
//           return 'Enter valid phone number';
//         }
//         return null;
//       },
//       onSaved: (String? value) {
//         phoneNumber = value;
//       },
//       onChanged: (val) {
//         user.phoneNumber = val;
//       },
//     );
//   }

//   Widget buildPassword() {
//     return TextFormField(
//       controller: _pass,
//       obscureText: _isObscure,
//       decoration: InputDecoration(
//         labelText: 'Password',
//         border: OutlineInputBorder(),
//         hintText: 'Enter a password',
//         suffixIcon: IconButton(
//           icon: Icon(_isObscure ? Icons.visibility : Icons.visibility_off),
//           onPressed: () {
//             setState(() {
//               _isObscure = !_isObscure;
//             });
//           },
//         ),
//       ),
//       validator: (value) {
//         if (value == null || value.isEmpty) {
//           return 'Password is required';
//         } else if (value.length < 8) {
//           return 'Password should be 8 or more characters';
//         }
//         return null;
//       },
//       onSaved: (String? value) {
//         password = value;
//       },
//       onChanged: (val) {
//         user.password = val;
//       },
//     );
//   }

//   Widget buildPassword2() {
//     return TextFormField(
//       obscureText: _isObscure,
//       decoration: InputDecoration(
//         labelText: 'Re-Enter password',
//         border: OutlineInputBorder(),
//         hintText: 'Re-Enter your password',
//         suffixIcon: IconButton(
//           icon: Icon(_isObscure ? Icons.visibility : Icons.visibility_off),
//           onPressed: () {
//             setState(() {
//               _isObscure = !_isObscure;
//             });
//           },
//         ),
//       ),
//       validator: (value) {
//         if (value == null || value.isEmpty) {
//           return 'Please re-enter password';
//         } else if (value.length < 8) {
//           return 'Password should be 8 or more characters';
//         } else if (value != _pass.text) {
//           return 'Passwords should be equal';
//         }
//         return null;
//       },
//       onSaved: (String? value) {
//         password2 = value;
//       },
//     );
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: Container(
//         margin: EdgeInsets.all(24),
//         child: Form(
//           key: formKey,
//           child: ListView(
//             children: <Widget>[
//               Container(
//                 height: 100.0,
//                 width: 100.0,
//                 child: Stack(
//                   children: [
//                     Positioned(
//                       top: 20.0,
//                       child: Text("Sign Up",
//                           style: TextStyle(
//                               fontFamily: 'TruneoCompany', fontSize: 30.0)),
//                     ),
//                   ],
//                 ),
//               ),
//               buildFirstName(),
//               SizedBox(height: 10.0),
//               buildLastName(),
//               SizedBox(height: 10.0),
//               buildEmail(),
//               SizedBox(height: 10.0),
//               buildPhoneNumber(),
//               SizedBox(height: 10.0),
//               buildLocationDropDown(),
//               SizedBox(height: 10.0),
//               buildPassword(),
//               SizedBox(height: 10.0),
//               buildPassword2(),
//               SizedBox(height: 50.0),
//               GestureDetector(
//                 onTap: () {
//                   if (formKey.currentState!.validate()) {
//                     ScaffoldMessenger.of(context)
//                         .showSnackBar(SnackBar(content: Text(' Signing up')));
//                     register();
//                   }
//                 },
//                 child: Container(
//                   height: 50.0,
//                   child: Material(
//                     borderRadius: BorderRadius.circular(25.0),
//                     color: companyYellow,
//                     elevation: 7.0,
//                     child: Center(
//                       child: Text(
//                         'SIGN UP',
//                         style: TextStyle(
//                             color: Colors.white, fontFamily: 'TruneoCompany'),
//                       ),
//                     ),
//                   ),
//                 ),
//               ),
//               SizedBox(height: 50.0),
//               Row(
//                 mainAxisAlignment: MainAxisAlignment.center,
//                 children: [
//                   Text("Have an account ? "),
//                   SizedBox(width: 5.0),
//                   InkWell(
//                     onTap: () {
//                       Navigator.of(context).push(MaterialPageRoute(
//                           builder: (_) => HomePageCustomer()));
//                     },
//                     child: Text(
//                       'Login',
//                       style: TextStyle(
//                         color: companyYellow,
//                         fontFamily: 'TruneoCompany',
//                       ),
//                     ),
//                   )
//                 ],
//               ),
//             ],
//           ),
//         ),
//       ),
//     );
//   }
// }
