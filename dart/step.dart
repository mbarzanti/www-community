class Steps {
  final int step;
  final String description;

  Steps({required this.step, required this.description});

  factory Steps.fromJson(Map<String, dynamic> json) {
    return Steps(step: json['step'], description: json['description']);
  }
}
