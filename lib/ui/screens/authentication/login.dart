import 'package:email_validator/email_validator.dart';
import 'package:express_delivery/ui/screens/authentication/registration.dart';
import 'package:flutter/material.dart';

class Login extends StatefulWidget {
  const Login({Key? key}) : super(key: key);

  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<Login> {
  final formKey = new GlobalKey<FormState>();

  String? email;
  String? password;

  Color companyYellow = Color(0xFFFEBC17);

  bool _isObscure = true;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        height: MediaQuery.of(context).size.height,
        width: MediaQuery.of(context).size.width,
        child: Form(
          key: formKey,
          child: _buildLoginForm(),
        ),
      ),
    );
  }

  Widget buildEmail() {
    return TextFormField(
      decoration: InputDecoration(
          labelText: 'Email',
          border: OutlineInputBorder(),
          hintText: 'name@Example.com'),
      validator: (value) {
        if (value == null || value.isEmpty) {
          return 'Email is required';
        } else if (!EmailValidator.validate(value)) {
          return 'Please enter valid email';
        }
        return null;
      },
      onSaved: (String? value) {
        email = value;
      },
    );
  }

  Widget buildPassword() {
    return TextFormField(
      obscureText: _isObscure,
      decoration: InputDecoration(
        labelText: 'Password',
        border: OutlineInputBorder(),
        hintText: 'Enter a password',
        suffixIcon: IconButton(
          icon: Icon(_isObscure ? Icons.visibility : Icons.visibility_off),
          onPressed: () {
            setState(() {
              _isObscure = !_isObscure;
            });
          },
        ),
      ),
      validator: (value) {
        if (value == null || value.isEmpty) {
          return 'Password is required';
        } else if (value.length < 8) {
          return 'Password should be 8 or more characters';
        }
        return null;
      },
      onSaved: (String? value) {
        password = value;
      },
    );
  }

  _buildLoginForm() {
    return Padding(
      padding: const EdgeInsets.only(left: 25.0, right: 25.0),
      child: ListView(
        children: [
          SizedBox(
            height: 85.0,
          ),
          Container(
            height: 125.0,
            width: 200.0,
            child: Stack(
              children: [
                Positioned(
                  top: 40.0,
                  child: Text("Login",
                      style: TextStyle(
                          fontFamily: 'TruneoCompany', fontSize: 60.0)),
                ),
                Positioned(
                  top: 100.0,
                  left: 195.0,
                  child: Container(
                    height: 10.0,
                    width: 10.0,
                    decoration: BoxDecoration(
                        shape: BoxShape.circle, color: companyYellow),
                  ),
                )
              ],
            ),
          ),
          SizedBox(
            height: 150.0,
          ),
          buildEmail(),
          SizedBox(height: 10.0),
          buildPassword(),
          SizedBox(height: 5.0),
          GestureDetector(
            onTap: () {
              //todo
            },
            child: Container(
              alignment: Alignment(1.0, 0.0),
              padding: EdgeInsets.only(top: 15.0, left: 20.0),
              child: InkWell(
                child: Text(
                  'Forgot Password',
                  style: TextStyle(
                    color: companyYellow,
                    fontFamily: 'TruneoCompany',
                    fontSize: 11.0,
                  ),
                ),
              ),
            ),
          ),
          SizedBox(height: 50.0),
          GestureDetector(
            onTap: () {
              if (formKey.currentState!.validate()) {
                ScaffoldMessenger.of(context)
                    .showSnackBar(SnackBar(content: Text('Logging in')));
              }
            },
            child: Container(
              height: 50.0,
              child: Material(
                borderRadius: BorderRadius.circular(25.0),
                color: companyYellow,
                elevation: 7.0,
                child: Center(
                  child: Text(
                    'LOGIN',
                    style: TextStyle(
                        color: Colors.white, fontFamily: 'TruneoCompany'),
                  ),
                ),
              ),
            ),
          ),
          SizedBox(height: 50.0),
          Row(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text("No account ? "),
              SizedBox(width: 5.0),
              InkWell(
                onTap: () {
                  Navigator.of(context)
                      .push(MaterialPageRoute(builder: (_) => Registration()));
                },
                child: Text(
                  'Create new account',
                  style: TextStyle(
                    color: companyYellow,
                    fontFamily: 'TruneoCompany',
                  ),
                ),
              )
            ],
          )
        ],
      ),
    );
  }
}
