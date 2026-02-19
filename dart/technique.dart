class Technique {
  final int number;
  final String description;

  Technique({required this.number, required this.description});

  factory Technique.fromJson(Map<String, dynamic> json) {
    return Technique(
      number: json['technique'],
      description: json['description'],
    );
  }
}
