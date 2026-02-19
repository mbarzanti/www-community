class GradeItem {
  final int item;
  final String description;

  const GradeItem({
    required this.item,
    required this.description,
  });
}

class GradeSection {
  final String title;
  final List<GradeItem> items;

  const GradeSection({
    required this.title,
    required this.items,
  });
}

class GradeProgram {
  final String color;
  final List<GradeSection> sections;

  const GradeProgram({
    required this.color,
    required this.sections,
  });
}
