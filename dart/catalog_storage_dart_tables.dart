import 'package:drift/drift.dart';

/// Список таблиц базы
const List<Type> tables = [
  CabinetSizes,
];

/// Модель таблицы с размерами шкафов
class CabinetSizes extends Table {
  /// Высота
  IntColumn get height => integer()();

  /// Ширина
  IntColumn get width => integer()();

  /// Глубина
  IntColumn get depth => integer()();
}
