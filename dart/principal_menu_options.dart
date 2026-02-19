// import 'package:lqh_app/models/mugi_hyeong/mugi_hyeong_models.dart';
import 'package:lqh_app/models/principal_menu_option.dart';
import 'package:lqh_app/screens/views/armed/hyeong/mugi_hyeong.dart';
import 'package:lqh_app/screens/views/unarmed/hyeong/hyeong.dart';
import 'package:lqh_app/screens/views/program.dart';
import 'package:lqh_app/screens/views/techniques/self_defense_techniques.dart';
import 'package:lqh_app/screens/views/unarmed/gibon/gibon.dart';
import 'package:lqh_app/screens/views/armed/gibon/basic_movements.dart';

final List<PrincipalMenuOption> principalMenuOption = [
  PrincipalMenuOption(
    icon: "📖",
    romanized: "Gwajeong",
    spanish: "(Programa)",
    hangul: "과정",
    screen: Programa(),
  ),
  PrincipalMenuOption(
    icon: "🛡️",
    romanized: "Ho Sin Sul",
    spanish: "(Técnicas de defensa)",
    hangul: "호신술",
    screen: SelfDefenseTechniques(),
  ),
  PrincipalMenuOption(
    icon: "🥋",
    romanized: "Hyeong",
    spanish: "(Figuras sin armas)",
    hangul: "형",
    screen: Hyeong(),
  ),
  PrincipalMenuOption(
    icon: "⚔️",
    romanized: "Mugi Hyeong",
    spanish: "(Figuras con armas)",
    hangul: "무기 형",
    screen: MugiHyeong(),
  ),
  PrincipalMenuOption(
    icon: "👊🏽",
    romanized: "Gibon",
    spanish: "(Básicos sin armas)",
    hangul: "기본",
    screen: Gibon(),
  ),
  PrincipalMenuOption(
    icon: "🔪",
    romanized: "Mugi Gibon",
    spanish: "(Básicos con armas)",
    hangul: "무기 기본",
    screen: BasicMovementsWithWeapons(),
  ),
];
