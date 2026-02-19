
import 'package:flutter/material.dart';

import '../models/details_item_model.dart';
import 'details_item.dart';

class IncomeDetailsListView extends StatelessWidget {
  const IncomeDetailsListView({super.key});


  static const List<DetailsItemModel> items = [
    DetailsItemModel(
      color: Color(0xff2088C7),
      title: 'Design service',
      value: '40%',
    ),
    DetailsItemModel(
      color: Color(0xff4DB7F2),
      title: 'Design product',
      value: '25%',
    ),
    DetailsItemModel(
      color: Color(0xff064060),
      title: 'Product royalti',
      value: '20%',
    ),
    DetailsItemModel(
      color: Color(0xffE2DECD),
      title: 'Other',
      value: '22%',
    ),
  ];

  @override
  Widget build(BuildContext context) {



    return Column(children: items.map((e) => DetailsItem(detailsItemModel: e)).toList());

    // return ListView.builder(
    //   // shrinkWrap: true,
    //   physics: const NeverScrollableScrollPhysics(),
    //   itemCount: items.length,
    //   itemBuilder: (context, index) {
    //     return DetailsItem(detailsItemModel: items[index]);
    //   },
    // );
  }
}

