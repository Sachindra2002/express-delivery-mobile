import 'package:flutter/material.dart';

class CustomerBottomNav extends StatefulWidget {
  const CustomerBottomNav({ Key? key }) : super(key: key);

  @override
  _CustomerBottomNavState createState() => _CustomerBottomNavState();
}

class _CustomerBottomNavState extends State<CustomerBottomNav> {
  int _selectedIndex = 0;
  List<Widget> _widgetOptions = <Widget>[
    Text('Home'),
    Text('From Me'),
    Text('To Me'),
    Text('Settings'),
  ];

  void _onItemTap(int index) {
    setState(() {
      _selectedIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      bottomNavigationBar: BottomNavigationBar(
        items: const <BottomNavigationBarItem>[
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: "Home", 
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.add_box),
            label: "From Me", 
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.post_add),
            label: "To Me", 
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.settings),
            label: "Settings", 
          ),
        ],
        currentIndex: _selectedIndex,
        onTap: _onItemTap,
      ),
    );
  }
}