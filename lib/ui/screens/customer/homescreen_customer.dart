import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:intl/intl.dart';

class HomePageCustomer extends StatefulWidget {
  const HomePageCustomer({Key? key}) : super(key: key);

  @override
  _HomePageCustomerState createState() => _HomePageCustomerState();
}

class _HomePageCustomerState extends State<HomePageCustomer> {
  Color companyYellow = Color(0xFFFEBC17);

  DateTime now = DateTime.now();

  var timeNow = int.parse(DateFormat('kk').format(DateTime.now()));
  var message = '';

  @override
  Widget build(BuildContext context) {
    if (timeNow <= 12) {
      message = 'Good Morning Sachindra Rodrigo';
    } else if ((timeNow > 12) && (timeNow <= 16)) {
      message = 'Good Afernoon Sachindra Rodrigo';
    } else if ((timeNow > 16) && (timeNow < 20)) {
      message = 'Good Evening Sachindra Rodrigo';
    } else {
      message = 'Good Evening Sachindra Rodrigo';
    }
    var size = MediaQuery.of(context).size;
    return Scaffold(
      bottomNavigationBar: Container(
        padding: EdgeInsets.symmetric(horizontal: 20, vertical: 10),
        height: 60.0,
        color: Colors.white,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: <Widget>[
            ButtonNavItem(
              navTitle: "Home",
              navSvgSrc: "assets/icons/home.svg",
              navPress: () {},
              isActive: true,
            ),
            ButtonNavItem(
              navTitle: "From Me",
              navSvgSrc: "assets/icons/fromme2.svg",
              navPress: () {},
            ),
            ButtonNavItem(
              navTitle: "To Me",
              navSvgSrc: "assets/icons/tome2.svg",
              navPress: () {},
            ),
            ButtonNavItem(
              navTitle: "Settings",
              navSvgSrc: "assets/icons/settings.svg",
              navPress: () {},
            ),
          ],
        ),
      ),
      body: Stack(
        children: <Widget>[
          Container(
            height: size.height * .45,
            decoration: BoxDecoration(
                color: companyYellow,
                image: DecorationImage(
                    alignment: Alignment.centerLeft,
                    image: AssetImage("assets/images/backgroundabstract.png"))),
          ),
          SafeArea(
              child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
            child: Column(
              children: <Widget>[
                Align(
                  alignment: Alignment.topRight,
                  child: Container(
                    alignment: Alignment.center,
                    height: 45,
                    width: 45,
                    decoration: BoxDecoration(
                      color: Color(0xFFFFAC542),
                      shape: BoxShape.circle,
                    ),
                    child: SvgPicture.asset("assets/icons/menu.svg"),
                  ),
                ),
                Text(
                  message,
                  style: TextStyle(
                    fontFamily: 'TruneoCompany',
                    fontSize: 30.0,
                    color: Colors.white,
                  ),
                ),
                Container(
                  margin: EdgeInsets.symmetric(vertical: 30),
                  padding: EdgeInsets.symmetric(horizontal: 30, vertical: 5),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(19.5),
                  ),
                  child: TextField(
                    decoration: InputDecoration(
                      hintText: "Enter tracking number",
                      icon: SvgPicture.asset("assets/icons/search.svg"),
                      border: InputBorder.none,
                    ),
                  ),
                ),
                Expanded(
                    child: GridView.count(
                  crossAxisCount: 2,
                  childAspectRatio: .95,
                  crossAxisSpacing: 20,
                  mainAxisSpacing: 20,
                  children: <Widget>[
                    CategoryCard(
                      title: "All Shipments",
                      svgSrc: "assets/icons/box2.svg",
                      press: () {},
                    ),
                    CategoryCard(
                      title: "From Me",
                      svgSrc: "assets/icons/fromme.svg",
                      press: () {},
                    ),
                    CategoryCard(
                      title: "To Me",
                      svgSrc: "assets/icons/tome.svg",
                      press: () {},
                    ),
                    CategoryCard(
                      title: "Partner Stores",
                      svgSrc: "assets/icons/shop.svg",
                      press: () {},
                    ),
                    CategoryCard(
                      title: "Completed",
                      svgSrc: "assets/icons/completed.svg",
                      press: () {},
                    ),
                    CategoryCard(
                      title: "Service Centers",
                      svgSrc: "assets/icons/location2.svg",
                      press: () {},
                    ),
                  ],
                ))
              ],
            ),
          )),
        ],
      ),
    );
  }
}

class ButtonNavItem extends StatelessWidget {
  final String navSvgSrc;
  final String navTitle;
  final Function navPress;
  final bool isActive;
  const ButtonNavItem({
    Key? key,
    required this.navSvgSrc,
    required this.navTitle,
    required this.navPress,
    this.isActive = false,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color companyYellow = Color(0xFFFEBC17);
    return GestureDetector(
      onTap: navPress(),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: <Widget>[
          Container(
              height: 20,
              width: 20,
              child: SvgPicture.asset(
                navSvgSrc,
                color: isActive ? companyYellow : Colors.black,
              )),
          Text(
            navTitle,
            style: TextStyle(color: isActive ? companyYellow : Colors.black),
          ),
        ],
      ),
    );
  }
}

class CategoryCard extends StatelessWidget {
  final String svgSrc;
  final String title;
  final Function() press;
  const CategoryCard({
    Key? key,
    required this.svgSrc,
    required this.title,
    required this.press,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(13),
      child: Container(
        //padding: EdgeInsets.all(20),
        decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(13),
            boxShadow: [
              BoxShadow(
                offset: Offset(0, 15),
                blurRadius: 15,
                spreadRadius: -23,
                color: Color(0xFFE6E6E6),
              )
            ]),
        child: Material(
          color: Colors.transparent,
          child: InkWell(
            onTap: press,
            child: Padding(
              padding: const EdgeInsets.all(20.0),
              child: Column(
                children: <Widget>[
                  Spacer(),
                  Container(
                    height: 80,
                    width: 80,
                    child: SvgPicture.asset(svgSrc),
                  ),
                  Spacer(),
                  Text(
                    title,
                    textAlign: TextAlign.center,
                    style: TextStyle(
                        // fontFamily: 'TruneoCompany',
                        fontSize: 15,
                        fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
